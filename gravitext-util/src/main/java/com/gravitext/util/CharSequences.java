/*
 * Copyright (c) 2007-2012 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.gravitext.util;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Utility methods for CharSequences and readers/writers.
 * @author David Kellum
 */
public final class CharSequences
{
    /**
     * Return given CharSequence as a CharBuffer either by casting or
     * wrapping.
     */
    public static CharBuffer asCharBuffer( CharSequence value )
    {
        if( value instanceof CharBuffer ) {
            return ( (CharBuffer) value );
        }
        return CharBuffer.wrap( value );
    }

    /**
     * Return the given CharSequence as a writable CharBuffer, copying if
     * necessary.
     */
    public static CharBuffer writableCharBuffer( CharSequence value )
    {
        CharBuffer cbuff = asCharBuffer( value );

        if( cbuff.isReadOnly() ) {
            CharBuffer out = CharBuffer.allocate( value.length() );
            out.append( value );
            out.flip();
            return out;
        }
        return cbuff;
    }

    /**
     * Return true if t1 equals t2, character wise.
     */
    public static boolean equals( CharSequence t1, CharSequence t2 )
    {
        if( t1 == t2 ) return true;
        if( ( t1 == null ) || ( t2 == null ) ) return false;

        final int len1 = t1.length();
        if( len1 != t2.length() ) return false;

        for( int i = 0; i < len1; ++i ) {
            if( t1.charAt( i ) != t2.charAt( i ) ) return false;
        }
        return true;
    }

    /**
     * Return an optimal Reader over the specified CharSequence.
     * The returned Reader is not internally synchronized.
     */
    public static Reader reader( CharSequence s )
    {
        if( s instanceof CharBuffer ) {
            return reader( (CharBuffer) s );
        }
        return new StringReader( s.toString() );
    }

    /**
     * Return an optimal Reader over the specified CharBuffer.
     * The returned Reader is not internally synchronized.
     * The buffer position will not be altered as the stream is read.
     */
    public static Reader reader( CharBuffer b )
    {
        return ( ( b.hasArray() && ! b.isReadOnly() ) ?
                   new CharArrayReader( b ) :
                   new CharBufferReader( b.duplicate() ) );
    }
}
