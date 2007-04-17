/**
 * 
 */
package com.gravitext.util.perftest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


class ConcurrentTester implements Runnable
{
    public ConcurrentTester( ConcurrentTest ctest, 
                             int runs, 
                             int threads )
    {
        _ctest = ctest;
        _runCount = runs;
        _threads = threads;
        _barrier = new CyclicBarrier( threads + 1, _totalRunTime );
    }
    
    /**
     * @throws RuntimeException from ConcurrentTest.runTest() or wrapped 
     * Exception
     * @throws Error for example when jUnit assertions are used in the 
     * ConcurrentTest
     */
    public final long runTest()
    {
        for(int i = 0; i < _threads; ++i ) {
            new Thread( this, testClass().getSimpleName() + ':' + i ).start();
        }
        
        try {
            _barrier.await(); //wait for all threads ready.
            _barrier.await(); //wait for completion
            
            synchronized( this ) {
                if( _error != null ) {
                    if( _error instanceof RuntimeException ) {
                        throw (RuntimeException) _error;
                    }
                    else if( _error instanceof Error ) {
                        throw (Error) _error;
                    }
                    else {
                        throw new RuntimeException( _error );
                    }
                }
                return resultSum();
            }
        }
        catch( InterruptedException e ) {
            throw new RuntimeException( "Test interrupted.", e );
        }
        catch( BrokenBarrierException e ) {
            throw new RuntimeException( "Test barrier broken.", e );
        } 
    }
    
    public Duration duration()
    {
        return _totalRunTime.duration();
    }

    public long resultSum()
    {
        return _resultSum.get();
    }

    public int runsTarget()
    {
        return _runCount;
    }
    
    public int runsExecuted()
    {
        return _lastRun.get();
    }
    
    public double meanThroughput()
    {
        return ( ( (double) runsExecuted() ) / duration().seconds() );
    }

    public Duration meanLatency()
    {
        return new Duration( _latencySum.get(), TimeUnit.NANOSECONDS )
                   .subdivide( runsExecuted() );
    }
    
    public void setSeed( int seed )
    {
        _seed = seed;
        
    }

    public Class<? extends ConcurrentTest> testClass()
    {
        return _ctest.getClass();
    }
    
    public final void run()
    {
        try {
            _barrier.await(); // Signal ready and wait
            try {
                int run;
                int count;
                final Stopwatch s = new Stopwatch();
                
                run_loop: 
                while( _error == null ) {

                    // atomic increment run up until == _runCount
                    while( true ) {
                        count = _lastRun.get();
                        run = count + 1;
                        if( run > _runCount ) break run_loop; 
                        if( _lastRun.compareAndSet( count, run ) ) break; 
                    }
                    
                    s.start();
                    count = _ctest.runTest( run, _seed );
                    s.stop();

                    _resultSum.addAndGet( count );
                    _latencySum.addAndGet( s.delta() );
                    
                }
            }
            catch( Throwable x ) {
                synchronized( this ) {
                    if( _error == null ) _error = x;
                }
            }
            finally {
                _barrier.await();
            }
        }
        catch( InterruptedException e ) {
            //ignore but terminate
        }
        catch( BrokenBarrierException e ) {
            //ignore but terminate
        }
    }

    private final ConcurrentTest _ctest;
    private final int _threads;
    private final int _runCount;
    private final AtomicInteger _lastRun = new AtomicInteger( 0 );
    private final AtomicLong _resultSum = new AtomicLong( 0 );
    private final AtomicLong _latencySum = new AtomicLong( 0 );
    private final Stopwatch _totalRunTime = new Stopwatch();
    private final CyclicBarrier _barrier;
    private int _seed = FastRandom.generateSeed();
    private volatile Throwable _error = null;
}