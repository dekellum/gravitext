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

import java.io.Writer;

/**
 * A writer to a {@link ResizableCharBuffer}. Provides an
 * unsynchronized and fast alternative to both {@link java.io.StringWriter}
 * (based on synchronized StringBuffer) and
 * {@link java.io.CharArrayWriter} (synchronized).
 *
 * @author David Kellum
 */
public final class ResizableCharBufferWriter extends Writer
{
    public ResizableCharBufferWriter( ResizableCharBuffer buffer )
    {
        _buff = buffer;
    }

    public ResizableCharBufferWriter( int capacity )
    {
        _buff = new ResizableCharBuffer( capacity );
    }

    @Override
    public void write( char[] src )
    {
        _buff.put( src );
    }

    @Override
    public void write( int c )
    {
        _buff.put( (char) c );
    }

    @Override
    public void write( String str, int offset, int length )
    {
        _buff.put( str, offset, offset + length );
    }

    @Override
    public void write( String str )
    {
        _buff.put( str );
    }

    @Override
    public void write( char[] src, int offset, int length )
    {
        _buff.put( src, offset, length );
    }

    @Override
    public void close()
    {
        // do nothing
    }

    @Override
    public void flush()
    {
        // do nothing
    }

    /**
     * Return the underlying buffer.
     */
    public ResizableCharBuffer buffer()
    {
        return _buff;
    }

    private final ResizableCharBuffer _buff;
}
