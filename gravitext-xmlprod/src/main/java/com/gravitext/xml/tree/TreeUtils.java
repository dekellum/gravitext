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

import com.gravitext.util.ByteArrayInputStream;
import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;

public class TreeUtils
{
    public static void produce( Node root, Indentor indent, Appendable out )
        throws IOException
    {
        XMLProducer pd = new XMLProducer( out );
        pd.setIndent( indent );
        new NodeWriter( pd ).putTree( root );
    }

    public static String produceString( Node root, Indentor indent )
        throws IOException
    {
        StringBuilder out = new StringBuilder( 256 );
        produce( root, indent, out );
        return out.toString();
    }

    public static ByteArrayInputStream byteStream( byte[] input )
    {
        return new ByteArrayInputStream( input, 0, input.length );
    }
}
