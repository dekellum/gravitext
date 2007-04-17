package com.gravitext.xml.producer.perftests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.concurrent.FastRandom;
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
    
    /**
     * @param indent null -> compressed, "" -> linebreaks only, 
     *               or else per level "xx"
     */
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
                System.out.print( ((CharArrayWriter)_writer).toString() );
            }
        }

        public int size()
        {
            return ( _doEncode ? _stream.size() : 
                                 ((CharArrayWriter)_writer).size() );
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
                    _writer = new CharArrayWriter( 1024 * 24 );
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
    
    private boolean _useWriter            = true;

    private Indentor _indentor   = Indentor.LINE_BREAK;
}
