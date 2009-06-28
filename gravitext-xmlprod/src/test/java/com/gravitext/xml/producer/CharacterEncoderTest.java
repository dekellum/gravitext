package com.gravitext.xml.producer;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class CharacterEncoderTest extends TestCase
{
    public void testDefaultModes() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        assertEquals( CharacterEncoder.Mode.ERROR,  enc.modeNUL() );
        assertEquals( CharacterEncoder.Mode.ERROR,  enc.modeC0() );
        assertEquals( CharacterEncoder.Mode.ENCODE, enc.modeC1() );
    }

    public void testDefaultAttrValue() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeAttrValue( "<\"" );
        assertEquals( "&lt;&quot;", out.toString() );
    }

    public void testWriterAttrValue() throws IOException
    {
        StringWriter out = new StringWriter();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeAttrValue( "<\"" );
        assertEquals( "&lt;&quot;", out.toString() );
    }

    public void testEncodeSamples() throws IOException
    {
        for( String[] test : ENCODE_SAMPLES ) {
            assertEquals( test[1], encodeAppendable( test[0] ) );
            assertEquals( test[1], encodeWriter( test[0] ) );
            assertEquals( test[1], encodeWriter( asSequence( test[0] ) ) );
            assertEquals( test[1], encodeWriter( asBuffer( test[0] ) ) );
        }
    }

    public void testDefaultErrorC0() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        try {
            enc.encodeCharData( "<\" *\u001F*" );
            fail();
        }
        catch( CharacterEncodeException e ) {
            _log.debug( "Expected:", e );
        }
    }

    public void testComentError() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        try {
            enc.encodeComment( "this isn't allowed -" );
            fail();
        }
        catch( CharacterEncodeException e ) {
            _log.debug( "Expected:", e );
        }
    }

    public void testComentError2() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        try {
            enc.encodeComment( "nor -- this" );
            fail();
        }
        catch( CharacterEncodeException e ) {
            _log.debug( "Expected:", e );
        }
    }

    public void testReplaceAttrValue() throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out )
        {
            @Override
            protected void replace( char c, int pos, Appendable out )
                throws IOException
            {
                out.append( 'X' );
            }
        };
        enc.setModeNUL( CharacterEncoder.Mode.REPLACE );
        enc.setModeC0( CharacterEncoder.Mode.REPLACE );
        enc.setModeC1( CharacterEncoder.Mode.REPLACE );

        enc.encodeAttrValue( "\u0001\u0000<\"\u0084" );
        assertEquals( "XX&lt;&quot;X", out.toString() );
    }

    private CharSequence asBuffer( String in )
    {
        CharBuffer buffer = CharBuffer.allocate( in.length() + 2 );
        buffer.put( '(' ); // test padding
        buffer.put( in );
        buffer.put( ')' );
        buffer.flip();
        return buffer.subSequence( 1, in.length() + 1 );
    }

    private CharSequence asSequence( String in )
    {
        return ( new StringBuilder().append( in ) );
    }

    private String encodeAppendable( CharSequence in ) throws IOException
    {
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeCharData( in );
        return out.toString();
    }

    private String encodeWriter( CharSequence in ) throws IOException
    {
        StringWriter out = new StringWriter();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeCharData( in );
        return out.toString();
    }

    private static final String[][] ENCODE_SAMPLES = {
        { "", "" },
        { "noop", "noop" },
        { ">", "&gt;" },
        { "]>", "]&gt;" },
        { "]]>", "]]&gt;" },
        { "n>", "n>" },
        { "no>", "no>" },
        { "yes]]>", "yes]]&gt;" },
        { "<", "&lt;" },
        { "&", "&amp;" },
        { "\u009A", "&#x9a;" },
        { "\t\n\r", "\t\n\r" },
        { "&remainder", "&amp;remainder" } };

    private Logger _log = LoggerFactory.getLogger( getClass() );
}
