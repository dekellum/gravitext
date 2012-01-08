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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gravitext.xml.producer.DOMWalker;
import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;

public class DOMUtils extends TreeUtils
{
    public static void produce( Document doc, Indentor indent, Appendable out )
        throws IOException
    {
        XMLProducer pd = new XMLProducer( out );
        pd.setIndent( indent );
        new DOMWalker( pd ).putDOM( doc );
    }

    public static String produceString( Document doc, Indentor indent )
        throws IOException
    {
        StringBuilder out = new StringBuilder( 128 );
        produce( doc, indent, out );
        return out.toString();
    }

    public static Document domParse( byte[] input )
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );
        dbf.setCoalescing( true );
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse( byteStream( input ) );
    }
}
