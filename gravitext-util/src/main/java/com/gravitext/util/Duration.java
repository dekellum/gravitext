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

package com.gravitext.util;

import java.util.concurrent.TimeUnit;

/**
 * Immutable representation of a span of time.
 * @author David Kellum
 */
public final class Duration
{
    /**
     * Construct given a long duration value and associated time unit.
     */
    public Duration( long delta, TimeUnit unit )
    {
        double s = (double) delta;

        switch( unit ) {
        case NANOSECONDS:  s /= 1e9d; break;
        case MICROSECONDS: s /= 1e6d; break;
        case MILLISECONDS: s /= 1e3d; break;
        case SECONDS:                 break;

        //FIXME: These were added in JDK 1.6 but we don't want that
        //dependency yet.
        //case MINUTES:      s *= 60d; break;
        //case HOURS:        s *= 60d * 60d; break;
        //case DAYS:         s *= 60d * 60d * 24d; break;

        default: throw new IllegalArgumentException( "Unknown: "+ unit );
        }

        _seconds = s;
    }

    /**
     * Construct given a double value in seconds.
     */
    public Duration( double seconds )
    {
        _seconds = seconds;
    }

    /**
     * Return duration as a double value in seconds.
     */
    public double seconds()
    {
        return _seconds;
    }

    /**
     * Return a new duration from this duration divided by the
     * specified divisor.
     */
    public Duration divide( double divisor )
    {
        return new Duration ( _seconds / divisor );
    }

    /**
     * Return a String representation of this duration using the most
     * appropriate unit suffix (ex: s ms µs ns) and seven total
     * characters.
     */
    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder( 16 );
        Metric.format( _seconds, b );
        b.append( 's' );
        return b.toString();
    }

    private final double _seconds; // Duration in seconds
}
