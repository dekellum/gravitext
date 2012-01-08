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

package com.gravitext.xml.tree;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.gravitext.xml.producer.Indentor;

import static org.junit.Assert.*;
import static com.gravitext.xml.tree.StAXUtils.*;
import static javax.xml.stream.XMLStreamConstants.*;

public class StAXConsumerTest
{
    @Test
    public void test_simple() throws IOException, XMLStreamException
    {
        assertEquals( SIMPLE_DOC, roundTripStAX( SIMPLE_DOC ) );
    }

    @Test
    public void test_partial() throws XMLStreamException, IOException
    {
        XMLStreamReader sr = newReader( SIMPLE_DOC );
        while( sr.next() != START_ELEMENT );

        Node node = StAXUtils.readCurrentElement( sr );
        assertEquals( SIMPLE_DOC, produceString( node ) );

        sr = newReader( SIMPLE_DOC );
        while( ( sr.next() != START_ELEMENT ) ||
               ! sr.getLocalName().equals( "a" ) );

        node = StAXUtils.readCurrentElement( sr );
        assertEquals( "<a>b</a>", produceString( node ) );
    }

    private XMLStreamReader newReader( String simpleDoc )
        throws FactoryConfigurationError, XMLStreamException
    {
        return staxReader( staxInput( simpleDoc ) );
    }

    private String produceString( Node node ) throws IOException
    {
        return StAXUtils.produceString( node, Indentor.COMPRESSED );
    }

    private static final String SIMPLE_DOC =
        "<doc>\n" +
        " <a>b</a>\n" +
        "</doc>";
}
