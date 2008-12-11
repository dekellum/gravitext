/*
 * Copyright 2007 David Kellum
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

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public final class JDomPerfTest 
    extends SerializePerfTest
{
    protected void serializeGraph( List<GraphItem> graph, TestOutput out ) 
        throws IOException
    {
        Document doc = new Document();
        
        doc.setDocType( new DocType( "testdoc", "bogus.dtd" ) );
        
        Element root = new Element( "testdoc" );
        doc.setRootElement( root );
        
        Namespace itemNS = 
            Namespace.getNamespace( "graph", "urn:some-unique-id" );
        
        for( GraphItem g : graph ) {
            Element item = new Element( "item", itemNS );
            
            item.setAttribute( "name", g.getName() );
            item.setAttribute( "value", String.valueOf( g.getValue() ) );
            item.setAttribute( "score", 
                               String.valueOf( g.getScore() ), 
                               itemNS );
            
            item.addContent( new Element( "content").
                                 setText( g.getContent() ) );
            
            if( g.getList().size() > 0 ) {
                Element list = new Element( "list" );
                for( String gl : g.getList() ) {
                    list.addContent( new Element( "listItem" ).setText( gl ) );
                }
                item.addContent( list );
            }

            root.addContent( item );
        }

        Format format = Format.getRawFormat();
        format.setEncoding( getEncoding() );
        format.setExpandEmptyElements( false );
        if( !getIndent().isCompressed() ) {
            format.setIndent( "" );
            format.setLineSeparator( "\n" );
        }

        XMLOutputter xmlOut = new XMLOutputter( format );

        if( useWriter() ) xmlOut.output( doc, out.getWriter() );
        else xmlOut.output( doc, out.getStream() );
    }
}