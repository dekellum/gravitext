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

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class StaxPerfTest
    extends SerializePerfTest
{
    protected void serializeGraph( List<GraphItem> graph, TestOutput out )
        throws XMLStreamException, UnsupportedEncodingException
    {
        XMLOutputFactory of = XMLOutputFactory.newInstance();

        // No automatic indentation support in StAX. Emulate linebreak mode
        // via writeCharacters() below.
        boolean lb = !(getIndent().isCompressed());

        XMLStreamWriter w;
        if( useWriter() ) w = of.createXMLStreamWriter( out.getWriter() );
        else w = of.createXMLStreamWriter( out.getStream(), getEncoding() );

        w.writeStartDocument( getEncoding(), "1.0" );
        if( lb ) w.writeCharacters( "\n" );

        w.writeDTD( "<!DOCTYPE testdoc SYSTEM \"bogus.dtd\">" );
        if( lb ) w.writeCharacters( "\n" );

        w.writeStartElement( "testdoc" );

        for( GraphItem g : graph ) {
            w.writeStartElement( "graph", "item", "urn:some-unique-id" );
            // In stax must manually trigger namespace decl. output:
            w.writeNamespace( "graph", "urn:some-unique-id" );
            w.writeAttribute( "name", g.getName() );
            w.writeAttribute( "value", String.valueOf( g.getValue() ) );
            w.writeAttribute( "graph", "urn:some-unique-id",
                              "score", String.valueOf( g.getScore() ) );
            if( lb ) w.writeCharacters( "\n" );
            w.writeStartElement( "content" );
            w.writeCharacters( g.getContent() );
            w.writeEndElement(); //content
            if( lb ) w.writeCharacters( "\n" );

            if( g.getList().size() > 0 ) {
                w.writeStartElement( "list" );
                if( lb ) w.writeCharacters( "\n" );
                for( String gl : g.getList() ) {
                    if( gl.length() > 0 ) {
                        w.writeStartElement( "listItem" );
                        w.writeCharacters( gl );
                        w.writeEndElement(); //listItem
                    }
                    else {
                        w.writeEmptyElement( "listItem" );
                    }
                    if( lb ) w.writeCharacters( "\n" );
                }
                w.writeEndElement(); //list
                if( lb ) w.writeCharacters( "\n" );
            }

            w.writeEndElement(); //item
            if( lb ) w.writeCharacters( "\n" );
        }

        w.writeEndElement(); //testdoc
        if( lb ) w.writeCharacters( "\n" );

        w.close();  //Documents can be truncated if you forget this.
    }
}
