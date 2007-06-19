package com.gravitext.perftest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.reflect.BeanAccessor;
import com.gravitext.reflect.BeanException;

public class Harness
{
    public static void main( String[] args ) throws Exception
    {
        new Harness().run( args );
    }
    
   
    public void run( String[] args ) throws Exception
    {
        processArgs( args );
        
        System.out.println( "Testing with (-c)oncurrency: " + _threadCount );
                           
        if( _verbose ) {
            runVerbose();
        }
        else {
            warmup();
            runComparisons();
        }
    }

    /**
     * Run the warmup iterations
     */
    private void warmup()
    {
        System.out.println( "==== Warmup Runs :" );
        System.out.print( PerformanceTester.header() );

        ArrayList<Runner> warmups = new ArrayList<Runner>();
        for( ConcurrentTest t : _instances ) {
            Runner r;
            if( _warmCount == 0 ) {
                r = new AdaptiveRunner( t );
            }
            else {
                r = new Runner( t, _warmCount, _warmIterations );
            }
            warmups.add( r );
        }
        boolean remaining = true;
        while( remaining ) {
            remaining = false;
            for( Runner warmup : warmups ) {
                warmup.run( null );
                if( warmup.hasMoreRuns() ) remaining = true;
            }
        }

        double minTP = Double.MAX_VALUE;
        double maxTP = Double.MIN_VALUE;
        for( Runner warmup : warmups ) {
            minTP = Math.min( minTP, warmup.prior().meanThroughput() );
            maxTP = Math.max( maxTP, warmup.prior().meanThroughput() );
        }

        if( _compCount == 0 ) {
            _compCount = (int) Math.min( _compTarget    * maxTP, 
                                         _compTargetMax * minTP );
        }
    }

    /**
     * Run the comparison iterations.
     */
    private void runComparisons()
    {
        System.out.println( "\n==== Comparison Runs :" );
        System.out.print( PerformanceTester.header() );
        
        ArrayList<Runner> comps = new ArrayList<Runner>();
        for( ConcurrentTest t : _instances ) {
            comps.add( new Runner( t, _compCount, _compIterations ) );
        }
        PerformanceTester lastFirst = null;
        boolean remaining = true;
        while( remaining ) {
            remaining = false;
            PerformanceTester first = null;
            for( Runner comp : comps ) {
                comp.run( (first != null) ? first : lastFirst );
                if( first == null ) lastFirst = first = comp.prior();
                if( comp.hasMoreRuns() ) remaining = true;
            }
        }
    }
    
    /**
     * Run verbose single execution.
     * @throws Exception
     */
    private void runVerbose() throws Exception
    {
        boolean wrap = ( _instances.size() > 1 );
        for( ConcurrentTest t : _instances ) {

            if( wrap ) System.out.println( 
                       "---- " + t.getClass().getName() + " ----" );

            t.runTest( 1, 32020172 );
            
            if( wrap ) System.out.println();
        }
    }

    
    private class Runner
    {
        Runner( ConcurrentTest ctest, int runCount, int iterations )
        {
            this( ctest );
            _runCount = runCount;
            _iterations = iterations;
            _fixedSeed = true;
        }

        void run( PerformanceTester prior )
        {
            if( prior == null ) prior = _prior;
            
            PerformanceTester tester = 
                new PerformanceTester( _ctest, _runCount, _threadCount );
            
            if( _fixedSeed ) tester.setSeed( 7936 * ( _iterations + 1234 ) );
                
            System.out.print( tester.formatStartLine() );
            tester.runTest();
            System.out.print( tester.formatResults( prior ) );

            _prior = tester;
            
            --_iterations;
        }

        boolean hasMoreRuns()
        {
            return ( _iterations > 0 );
        }

        PerformanceTester prior() 
        {
            return _prior;
        }
                
        protected Runner( ConcurrentTest ctest )
        {
            _ctest = ctest;
        }
        
        protected final ConcurrentTest _ctest;
        protected int _runCount = 1;
        protected PerformanceTester _prior = null;

        private boolean _fixedSeed = false;
        private int _iterations = 0;
    }
    
    private class AdaptiveRunner extends Runner
    {
        AdaptiveRunner( ConcurrentTest ctest )
        {
            super( ctest );
        }
               
