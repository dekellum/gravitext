package com.gravitext.xml.producer.perftests;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.util.FastRandom;
import com.gravitext.util.ResizableCharBufferWriter;
import com.gravitext.xml.producer.CharacterEncoder;

public class StringBufferEncodePerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed ) throws Exception
    {   
        FastRandom rnd = new FastRandom( seed + run );
        
        TestOutput out = new TestOutput();
        
        CharacterEncoder encoder = new CharacterEncoder( out.getWriter() );
        
        for( int t = rnd.nextInt( 50 ) + 51; t > 0; --t ) {
            int length = ( ( rnd.nextInt( 5 ) + 1 ) * 
                           ( rnd.nextInt( 10 ) + 1 ) * 
                           ( rnd.nextInt( 20 ) + 1 ) +
                           rnd.nextInt( 40 ) );

            int offset = rnd.nextInt( CHAR_DATA.length - length );

            CharSequence cs;
            if( _useCharBuffer ) {
                cs = CharBuffer.wrap( CHAR_DATA, offset, length );
            }
            else {
                cs = new String( CHAR_DATA, offset, length );
            }

            encoder.encodeCharData( cs ); 
        }
        
        out.flush();

        if( _doVerbose ) out.print();
        

        return out.size();
    }

    public void setVerbose( boolean doVerbose )
    {
        _doVerbose = doVerbose;
    }

    public void setDoEncode( boolean doEncode )
    {
        _doEncode = doEncode;
    }
    public void setEncoding( String encoding )
    {
        _encoding = encoding;
    }
    
    public void setUseCharBuffer( boolean useCharBuffer )
    {
        _useCharBuffer = useCharBuffer;
    }

        
    protected final class TestOutput
    {
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
                    _writer = new ResizableCharBufferWriter( 1024 * 100 );
                }
            }
            return _writer;
        }

        private OutputStream getStream() 
        {
            if( !_doEncode ) throw new IllegalStateException
            ( "If not doEncode, shouldn't use stream." );

            if( _stream == null ) {
                _stream = new ByteArrayOutputStream( 1024 * 100 );
            }
            return _stream;
        }
        
        private ByteArrayOutputStream _stream = null;
        private Writer _writer                = null;
    }

    private boolean _doVerbose   = false;
    private String  _encoding    = "ISO-8859-1";
    private boolean _doEncode    = true;
   
    private boolean _useCharBuffer = true;
    
    private static final char[] CHAR_DATA; //10000 bytes minimum
    
    static {
        StringBuilder b = new StringBuilder( 11000 );
        FastRandom random = new FastRandom( 1 );
        while( b.length() < ( 10000 ) ) {
            if( random.nextInt(10) == 0 ) {
                b.append( "[ Cérébrales escaped \"char & data\"] ");
            }
            else {
                b.append( "otherwise clean text " );
            }

        }
        CHAR_DATA = b.toString().toCharArray();    
    }
}
