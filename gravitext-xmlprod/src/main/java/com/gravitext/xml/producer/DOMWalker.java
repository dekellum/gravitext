/*
 * Copyright (c) 2008-2010 David Kellum
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
     *     typically undefined/random.</li>
     * <li>All other node types are ignored.</li>
     * </ul>
     *
     * <p>Any namespace information in the DOM is currently ignored.</p>
     */
    public void putDOM( final Node node ) throws IOException
    {
        if( node instanceof Element ) {

            _pd.startTag( node.getNodeName() );

            // Add attributes
            NamedNodeMap atts = node.getAttributes();
            final int end = atts.getLength();
            for( int i = 0; i < end; ++i ) {
                Attr attr = (Attr) atts.item( i );
                _pd.addAttr( attr.getName(), attr.getValue() );
            }

            // Add Contents
            putNodeList( node.getChildNodes() );

            _pd.endTag( node.getNodeName() );

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

    private void putNodeList( final NodeList list ) throws IOException
    {
        final int end = list.getLength();

        for( int i = 0; i < end; ++i ) {
            putDOM( list.item( i ) );
        }
    }

    private final XMLProducer _pd;
}
