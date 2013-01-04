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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;

/**
 * An InputStream reading from a byte array. A faster replacement for
 * {@link java.io.ByteArrayInputStream}. This implementation is
 * unsynchronized and unsafe in the presence of concurrent reads on a
 * single instance.
 *
 * @author David Kellum
 */
public final class ByteArrayInputStream
    extends InputStream
    implements Closeable
{

    /**
     * Construct from array backed ByteBuffer. The buffer position will not
     * be altered as part of read operations.
     * @throws UnsupportedOperationException if buffer isn't backed by an array.
     * @throws ReadOnlyBufferException if buffer is is read-only.
     */
    public ByteArrayInputStream( ByteBuffer buffer )
    {
         this( buffer.array(),
               buffer.arrayOffset() + buffer.position(),
               buffer.remaining() );
    }

    public ByteArrayInputStream( byte[] input )
    {
        this( input, 0, input.length );
    }

    public ByteArrayInputStream( byte[] input, int offset, int length )
    {
        _b = input;
        _pos = offset;
        _end = offset + length;
        if( _end > input.length ) {
            throw new ArrayIndexOutOfBoundsException(
                String.format( "offset:%d + len:%d > array.length:%d",
                               offset, length, input.length ) );
        }
    }

    @Override
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
    public int read( final byte[] out, final int offset, int length )
    {
       length = constrain( length );
       if( length > 0 ) {
           System.arraycopy( _b, _pos, out, offset, length );
           _pos += length;
           return length;
       }
       return -1;
    }

    @Override
    public int read( final byte[] b )
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
        return ( _pos < _end ) ? ( _b[_pos++] & 0xFF ) : -1;
    }

    private int constrain( int length )
    {
        return Math.min( length, available() );
    }

    private final byte[] _b;
    private int _pos;
    private final int _end;
    private int _mark = -1;
}
