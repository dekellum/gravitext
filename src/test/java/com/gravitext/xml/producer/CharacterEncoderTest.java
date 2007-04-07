package com.gravitext.xml.producer;

import java.io.IOException;
import java.io.StringWriter;


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
    
    public void testDefaultCharData() throws IOException
    {        
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeCharData
            ( new StringBuilder().append( "<\" \t\n\r(c1:\u009A)&&" ) );
        assertEquals( "&lt;\" \t\n\r(c1:&#x9a;)&amp;&amp;", out.toString() );
    }
    
    public void testAltInputTypes() throws IOException
    {        
        StringWriter out = new StringWriter();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeCharData
            ( new StringBuilder().append( "<\" \t\n\r(c1:\u009A)&" ) );
        enc.encodeCharData( "& even & more &" );
        assertEquals( "&lt;\" \t\n\r(c1:&#x9a;)&amp;" +
                      "&amp; even &amp; more &amp;",
                      out.toString() );
    }
    
    public void testEmpty() throws IOException
    {        
        StringBuilder out = new StringBuilder();
        CharacterEncoder enc = new CharacterEncoder( out );
        enc.encodeCharData( "" );
        enc.encodeCharData( "&" );
        enc.encodeCharData( "" );
        enc.encodeCharData( "}" );
        assertEquals( "&amp;}", out.toString() );
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
            System.out.println( "Expected Error: " + e );
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
            System.out.println( "Expected Error: " + e );
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
            System.out.println( "Expected Error: " + e );
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
   
    
}
