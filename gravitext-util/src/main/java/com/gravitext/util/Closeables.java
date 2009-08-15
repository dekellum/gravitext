package com.gravitext.util;

import java.io.IOException;

/**
 * Utilities for Closeable instances.
 */
public class Closeables
{
    /**
     * Close target if it implements {@link com.gravitext.util.Closeable}
     * @return true if close was called successfully.
     */
    public static boolean closeIf( Object target )
    {
        if( ( target != null ) && ( target instanceof Closeable ) ) {
            ( (Closeable) target ).close();
            return true;
        }
        return false;
    }

    /**
     * Close target it if implements either {@link com.gravitext.util.Closeable}
     * or {@link java.io.Closeable}.
     * @return true if close was called successfully.
     * @throws IOException from {@link java.io.Closeable#close()}
     */
    public static boolean closeIfAny( Object target ) throws IOException
    {
        if( closeIf( target ) ) return true;

        if( ( target != null ) && ( target instanceof java.io.Closeable ) ) {
            ( (java.io.Closeable) target ).close();
            return true;
        }
        return false;
    }
}
