/**
 * 
 */
package com.gravitext.xml.producer;

/**
 * Immutable XML namespace identifier.
 * @author David Kellum
 */
public final class Namespace
{
    public static final String DEFAULT = new String( "" );

    public Namespace( final String prefix, final String nameIRI )
    {
        if( prefix == null ) throw new NullPointerException( "prefix" );
        if( nameIRI == null ) throw new NullPointerException( "nameIRI" );

        if( ( prefix != DEFAULT ) && ( prefix.length() == 0 ) ) {
            throw new IllegalStateException
            ( "Illegal attempt to construct Namespace with empty prefix." +
              "  Use Namespace.DEFAULT instead." );
        }
        
        if( ( nameIRI.length() == 0 ) ) {
            throw new IllegalStateException
            ( "Illegal attempt to construct Namespace with empty nameIRI." );
        }
        //FIXME: Other nameIRI and prefix validity tests
        //FIXME: exclude prefix: "xml" and "xmlns"
        
        _nameIRI = nameIRI;
        _prefix = prefix;
        
        StringBuilder qName = new StringBuilder(64);
        qName.append( " xmlns" ); //Note leading space.
        if( !isDefault() ) {
            qName.append( ':' ).append( _prefix );
        }
        qName.append( "=\"" );
        _beginDecl = qName.toString();
        
    }

    public String prefix()
    {
        return _prefix;
    }
    
    public String nameIRI()
    {
        return _nameIRI;
    }

    /**
     * @return true if this is default Namespace declaration with a DEFAULT
     *         (empty) prefix.
     */
    public boolean isDefault()
    {
        return ( _prefix == DEFAULT );
    }
    
    String beginDecl()
    {
        return _beginDecl;
    }
    
    private final String _prefix;
    private final String _nameIRI;
    private final String _beginDecl;
}