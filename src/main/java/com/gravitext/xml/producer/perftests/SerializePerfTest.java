package com.gravitext.xml.producer.perftests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import com.gravitext.util.perftest.FastRandom;
import com.gravitext.util.perftest.PerformanceTest;
import com.gravitext.xml.producer.Indentor;

public abstract class SerializePerfTest implements PerformanceTest
{
    public int runTest( FastRandom random ) throws Exception
    {
        GraphGenerator generator = new GraphGenerator( random );
        
        _writer = null;
        _stream = null;
        
        List<GraphItem> g = generator.nextGraph();
        
        if( _doSerialize ) serializeGraph( g );

        if( _writer != null ) _writer.flush();

        if( _doVerbose ) {
            if( _doEncode ) {
                System.out.print
                    ( new String( _stream.toByteArray(), _encoding ) );
            }
            else {
                System.out.print( ((CharArrayWriter)_writer).toString() );
            }
        }
        if( _doSerialize ) {
            return ( _doEncode ? _stream.size() : 
                                 ((CharArrayWriter)_writer).size() );
        }
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
    
    
    protected final OutputStream getStream() 
    {
        if( !_doEncode ) throw new IllegalStateException
            ( "If not doEncode, shouldn't use stream." );
        
        if( _stream == null ) {
            _stream = new ByteArrayOutputStream( 1024 * 24 );
        }
        return _stream;
    }
    
    protected final Writer getWriter() throws UnsupportedEncodingException
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

    protected abstract void serializeGraph( List<GraphItem> graph )
        throws Exception;

    private boolean _doVerbose   = false;
    private String _encoding     = "ISO-8859-1";
    private boolean _doEncode    = false;
    private boolean _doSerialize = true; // Test generation time exclusively 
    
    private ByteArrayOutputStream _stream = null;
    private Writer _writer                = null;
    private boolean _useWriter            = true;

    private Indentor _indentor   = Indentor.LINE_BREAK;
}
