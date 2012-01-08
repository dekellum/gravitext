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

package com.gravitext.xml.producer.perftests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.gravitext.util.ResizableCharBufferWriter;

public class OutputPerfTest
{
    protected final class TestOutput
    {
        public OutputStream getStream()
        {
            if( !_doEncode ) throw new IllegalStateException
            ( "If not doEncode, shouldn't use stream." );

            if( _stream == null ) {
                _stream = new ByteArrayOutputStream( 1024 * 24 );
            }
            return _stream;
        }

        public void print() throws UnsupportedEncodingException
        {
            if( _doEncode ) {
                System.out.print( new String( _stream.toByteArray(),
                                              _encoding ) );
            }
            else {
                System.out.print( ( (ResizableCharBufferWriter) _writer ).
                                  buffer() );
            }
        }

        public int size()
        {
            return ( _doEncode ? _stream.size() :
                                 ( (ResizableCharBufferWriter) _writer).
                                 buffer().position() );
        }

        public void flush() throws IOException
        {
            if( _writer != null ) _writer.flush();
        }

        public Writer getWriter() throws UnsupportedEncodingException
        {
            if( _writer == null  ) {
                if( _doEncode ) {
                    Writer t = new OutputStreamWriter( getStream(), _encoding );
                    _writer = new BufferedWriter( t, 1024 );
                }
                else {
                    _writer = new ResizableCharBufferWriter( 1024 * 24 );
                }
            }
            return _writer;
        }

        private ByteArrayOutputStream _stream = null;
        private Writer _writer                = null;
    }

    public void setDoEncode( boolean doEncode )
    {
        _doEncode = doEncode;
    }

    public void setEncoding( String encoding )
    {
        _encoding = encoding;
    }

    public String getEncoding()
    {
        return _encoding;
    }

    private boolean _doEncode = false;
    private String _encoding = "ISO-8859-1";

}
