/*
 * Copyright (c) 2008-2011 David Kellum
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
import com.gravitext.xml.producer.Indentor;

public class TreePerfTest implements TestFactory
{
    public enum Impl
    {
        DOM,
        TREE_NODE_SAX,
        TREE_NODE_STAX
    };

    public TreePerfTest( byte[][] xml, Impl impl, boolean doWrite )
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
                    Element element = SAXUtils.saxParse(
                        SAXUtils.saxInput( _xml[ run % _xml.length ] ) );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        TreeUtils.produce( element, Indentor.PRETTY, buffer );
                        return buffer.length();
                    }
                    else {
                        return element.children().size();
                    }
                }
            };
        case TREE_NODE_STAX :
            return new TestRunnable() {
                public int runIteration( int run )
                    throws XMLStreamException, IOException
                {
                    Element element = StAXUtils.staxParse(
                        StAXUtils.staxInput( _xml[ run % _xml.length ] ) );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        TreeUtils.produce( element, Indentor.PRETTY, buffer );
                        return buffer.length();
                    }
                    else {
                        return element.children().size();
                    }
                }
            };

        case DOM :
            return new TestRunnable() {
                public int runIteration( int run )
                    throws SAXException, IOException,
                        ParserConfigurationException
                {
                    Document doc =
                        DOMUtils.domParse( _xml[ run % _xml.length ] );
                    if( _doWrite ) {
                        StringBuilder buffer =
                            new StringBuilder( _xml.length * 4/3 );
                        DOMUtils.produce( doc,Indentor.PRETTY, buffer );
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

    private final byte[][] _xml;
    private final Impl _impl;
    private final boolean _doWrite;
}
