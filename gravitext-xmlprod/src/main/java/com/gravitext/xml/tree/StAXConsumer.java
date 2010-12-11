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

import com.gravitext.xml.NamespaceCache;
import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Consumes events from a StAX XMLStreamReader, for a single Element or
 * entire document, and producing a Node tree.
 *
 * @see StAXUtils
 */
public final class StAXConsumer
{
    public Element readCurrentElement( XMLStreamReader sr )
        throws XMLStreamException
    {
        if( sr.getEventType() != START_ELEMENT ) {
            throw new XMLStreamException(
                "Reader not on START_ELEMENT (" + sr.getEventType() + ")." );
        }
        startElement( sr );
        consume( sr );
        Element root = _root;
        _root = _current = null;
        return root;
    }

    public Element readDocument( XMLStreamReader sr )
        throws XMLStreamException
    {
        consume( sr );
        Element root = _root;
        _root = _current = null;
        return root;
    }

    private Element consume( XMLStreamReader sr ) throws XMLStreamException
    {
        int depth = ( sr.getEventType() == START_ELEMENT ) ? 1 : 0;
        loop: while( true ) {
            switch( sr.next() ) {
            case START_DOCUMENT:
                ++depth;
                break;
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
                if( --depth <= 0 ) break loop;
                break;
            case END_DOCUMENT:
                break loop;
            }
        }

        return _root;
    }

    private void startElement( XMLStreamReader sr )
    {
        Namespace ns = _cache.namespace( sr.getPrefix(),
                                         sr.getNamespaceURI() );

        Element element = new Element( _cache.tag( sr.getLocalName(), ns ) );

        final int nsds = sr.getNamespaceCount();
        for( int i = 0; i < nsds; ++i ) {

            Namespace decl = _cache.namespace( sr.getNamespacePrefix( i ),
                                               sr.getNamespaceURI( i ) );

            if( decl != ns ) element.addNamespace( decl );
        }

        copyAttributes( sr, element );

        if( _root == null ) {
            _root = _current = element;
        }
        else {
            _current.addChild( element );
            _current = element;
        }
    }

    private void characters( XMLStreamReader sr )
    {
        //FIXME: Check _current set?
        _current.addChild( new Characters( sr.getText() ) );
    }

    private void endElement()
    {
        //FIXME: Check _current set?
        _current = _current.parent();
    }

    private void copyAttributes( XMLStreamReader sr, Element element )
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

            element.setAttributes( atts );
        }
    }

    private Element _root = null;
    private Element _current = null;

    private final NamespaceCache _cache = new NamespaceCache();
}
