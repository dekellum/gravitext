package com.gravitext.util.perftest;

import java.util.concurrent.TimeUnit;

/**
 * Nanosecond resolution stopwatch. Can also be used as a Barrier timer. 
 * @author David Kellum
 */
public final class Stopwatch implements Runnable
{
    public Stopwatch()
    {
    }

    public Stopwatch start()
    {
        if( _started ) {
            throw new IllegalStateException( "Stopwatch already started." );
        }
        _started = true;
        _start = System.nanoTime();
        return this;
    }
    
    public Stopwatch stop()
    {   
        if( ! _started ) {
            throw new IllegalStateException( "Stopwatch not started." );
        }
        _end = System.nanoTime();
        _started = false;
        return this;
    }

    public void run()
    {
        if( ! _started ) start();
        else stop();
    }

    /**
     * @return time delta in nanoseconds between calls to start and stop, 
     * or zero if never started
     */
    public long delta()
    {
        if( _started ) {
            throw new IllegalStateException( "Stopwatch not stopped" );
        }
        return ( _end - _start ); 
    }
    
    /**
     * @return Duration between calls to start and stop, or zero if never 
     * started.
     */
    public Duration duration()
    {
        return new Duration( delta(), TimeUnit.NANOSECONDS );
    }

    private boolean _started = false;
    private long _start = 0;
    private long _end = 0;
}
