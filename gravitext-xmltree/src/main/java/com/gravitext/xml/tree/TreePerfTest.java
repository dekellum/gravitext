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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.xml.producer.DOMWalker;
import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;

import static com.gravitext.xml.tree.TreeUtils.*;

public class TreePerfTest implements TestFactory
{
    public enum Impl
    {
        DOM,
        TREE_NODE_SAX,
        TREE_NODE_STAX
    };

    public TreePerfTest( byte[] xml, Impl impl, boolean doWrite )
    {
        _xml = xml;
        _impl = impl;
        _doWrite = doWrite;
    }

    public String name()
    {
        return _doWrite ? _impl.name() + "+write" : _impl.name();
    }

    public TestRunnable createTestRunnable( int seed )
    {
        switch( _impl ) {
        case TREE_NODE_SAX :
            return new TestRunnable() {
                public int runIteration( int run )
                    throws SAXException, IOException
                {
                    Node node = saxParse( saxInputSource( _xml ) );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        produce( node, Indentor.PRETTY, buffer );
                        return buffer.length();
                    }
                    else {
                        return node.children().size();
                    }
                }
            };
        case TREE_NODE_STAX :
            return new TestRunnable() {
                public int runIteration( int run )
                    throws XMLStreamException, IOException
                {
                    Node node = staxParse( staxSource( _xml ) );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        produce( node, Indentor.PRETTY, buffer );
                        return buffer.length();
                    }
                    else {
                        return node.children().size();
                    }
                }
            };

        case DOM :
            return new TestRunnable() {
                public int runIteration( int run )
                    throws SAXException, IOException,
                        ParserConfigurationException
                {
                    Document doc = domParse( _xml );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        XMLProducer pd = new XMLProducer( buffer );
                        pd.setIndent( Indentor.PRETTY );
                        new DOMWalker( pd ).putDOM( doc );
                        return buffer.length();
                    }
                    else {
                        return doc.getDocumentElement()
                            .getChildNodes().getLength();
                    }
                }
            };
        }
        throw new RuntimeException();
    }

    private final byte[] _xml;
    private final Impl _impl;
    private final boolean _doWrite;
}
