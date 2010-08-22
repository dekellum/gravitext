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
        _nextNS.add( _cache.namespace( prefix, iri ) );
    }

    @Override
    public void startElement( String iri, String localName, String qName,
                              Attributes attributes )
    {
        bufferToChars();

        Namespace ns = findNamespace( iri );
        Node node = Node.newElement( _cache.tag( localName, ns ) );

        // Add any namespaces declared and not
        // already used by this element.
        for( Namespace decl: _nextNS ) {
            if( decl != ns ) node.addNamespace( decl );
        }

        copyAttributes( attributes, node );

        _nextNS.clear();

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

    private void copyAttributes( Attributes attributes, Node node )
    {
        final int end = attributes.getLength();
        if( end == 0 ) return;

        final ArrayList<AttributeValue> atts
            = new ArrayList<AttributeValue>( end );

        for( int i = 0; i < end; ++i ) {
            final Attribute attr =
                _cache.attribute( attributes.getLocalName( i ),
                                  findNamespace( attributes.getURI( i ) ) );
            atts.add( new AttributeValue( attr,
                                          attributes.getValue( i ) ) );
        }

        node.setAttributes( atts );
    }

    private Namespace findNamespace( String iri )
    {
        if( ( iri != null ) && ! iri.isEmpty() ) {
            for( Namespace ns : _nextNS ) {
                if( ns.nameIRI().equals( iri ) ) return ns;
            }

            Node n = _current; //Effective parent of new tag being handled.
            while( n != null ) {

                Namespace nns = n.namespace();
                if( ( nns != null ) && nns.nameIRI().equals( iri ) ) {
                    return nns;
                }

                for( Namespace ns : n.namespaceDeclarations() ) {
                   if( ns.nameIRI().equals( iri ) ) return ns;
                }
                n = n.parent();

            }
            if( iri.equals( "http://www.w3.org/XML/1998/namespace" ) ) {
                return _cache.namespace( "xml", iri );
            }
            throw new IllegalStateException( "ns: " + iri + " not found!" );
        }
        return null;
    }

    private Node _root = null;
    private Node _current = null;
    private final NamespaceCache _cache = new NamespaceCache();
    private final ArrayList<Namespace> _nextNS = new ArrayList<Namespace>( 8 );
    private StringBuilder _buffer = null;

}
