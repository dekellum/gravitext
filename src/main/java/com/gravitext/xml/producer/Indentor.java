package com.gravitext.xml.producer;

import java.io.IOException;

public final class Indentor implements Cloneable
{
    public static final Indentor COMPRESSED = new Indentor( null );
    public static final Indentor LINE_BREAK = new Indentor( "" );
    public static final Indentor PRETTY = new Indentor( " " );
    
    public Indentor( String indent )
    {
        if( indent == null ) {
            _indentSize = -1;
            _indents = null;
        }
        else {
            _indentSize = indent.length();
            StringBuilder b = new StringBuilder( 1 + _indentSize * 16 );
            b.append( '\n' );
            if( _indentSize > 0 ) {
                for( int i = 0; i < 16; ++i ) b.append( indent );
            }
            _indents = b.toString();
        }
    }
    
    void indent( final Appendable out, final int level ) 
        throws IOException 
    {
        if( _indentSize == 0 ) out.append( '\n' );
        else if( _indentSize > 0 ) {
            int len = 1 + ( level * _indentSize );
            while( len > _indents.length() ) {
                _indents = _indents + _indents.substring(1);
            }
            out.append( _indents, 0, len );
        }
    }

    @Override
    public Indentor clone()
    {
        try {
            return (Indentor) super.clone();
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }
    
    public boolean isCompressed()
    {
        return ( _indentSize == -1 );
    }
    
    public boolean isLineBreak()
    {
        return ( _indentSize == 0 );
    }
    
    private final int _indentSize;
    private String _indents;
 
}
