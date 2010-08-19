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
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;

public class TreeUtils
{
    private static String produceString( Node root, Indentor indent )
        throws IOException
    {
        StringBuilder out = new StringBuilder( 128 );
        produce( root, indent, out );
        return out.toString();
    }

    private static void produce( Node root, Indentor indent, Appendable out )
        throws IOException
    {
        XMLProducer pd = new XMLProducer( out );
        pd.setIndent( indent );
        new NodeWriter( pd ).putTree( root );
    }

    public static Node saxParse( InputSource input )
        throws SAXException, IOException
    {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        SAXHandler handler = new SAXHandler();
        reader.setContentHandler( handler );
        reader.parse( input );
        return handler.root();
    }

    public static InputSource saxInputSource( String input )
    {
        return new InputSource( new StringReader( input ) );
    }

    public static String roundTripSAX( String input )
        throws SAXException, IOException
    {
        return roundTripSAX( input, Indentor.COMPRESSED );
    }

    public static String roundTripSAX( String input, Indentor indent )
        throws SAXException, IOException
    {
        Node root = saxParse( saxInputSource( input ) );

        return produceString( root, indent );
    }
}
