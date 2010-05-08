/*
 * Copyright (c) 2008-2010 David Kellum
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

package com.gravitext.xml.producer;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Encodes text as XML character data and attribute values.
 *
 * @author David Kellum
 */
public class CharacterEncoder
{
    /**
     * Modes for handling special characters on XML output.
     */
    public static enum Mode
    {
        /**
         * Throw a CharacterEncodeException if such a character is encountered.
         */
        ERROR,

        /**
         * Encode the character reference
         */
        ENCODE,

        /**
         * Replace the character via the replace() method. The default
         * implementation skips the character.
         *
         * @see CharacterEncoder#replace(char, int, Appendable)
         */
        REPLACE
    }

    /**
     * Construct with defaults for XML 1.0
     */
    public CharacterEncoder( Appendable out )
    {
        this( out, Version.XML_1_0 );
    }

    /**
     * Construct with defaults based on the XML version.
     */
    public CharacterEncoder( Appendable out, Version version )
    {
        if( version == Version.XML_1_1 ) _modeC0 = Mode.ENCODE;
        _version = version;

        _outW = (out instanceof Writer) ? (Writer) out : null;
        _outA = out;
    }

    /**
     * Set the mode for handling a NUL (0) character. This character is not
     * allowed in XML in any form. The default is Mode.ERROR.
     */
    public final void setModeNUL( Mode mode )
    {
        _modeNUL = mode;
    }

    /**
     * Set the mode for handling non-characters U+FFFE or
     * U+FFFF. These are not allowed in XML in any form. The default
     * is Mode.ERROR.
     */
    public final void setModeNAC( Mode mode )
    {
        _modeNAC = mode;
    }

    /**
     * Set the mode for handling the "C0" control characters in the range 0x01
     * to 0x1F excluding TAB, CR, and LF. These characters are disallowed in XML
     * 1.0. In XML 1.1 they are allowed as character references. The default is
     * Mode.ENCODE if constructed with Version.V1_1, otherwise Mode.ERROR.
     */
    public final void setModeC0( Mode mode )
    {
        _modeC0 = mode;
    }

    /**
     * Set the mode for handling the "C1" control characters and DEL in the
     * range 0x7F to 0x9F excluding NEL. These characters must be escaped as
     * character references in XML 1.1 but are allowed in XML 1.0. The default
     * is Mode.ENCODE.
     */
    public final void setModeC1( Mode mode )
    {
        _modeC1 = mode;
    }

    public final Mode modeNUL()
    {
        return _modeNUL;
    }

    public final Mode modeNAC()
    {
        return _modeNAC;
    }

    public final Mode modeC0()
    {
        return _modeC0;
    }

    public final Mode modeC1()
    {
        return _modeC1;
    }

    public final Appendable output()
    {
        return _outA;
    }

    public final Version version()
    {
        return _version;
    }

