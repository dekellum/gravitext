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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResizableCharBufferTest
{
    @Test
    public void putString()
    {
        ResizableCharBuffer buffer = new ResizableCharBuffer( 0 );
        buffer.put( "moose" );
        assertEquals( 5, buffer.position() );
        assertBuffer( "moose", buffer );
    }

    @Test
    public void putCharBuffer()
    {
        String text = "large moose on fire";
        CharBuffer cb = CharBuffer.wrap( text, 6, 11 );
        assertEquals( "moose", cb.toString() );
        ResizableCharBuffer rb = new ResizableCharBuffer( 0 );
        rb.put( "large" ).put( ' ' );
        rb.put( CharBuffer.allocate( 0 ) ); //Zero-length
        rb.put( cb );
        rb.put( text.toCharArray(), 11, 8 );
        assertBuffer( text, rb );
    }

    @Test
    public void putCharBufferAsSequence()
    {
        String text = "large moose on fire";
        CharSequence cs = CharBuffer.wrap( text, 6, 11 );
        assertEquals( "moose", cs.toString() );
        ResizableCharBuffer rb = new ResizableCharBuffer( 1 );
        rb.put( cs ).put( ' ' );
        rb.put( cs, 0, 5 ).put( ' ' );
        rb.put( "mo" ).put( cs, 2, 3 ).put( "se" );
        assertBuffer( "moose moose moose", rb );
    }

    @Test
    public void putSequence()
    {
        String text = "large moose on fire";
        CharSequence cs = text.subSequence( 6, 11 );
        assertEquals( "moose", cs.toString() );
        ResizableCharBuffer rb = new ResizableCharBuffer( 1 );
        rb.put( cs ).put( ' ' );
        rb.put( cs, 0, 5 ).put( ' ' );
        rb.put( "mo" ).put( cs, 2, 3 ).put( "se" );
        assertBuffer( "moose moose moose", rb );
    }

    @Test
    public void putFromReader() throws IOException
    {
        ResizableCharBuffer buffer = new ResizableCharBuffer( 0 );
        buffer.putFromReader( createReader( "spam" ), 5, 3 );
        assertEquals( 4, buffer.position() );
        assertBuffer( "spam", buffer );
    }

    @Test
    public void putFromReaderTwice() throws IOException
    {
        ResizableCharBuffer buffer = new ResizableCharBuffer( 0 );
        Reader reader = createReader( "dog " );
        buffer.putFromReader( reader, 3, 1  );
        assertEquals( 3, buffer.position() );
        assertBuffer( "dog", buffer );
        assertTrue( reader.ready() );

        buffer.putFromReader( createReader( " food" ), 5, 12 );
        assertEquals( 8, buffer.position() );
        assertBuffer( "dog food", buffer );
    }

    @Test
    public void putZeroLengthReader() throws IOException
    {
        ResizableCharBuffer buffer = new ResizableCharBuffer(0);
        Reader reader = createReader( "never used" );

        buffer.putFromReader( reader, -1, -1 );
        buffer.putFromReader( reader, 3, 0 );
        buffer.putFromReader( reader, 0, 3 );
        buffer.putFromReader( reader, 0, 0 );

        assertEquals( 0, buffer.position() );
        assertTrue( reader.ready() );
    }

    private void assertBuffer( String expected, ResizableCharBuffer buffer )
    {
        CharBuffer out = buffer.flipAsCharBuffer();
        assertEquals( expected, out.toString() );
    }

    private StringReader createReader( String text )
    {
        return new StringReader( text );
    }
}
