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

            _pd.startTag( node.tag() );

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