    public final void encodeComment( final CharSequence in )
        throws IOException
    {
        int i = 0;
        int last = 0;
        final int end = in.length();

        while( i < end ) {
            final char c = in.charAt( i );

            if ( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore
                ++i;
            }
            // Dash at very end or "--" anyplace is invalid.
            else if( c == '-' ) {
                if( ( i+1 == end ) || ( in.charAt(i+1) == '-' ) ) {
                    _outA.append( in, last, i );
                    handleSpecialChar( _modeCommentDash, c, i );
                    last = ++i;
                }
                else ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outA.append( in, last, i );
                handleSpecialChar( (c == 0x0) ? _modeCommentNUL :
                                                _modeCommentC0, c, i );
                last = ++i;
            }
            else if ( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outA.append( in, last, i );
                handleSpecialChar( _modeCommentC1, c, i );
                last = ++i;
            }
            else if( c >= 0xFFFE ) {
                _outA.append( in, last, i );
                handleSpecialChar( _modeCommentNAC, c, i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outA.append( in, last, i );
    }

    /**
     * Encodes the special characters '&amp;' and '&lt;' to "&amp;amp;" and
     * "&amp;lt;" and writes the results to out.
     *
     * @throws IOException from Appendable out.
     */
    public final void encodeCharData( final CharSequence in )
        throws IOException
    {
        if( _outW != null ) {
            if( in instanceof String ) {
                encodeString( (String) in, false );
                return;
            }
            if( in instanceof CharBuffer ) {
                final CharBuffer cb = (CharBuffer) in;
                if( cb.hasArray() ) {
                    encodeArray( cb.array(),
                                 cb.arrayOffset() + cb.position(),
                                 cb.remaining(), false );
                    return;
                }
            }
        }
        encodeCharSequence( in, false );
    }

    /**
     * Encodes the special characters '&amp;', '&lt;', and '"' to
     * "&amp;amp;", "&amp;lt;", and "&amp;quot;" and writes the
     * results to out. This assumes attribute values are surrounded in
     * double quotes.
     *
     * @throws IOException from append out.
     */
    public final void encodeAttrValue( final CharSequence in )
        throws IOException
    {
        if( ( in instanceof String ) && ( _outW != null ) ) {
            encodeString( (String) in, true );
        }
        else encodeCharSequence( in, true );
    }

    private final void encodeCharSequence( final CharSequence in,
                                           final boolean doEncodeQuote )
        throws IOException
    {
        int i = 0;
        int last = 0;
        final int end = in.length();

        while( i < end ) {
            final char c = in.charAt( i );

            if( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore
                ++i;
            }
            else if( doEncodeQuote && c == '"' ) {
                _outA.append( in, last, i );
                _outA.append( "&quot;" );
                last = ++i;
            }
            else if( c == '<' ) {
                _outA.append( in, last, i );
                _outA.append( "&lt;" );
                last = ++i;
            }
            // Must encode '>' as "&gt;" when it appears in "]]>"
            // Look back in input to see if required, but if at beginning
            // we must assume a prior putChars() included the ']]'.
            else if( ( c == '>' ) &&
                     ( ( i < 1 ) || ( in.charAt( i - 1 ) == ']' ) ) &&
                     ( ( i < 2 ) || ( in.charAt( i - 2 ) == ']' ) ) ) {
                _outA.append( in, last, i );
                _outA.append( "&gt;" );
                last = ++i;
            }
            else if( c == '&' ) {
                _outA.append( in, last, i );
                _outA.append( "&amp;" );
                last = ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outA.append( in, last, i );
                handleSpecialChar( (c == 0x0) ? _modeNUL : _modeC0, c, i );
                last = ++i;
            }
            else if( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outA.append( in, last, i );
                handleSpecialChar( _modeC1, c, i );
                last = ++i;
            }
            else if( c >= 0xFFFE ) {
                _outA.append( in, last, i );
                handleSpecialChar( _modeNAC, c, i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outA.append( in, last, i );
    }

    private final void encodeString( final String in,
                                     final boolean doEncodeQuote )
        throws IOException
    {
        int i = 0;
        int last = 0;
        final int end = in.length();

        while( i < end ) {
            final char c = in.charAt( i );

            if( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore
                ++i;
            }
            else if( doEncodeQuote && c == '"' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&quot;" );
                last = ++i;
            }
            else if( c == '<' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&lt;" );
                last = ++i;
            }
            // Must encode '>' as "&gt;" when it appears in "]]>"
            // Look back in input to see if required, but if at beginning
            // we must assume a prior putChars() included the ']]'.
            else if( ( c == '>' ) &&
                     ( ( i < 1 ) || ( in.charAt( i - 1 ) == ']' ) ) &&
                     ( ( i < 2 ) || ( in.charAt( i - 2 ) == ']' ) ) ) {
                _outW.write( in, last, i - last );
                _outW.write( "&gt;" );
                last = ++i;
            }
            else if( c == '&' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&amp;" );
                last = ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outW.write( in, last, i - last );
                handleSpecialChar( (c == 0x0) ? _modeNUL : _modeC0, c, i );
                last = ++i;
            }
            else if( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outW.write( in, last, i - last );
                handleSpecialChar( _modeC1, c, i );
                last = ++i;
            }
            else if( c >= 0xFFFE ) {
                _outW.write( in, last, i - last );
                handleSpecialChar( _modeNAC, c, i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outW.write( in, last, i - last );
    }

    private final void encodeArray( final char[] in,
                                    final int offset,
                                    final int length,
                                    final boolean doEncodeQuote )
        throws IOException
    {
        int i = offset;
        int last = i;
        final int end = i + length;

        while( i < end ) {
            final char c = in[i];

            if( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore
                ++i;
            }
            else if( doEncodeQuote && c == '"' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&quot;" );
                last = ++i;
            }
            else if( c == '<' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&lt;" );
                last = ++i;
            }
            // Must encode '>' as "&gt;" when it appears in "]]>"
            // Look back in input to see if required, but if at beginning
            // we must assume a prior putChars() included the ']]'.
            else if( ( c == '>' ) &&
                     ( ( ( i - offset ) < 1 ) || ( in[ i - 1 ] == ']' ) ) &&
                     ( ( ( i - offset ) < 2 ) || ( in[ i - 2 ] == ']' ) ) ) {
                _outW.write( in, last, i - last );
                _outW.write( "&gt;" );
                last = ++i;
            }
            else if( c == '&' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&amp;" );
                last = ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outW.write( in, last, i - last );
                handleSpecialChar( (c == 0x0) ? _modeNUL : _modeC0, c, i );
                last = ++i;
            }
            else if( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outW.write( in, last, i - last );
                handleSpecialChar( _modeC1, c, i );
                last = ++i;
            }
            else if( c >= 0xFFFE ) {
                _outW.write( in, last, i - last );
                handleSpecialChar( _modeNAC, c, i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outW.write( in, last, i - last );
    }

    private final void handleSpecialChar( final Mode mode,
                                          final char c,
                                          final int pos )
        throws IOException
    {
        switch( mode ) {

        case ENCODE:
            _outA.append( "&#x" );
            if( c > 0xfff ) _outA.append( HEX_DIGITS[( c >>> 12 ) & 0xf] );
            if( c >  0xff ) _outA.append( HEX_DIGITS[( c >>>  8 ) & 0xf] );
            if( c >   0xf ) _outA.append( HEX_DIGITS[( c >>>  4 ) & 0xf] );
                            _outA.append( HEX_DIGITS[( c        ) & 0xf] );
            _outA.append( ';' );
            break;

        case REPLACE:
            replace( c, pos, _outA );
            break;

        case ERROR:
        default:
            throw new CharacterEncodeException( String.format(
                "Invalid XML character 0x%04x in input at position %d.",
                (int) c, pos ) );
        }
    }

    /**
     * Write a replacement for c to out. This is called when the mode
     * for the class of characters containing c is Mode.REPLACE. The default
     * implementation writes nothing and thus c is silently skipped. Override
     * this method to provide a replacement character and/or to log when this
     * occurs.
     * @param c character in input to be replaced
     * @param pos position index into the original input where c was found
     * @param out output Appendable on which to write replacement
     */
    protected void replace( final char c,
                            final int pos,
                            final Appendable out )
        throws IOException
    {
        // Do nothing.
    }

    private final Version _version;
    private final Writer _outW;
    private final Appendable _outA;

    private Mode _modeNUL   = Mode.ERROR;
    private Mode _modeNAC   = Mode.ERROR;
    private Mode _modeC0    = Mode.ERROR;
    private Mode _modeC1    = Mode.ENCODE;

    private static final Mode _modeCommentNUL   = Mode.ERROR;
    private static final Mode _modeCommentNAC   = Mode.ERROR;
    private static final Mode _modeCommentC0    = Mode.ERROR;
    private static final Mode _modeCommentC1    = Mode.ERROR;
    private static final Mode _modeCommentDash  = Mode.ERROR;

    // FIXME: Provide override mechanism for the comment modes if
    // someone ever asks to override them. Unless comments are somehow
    // generated then such characters can be treated as programmatic errors and
    // thus the ERROR modes are appropriate.

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
}
