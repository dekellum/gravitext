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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;

import static javax.xml.stream.XMLStreamConstants.*;

public class StaxConsumer
{
    public Node read( XMLStreamReader sr ) throws XMLStreamException
    {
        int depth = 0;
        loop: while( true ) {
            switch( sr.next() ) {
            case START_ELEMENT:
                startElement( sr );
                ++depth;
                break;
            case CHARACTERS:
            case CDATA:
                characters( sr );
                break;
            case END_ELEMENT:
                endElement();
                if( --depth == 0 ) break loop;
            }
        }

        return _root;
    }

    private void startElement( XMLStreamReader sr )
    {
        Namespace ns = null;
        String iri = sr.getNamespaceURI();
        if( iri != null ) {
            ns = findNamespace( iri, sr.getPrefix() );
        }
        Node node = Node.newElement( sr.getLocalName(), ns );

        final int nsds = sr.getNamespaceCount();
        for( int i = 0; i < nsds; ++i ) {
            Namespace decl = findNamespace( sr.getNamespaceURI( i ),
                                            sr.getNamespacePrefix( i ) );
            if( decl != ns ) node.addNamespace( decl );
        }

        copyAttributes( sr, node );

        if( _root == null ) {
            _root = _current = node;
        }
        else {
            _current.addChild( node );
            _current = node;
        }
    }

    public void characters( XMLStreamReader sr )
        throws XMLStreamException
    {
        _current.addChild( Node.newCharacters( sr.getText() ) );
    }

    public void endElement()
    {
        _current = _current.parent();
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
            if( prefix == null || prefix.isEmpty()) {
                prefix = Namespace.DEFAULT;
            }
            ns = new Namespace( prefix, iri );
            _activeNS.add( ns );
        }
        return ns;
    }

    private void copyAttributes( XMLStreamReader sr, Node node )
    {
        final int end = sr.getAttributeCount();
        if( end > 0 ) {

            final ArrayList<AttributeValue> atts
                = new ArrayList<AttributeValue>( end );

            for( int i = 0; i < end; ++i ) {
                final String ln = sr.getAttributeLocalName( i );
                Namespace ns = null;
                String iri = sr.getAttributeNamespace( i );
                if( iri != null ) ns = findNamespace( iri );
                //  FIXME: Cache attribute defs?
                atts.add( new AttributeValue( new Attribute( ln, ns ),
                                              sr.getAttributeValue( i ) ) );
            }

            node.setAttributes( atts );
        }
    }

    private Node _root = null;
    private Node _current = null;

    private final ArrayList<Namespace> _activeNS =
        new ArrayList<Namespace>( 8 );
}
