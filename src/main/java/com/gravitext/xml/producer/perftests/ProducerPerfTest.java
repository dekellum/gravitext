package com.gravitext.xml.producer.perftests;

import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;
import com.gravitext.xml.producer.Tag;
import com.gravitext.xml.producer.XMLProducer;

import java.io.IOException;
import java.util.List;

public final class ProducerPerfTest 
    extends SerializePerfTest
{
    private static final Namespace itemNS = 
        new Namespace( "graph", "urn:some-unique-id" );

    private static final Tag item     = new Tag( "item", itemNS );    
    private static final Attribute name  = new Attribute( "name" );
    private static final Attribute value = new Attribute( "value" );
    private static final Attribute score = new Attribute( "score", itemNS );

    private static final Tag testdoc  = new Tag( "testdoc" );
    private static final Tag content  = new Tag( "content" );
    private static final Tag list     = new Tag( "list" );
    private static final Tag listItem = new Tag( "listItem" );

    protected void serializeGraph( List<GraphItem> graph, TestOutput out ) 
        throws IOException
    {
        XMLProducer p = new XMLProducer( out.getWriter() );
        p.setIndent( getIndent() );
                
        p.putXMLDeclaration( getEncoding() );
        p.putSystemDTD( "testdoc", "bogus.dtd" );
        
        p.startTag( testdoc );
        
        for( GraphItem g : graph ) {
            p.startTag( item ).addAttr( name, g.getName() )
                              .addAttr( value, g.getValue() )
                              .addAttr( score, g.getScore() );
            
            p.startTag( content ).putChars( g.getContent() ).endTag();
            
            if( g.getList().size() > 0 ) {
                p.startTag( list );
                for( String gl : g.getList() ) {
                    p.startTag( listItem ).putChars( gl ).endTag();
                }
                p.endTag( list );
            }
            
            p.endTag( item );
        }
        
        p.endTag( testdoc );
    }
}