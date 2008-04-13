/*
 * Copyright 2007 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.xml.producer.perftests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.util.FastRandom;
import com.gravitext.util.ResizableCharBufferWriter;
import com.gravitext.xml.producer.Indentor;

public abstract class SerializePerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed ) throws Exception
    {   
        FastRandom random = new FastRandom( seed + run );
        
        GraphGenerator generator = new GraphGenerator( random );
        
        List<GraphItem> g = generator.nextGraph();
        
        TestOutput out = new TestOutput();
        
        if( _doSerialize ) serializeGraph( g, out );

        out.flush();

        if( _doVerbose ) out.print();
        
        if( _doSerialize ) return out.size();
        
        return ( ( random.nextInt() & 0x7fffffff ) % 3 );
    }

    public void setVerbose( boolean doVerbose )
    {
        _doVerbose = doVerbose;
    }
    
    public void setDoEncode( boolean doEncode )
    {
        _doEncode = doEncode;
    }

    public String getEncoding()
    {
        return _encoding;
    }

    public void setEncoding( String encoding )
    {
        _encoding = encoding;
    }

    public boolean useWriter()
    {
        return _useWriter;
    }
    

    public void setUseWriter( boolean useWriter )
    {
        _useWriter = useWriter;
    }
    
    public void setIndent( Indentor indentor )
    {
        _indentor = indentor;
    }
    
    public Indentor getIndent()
    {
        return _indentor;
    }
    
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

    protected abstract void serializeGraph( List<GraphItem> graph, 
                                            TestOutput out )
        throws Exception;

    private boolean _doVerbose   = false;
    private String  _encoding    = "ISO-8859-1";
    private boolean _doEncode    = false;
    private boolean _doSerialize = true; // vs. test generation time exclusively 
    
    private boolean _useWriter   = true;

    private Indentor _indentor   = Indentor.LINE_BREAK;
}
