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
        Namespace ns = _cache.namespace( sr.getPrefix(),
                                         sr.getNamespaceURI() );

        Node node = Node.newElement( _cache.tag( sr.getLocalName(), ns ) );

        final int nsds = sr.getNamespaceCount();
        for( int i = 0; i < nsds; ++i ) {

            Namespace decl = _cache.namespace( sr.getNamespacePrefix( i ),
                                               sr.getNamespaceURI( i ) );

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

    private void copyAttributes( XMLStreamReader sr, Node node )
    {
        final int end = sr.getAttributeCount();
        if( end > 0 ) {

            final ArrayList<AttributeValue> atts
                = new ArrayList<AttributeValue>( end );

            for( int i = 0; i < end; ++i ) {
                final Attribute attr =
                    _cache.attribute( sr.getAttributeLocalName( i ),
                                      _cache.namespace(
                                          sr.getAttributePrefix( i ),
                                          sr.getAttributeNamespace( i ) ) );
                atts.add( new AttributeValue( attr,
                                              sr.getAttributeValue( i ) ) );
            }

            node.setAttributes( atts );
        }
    }

    private Node _root = null;
    private Node _current = null;

    private final NamespaceCache _cache = new NamespaceCache();
}
