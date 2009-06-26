/*
 * Copyright (C) 2008 David Kellum
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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.gravitext.util.URL64.FormatException;

public class URL64Test
{
    @Test
    public void testEncode()
    {
        assertEquals( "Lw..", encode( "/" ) );
        assertEquals( "JHg.", encode( "$x" ) );
        assertEquals( "KSFe", encode( ")!^" ) );
        assertEquals( "WCYoJU4lbjE1eHZhcyUlKjE.",
                      encode( "X&(%N%n15xvas%%*1" ) );
        assertEquals( "AP8.", new String(
                          URL64.encode( new byte[] { 0, (byte) 255 } ) ) );
    }

    @Test
    public void testDecode() throws FormatException
    {
        assertEquals( "/",   decode( "Lw.." ) );
        assertEquals( "$x",  decode( "JHg." ) );
        assertEquals( ")!^", decode( "KSFe" ) );
        assertEquals( "X&(%N%n15xvas%%*1",
                      decode( "WCYoJU4lbjE1eHZhcyUlKjE." ) );
    }

    public String encode( String in )
    {
        try {
            return new String( URL64.encode( in.getBytes( "UTF-8" ) ) );
        }
        catch( UnsupportedEncodingException x ) {
            throw new RuntimeException( x );
        }
    }

    public String decode( String in ) throws FormatException
    {
        try {
            return new String( URL64.decode( in.toCharArray() ), "UTF-8" );
        }
        catch( UnsupportedEncodingException x ) {
            throw new RuntimeException( x );
        }

    }
}
