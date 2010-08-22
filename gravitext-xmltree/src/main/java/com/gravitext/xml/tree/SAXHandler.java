/*
 * Copyright (c) 2010 David Kellum
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

package com.gravitext.xml.tree;

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

        _nextNS.add( findNamespace( iri, prefix ) );
    }

    @Override
    public void startElement( String iri, String localName, String qName,
                              Attributes attributes )
    {
        bufferToChars();

        Namespace ns = null;
        if( ! iri.isEmpty() ) ns = findNamespace( iri );
        Node node = Node.newElement( localName, ns );

        // Add namespaces declared and not already used by this element.
        for( Namespace decl: _nextNS ) {
            if( decl != ns ) node.addNamespace( decl );
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
        bufferToChars();
        _current = _current.parent();
    }

    @Override
    public void characters( char[] ch, int start, int length )
    {
        if( _buffer == null ) {
            _buffer = new StringBuilder( length + 16 );
        }
        _buffer.append( ch, start, length );
    }

    private void bufferToChars()
    {
        if( _buffer != null ) {
            _current.addChild( Node.newCharacters( _buffer ) );
            _buffer = null;
        }
    }

    private Namespace findNamespace( String iri )
    {
        for( Namespace ns : _activeNS ) {
            if( ns.nameIRI() == iri ) return ns;
        }
        return null;
    }

    private Namespace findNamespace( String iri, String prefix )
    {
        Namespace ns = findNamespace( iri );
        if( ns == null ) {
            ns = new Namespace( prefix, iri );
            _activeNS.add( ns );
        }
        return ns;
    }

    private ArrayList<AttributeValue> copyAttributes( Attributes attributes )
    {
        final int end = attributes.getLength();
        //FIXME: Handle zero case for perf here?

        final ArrayList<AttributeValue> atts
            = new ArrayList<AttributeValue>( end );

        for( int i = 0; i < end; ++i ) {
            final String ln = attributes.getLocalName( i );
            Namespace ns = null;
            String iri = attributes.getURI( i );
            if( ! iri.isEmpty() ) ns = findNamespace( iri );
            //  FIXME: Cache attribute defs?
            atts.add( new AttributeValue( new Attribute( ln, ns ),
                                          attributes.getValue( i ) ) );
        }
        return atts;
    }

    private Node _root = null;
    private Node _current = null;

    private final ArrayList<Namespace> _activeNS = new ArrayList<Namespace>( 8 );
    private final ArrayList<Namespace> _nextNS = new ArrayList<Namespace>( 8 );
    private StringBuilder _buffer = null;

}
