package com.gravitext.xml.tree;

import java.io.IOException;

import com.gravitext.xml.producer.Namespace;
import com.gravitext.xml.producer.XMLProducer;

public final class NodeWriter
{
    public NodeWriter( XMLProducer producer )
    {
        _pd = producer;
    }

    /**
     * Write the tree node and its children to the producer by recursive
     * descent.
     */
    public void putTree( final Node node ) throws IOException
    {
        if( node.isElement() ) {

            _pd.startTag( node.name(), node.namespace() );

            for( Namespace ns : node.namespaceDeclarations() ) {
                _pd.addNamespace( ns );
            }

            // Add attributes
            for( AttributeValue av : node.attributes() ) {
                _pd.addAttr( av.attribute(), av.value() );
            }

            // Add Contents
            for( Node child : node.children() ) {
                putTree( child );
            }

            _pd.endTag(); //FIXME: Checked end tag? Move cache up here?
        }
        else {
            _pd.putChars( node.characters() );
        }
    }

    private final XMLProducer _pd;
}
