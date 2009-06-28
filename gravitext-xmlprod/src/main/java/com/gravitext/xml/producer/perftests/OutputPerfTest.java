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
