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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A resizable byte buffer complementing {@link java.nio.ByteBuffer}.
 * @author David Kellum
 */
public class ResizableByteBuffer
{
    /**
     * Construct given initial capacity.
     */
    public ResizableByteBuffer( final int capacity )
    {
        _b = new byte[capacity];
        _pos = 0;
        _limit = capacity;
    }

    /**
     * Put the byte value at the index position.
     * @throws IndexOutOfBoundsException if the put does not fit in
     * underlying array.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( final int index,
                                          final byte value )
    {
        _b[ index ] = value;
        return this;
    }

    /**
     * Put byte at the current position and advance. The
     * underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( final byte value )
    {
        requestCapacity( 1 );
        return put( _pos++, value );
    }

    /**
     * Put the byte array value at the current position and advance
     * value.length bytes. The underlying buffer will be resized if
     * needed.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( final byte[] value )
    {
        return put( value, 0, value.length );
    }

    /**
     * Put the range [offset, offset+length) of the byte array value
     * to current position and advance length bytes. The underlying
     * buffer will be resized if needed.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( byte[] value,
                                          int offset, int length )
    {
        requestCapacity( length );
        put( _pos, value, offset, length );
        _pos += length;
        return this;
    }

    /**
     * Put the range [offset, offset+length) of the byte array value
     * at the index position.
     * @throws IndexOutOfBoundsException if the put does not fit in
     * underlying array.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( final int index,
                                          final byte[] value,
                                          int offset,
                                          int length )
    {
        System.arraycopy( value, offset, _b, index, length );
        return this;
    }

    /**
     * Put ByteBuffer contents at the current position and advance.
     * The underlying buffer will be resized if needed. The in buffer
     * will be consumed and positioned at limit.
     * @return This buffer.
     */
    public final ResizableByteBuffer put( final ByteBuffer in )
    {
        final int len = in.remaining();
        requestCapacity( len );
        in.get( _b, _pos, len );
        _pos += len;
        return this;
    }

    /**
     * Read from input stream to end-of-file and put into this buffer,
     * resizing as needed.
     * @param in to be read
     * @param maxLength maximum bytes to read
     * @param chunkSize suggested number of bytes in a single read
     * @return the number of bytes read
     * @throws java.io.IOException from in
     */
    public final int putFromStream( final InputStream in,
                                    int maxLength,
                                    final int chunkSize )
        throws IOException
    {
        int len = 0;
        int start = _pos;
        int readLen;

        while( maxLength > 0 ) {
            requestCapacity( Math.min( chunkSize, maxLength ) );

            readLen = Math.min( _limit - _pos, maxLength );
            if( readLen <= 0 ) break;

            len = in.read( _b, _pos, readLen );
            if( len < 1 ) break;

            _pos += len;
            maxLength -= len;
        }

        return ( _pos - start );
    }

    /**
     * Return the current position offset into the underlying buffer.
     */
    public final int position()
    {
        return _pos;
    }

    /**
     * Set the position offset into the underlying buffer.
     */
    public final void setPosition( final int position )
    {
        _pos = position;
    }

    /**
     * Insure that length additional capacity is available.
     */
    public final void requestCapacity( final int length )
    {
        if( ( _pos + length ) > _limit ) {

            int size = _limit;
            if( size == 0 ) size = 1;
            size *= 2;
            if( size < ( _pos + length ) ) size = _pos + length;

            byte[] b = new byte[ size ];

            System.arraycopy( _b, 0, b, 0, _pos );

            _b = b;
            _limit = size;
        }
    }

    /**
     * Returns a new ByteBuffer which wraps the underlying array from
     * [0, position). Changes to the returned buffer or its
     * accessible underlying byte array may change the contents of
     * this buffer.
     */
    public final ByteBuffer flipAsByteBuffer()
    {
        return ByteBuffer.wrap( _b, 0, _pos );
    }

    private byte[] _b;
    private int _pos = 0;
    private int _limit = 0;
}
