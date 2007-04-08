package com.gravitext.xml.producer;

import java.io.IOException;
import java.io.Writer;

/**
 * Encodes java text as XML character data and attribute values. 
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

    public CharacterEncoder( Appendable out )
    {
        this( out, Version.XML_1_0 );
    }
    
    /**
     * Construct with defaults based on the XML version.
     * @param version
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
    
    public Version version()
    {
        return _version;
    }
    
    
    /**
     * Encodes the special characters '&amp;' and '<' to "&amp;amp;" and
     * "&amp;lt;" and writes the results to out.
     * 
     * @throws IOException from Appendable out.
     */
    public final void encodeCharData( final CharSequence in )
        throws IOException
    {
        // We effectively mitigate the performance cost of the CharSequence 
        // and Appendable abstractions for the common case here. This is a 10%
        // improvement in perf. testing.
        
        if( ( in instanceof String ) && ( _outW != null ) ) {
            encodeChars( (String) in, false );
        }
        else encodeChars( in, false );
    }

    /**
     * Encodes the special characters '&amp;', '<' , and '"' to "&amp;amp;",
     * "&amp;lt;", and "&amp;quot;" and writes the results to out. This
     * assumes attribute values are surrounded in double quotes.
     * 
     * @throws IOException from append out.
     */
    public final void encodeAttrValue( final CharSequence in )
        throws IOException
    {
        if( ( in instanceof String ) && ( _outW != null ) ) {
            encodeChars( (String) in, true );
        }
        else encodeChars( in, true );
    }
   
    private final void encodeChars( final CharSequence in, 
                                    final boolean doEncodeQuote ) 
        throws IOException
    {
        int i = 0;
        int last = 0;
        int end = in.length();

        while( i < end ) {
            char c = in.charAt( i );

            if ( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore 
                ++i;
            }
            else if ( doEncodeQuote && c == '"' ) {
                _outA.append( in, last, i );
                _outA.append( "&quot;" );
                last = ++i;
            }
            else if ( c == '<' ) {
                _outA.append( in, last, i );
                _outA.append( "&lt;" );
                last = ++i;
            }
            else if ( c == '&' ) {
                _outA.append( in, last, i );
                _outA.append( "&amp;" );
                last = ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outA.append( in, last, i );
                handleSpecialChar( (c == 0x0) ? _modeNUL : _modeC0, 
                                   in.charAt( i ), i );
                last = ++i;
            }
            else if ( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outA.append( in, last, i );
                handleSpecialChar( _modeC1, in.charAt( i ), i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outA.append( in, last, i );
    }

    private final void encodeChars( final String in, 
                                    final boolean doEncodeQuote )
        throws IOException
    {
        int i = 0;
        int last = 0;
        int end = in.length();

        while( i < end ) {
            char c = in.charAt( i );

            if ( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore 
                ++i;
            }
            else if ( doEncodeQuote && c == '"' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&quot;" );
                last = ++i;
            }
            else if ( c == '<' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&lt;" );
                last = ++i;
            }
            else if ( c == '&' ) {
                _outW.write( in, last, i - last );
                _outW.write( "&amp;" );
                last = ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outW.write( in, last, i - last );
                handleSpecialChar( (c == 0x0) ? _modeNUL : _modeC0, 
                                   in.charAt( i ), i );
                last = ++i;
            }
            else if ( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outW.write( in, last, i - last );
                handleSpecialChar( _modeC1, in.charAt( i ), i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outW.write( in, last, i - last );
    }

    public final void encodeComment( final CharSequence in ) 
        throws IOException
    {
        int i = 0;
        int last = 0;
        int end = in.length();
    
        while( i < end ) {
            char c = in.charAt( i );
    
            if ( c == 0x09 || c == 0x0A || c == 0x0D ) { // TAB, CR, LF
                // ignore 
                ++i;
            }
            // Dash at very end or "--" anyplace is invalid.
            else if( c == '-' ) {
                if( ( i+1 == end ) || ( in.charAt(i+1) == '-' ) ) {
                    _outA.append( in, last, i );
                    handleSpecialChar( _modeCommentDash, in.charAt( i ), i );
                    last = ++i;
                }
                else ++i;
            }
            else if( c <= 0x1F ) { // Other than TAB, CR, LF
                _outA.append( in, last, i );
                handleSpecialChar
                ( (c == 0x0) ? _modeCommentNUL : _modeCommentC0,
                  in.charAt( i ), i );
                last = ++i;
            }
            else if ( c >= 0x7F && c <= 0x9F && c != 0x85 ) { // NEL
                _outA.append( in, last, i );
                handleSpecialChar( _modeCommentC1, in.charAt( i ), i );
                last = ++i;
            }
            else ++i; // And ignore everything else.
        } // while
        _outA.append( in, last, i );
    }

    

    private final void handleSpecialChar( Mode mode, char c, int pos )
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
    protected void replace( char c, int pos, Appendable out )
        throws IOException
    {
        // Do nothing.
    }

    private final Version _version;
    private final Writer _outW;
    private final Appendable _outA;

    private Mode _modeNUL   = Mode.ERROR;
    private Mode _modeC0    = Mode.ERROR;
    private Mode _modeC1    = Mode.ENCODE;

    private Mode _modeCommentNUL   = Mode.ERROR;
    private Mode _modeCommentC0    = Mode.ERROR;
    private Mode _modeCommentC1    = Mode.ERROR;
    private Mode _modeCommentDash  = Mode.ERROR;
    
    // FIXME: Provide override mechanism for the comment modes if
    // someone ever asks to override them. Unless comments are somehow 
    // generated then such characters can be treated as programmatic errors and
    // thus the ERROR modes are appropriate.
    
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
}
