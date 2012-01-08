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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import org.junit.Test;

public class CharReaderTest
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
        assertMark( INPUT, stringInput( INPUT ) );
    }

    @Test
    public void testOneByteReadString() throws IOException
    {
        assertOneByteRead( INPUT, stringInput( INPUT ) );
    }

    @Test
    public void testBlockReadString() throws IOException
    {
        assertBlockRead( INPUT, stringInput( INPUT ) );
    }

    @Test
    public void testMarkString() throws IOException
    {
        assertMark( INPUT, bufferInput( INPUT ) );
    }

    private void assertOneByteRead( String chars, Reader in )
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
        in.close();
    }

    private void assertBlockRead( String chars, Reader in )
        throws IOException
    {
        char buff[] = new char[ chars.length() * 2 ];
        assertEquals( chars.length(), in.read( buff ) );
        assertEquals( chars, new String( buff, 0, chars.length() ) );
        assertEquals( -1, in.read() );
        in.close();
    }

    private void assertMark( String chars, Reader in )
        throws IOException
    {
        assertEquals( 5, in.read( new char[5] ) );

        assertTrue( in.markSupported() );
        in.mark( chars.length() - 5 );

        assertEquals( chars.length() - 5,
                      in.read( new char[ chars.length() + 2 ] ) );
        assertEquals( -1, in.read() );

        in.reset();
        assertEquals( (int) ' ', in.read() );
        String sub = chars.substring( 6 );
        char buff[] = new char[ chars.length() + 2 ];
        assertEquals( sub.length(), in.read( buff ) );
        assertEquals( sub, new String( buff, 0, sub.length() ) );
        in.close();
    }

    private Reader arrayInput( String chars )
    {
        return new CharArrayReader( chars.toCharArray() );
    }

    private Reader bufferInput( String chars )
    {
        return new CharBufferReader( CharBuffer.wrap( chars ) );
    }

    private Reader stringInput( String chars )
    {
        return new StringReader( chars );
    }

    private static final String INPUT = "large möose on fire";
}
