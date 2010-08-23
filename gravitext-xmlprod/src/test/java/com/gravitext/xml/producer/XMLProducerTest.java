/*
 * Copyright (c) 2008-2010 David Kellum
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
import java.io.StringReader;
import java.nio.CharBuffer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gravitext.util.ResizableCharBufferWriter;

import junit.framework.TestCase;

public class XMLProducerTest extends TestCase
{
    static final Namespace DEF =
        new Namespace( Namespace.DEFAULT, "urn:foo.def" );
    static final Namespace NS1 = new Namespace( "ns1", "urn:foo.ns1" );
    static final Namespace NSA = new Namespace( "nsa", "urn:foo.nsa" );

    static final Tag DOC = new Tag( "doc"      );
    static final Tag SUB = new Tag( "sub"      );
    static final Tag SB2 = new Tag( "sb2"      );
    static final Tag NSB = new Tag( "sub", NS1 );
    static final Tag ODF = new Tag( "odf", DEF );

    static final Attribute AT  = new Attribute( "at" );
    static final Attribute AT1 = new Attribute( "at1", NS1 );
    static final Attribute AT2 = new Attribute( "at2", NSA );

    public void testTags() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.PRETTY );
        p.putXMLDeclaration( "ISO-8859-1" );
        p.putComment   ( "dtd" );
        p.putComment   ( "next" );
        p.putSystemDTD ( DOC.name(), "./bogus.dtd" );
        p.putComment   ( "start" );
        p.startTag     ( DOC ).addAttr( "att", "attval" );
        p.putComment   (  "inside" );
        p.startTag     (  SUB );
        p.putChars     (   "<!" ).putChars( "&" );
        p.putComment   (   "more" ).putChars( "chars" ).endTag( SUB );
        p.putComment   (  "almost" );
        p.endTag       ( DOC );
        p.putComment   ( "after" );
        p.putComment   ( "end" );

        assertEquals( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                      "<!--dtd-->\n" +
                      "<!--next-->\n" +
                      "<!DOCTYPE doc SYSTEM \"./bogus.dtd\">\n" +
                      "<!--start-->\n" +
                      "<doc att=\"attval\">\n" +
                      " <!--inside-->\n" +
                      " <sub>&lt;!&amp;\n" +
                      "  <!--more-->chars</sub>\n" +
                      " <!--almost-->\n" +
                      "</doc>\n" +
                      "<!--after-->\n" +
                      "<!--end-->\n",
                      out.toString() );
    }

    private static enum Colors {
        BLUE
    }

    public void testTypes() throws IOException
    {
        ResizableCharBufferWriter out = new ResizableCharBufferWriter( 16 );

        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.PRETTY );

        p.startTag  ( DOC );
        p.startTag  (  SUB ).putChars( (short) -12 ).endTag();
        p.startTag  (  SUB ).putChars( 1234567     ).endTag();
        p.startTag  (  SUB ).putChars( -123456789L ).endTag();
        p.startTag  (  SUB ).putChars( Colors.BLUE ).endTag();
        p.startTag  (  SUB ).putChars(
            CharBuffer.wrap( "more&mooses".toCharArray() ) );
        p.endTag    (  SUB );

        p.startTag  (  SUB ).addAttr( AT,   (short) 12  ).endTag();
        p.startTag  (  SUB ).addAttr( AT,   -1234567    ).endTag();
        p.startTag  (  SUB ).addAttr( AT,   1234567890L ).endTag();
        p.startTag  (  SUB ).addAttr( AT,   Colors.BLUE ).endTag();

        p.startTag  (  SUB ).addAttr( "at", (short) 12  ).endTag();
        p.startTag  (  SUB ).addAttr( "at", -1234567    ).endTag();
        p.startTag  (  SUB ).addAttr( "at", 1234567890L ).endTag();
        p.startTag  (  SUB ).addAttr( "at", Colors.BLUE ).endTag();

        p.endTag    ( DOC );

        assertEquals( "<doc>\n" +
                      " <sub>-12</sub>\n" +
                      " <sub>1234567</sub>\n" +
                      " <sub>-123456789</sub>\n" +
                      " <sub>BLUE</sub>\n" +
                      " <sub>more&amp;mooses</sub>\n" +
                      " <sub at=\"12\"/>\n" +
                      " <sub at=\"-1234567\"/>\n" +
                      " <sub at=\"1234567890\"/>\n" +
                      " <sub at=\"BLUE\"/>\n" +
                      " <sub at=\"12\"/>\n" +
                      " <sub at=\"-1234567\"/>\n" +
                      " <sub at=\"1234567890\"/>\n" +
                      " <sub at=\"BLUE\"/>\n" +
                      "</doc>\n",
                      out.buffer().toString() );
    }

    public void testCompressed() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.COMPRESSED );

        p.startTag( DOC );
        p.startTag(  SUB ).putChars( "val" ).endTag( SUB );
        p.endTag  ( DOC );

        assertEquals( "<doc><sub>val</sub></doc>", out.toString() );
    }

    public void testCustomIndent() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( new Indentor("\t ") );

        p.startTag( DOC );
        p.startTag(  SUB );
        p.startTag(   SB2 ).putChars( "val" ).endTag();
        p.endTag  (  SUB );
        p.endTag  ( DOC );

        assertEquals( "<doc>\n" +
                      "\t <sub>\n" +
                      "\t \t <sb2>val</sb2>\n" +
                      "\t </sub>\n" +
                      "</doc>\n",
                      out.toString() );
    }

    public void test11Encoder() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer
            ( new CharacterEncoder( out, Version.XML_1_1 ) );
        p.setIndent( Indentor.LINE_BREAK );
        p.putXMLDeclaration( "UTF-8" );
        p.startTag( DOC ).putChars( "\b" ).endTag();

        assertEquals( "<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n" +
                      "<doc>&#x8;</doc>\n",
                      out.toString() );
    }

    public void testNamespaces() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.PRETTY );

        p.startTag( DOC ).addNamespace( DEF );
        p.startTag(  NSB );
        p.startTag(   NSB ).putChars( "val" ).endTag();
        p.startTag(   ODF ).addAttr( AT1, "av1" ).endTag();
        p.endTag  (  NSB );
        p.startTag(  NSB );
        p.startTag(   ODF ).addAttr( AT2, "av2" ).endTag();
        p.endTag  (  NSB );
        p.endTag  ( DOC );

        assertEquals(
            "<doc xmlns=\"urn:foo.def\">\n" +
            " <ns1:sub xmlns:ns1=\"urn:foo.ns1\">\n" +
            "  <ns1:sub>val</ns1:sub>\n" +
            "  <odf ns1:at1=\"av1\"/>\n" +
            " </ns1:sub>\n" +
            " <ns1:sub xmlns:ns1=\"urn:foo.ns1\">\n" +
            "  <odf xmlns:nsa=\"urn:foo.nsa\" nsa:at2=\"av2\"/>\n" +
            " </ns1:sub>\n" +
            "</doc>\n",
            out.toString() );
    }

    public void testStateError1() throws IOException
    {
        StringBuilder out = new StringBuilder();

        XMLProducer p = new XMLProducer( out );
        try {
            p.putComment( "comment" );
            p.putXMLDeclaration( "UTF-8" );
            fail();
        }
        catch( IllegalStateException e ) {
            _log.debug( "Expected: " + e );
        }
    }

    public void testStateError2() throws IOException
    {
        StringBuilder out = new StringBuilder();

        XMLProducer p = new XMLProducer( out );
        try {
            p.startTag( new Tag( "doc" ) ).endTag();
            p.putComment( "recover?" );
            p.startTag( new Tag( "another" ) );
            fail();
        }
        catch( IllegalStateException e ) {
            _log.debug( "Expected: " + e );
        }
    }
    static final Namespace NS_I = new Namespace( Namespace.DEFAULT,
                                                 "urn:foo.ns_i" );

    public void testSubDefaultNS() throws IOException
    {
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.PRETTY );
        p.startTag( "outer" );
        p.startTag( "inner", NS_I );
        p.startTag( "more", NS_I ).endTag();
        p.endTag( "inner", NS_I );
        p.endTag( "outer" );

        assertEquals( "<outer>\n" +
                      " <inner xmlns=\"urn:foo.ns_i\">\n" +
                      "  <more/>\n" +
                      " </inner>\n" +
                      "</outer>\n",
                      out.toString() );
    }

    public void testDOM() throws Exception
    {
        assertDomRT( "<p>hello</p>" );
        assertDomRT( "<p><a href=\"foo\">hello</a><b>world</b></p>" );
    }

    /**
     *  Assert that XMLPRoducer.putDom result is identical to input xml parsed
     *  to DOM.
     */
    private void assertDomRT( String xml )
        throws IOException, ParserConfigurationException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
        StringBuilder out = new StringBuilder();
        XMLProducer p = new XMLProducer( out );
        p.setIndent( Indentor.COMPRESSED );
        new DOMWalker( p ).putDOM( doc );
        assertEquals( xml, out.toString() );
    }

    private Logger _log = LoggerFactory.getLogger( getClass() );
}
