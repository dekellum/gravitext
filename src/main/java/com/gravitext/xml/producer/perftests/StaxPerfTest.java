package com.gravitext.xml.producer.perftests;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class StaxPerfTest 
    extends SerializePerfTest
{
    protected void serializeGraph( List<GraphItem> graph ) 
        throws XMLStreamException, UnsupportedEncodingException
    {
        XMLOutputFactory of = XMLOutputFactory.newInstance();

        // No automatic indentation support in StAX. Emulate linebreak mode
        // via writeCharacters() below.
        boolean lb = !(getIndent().isCompressed());
        
        XMLStreamWriter w;
        if( useWriter() ) w = of.createXMLStreamWriter( getWriter() );
        else w = of.createXMLStreamWriter( getStream(), getEncoding() );
        
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
