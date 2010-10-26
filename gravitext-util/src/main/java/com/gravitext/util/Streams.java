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
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Utility methods for input/output streams.
 * @author David Kellum
 */
public final class Streams
{
    /**
     * Return an optimal InputStream over the specified ByteBuffer.
     * The returned InputStream is not internally synchronized.
     * The buffer position will not be altered as the stream is read.
     */
    public static InputStream inputStream( final ByteBuffer b )
    {
        return ( ( b.hasArray() && ! b.isReadOnly() ) ?
                    new ByteArrayInputStream( b ) :
                    new ByteBufferInputStream( b.duplicate() ) );
    }

    /**
     * Copy in to out using a default 4k buffer.
     * @see #copy(InputStream, OutputStream, int)
     * @return the number of bytes copied.
     */
    public static int copy( final InputStream in,
                            final OutputStream out )
        throws IOException
    {
        return copy( in, out, 4 * 1024 );
    }

    /**
     * Copy in to out using the specified intermediary byte buffer size.
     * @return the number of bytes copied.
     */
    public static int copy( final InputStream in,
                            final OutputStream out,
                            final int bufferSize )
        throws IOException
    {
        int count = 0;
        final byte[] buff = new byte[ bufferSize ];
        while( true ) {
            final int len = in.read( buff );
            if( len > 0 ) {
                out.write( buff, 0, len );
                count += len;
            }
            else break;
        }
        return count;
    }
}
