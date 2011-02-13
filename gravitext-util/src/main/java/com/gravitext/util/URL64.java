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

/**
 * A URL64 encoder and decoder utility. URL64 is identical to Base64
 * except that all characters can be inserted into a URL parameter
 * without encoding. The 65 character grammar includes: A-Z a-z 0-9
 * '-' '_' and '.' The '.' is used only as a padding character.
 */
public final class URL64
{

    public final static class FormatException
        extends Exception
    {
        public FormatException( String message )
        {
            super( message );
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * Encode bytes to URL64 characters
     */
    static public char[] encode( final byte[] in )
    {
        return encode( in, 0, in.length );
    }

    /**
     * Encode bytes to URL64 characters
     */
    static public char[] encode( final byte[] in,
                                 final int offset,
                                 final int length )
    {
        final char[] out = new char[ ( length + 2 ) / 3 * 4 ];

        int o = 0;
        int i = offset;
        final int end = i + length;
        while( i < end ) {

            // IN: 1-3 bytes, with 8b left shifted pad
            int b = 8;
            int val = 0;
            while( i < end && b < 32 ) {
                val |= in[ i++ ] & 0xFF;
                val <<= 8;
                b += 8;
            }
            // b is high bit (16,24,32)

            // OUT: 4 x 6b chars (0-2 pads)
            final int p = o + 4;
            while( o < p ) {
                b -= 6;
                out[ o++ ] = LEXICON[( b > 0 ) ? ( ( val >> b ) & 0x3F ) : 64];
            }
        }
        return out;
    }

    /**
     * Decode URL64 encoded characters and return original bytes.
     */
    static public byte[] decode( final char[] in ) throws FormatException
    {
        final int end = in.length;
        int olen = ( ( end + 3 ) / 4 ) * 3;
        if( end >= 1 && in[ end - 1 ] == '.' ) --olen;
        if( end >= 2 && in[ end - 2 ] == '.' ) --olen;

        final byte[] out = new byte[ olen ];

        int b = 0;
        int val = 0;
        int o = 0;

        for( int i = 0; i < end; i++ ) {
            int pnt = ( in[i] <= 255 ) ? VALUES[ in[i] ] : OUT_OF_RANGE;
            if( pnt == OUT_OF_RANGE ) {
                throw new FormatException(
                   "Invalid URL64 character '" + in[i] + "'." );
            }

            if( pnt == PAD ) break;

            val <<= 6;
            b += 6;
            val |= pnt;
            if( b >= 8 ) {
                b -= 8;
                out[ o++ ] = (byte)( ( val >> b ) & 0xFF );
            }
        }

        if( o != olen ) {
            throw new FormatException(
                "Invalid URL64 sequence at position " + o + '.' );
        }

        return out;
    }

    private static final int PAD          = -1;
    private static final int OUT_OF_RANGE = -2;

    // code characters for values 0..64
    private static final char[] LEXICON =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_."
        .toCharArray();

    // lookup table for converting URL64 characters to value in range 0..63
    private static final byte[] VALUES = new byte[ 256 ];
    static {
        for( int i=0; i<256; i++ ) VALUES[i] = OUT_OF_RANGE;
        for( int i = 'A'; i <= 'Z'; i++ ) VALUES[i] = (byte)(      i - 'A' );
        for( int i = 'a'; i <= 'z'; i++ ) VALUES[i] = (byte)( 26 + i - 'a' );
        for( int i = '0'; i <= '9'; i++ ) VALUES[i] = (byte)( 52 + i - '0' );
        VALUES['-'] = 62;
        VALUES['_'] = 63;
        VALUES['.'] = PAD;
    }
}