        @Override
        void run( PerformanceTester altPrior )
        {
            PerformanceTester prior = prior();
            super.run( altPrior );
            PerformanceTester current = prior(); 

            _warmTime += current.duration().seconds();
            _tchange = current.throughputChange( prior );
            _runCount = (int) ( ( _warmTarget * _warmInterval ) * _runCount
                        * 1.25d
                        / current.duration().seconds() );
        }
        
        @Override
        boolean hasMoreRuns()
        {
            return ( ( _warmTime < _warmTarget ) || 
                     ( _tchange < -_warmTolerance ) || 
                     ( _tchange > _warmTolerance ) );
        }
        
        private double _warmTime = 0d;
        private double _tchange = Double.NaN;
    }
    
    

    private void processArgs( String[] args ) 
        throws ClassNotFoundException, 
               InstantiationException, 
               IllegalAccessException,
               BeanException
    {
        int i = 0;  
        char flag = 0;
        ArrayList<String> commonArgs = new ArrayList<String>();

        ConcurrentTest _lastTest = null;
        
        while( i < args.length ) {

            if( args[i].startsWith("--") ) {
                if( _lastTest == null ) commonArgs.add( args[i] );
                else applyArgument( _lastTest, args[i] );
            }
            else if( flag != 0 ) {
                if( flag == 'c' )      _threadCount=Integer.parseInt(args[i]);
                else if( flag == 'w' ) _warmCount=Integer.parseInt(args[i]);
                else if( flag == 'r' ) _compCount=Integer.parseInt(args[i]);
                else {
                    System.err.println(
                        "ERROR: Invalid flag [-" + flag + "]." );
                    usage();
                }
                flag = 0;
            }
            else if( args[i].startsWith( "-" ) ) {
                if( args[i].length() != 2 ) usage();
                char f = args[i].charAt(1);
                if( f == 'v' ) { _verbose = true; }
                else flag = f;
            }
            else {
                Class<? extends ConcurrentTest> ctClass =
                    Class.forName( args[i] ).asSubclass( ConcurrentTest.class );
                _lastTest = ctClass.newInstance();
                
                if( _verbose ) setVerbose( _lastTest );
                
                for( String arg : commonArgs ) {
                    applyArgument( _lastTest, arg );
                }
                
                _instances.add( _lastTest );
            }
            i++;
        }
        if( flag != 0 ) usage();
        if( _instances.size() < 1 ) usage();
    }

    private void setVerbose( Object bean )
    {
        try {
            new BeanAccessor( bean ).setProperty( "verbose", true );
        }
        catch( BeanException x ) {
            // Ignore in verbose case.
        }
    }


    private void applyArgument( Object bean, String argument ) 
        throws BeanException
    {
        Matcher m = ARG_PATTERN.matcher( argument );
        if( m.find() ) {
            String property = m.group( 1 );
            Object value = m.group( 3 );
            if( value == null ) value = true;
            new BeanAccessor( bean ).setProperty( property, value );
        }
        else {
            System.err.println(
                "ERROR: Invalid bean argument [" + argument + "]." );
            usage();
        }
    }

    private static final Pattern ARG_PATTERN = 
        Pattern.compile( "^\\-\\-([^=\\s]+)(=(.+))?$" );
    
    private void usage()
    {
        System.err.println( 
    "Usage: " + getClass().getName() + " [-c] [-w] [-r] [-v]\n" +
    "       [--globalProp[=value]]... [<TestClass> --prop[=value]...] ... \n" +
    " -c threads : thread count.\n" +
    " -w count   : test run count per warmup iteration.\n" +
    " -r count   : test run count per comparison run iteration.\n" +
    " -v         : Verbose mode (TestClass.setVerbose(true)).\n" +
    " --globalProp[=value] : Set globalProp value on all TestClass instances.\n" +
    " --prop[=value]       : Set prop value on previous TestClass only.\n\n"
        );
        System.exit(1);
    }

    private int _threadCount = Runtime.getRuntime().availableProcessors();

    private int    _warmCount      = 0;
    private int    _warmIterations = 3;
    private double _warmTarget     = 40.0d; //seconds
    private double _warmInterval   = 1.0d / 4.0d; //~10 second intervals
    private double _warmTolerance  = 0.05d; // +/- 5%
    
    private int    _compCount      = 0;
    private int    _compIterations = 3;
    private double _compTarget     = 10.0d; //seconds
    private double _compTargetMax  = 60.0d; //seconds
    
    private boolean _verbose       = false;
        
    private ArrayList<ConcurrentTest> _instances =
        new ArrayList<ConcurrentTest>();
}
    
