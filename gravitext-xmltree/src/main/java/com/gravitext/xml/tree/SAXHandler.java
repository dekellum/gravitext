package com.gravitext.xml.tree;

import java.nio.CharBuffer;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;

public final class SAXHandler
    extends DefaultHandler
{
    public SAXHandler()
    {
    }

    public Node root()
    {
        return _root;
    }

    @Override
    public void startPrefixMapping( String prefix, String iri )
    {
        if( prefix.isEmpty() ) {
            prefix = Namespace.DEFAULT;
        }

        Namespace ns = new Namespace( prefix, iri );
        _current.addNamespace( ns );
        _activeNS.add( ns );
        _nextNS.add( ns );
    }

    @Override
    public void startElement( String iri, String localName, String qName,
                              Attributes attributes )
    {
        Namespace ns = null;
        if( ! iri.isEmpty() ) ns = findNamespace( iri );

        Node node = Node.newElement( localName, ns );

        for( Namespace decl: _nextNS ) {
            node.addNamespace( decl );
        }
        _nextNS.clear();

        node.setAttributes( copyAttributes( attributes ) );

        if( _root == null ) {
            _root = _current = node;
        }
        else {
            _current.addChild( node );
            _current = node;
        }
    }

    @Override
    public void endElement( String iri, String localName, String qName )
    {
        _current = _current.parent();
    }

    @Override
    public void characters( char[] ch, int start, int length )
    {
        _current.addChild(
            Node.newContent( CharBuffer.wrap( ch, start, length ) ) );
    }

    private Namespace findNamespace( String iri )
    {
        for( Namespace ns : _activeNS ) {
            if( ns.nameIRI() == iri ) return ns;
        }
        return null;
    }

    private ArrayList<AttributeValue> copyAttributes( Attributes attributes )
    {
        final int end = attributes.getLength();
        //FIXME: Handle zero case for perf here?

        final ArrayList<AttributeValue> atts
            = new ArrayList<AttributeValue>( end );

        for( int i = 0; i < end; ++i ) {
            Namespace ns = null;
            String iri = attributes.getURI( i );
            if( ! iri.isEmpty() ) ns = findNamespace( iri );

            Attribute attr = new Attribute( attributes.getLocalName( i ), ns );
            atts.add( new AttributeValue( attr, attributes.getValue( i ) ) );
        }
        return atts;
    }

    private Node _root = null;
    private Node _current = null;

    private ArrayList<Namespace> _activeNS = new ArrayList<Namespace>( 8 );
    private ArrayList<Namespace> _nextNS = new ArrayList<Namespace>( 8 );
}
