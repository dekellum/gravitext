/*
 * Copyright (c) 2007-2013 David Kellum
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
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import static org.junit.Assert.*;
import static com.gravitext.util.Charsets.ISO_8859_1;

public class ResizableByteBufferOutputStreamTest
{
    @Test
    public void write() throws IOException
    {
        String text = "large möose on fire";
        ResizableByteBuffer rb = new ResizableByteBuffer( 0 );
        OutputStream out = new ResizableByteBufferOutputStream( rb );
        writeBytes( out, "large" );
        writeBytes( out, " " );
        writeBytes( out, "" );
        writeBytes( out, "möose" );
        out.write( bytes( text ), 11, 8 );
        assertBuffer( text, rb );
    }

    private void writeBytes( OutputStream out, String value )
        throws IOException
    {
       byte[] bs = bytes( value );
       if( bs.length == 1 ) {
           out.write( bs[0] & 0xFF );
       }
       else {
           out.write( bs );
       }
    }

    private byte[] bytes( String text )
    {
        return text.getBytes( ISO_8859_1 );
    }

    private void assertBuffer( String expected, ResizableByteBuffer buffer )
    {
        ByteBuffer out = buffer.flipAsByteBuffer();
        assertEquals( expected, ISO_8859_1.decode( out ).toString() );
    }
}
