/*
 * Copyright (c) 2007-2012 David Kellum
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

package com.gravitext.util;

import java.util.concurrent.TimeUnit;

/**
 * Nanosecond resolution stopwatch. A single instance may be used for
 * multiple {@code start()}, {@code stop()}, and {@code duration()}
 * cycles.  A Stopwatch can also be used to time the duration between
 * trips of a {@code CyclicBarrier}. The {@code Runnable} interface is
 * implemented for this purpose.
 * @see java.util.concurrent.CyclicBarrier#CyclicBarrier(int, java.lang.Runnable)
 * @author David Kellum
 */
public final class Stopwatch implements Runnable
{
    /**
     * Default constructor (does not start).
     */
    public Stopwatch()
    {
    }

    /**
     * Start by setting the start time to the current time.
     * @return this Stopwatch
     */
    public Stopwatch start()
    {
        if( _running ) {
            throw new IllegalStateException( "Stopwatch already started." );
        }
        _running = true;
        _start = System.nanoTime();
        return this;
    }

    /**
     * Stop by setting the stop time to the current time.
     * @return this Stopwatch
     */
    public Stopwatch stop()
    {
        if( ! _running ) {
            throw new IllegalStateException( "Stopwatch not started." );
        }
        _end = System.nanoTime();
        _running = false;
        return this;
    }

    /**
     * Toggles between start/stop states.  If not started, starts the
     * stopwatch.  If already started, stops the stopwatch.
     */
    public void run()
    {
        if( ! _running ) start();
        else stop();
    }

    /**
     * Returns Duration between calls to start and stop.
     * @throws IllegalStateException if not previously started and
     * stopped.
     */
    public Duration duration()
    {
        return new Duration( delta(), TimeUnit.NANOSECONDS );
    }

    /**
     * Returns the time delta in nanoseconds between calls to start
     * and stop.
     * @throws IllegalStateException if not previously started and
     * stopped.
     */
    public long delta()
    {
        if( _start == 0 ) {
            throw new IllegalStateException( "Stopwatch not started" );
        }
        if( _running ) {
            throw new IllegalStateException( "Stopwatch not stopped" );
        }
        return ( _end - _start );
    }

    private boolean _running = false;
    private long _start = 0;
    private long _end = 0;
}
