/*
 * Copyright (c) 2012 David Kellum
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

package com.gravitext.xml.jruby;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.gravitext.jruby.IOUtils;
import com.gravitext.util.ResizableCharBuffer;
import com.gravitext.util.Streams;
import com.gravitext.xml.producer.CharacterEncoder;
import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;
import com.gravitext.xml.producer.CharacterEncoder.QuoteMark;
import com.gravitext.xml.tree.Element;
import com.gravitext.xml.tree.NodeWriter;
import com.gravitext.xml.tree.StAXUtils;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

@JRubyClass( name="Gravitext::XMLProd::XMLHelper" )
public class XMLHelper
{
    @JRubyMethod( name = "write_element",
                  meta = true,
                  required = 3,
                  argTypes = { Element.class,
                               Indentor.class,
                               QuoteMark.class } )
    public static IRubyObject writeElement( ThreadContext tc,
                                            IRubyObject klazz,
                                            IRubyObject elm,
                                            IRubyObject ident,
                                            IRubyObject qmark )
    {
        Element root = (Element) elm.toJava( Element.class );

        ResizableCharBuffer out = new ResizableCharBuffer( 1024 );

        CharacterEncoder enc = new CharacterEncoder( out );
        enc.setQuoteMark( (QuoteMark) qmark.toJava( QuoteMark.class ) );

        XMLProducer pd = new XMLProducer( enc );
        pd.setIndent( (Indentor) ident.toJava( Indentor.class ) );

        try {
            new NodeWriter( pd ).putTree( root );

            return IOUtils.toRubyString( tc, out.flipAsCharBuffer() );
        }
        catch( IOException e ) {
            throw new RuntimeException( e ); // Shouldn't happen with ours.
        }
    }

    @JRubyMethod( name = "element_characters",
                  meta = true,
                  required = 1,
                  argTypes = { Element.class } )
    public static IRubyObject elementCharacters( ThreadContext tc,
                                                 IRubyObject klazz,
                                                 IRubyObject elm )
    {
        Element element = (Element) elm.toJava( Element.class );
        return IOUtils.toRubyString( tc, element.characters() );
    }

    @JRubyMethod( name = "stax_parse_string",
                  meta = true,
                  required = 2,
                  argTypes = { RubyString.class,
                               ReturnElement.class } )
    public static IRubyObject staxParse( ThreadContext tc,
                                         IRubyObject klazz,
                                         IRubyObject inp,
                                         IRubyObject r )
        throws FactoryConfigurationError, XMLStreamException
    {
        ByteBuffer in = IOUtils.toByteBuffer( inp.convertToString() );

        StreamSource source = new StreamSource( Streams.inputStream( in ) );
        XMLStreamReader staxReader = StAXUtils.staxReader( source );

        ReturnElement re = (ReturnElement) r.toJava( ReturnElement.class );
        re.setValue( StAXUtils.readDocument( staxReader ) );

        return tc.getRuntime().getNil();
    }

}
