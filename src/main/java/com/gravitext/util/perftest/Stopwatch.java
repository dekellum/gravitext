package com.gravitext.util.perftest;

import java.util.concurrent.TimeUnit;

/**
 * Nano resolution stopwatch. Can also be used as a Barrier timer. 
 * @author David Kellum
 */
public final class Stopwatch implements Runnable
{
    private boolean _started = false;
    private long _start = 0;
    private long _end = 0;

    public Stopwatch()
    {
    }

    public Stopwatch start()
    {
        _start = System.nanoTime();
        _started = true;
        return this;
    }
    
    public Stopwatch stop()
    {   
        //FIXME: Insure started first.
        _end = System.nanoTime();
        return this;
    }

    public void run()
    {
        if( ! _started ) start();
        else stop();
    }
    
    /**
     * Returns Duration between calls to start and stop.
     * @return 
     */
    public Duration duration()
    {
        // FIXME: Test state
        
        return new Duration( _end - _start, TimeUnit.NANOSECONDS );
    }
    /**
     * @return time delta in nanoseconds.
     */
    public long delta()
    {
        return ( _end - _start ); 
    }
    
}
