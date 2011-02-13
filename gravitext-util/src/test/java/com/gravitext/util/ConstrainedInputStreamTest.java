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

public class ConstrainedInputStreamTest
{

    @Test
    public void testGoodByte() throws IOException
    {
        ConstrainedInputStream in = new ConstrainedInputStream( source(), 11 );

        int c = 0;
        while( c != -1 ) {
            c = in.read();
        }
        assertEquals( -1, c );
        assertEquals( 10, in.readLength() );
    }

    @Test
    public void testBadByte() throws IOException
    {
        ConstrainedInputStream in = new ConstrainedInputStream( source(), 9 );

        for( int i = 0; i < 9; ++i ) {
            assertTrue( -1 != in.read() );
        }

        try {
            in.read();
            fail();
        }
        catch( ConstrainedInputStream.MaxLengthException x ) {
            // expected.
        }
    }

    @Test
    public void testGoodBlock() throws IOException
    {
        ConstrainedInputStream in = new ConstrainedInputStream( source(), 9 );

        byte buff[] = new byte[9];
        in.read( buff );

        assertEquals( "123456789", new String( buff, ISO_8859_1 ) );
    }

    @Test
    public void testMark() throws IOException
    {
        ConstrainedInputStream in = new ConstrainedInputStream( source(), 10 );
        assertEquals( 1, in.read( new byte[1] ) );

        assertTrue(  in.markSupported() );

        in.mark( 9 );
        assertEquals( 8, in.read( new byte[8] ) );
        in.reset();

        byte buff[] = new byte[9];
        in.read( buff );
        assertEquals( "234567890", new String( buff, ISO_8859_1 ) );
        assertEquals( 10, in.readLength() );
    }

    @Test
    public void testBadBlock() throws IOException
    {
        ConstrainedInputStream in = new ConstrainedInputStream( source(), 9 );

        byte buff[] = new byte[8];
        assertEquals( 8, in.read( buff ) );

        assertEquals( 1, in.read( buff ) );

        try {
            in.read( buff );
            fail();
        }
        catch( ConstrainedInputStream.MaxLengthException x ) {
            // expected.
        }
    }

    private InputStream source()
    {
        return Streams.inputStream( ISO_8859_1.encode( "1234567890" ) );
    }
 }
