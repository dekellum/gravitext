/**
 * 
 */
package com.gravitext.perftest;

import com.gravitext.concurrent.ConcurrentTest;

class Runner
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
    
    void setThreadCount( int count )
    {
        _threadCount = count;
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
    private int _threadCount = Runtime.getRuntime().availableProcessors();
}