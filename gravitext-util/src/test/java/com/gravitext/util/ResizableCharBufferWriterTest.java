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

import java.nio.CharBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResizableCharBufferWriterTest
{

    @Test
    public void write()
    {
        String text = "large moose on fire";
        ResizableCharBuffer rb = new ResizableCharBuffer( 0 );
        ResizableCharBufferWriter writer = new ResizableCharBufferWriter( rb );
        writer.write( "large" );
        writer.write( ' ' );
        writer.write( "" ); //Zero-length
        writer.write( "moose".toCharArray() );
        writer.write( text.toCharArray(), 11, 8 );
        assertBuffer( text, rb );
    }

    private void assertBuffer( String expected, ResizableCharBuffer buffer )
    {
        CharBuffer out = buffer.flipAsCharBuffer();
        assertEquals( expected, out.toString() );
    }
 }
