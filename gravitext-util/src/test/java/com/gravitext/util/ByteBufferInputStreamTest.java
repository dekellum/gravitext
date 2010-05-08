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

import static org.junit.Assert.*;
import static com.gravitext.util.Charsets.ISO_8859_1;

import java.io.IOException;

import org.junit.Test;

public class ByteBufferInputStreamTest
{

    @Test
    public void testOneByteRead()
    {
        String chars = "large möose on fire";
        ByteBufferInputStream in = source( chars );

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

    @Test
    public void testBlockRead()
    {
        String chars = "large möose on fire";
        ByteBufferInputStream in = source( chars );
        assertEquals( in.available(), chars.length() );

        byte buff[] = new byte[ chars.length() * 2 ];
        assertEquals( chars.length(), in.read( buff ) );
        assertEquals( chars, new String(buff, 0, chars.length(), ISO_8859_1) );
        assertEquals( -1, in.read() );

        in.close();
    }

    @Test
    public void testMark() throws IOException
    {
        String chars = "large möose on fire";
        ByteBufferInputStream in = source( chars );

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

    private ByteBufferInputStream source( String chars )
    {
        return new ByteBufferInputStream( ISO_8859_1.encode( chars ) );
    }
}
