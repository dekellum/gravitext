package com.gravitext.xml.producer.perftests;

import java.io.IOException;
import java.nio.CharBuffer;

public final class ResizableCharBuffer implements Appendable
{
    public ResizableCharBuffer( int capacity )
    {
        _b = new char[capacity];
        _pos = 0;
        _limit = capacity;
    }
    

    public Appendable append( CharSequence in ) throws IOException
    {
        return append( in, 0, in.length() );
    }

    public Appendable append( char c ) throws IOException
    {
        requestCapacity( 1 );
        _b[ _pos++ ] = c;
        return this;
    }

    public Appendable append( CharSequence in, int start, int end )
            throws IOException
    {
        int length = end - start;
        requestCapacity( length );
        
        if( in instanceof String ) {
            ((String) in).getChars( start, end, _b, _pos );
            _pos += length;
        }
        else throw new IllegalArgumentException( "not supported!" );
        return this;
    }
    
    public final void requestCapacity( int length )
    {
        if( ( _pos + length ) > _limit ) {

            int size = _limit;

            // Grow size by multiples of 2 until large enough
            if( size == 0 ) size = 1;
            while( ( _pos + length ) > ( size *= 2 ) ); 
            
            char[] b = new char[ size ];

            System.arraycopy( _b, 0, b, 0, _pos );
            
            _b = b;
            _limit = size;
        }
    }
    /**
     * Returns a new CharBuffer which wraps the underlying
     * array from [ 0, position ).
     */
    public final CharBuffer flipAsCharBuffer()
    {
        return java.nio.CharBuffer.wrap( _b, 0, _pos );
    }

    protected char[] _b;
    
    private int _pos = 0;
    
    private int _limit = 0;
}
