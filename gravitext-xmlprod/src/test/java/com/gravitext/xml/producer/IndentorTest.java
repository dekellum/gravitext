/*
 * Copyright (c) 2008-2011 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.gravitext.xml.producer;

import java.io.IOException;

import junit.framework.TestCase;

public class IndentorTest extends TestCase
{
    public void testInvarients()
    {
        assertTrue ( Indentor.COMPRESSED.isCompressed() );
        assertTrue (          new Indentor(null).isCompressed() );
        assertFalse( Indentor.LINE_BREAK.isCompressed() );
        assertFalse(           new Indentor("X").isCompressed() );

        assertTrue ( Indentor.LINE_BREAK.isLineBreak() );
        assertTrue (            new Indentor("").isLineBreak() );
        assertFalse( Indentor.COMPRESSED.isLineBreak() );
        assertFalse(           new Indentor("X").isLineBreak() );
    }

    public void testLargeIndent() throws IOException
    {
        Indentor i = new Indentor("12");
        StringBuilder b = new StringBuilder(128);
        i.indent( b, 3 );
        assertEquals( "\n121212", b.toString() );
        b.setLength( 0 );
        i.indent( b, 17 );
        assertEquals( 2 * 17 + 1, b.length() );
        b.setLength(  0 );
        i.indent( b, 67 );
        assertEquals( 2 * 67 + 1, b.length() );
    }

    public void testCompressed() throws IOException
    {
        Indentor i = Indentor.COMPRESSED;
        StringBuilder b = new StringBuilder(128);
        i.indent( b, 3 );
        assertEquals( "", b.toString() );
    }

    public void testLineBreaks() throws IOException
    {
        Indentor i = Indentor.LINE_BREAK;
        StringBuilder b = new StringBuilder(128);
        i.indent( b, 3 );
        assertEquals( "\n", b.toString() );
    }

}
