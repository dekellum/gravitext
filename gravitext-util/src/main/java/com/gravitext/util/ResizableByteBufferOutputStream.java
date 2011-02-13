/*
 * Copyright (c) 2007-2011 David Kellum
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

import java.io.OutputStream;

/**
 * An OutputStream to a {@link ResizableByteBuffer}. Provides an
 * unsynchronized and fast alternative to a
 * {@link java.io.ByteArrayOutputStream} (synchronized).
 *
 * @author David Kellum
 */
public final class ResizableByteBufferOutputStream extends OutputStream
{
    public ResizableByteBufferOutputStream( ResizableByteBuffer buffer )
    {
        _buff = buffer;
    }

    public ResizableByteBufferOutputStream( int capacity )
    {
        _buff = new ResizableByteBuffer( capacity );
    }

    @Override
    public void write( byte[] src )
    {
        _buff.put( src );
    }

    @Override
    public void write( int c )
    {
        _buff.put( (byte) c );
    }

    @Override
    public void write( byte[] src, int offset, int length )
    {
        _buff.put( src, offset, length );
    }

    /**
     * {@inheritDoc}
     * This implementation does nothing, and retains reference to buffer.
     */
    @Override
    public void close()
    {
    }

    @Override
    public void flush()
    {
    }

    /**
     * Return the underlying buffer.
     */
    public ResizableByteBuffer buffer()
    {
        return _buff;
    }

    private final ResizableByteBuffer _buff;
}
