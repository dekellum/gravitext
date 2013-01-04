/*
 * Copyright (c) 2008-2013 David Kellum
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

package com.gravitext.xml.producer.perftests;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class JaxpPerfTest
    extends SerializePerfTest
{
    protected void serializeGraph( List<GraphItem> graph, TestOutput out )
        throws IOException, TransformerConfigurationException,
               TransformerFactoryConfigurationError, SAXException
    {
        StreamResult sr;
        if( useWriter() ) sr = new StreamResult( out.getWriter() );
        else sr = new StreamResult( out.getStream() );

        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
                .newInstance();
        TransformerHandler hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty( OutputKeys.ENCODING, getEncoding() );
        serializer.setOutputProperty( OutputKeys.DOCTYPE_SYSTEM, "bogus.dtd" );

        if( !getIndent().isCompressed() ) {
            serializer.setOutputProperty( OutputKeys.INDENT, "yes" );
        }

        hd.setResult( sr );
        hd.startDocument();
        hd.startElement( "", "", "testdoc", emptyAtts );

        for( GraphItem g : graph ) {
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute( "", "", "name", "CDATA", g.getName() );
            atts.addAttribute( "", "", "value", "CDATA",
                               String.valueOf( g.getValue() ) );
            atts.addAttribute( "urn:some-unique-id", "score",
                               "graph:score", "CDATA",
                               String.valueOf( g.getScore() ) );
            hd.startElement( "urn:some-unique-id", "item", "graph:item", atts );

            hd.startElement( "", "", "content", emptyAtts );
            String cdata = g.getContent();
            hd.characters( cdata.toCharArray(), 0, cdata.length() );
            hd.endElement( "", "", "content" );

            if( g.getList().size() > 0 ) {
                hd.startElement( "", "", "list", emptyAtts );

                for( String gl : g.getList() ) {
                    hd.startElement( "", "", "listItem", emptyAtts );
                    hd.characters( gl.toCharArray(), 0, gl.length() );
                    hd.endElement( "", "", "listItem" );
                }

                hd.endElement( "", "", "list" );
            }

            //Note: Invalid output unchecked without namespace here.
            hd.endElement( "urn:some-unique-id", "item", "graph:item" );
        }

        hd.endElement( "", "", "testdoc" );
        hd.endDocument();
    }

    private static final AttributesImpl emptyAtts = new AttributesImpl();

}
