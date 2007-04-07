package com.gravitext.util.perftest;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

public class Harness
{
    public static void main( String[] args ) throws Exception
    {
        // TODO Auto-generated method stub
        new Harness().run( args );
    }

    
    private class WarmupState
    {

        public WarmupState(Class<? extends PerformanceTest> prclass)
        {
            _prclass = prclass;
        }
        
        Class<? extends PerformanceTest> _prclass;
        int _runCount = 1;
        TestRun _prior = null;
        double _warmTime = 0d;
        private double _tchange = Double.NaN;
        
        public boolean run() 
            throws InterruptedException, BrokenBarrierException
        {
            if( ( _warmTime < _warmTarget ) 
                || ( _tchange < -_warmDeltaTarget ) 
                || ( _tchange > _warmDeltaTarget ) ) {

                TestRun tr = new TestRun( _prclass, _runCount, _threadCount );

                System.out.print( tr.formatStartLine() );
                tr.run();
                System.out.print( tr.formatResults( _prior ) );

                _warmTime += tr.duration().seconds();
                
                _tchange = tr.throughputChange( _prior );

                _runCount = (int) ( ( _warmTarget * _warmInterval ) * _runCount
                            * 1.1d
                            / tr.duration().seconds() );
                _prior = tr;

                return true;
            }
            return false;
        }

        public int lastRunCount()
        {
            return _runCount;
        }
    }
    
    private class RunState
    {

        public RunState( Class<? extends PerformanceTest> prclass,
                         int runCount, int iterations )
        {
            _prclass = prclass;
            _runCount = runCount;
            _iterations = iterations;
        }
        
        Class<? extends PerformanceTest> _prclass;
        int _runCount;
        int _iterations;
        TestRun _prior = null;
        
        public boolean run() 
            throws InterruptedException, BrokenBarrierException
        {
            if( _iterations-- > 0 ) {

                TestRun tr = new TestRun( _prclass, _runCount, _threadCount );

                tr.setSeed( 79363 * ( _iterations + 1248 ) );
                
                System.out.print( tr.formatStartLine() );
                tr.run();
                System.out.print( tr.formatResults( _prior ) );

                _prior = tr;

                return true;
            }
            return false;
        }
    }
    
    
    
    public void run( String[] args ) 
        throws Exception
    {
        processArgs( args );
        System.out.println( "Testing with (-c)oncurrency: " + _threadCount );
                           
        // Build an instance of each test class up front and keep a reference
        // to avoid loosing any optimizer passes with class gc.
        for( Class<? extends PerformanceTest> prclass : _runnables ) {
            _instances.add( prclass.newInstance() );
        }
        
        if( _verbose ) {
            boolean wrap = ( _runnables.size() > 1 );
            for( PerformanceTest t : _instances ) {
                Class<? extends PerformanceTest> prclass = t.getClass();
                
                if( wrap ) {
                    System.out.println( 
                            "---- " + prclass.getName() + " ----" );
                }
                PerformanceTest test = prclass.newInstance();
                test.setVerbose( true );
                test.runTest( new FastRandom( 32020172 ) );
                
                if( wrap ) System.out.println();
            }
            return;
        }
        
        System.out.println( "==== Warmup Runs :" );

        System.out.print( TestRun.header() );

        int lCount = Integer.MAX_VALUE;
        if( _warmCount == 0 ) {
            ArrayList<WarmupState> warmups = new ArrayList<WarmupState>();
            for( PerformanceTest t : _instances ) {
                Class<? extends PerformanceTest> prclass = t.getClass();
                warmups.add( new WarmupState( prclass ) );
            }
            boolean remaining = true;
            while( remaining ) {
                remaining = false;
                for( WarmupState warmup : warmups ) {
                    if( warmup.run() ) remaining = true;
                }
            }
            for( WarmupState warmup : warmups ) {
                lCount = Math.min(  warmup.lastRunCount(), lCount );
            }
        }
        else {
            ArrayList<RunState> warmups = new ArrayList<RunState>();
            for( PerformanceTest t : _instances ) {
                Class<? extends PerformanceTest> prclass = t.getClass();
                warmups.add( 
                    new RunState( prclass, _warmCount, _warmIterations ) );
            }
            boolean remaining = true;
            while( remaining ) {
                remaining = false;
                for( RunState r: warmups ) {
                    if( r.run() ) remaining = true;
                }
            }
            lCount = _warmCount;
        }
        int rCount = ( _requestCount == 0 ) ? lCount : _requestCount;
        
        System.out.println( "\n==== Comparison Runs :" );

        System.out.print( TestRun.header() );

        TestRun lastFirst = null;
        for( int i = 0; i < _requestIterations; ++i ) {
            TestRun first = null;
            for( PerformanceTest t : _instances ) {
                Class<? extends PerformanceTest> prclass = t.getClass();

                TestRun tr = 
                    new TestRun( prclass, rCount, _threadCount );

                tr.setSeed( 2828065 * ( i + 939829 ) );

                System.out.print( tr.formatStartLine() );
                tr.run();
                System.out.print
                ( tr.formatResults( (first == null) ? lastFirst : first  ) );

                if( first == null ) lastFirst = first = tr;
            }
        }
    }

    private void processArgs( String[] args ) 
        throws ClassNotFoundException
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
                if( flag == 'c' ) _threadCount=Integer.parseInt(args[i]);
                else if( flag == 'w' ) _warmCount=Integer.parseInt(args[i]);
                else if( flag == 'r' ) _requestCount=Integer.parseInt(args[i]);
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
                _runnables.add( 
                    Class.forName(args[i]).asSubclass( PerformanceTest.class ) );
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
    private int _warmCount = 0;
    private int _warmIterations = 3;
    private int _requestCount = 0;
    private int _requestIterations = 3;
    private double _warmTarget = 60.0d;
    private double _warmInterval = 1.0d / 3.0d;
    private double _warmDeltaTarget = 0.05d;
    private boolean _verbose = false;
    
    private ArrayList<Class<? extends PerformanceTest>> _runnables =
        new ArrayList<Class<? extends PerformanceTest>>();
    
    private ArrayList<PerformanceTest> _instances =
        new ArrayList<PerformanceTest>();
    
    private String[] _runArgs = new String[0];
}
    
