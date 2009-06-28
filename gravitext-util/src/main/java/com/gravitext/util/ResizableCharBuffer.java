/*
 * Copyright 2007 David Kellum
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
import java.nio.CharBuffer;

/**
 * A resizable char buffer complementing {@link java.nio.CharBuffer}.
 * @author David Kellum
 */
public final class ResizableCharBuffer
    implements Appendable
{

    /**
     * Construct given initial capacity.
     */
    public ResizableCharBuffer( final int capacity )
    {
        _b = new char[capacity];
        _pos = 0;
        _limit = capacity;
    }

    /**
     * Put char at the current position and advance. The
     * underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final char c )
    {
        requestCapacity( 1 );
        _b[ _pos++ ] = c;
        return this;
    }

    /**
     * Put string at the current position and advance. The
     * underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final String in )
    {
        return put( in, 0, in.length() );
    }

    /**
     * Put string [start, end) at the current position and
     * advance. The underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final String in,
                                    final int start,
                                    final int end )
    {
        int length = end - start;
        requestCapacity( length );
        in.getChars( start, end, _b, _pos );
        _pos += length;
        return this;
    }

    /**
     * Put char array at the current position and advance. The
     * underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final char[] src )
    {
        return put( src, 0, src.length );
    }

    /**
     * Put char array range [offset, offset+length) at the current
     * position and advance. The underlying buffer will be resized if
     * needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final char[] src,
                                    final int offset,
                                    final int length )
    {
        requestCapacity( length );
        System.arraycopy( src, offset, _b, _pos, length );
        _pos += length;
        return this;
    }

    /**
     * Put CharBuffer contents at the current position and
     * advance. The underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final CharBuffer in )
    {
        if( in.hasArray() ) {
            return put( in.array(),
                        in.arrayOffset() + in.position(),
                        in.remaining() );
        }
        return put( in.toString() );
    }

    /**
     * Put sequence at the current position and advance. The
     * underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final CharSequence in )
    {
        if( in instanceof CharBuffer ) {
            return put( (CharBuffer) in );
        }
        return put( in.toString() );
    }

    /**
     * Put sequence [start, end) at the current position and
     * advance. The underlying buffer will be resized if needed.
     * @return This buffer.
     */
    public ResizableCharBuffer put( final CharSequence in,
                                    final int start,
                                    final int end )
    {
        if( in instanceof CharBuffer ) {
            final CharBuffer cb = (CharBuffer) in;
            if( cb.hasArray() ) {

                if( end - start > cb.remaining() ) {
                    throw new IndexOutOfBoundsException( "end - start" );
                }

                return put( cb.array(),
                            cb.arrayOffset() + cb.position() + start,
                            end - start );
            }
        }
        return put( in.toString(), start, end );
    }

    /**
     * Read from reader to end-of-file and put into this buffer,
     * resizing as needed.
     * @param in to be read
     * @param maxLength maximum bytes to read
     * @param chunkSize suggested number of bytes in a single read
     * @return the number of bytes read
     * @throws java.io.IOException from in
     */
    public final int putFromReader( final Reader in,
                                    int maxLength,
                                    final int chunkSize )
        throws IOException
    {
        int len = 0;
        int start = _pos;
        int readLen;

        while( maxLength > 0 ) {
            requestCapacity( Math.min( chunkSize, maxLength) );

            readLen = Math.min( _limit - _pos, maxLength );
            if( readLen <= 0 ) break;

            len = in.read(_b, _pos, readLen);
            if (len < 0) break;

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
    public final void setPosition( int position )
    {
        _pos = position;
    }

    public ResizableCharBuffer append( final char c )
    {
        return put( c );
    }

    public ResizableCharBuffer append( final CharSequence in )
    {
        return put( in );
    }

    public ResizableCharBuffer append( final CharSequence in,
                                       final int start,
                                       final int end )
    {
        return put( in, start, end );
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

            char[] b = new char[ size ];
            System.arraycopy( _b, 0, b, 0, _pos );
            _b = b;

            _limit = size;
        }
    }

    /**
     * Returns a new CharBuffer wraping the underlying array from [0,
     * position). Changes to the returned buffer or its accessible
     * underlying char array may change the contents of this buffer.
     */
    public final CharBuffer flipAsCharBuffer()
    {
        //FIXME: Give up our copy for safety?

        return CharBuffer.wrap( _b, 0, _pos );
    }

    @Override
    public String toString()
    {
        return new String( _b, 0, _pos );
    }

    private char[] _b;
    private int _pos = 0;
    private int _limit = 0;
}
