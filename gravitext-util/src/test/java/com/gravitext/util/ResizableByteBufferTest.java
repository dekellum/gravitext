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
import java.nio.ByteBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

import static com.gravitext.util.Charsets.UTF_8;

public class ResizableByteBufferTest
{

    @Test
    public void putFromStream() throws IOException
    {
        ResizableByteBuffer buffer = new ResizableByteBuffer( 0 );
        buffer.putFromStream( createStream( "spam" ), 5, 3 );
        assertEquals( 4, buffer.position() );
        assertBuffer( "spam", buffer );
    }

    @Test
    public void putFromStreamTwice() throws IOException
    {
        ResizableByteBuffer buffer = new ResizableByteBuffer( 0 );
        InputStream bytes = createStream( "dog " );
        buffer.putFromStream( bytes, 3, 1  );
        assertEquals( 3, buffer.position() );
        assertBuffer( "dog", buffer );
        assertEquals( 1, bytes.available() );

        buffer.putFromStream( createStream( " food" ), 5, 12 );
        assertEquals( 8, buffer.position() );
        assertBuffer( "dog food", buffer );
    }

    @Test
    public void putZeroLengthStream() throws IOException
    {
        ResizableByteBuffer buffer = new ResizableByteBuffer(0);
        InputStream bytes = createStream( "never used" );

        buffer.putFromStream( bytes, -1, -1 );
        buffer.putFromStream( bytes, 3, 0 );
        buffer.putFromStream( bytes, 0, 3 );
        buffer.putFromStream( bytes, 0, 0 );

        assertEquals( 0, buffer.position() );
        assertEquals( "never used".length(), bytes.available() );
    }

    private void assertBuffer( String expected, ResizableByteBuffer buffer )
    {
        ByteBuffer out = buffer.flipAsByteBuffer();

        assertEquals( expected, UTF_8.decode( out ).toString() );
    }

    private InputStream createStream( String text )
    {
        return Streams.inputStream( UTF_8.encode( text ) );
    }
}
