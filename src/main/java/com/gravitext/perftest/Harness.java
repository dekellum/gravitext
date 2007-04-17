package com.gravitext.perftest;

import java.util.ArrayList;

import com.gravitext.concurrent.ConcurrentTest;

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
               IllegalAccessException
    {
        int i = 0;  
        char flag = 0;
        boolean doRunArgs = false;
        ArrayList<String> runArgs = new ArrayList<String>();
        
        while( i < args.length ) {
            if( doRunArgs ) {
                if( ! args[i].startsWith( "-" ) ) {
                    doRunArgs = false;
                    continue;
                }
                runArgs.add( args[i] );
            }
            else if( args[i].equals("--") ) doRunArgs = true;

            else if( flag != 0 ) {
                if( flag == 'c' )      _threadCount=Integer.parseInt(args[i]);
                else if( flag == 'w' ) _warmCount=Integer.parseInt(args[i]);
                else if( flag == 'r' ) _compCount=Integer.parseInt(args[i]);
                else usage();
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
                
                _instances.add( ctClass.newInstance() );
            }
            i++;
        }
        if( flag != 0 ) usage();
        
        _runArgs = runArgs.toArray( _runArgs );
    }

    private void usage()
    {
        System.out.println( 
        "Usage: " + getClass().getName() + '\n' +
        "       -c -w -r -v [-- <global args>] <class> ...\n"
        );
        System.exit(1);
    }

    private int _threadCount = Runtime.getRuntime().availableProcessors();

    private int    _warmCount      = 0;
    private int    _warmIterations = 3;
    private double _warmTarget     = 40.0d;
    private double _warmInterval   = 1.0d / 4.0d; //~10 second intervals
    private double _warmTolerance  = 0.05d; // +/- 5%
    
    private int    _compCount      = 0;
    private int    _compIterations = 3;
    private double _compTarget     = 10.0d; //seconds
    private double _compTargetMax  = 60.0d; //seconds
    
    private boolean _verbose       = false;
        
    private ArrayList<ConcurrentTest> _instances =
        new ArrayList<ConcurrentTest>();
    
    private String[] _runArgs = new String[0]; //FIXME: Use?
}
    
