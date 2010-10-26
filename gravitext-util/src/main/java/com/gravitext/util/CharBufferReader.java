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
import java.nio.CharBuffer;
import java.nio.InvalidMarkException;

/**
 * A Reader over a {@link java.nio.CharBuffer}. This
 * implementation is unsynchronized and unsafe in the presence of
 * concurrent reads on a single instance.
 *
 * @author David Kellum
 */
public final class CharBufferReader
    extends Reader
    implements Closeable
{
    /**
     * Construct given buffer to read. The buffer should be ready to read
     * (flip() as needed), and will be consumed as as read.
     */
    public CharBufferReader( CharBuffer buffer )
    {
        _b = buffer;
    }

    public int available()
    {
        return _b.remaining();
    }

    /**
     * {@inheritDoc}
     * This implementation sets the internal buffers limit to the current
     * position, such that any subsequent reads return EOF.
     */
    @Override
    public void close()
    {
        // Could aid GC by doing something like _b = CLOSED (empty buffer)
        // but _b couldn't be final then.
        _b.limit( _b.position() );
    }

    @Override
    public void mark( int readlimit )
    {
        _b.mark();
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
           _b.get( out, offset, length );
           return length;
       }
       return -1;
    }

    @Override
    public int read( final char[] out )
    {
        return read( out, 0, out.length );
    }

    @Override
    public void reset() throws IOException
    {
        try {
            _b.reset();
        }
        catch( InvalidMarkException x ) {
            throw new IOException( x );
        }
    }

    @Override
    public long skip( long n )
    {
        int length = constrain( ( n > Integer.MAX_VALUE ) ?
                                Integer.MAX_VALUE : (int) n );
        _b.position( _b.position() + length );

        return length;
    }

    @Override
    public int read()
    {
        if( _b.remaining() > 0 ) {
            return _b.get();
        }
        return -1;
    }

    private int constrain( int length )
    {
        return Math.min( length, _b.remaining() );
    }

    private final CharBuffer _b;

}
