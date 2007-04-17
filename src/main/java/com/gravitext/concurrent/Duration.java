package com.gravitext.concurrent;

import java.util.concurrent.TimeUnit;

public final class Duration
{
    public Duration(long delta, TimeUnit unit)
    {
        switch( unit ) {
        case NANOSECONDS:  _seconds = (double) delta / 1e9d; break;
        case MICROSECONDS: _seconds = (double) delta / 1e6d; break;
        case MILLISECONDS: _seconds = (double) delta / 1e3d; break;
        case SECONDS:      _seconds = (double) delta; break;
        }
    }
    
    public Duration( double seconds )
    {
        _seconds = seconds;
    }
    
    public double seconds()
    {
        return _seconds;
    }
    
    public Duration subdivide( long divisor )
    {
        return new Duration ( _seconds / divisor );
    }
    
    /**
     * Returns a String representation of this duration using the most
     * appropriate unit [s ms µs ns] and 3 fractional digits.
     */
    public String toString()
    {
        StringBuilder b = new StringBuilder( 16 );
        Metric.format( _seconds, b );
        b.append( 's' );
        return b.toString();
    }

    private double _seconds; // In seconds.
}
