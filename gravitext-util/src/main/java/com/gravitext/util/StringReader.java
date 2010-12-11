/*
 * Copyright (c) 2007-2010 David Kellum
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

import java.io.IOException;
import java.io.Reader;
import java.nio.InvalidMarkException;

/**
 * A Reader over a {@link java.lang.String}. This
 * implementation is unsynchronized and unsafe in the presence of
 * concurrent reads on a single instance.
 *
 * @author David Kellum
 */
public final class StringReader
    extends Reader
    implements Closeable
{

    public StringReader( String input )
    {
        this( input, 0, input.length() );
    }

    /**
     * Construct given buffer to read. The buffer should be ready to read
     * (flip() as needed), and will be consumed as as read.
     */
    public StringReader( String input, int offset, int length )
    {
        _s = input;
        _pos = offset;
        _end = offset + length;
    }

    /**
     * Return the remaining number of characters that may be read.
     */
    public int available()
    {
        return ( _end - _pos );
    }

    /**
     * {@inheritDoc}
     * This implementation sets the internal buffers limit to the current
     * position, such that any subsequent reads return EOF.
     */
    @Override
    public void close()
    {
        _pos = _end;
    }

    @Override
    public void mark( int readlimit )
    {
        _mark = _pos;
    }

    @Override
    public boolean markSupported()
    {
        return true;
    }

    @Override
    public int read( final char[] out, final int offset, int length )
    {
       length = constrain( length );
       if( length > 0 ) {
           _s.getChars( _pos, _pos + length, out, offset );
           _pos += length;
           return length;
       }
       return -1;
    }

    @Override
    public int read( final char[] b )
    {
        return read( b, 0, b.length );
    }

    @Override
    public void reset() throws IOException
    {
        if( _mark < 0 ) throw new InvalidMarkException();
        _pos = _mark;
    }

    @Override
    public long skip( long n )
    {
        int length = constrain( ( n > Integer.MAX_VALUE ) ?
                                Integer.MAX_VALUE : (int) n );
        _pos += length;

        return length;
    }

    @Override
    public int read()
    {
        return ( _pos < _end ) ? _s.charAt( _pos++ ) : -1;
    }

    private int constrain( int length )
    {
        return Math.min( length, available() );
    }

    private final String _s;
    private int _pos;
    private final int _end;
    private int _mark = -1;

}
