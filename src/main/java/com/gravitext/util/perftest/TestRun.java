/**
 * 
 */
package com.gravitext.util.perftest;

import java.util.Formatter;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class TestRun
{
    
    TestRun( Class<? extends PerformanceTest> prclass, 
             int runCount, int threads )
    {
        _prclass = prclass;
        _runCount = runCount;
        _countDown = new AtomicInteger(runCount);
        _barrier = new CyclicBarrier( threads + 1, _stopwatch );
        _threads = threads;
    }
    
    void setSeed( int seed )
    {
        _seed = seed;
    }
    
    long run() throws InterruptedException, BrokenBarrierException
    {
        for(int i = 0; i < _threads; ++i ) {
            new RunThread( "TestRun: " + i ).start();
        }
        _barrier.await(); //wait for all threads ready.
        _barrier.await(); //wait for completion
        return resultSum();
    }
    
    public Duration duration()
    {
        return _stopwatch.duration();
    }

    public long resultSum()
    {
        return _resultSum.get();
    }
    
    public int ranCount()
    {
        return _runCount;
    }
    
    public double meanThroughput()
    {
        return ( (double) ranCount() / _stopwatch.delta() * 1e9D );
    }
    
    public double throughputChange( TestRun prior )
    {
        if( prior == null ) return Double.NaN;
        
        double c = meanThroughput();
        double p = prior.meanThroughput();
        
        return ( ( c - p ) / p );     
    }

    public double latencyChange( TestRun prior )
    {
        if( prior == null ) return Double.NaN;
        double c = meanLatency().seconds();
        double p = prior.meanLatency().seconds();
        
        return ( ( c - p ) / p );        
    }
    
    Duration meanLatency()
    {
        return new Duration( _latencySum.get(), TimeUnit.NANOSECONDS )
                   .subdivide( ranCount() );
    }

    public static CharSequence header()
    {
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%-20s %-6s %-7s %-6s %8s %10s(%6s) %-9s (%6s)\n",
                  "Test Class",
                  "Count",
                  "Time",
                  "R Sum",
                  "~R Value",
                  "Throughput",
                  "Change",
                  "~Latency",
                  "Change" );
        o.append( "---------------------------------------------" + 
                  "--------------------------------------------\n" );
        
        return o;
    }
    
    public CharSequence formatStartLine()
    {
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%-20s %6s ",
                  _prclass.getSimpleName(),
                  Metric.format( _runCount ) );
        return o;
    }
    
    
    public CharSequence formatResults(TestRun prior)
    {
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%7s %6s %6s/r %6sr/s (%6s) %7s/r (%6s)\n",
                  _stopwatch.duration(),
                  Metric.format( (double) resultSum() ),
                  Metric.format( (double) resultSum() / ranCount() ),
                  Metric.format( meanThroughput() ),
                  Metric.formatDifference( throughputChange( prior ) ),     
                  meanLatency(),
                  Metric.formatDifference( latencyChange( prior ) ) );
        return o;
    }
    
   
    private final class RunThread extends Thread
    {
        private RunThread(String name)
        {
            super( name );
        }

        public void run()
        {
            try {
                PerformanceTest pr = _prclass.newInstance();

                _barrier.await(); // Signal ready and wait
                
                int cd;
                while( ( cd = _countDown.getAndDecrement() ) > 0 ) {
                    Stopwatch s = new Stopwatch().start();
                    int count = pr.runTest( new FastRandom( _seed + cd  ) );
                    _latencySum.addAndGet( s.stop().delta() );
                    _resultSum.addAndGet( count );
                }
                _barrier.await(); // Signal end
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            catch( BrokenBarrierException e ) {
                e.printStackTrace();
            }
            catch( Exception x ) {
                System.err.println( "Death of " + this.getName() );
                x.printStackTrace();
                _barrier.reset();
            }
        }
    }
    
    private final Class<? extends PerformanceTest> _prclass;
    private final int _runCount;
    private final int _threads;
    private final Stopwatch _stopwatch = new Stopwatch();
    private final CyclicBarrier _barrier;
    private final AtomicLong _resultSum = new AtomicLong(0);
    private final AtomicLong _latencySum = new AtomicLong(0);
    private final AtomicInteger _countDown;
    private int _seed = FastRandom.generateSeed();
}