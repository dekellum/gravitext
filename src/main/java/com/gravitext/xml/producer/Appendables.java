package com.gravitext.xml.producer;

import java.io.IOException;

/**
 * Utility class for optimized append functions for the generic Appendable 
 * interface.  
 *  
 * @author David Kellum
 *
 */
public final class Appendables
{
    //FIXME: Really optimize these based on perf tests?

    public static void append( Appendable out, double f ) throws IOException
    {
        out.append( Double.toString( f ) );
    }

    public static void append( Appendable out, float f ) throws IOException
    {
        out.append( Float.toString( f ) );
    }
    
    public static void append( Appendable out, long i ) throws IOException
    {
        out.append( Long.toString( i ) );
    }

    public static void append( Appendable out, int i ) throws IOException
    {
        out.append( Integer.toString( i ) );
    }

    public static void append( Appendable out, short i ) throws IOException
    {
        out.append( Short.toString( i ) );
    }
    
    private Appendables() {}
}
