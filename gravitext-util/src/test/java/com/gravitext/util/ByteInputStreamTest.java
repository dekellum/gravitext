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

import static org.junit.Assert.*;
import static com.gravitext.util.Charsets.ISO_8859_1;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ByteInputStreamTest
{
    @Test
    public void testOneByteRead() throws IOException
    {
        assertOneByteRead( INPUT, arrayInput( INPUT ) );
    }

    @Test
    public void testBlockRead() throws IOException
    {
        assertBlockRead( INPUT, arrayInput( INPUT ) );
    }

    @Test
    public void testMark() throws IOException
    {
        assertMark( INPUT, arrayInput( INPUT ) );
    }

    @Test
    public void testOneByteReadBuffer() throws IOException
    {
        assertOneByteRead( INPUT, bufferInput( INPUT ) );
    }

    @Test
    public void testBlockReadBuffer() throws IOException
    {
        assertBlockRead( INPUT, bufferInput( INPUT ) );
    }

    @Test
    public void testMarkBuffer() throws IOException
    {
        assertMark( INPUT, bufferInput( INPUT ) );
    }

    private void assertOneByteRead( String chars, InputStream in )
        throws IOException
    {
        int i = 0;
        while( true ) {
            int b = in.read();
            if( b == -1 ) break;
            assertEquals( chars.charAt( i ), b );
            ++i;
        }
        assertEquals( chars.length(), i );
        assertEquals( 0, in.available() );
        in.close();
    }

    private void assertBlockRead( String chars, InputStream in )
        throws IOException
    {
        assertEquals( in.available(), chars.length() );

        byte buff[] = new byte[ chars.length() * 2 ];
        assertEquals( chars.length(), in.read( buff ) );
        assertEquals( chars, new String(buff, 0, chars.length(), ISO_8859_1) );
        assertEquals( -1, in.read() );

        in.close();
    }

    private void assertMark( String chars, InputStream in )
        throws IOException
    {
        assertEquals( 5, in.read( new byte[5] ) );

        assertTrue( in.markSupported() );
        in.mark( in.available() );

        assertEquals( in.available(),
                      in.read( new byte[ in.available() + 2 ] ) );
        assertEquals( 0, in.available() );
        assertEquals( -1, in.read() );

        in.reset();
        assertEquals( ' ' & 0xFF, in.read() );
        String sub = chars.substring( 6 );
        byte buff[] = new byte[ in.available() + 2 ];
        assertEquals( sub.length(), in.read( buff ) );
        assertEquals( sub, new String( buff, 0, sub.length(), ISO_8859_1 ) );
        assertEquals( 0, in.available() );

        in.close();
    }

    private InputStream arrayInput( String chars )
    {
        return new ByteArrayInputStream( ISO_8859_1.encode( chars ) );
    }

    private InputStream bufferInput( String chars )
    {
        return new ByteBufferInputStream( ISO_8859_1.encode( chars ) );
    }

    private static final String INPUT = "large möose on fire";
}
