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
import java.io.InputStream;

/**
 * Constrains a wrapped InputStream to a maximum read length, throwing a
 * {@link MaxLengthException} if exceeded. This implementation is
 * unsynchronized and unsafe in the presence of concurrent reads on a
 * single instance.
 *
 * @author David Kellum
 */
public final class ConstrainedInputStream extends InputStream
{
    /**
     * Thrown if constructed maxLength is exceeded by reads.
     */
    public final static class MaxLengthException extends IOException
    {
        public MaxLengthException( int length )
        {
            super( "Attempt to read input length > " + length );
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @param base stream to read from.
     * @param maxLength total length in bytes to allow from reads
     */
    public ConstrainedInputStream( InputStream base, int maxLength )
    {
        _base = base;
        _capacity = maxLength;
        _maxLength = maxLength;
    }

    /**
     * Returns the number of bytes actually read or skipped from this
     * stream.
     */
    public int readLength()
    {
        return _maxLength - _capacity;
    }

    /**
     * {@inheritDoc}
     * Constrains available bytes to the least of base.available() and
     * remaining maxLength.
     */
    @Override
    public int available() throws IOException
    {
        return Math.min( _base.available(), _capacity );
    }

    /**
     * {@inheritDoc}
     * @throws MaxLengthException if maximum length exceeded.
     */
    public int read() throws IOException
    {
        if( _capacity < 1 ) {
            throw new MaxLengthException( _maxLength );
        }
        int out = _base.read();
        if( out != -1 ) --_capacity;
        return out;
    }

    /**
     * {@inheritDoc}
     * @throws MaxLengthException if maximum length exceeded.
     */
    @Override
    public int read( final byte[] b, final int off, int len )
        throws IOException
    {
        if( ( len > 0 ) && ( _capacity < 1 ) ) {
            throw new MaxLengthException( _maxLength );
        }
        len = Math.min( len, _capacity );
        final int count = _base.read( b, off, len );

        if( count > 0 ) _capacity -= count;

        return count;
    }

    /**
     * {@inheritDoc}
     * @throws MaxLengthException if maximum length exceeded.
     */
    @Override
    public long skip( long len ) throws IOException
    {
        if( ( len > 0 ) && ( _capacity < 1 ) ) {
            throw new MaxLengthException( _maxLength );
        }
        len = Math.min( len, _capacity );
        final long count = _base.skip( len );
        if( count > 0 ) _capacity -= count;
        return count;
    }

    @Override
    public void close() throws IOException
    {
        _base.close();
    }

    @Override
    public void mark( int readlimit )
    {
        _base.mark( readlimit );
        _markCapacity = _capacity;
    }

    @Override
    public boolean markSupported()
    {
        return _base.markSupported();
    }

    @Override
    public int read( byte[] b ) throws IOException
    {
        return read( b, 0, b.length );
    }

    @Override
    public void reset() throws IOException
    {
        _base.reset();  //may throw if not reset
        _capacity = _markCapacity;
    }

    private final InputStream _base;
    private int _capacity;
    private int _markCapacity;
    private final int _maxLength;
}
