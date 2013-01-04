/*
 * Copyright (c) 2007-2013 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.gravitext.util.Duration;
import com.gravitext.util.FastRandom;
import com.gravitext.util.Stopwatch;

/**
 * Runs a concurrent test constructed from a TestFactory.
 *
 * @author David Kellum
 */
public class TestExecutor
{
    /**
     * @param factory to construct TestRunnable instances (per thread)
     * @param runs the total number of runs to test
     * @param threads the number of threads to concurrently run the test
     */
    public TestExecutor( TestFactory factory, int runs, int threads )
    {
        _factory = factory;
        _runCount = runs;
        _threads = threads;
        _barrier = new CyclicBarrier( threads + 1, _totalRunTime );
    }

    /**
     * Run specified test. The first of any Exceptions thrown in a test thread
     * from {@code TestRunnable.runIteration()} will be re-thrown from this
     * method after wrapping as needed in a RuntimeException.
     * @param factory the test factory instance
     * @param runs the total number of runs to test
     * @param threads the number of threads to concurrently run the test
     * @throws RuntimeException from TestRunnable.runIteration()
     * @throws Error, for example, when jUnit assertions are used in the
     *        TestRunanble
     * @return the sum of result counts returned from {@code runIteration()}
     */
    public static long run( TestFactory factory, int runs, int threads )
    {
        TestExecutor exec = new TestExecutor( factory, runs, threads );
        exec.setDoPerRunTiming( false );
        return exec.runTest();
    }

    /**
     * Set an alternative seed value to pass to each call of {@code
     * ConcurrentTest.runTest()}. The default seed is randomly selected with
     * such factors as the current system clock.
     */
    public void setSeed( int seed )
    {
        _seed = seed;
    }

    /**
     * Set whether to do per-run latency timing. This timing can skew very fast
     * test runs.
     */
    public void setDoPerRunTiming( boolean doTime )
    {
        _doPerRunTiming = doTime;
    }

    /**
     * Run the test based on constructed values. The first of any Exceptions
     * thrown in a test thread from {@code TestRunnable.runIteration()} will be
     * re-thrown from this method after wrapping as needed in a
     * RuntimeException.
     *
     * @throws RuntimeException from TestRunnable.runIteration()
     * @throws Error, for example, when jUnit assertions are used in the
     *             TestRunanble
     * @return the sum of result counts returned from {@code
     *         ConcurrentTest.runTest()}
     */
    public final long runTest()
    {
        for( int i = 0; i < _threads; ++i ) {
            new Thread( new Runner( _factory.createTestRunnable( _seed + i ) ),
                        _factory.name() + ':' + i ).start();
        }

        try {
            _barrier.await(); // wait for all threads ready.
            _barrier.await(); // wait for completion

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

    public final TestFactory factory()
    {
        return _factory;
    }

    /**
     * Return the total (wall) time used to run the test.
     */
    public final Duration duration()
    {
        return _totalRunTime.duration();
    }

    /**
     * Return the sum of result counts returned from {@code
     * ConcurrentTest.runTest()}.
     */
    public final long resultSum()
    {
        return _resultSum.get();
    }

    /**
     * Return the number of runs requested on construction.
     */
    public final int runsTarget()
    {
        return _runCount;
    }

    /**
     * Return the number of runs actually executed (before completed or error).
     */
    public final int runsExecuted()
    {
        return _lastRun.get();
    }

    /**
     * Return the mean throughput in runs/second.
     */
    public final double meanThroughput()
    {
        return ( ( (double) runsExecuted() ) / duration().seconds() );
    }

    /**
     * Return the mean latency per test run.
     */
    public final Duration meanLatency()
    {
        return new Duration( _latencySum.get(), TimeUnit.NANOSECONDS )
            .divide( runsExecuted() );
    }

    private final class Runner implements Runnable
    {
        public Runner( TestRunnable ctest )
        {
            _ctest = ctest;
        }

        public final void run()
        {
            try {
                _barrier.await(); // Signal ready and wait
                long count = 0;
                long latency = 0;
                final Stopwatch stime =
                    _doPerRunTiming ? null : new Stopwatch();
                if( stime != null ) stime.start();
                try {
                    int run;
                    final Stopwatch rtime =
                        _doPerRunTiming ? new Stopwatch() : null;

                    run_loop: while( _error == null ) {

                        // atomic increment run while run <= _runCount
                        while( true ) {
                            final int last = _lastRun.get();
                            run = last + 1;
                            if( run > _runCount ) break run_loop;
                            if( _lastRun.compareAndSet( last, run ) ) break;
                        }

                        if( rtime != null ) rtime.start();
                        count += _ctest.runIteration( run );
                        if( rtime != null ) latency += rtime.stop().delta();
                    }
                }
                catch( Throwable x ) {
                    synchronized( TestExecutor.this ) {
                        if( _error == null ) _error = x;
                    }
                }
                finally {
                    if( stime != null ) latency = stime.stop().delta();
                    _latencySum.addAndGet( latency );

                    _resultSum.addAndGet( count );

                    _barrier.await();
                }
            }
            catch( InterruptedException e ) {
                // ignore but terminate
            }
            catch( BrokenBarrierException e ) {
                // ignore but terminate
            }
        }

        private final TestRunnable _ctest;
    }

    private final TestFactory   _factory;
    private final int           _threads;
    private final int           _runCount;
    private final AtomicInteger _lastRun        = new AtomicInteger( 0 );
    private final AtomicLong    _resultSum      = new AtomicLong( 0 );
    private final AtomicLong    _latencySum     = new AtomicLong( 0 );
    private boolean             _doPerRunTiming = true;
    private final Stopwatch     _totalRunTime   = new Stopwatch();
    private final CyclicBarrier _barrier;
    private int                 _seed           = FastRandom.generateSeed();
    private volatile Throwable  _error          = null;
}
