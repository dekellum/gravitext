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
import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.gravitext.xml.producer.Indentor;

public class StAXUtils extends TreeUtils
{
    public static Source staxInput( byte[] input )
    {
        return new StreamSource( byteStream( input ) );
    }

    public static Source staxInput( String input )
    {
        return new StreamSource( new StringReader( input ) );
    }

    public static XMLStreamReader staxReader( Source source )
        throws FactoryConfigurationError, XMLStreamException
    {
        XMLInputFactory inf = XMLInputFactory.newFactory();
        inf.setProperty( "javax.xml.stream.isCoalescing", true );
        inf.setProperty( "javax.xml.stream.supportDTD", false );
        XMLStreamReader sr = inf.createXMLStreamReader( source );
        return sr;
    }

    public static Element readCurrentElement( XMLStreamReader sr )
        throws XMLStreamException
    {
        return new StAXConsumer().readCurrentElement( sr );
    }

    public static Element readDocument( XMLStreamReader sr )
        throws XMLStreamException
    {
        return new StAXConsumer().readDocument( sr );
    }

    public static Element staxParse( Source source )
        throws XMLStreamException
    {
        return readDocument( staxReader( source ) );
    }

    public static String roundTripStAX( String input )
        throws IOException, XMLStreamException
    {
        return roundTripStAX( input, Indentor.COMPRESSED );
    }

    public static String roundTripStAX( String input, Indentor indent )
        throws IOException, XMLStreamException
    {
        Node root = staxParse( staxInput( input ) );

        return produceString( root, indent );
    }
}
