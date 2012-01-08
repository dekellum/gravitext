/*
 * Copyright (c) 2008-2012 David Kellum
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

package com.gravitext.xml.producer;

import java.io.IOException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.gravitext.xml.NamespaceCache;

/**
 * XMLProducer helper for writing from W3C DOM trees.
 * @author David Kellum
 */
public final class DOMWalker
{
    public DOMWalker( XMLProducer producer )
    {
        _pd = producer;
    }

    /**
     * Writes the specified DOM node and its children to the producer by
     * recursive descent. Only some node types are output:
     *
     * <ul>
     * <li>Document and DocumentFragment nodes are only descended into.</li>
     * <li>CDATASection and Text nodes are written identically via
     *     putChars.</li>
     * <li>Element attributes are written in the order found, which is
     *     typically undefined/</li>
     * <li>All other node types are ignored.</li>
     * </ul>
     *
     * <p>Namespace information in the DOM is observed. For best results, enable
     * Namespace processing when constructing the DOM.</p>
     */
    public void putDOM( final Node node ) throws IOException
    {
        if( node instanceof Element ) {
            putElement( node );
        }
        else if( ( node instanceof Text ) ||
                 ( node instanceof CDATASection ) ) {

            _pd.putChars( node.getNodeValue() );
        }
        else if( ( node instanceof Document ) ||
                 ( node instanceof DocumentFragment ) ) {

            putNodeList( node.getChildNodes() );
        }
    }

    private void putElement( final Node node ) throws IOException
    {
        final Namespace ns =
            _cache.namespace( node.getPrefix(), node.getNamespaceURI() );
        final Tag tag = _cache.tag( lname( node ), ns );

        _pd.startTag( tag );

        // Add Namespace declarations
        NamedNodeMap atts = node.getAttributes();
        final int end = atts.getLength();
        for( int i = 0; i < end; ++i ) {
            final Attr dattr = (Attr) atts.item( i );
            final String iri = dattr.getNamespaceURI();
            if( XMLNS_200_URI.equals( iri ) ) {
                final Namespace na =
                    _cache.namespace( dattr.getLocalName(), dattr.getValue() );
                if( na != ns ) _pd.addNamespace( na );
            }
        }

        // Add attributes (after all namespaces)
        for( int i = 0; i < end; ++i ) {
            final Attr dattr = (Attr) atts.item( i );
            final String iri = dattr.getNamespaceURI();
            if( ! XMLNS_200_URI.equals( iri ) ) {
                final Attribute attr =
                    _cache.attribute( lname( dattr ),
                                      _cache.namespace( dattr.getPrefix(),
                                                        iri ) );
                _pd.addAttr( attr, dattr.getValue() );
            }
        }

        // Add Contents
        putNodeList( node.getChildNodes() );

        _pd.endTag( tag );
    }

    private String lname( Node node )
    {
        String name = node.getLocalName();
        if( name == null ) name = node.getNodeName();
        return name;
    }

    private void putNodeList( final NodeList list ) throws IOException
    {
        final int end = list.getLength();

        for( int i = 0; i < end; ++i ) {
            putDOM( list.item( i ) );
        }
    }

    private static final String XMLNS_200_URI = "http://www.w3.org/2000/xmlns/";

    private final NamespaceCache _cache = new NamespaceCache();
    private final XMLProducer _pd;
}
