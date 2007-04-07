package com.gravitext.xml.producer;

/**
 * Immutable XML attribute identifier.
 * @author David Kellum
 */
public final class Attribute
{
    public Attribute( final String name )
    {
        this( name, null );
    }
    
    public Attribute( final String name, final Namespace ns )
    {
        if( name == null ) throw new NullPointerException("name");
        
        if( name.length() == 0 ) {
            throw new IllegalArgumentException( "Name must be non-empty." );
        }
        
        //FIXME: Test other attribute name validations here.
        
        _name = name;
        _namespace = ns;
        
        StringBuilder qName = new StringBuilder(64);
        qName.append( ' ' ); //Note leading space.
        if( ( _namespace != null ) && (! _namespace.isDefault() ) ) {
            qName.append( _namespace.prefix() ).append( ':' );
        }
        qName.append( _name ).append( "=\"" );
        
        _beginAttribute = qName.toString();
    }

    public String name()
    {
        return _name;
    }
    
    /**
     * @return the Namespace associated with this Tag or null if no Namespace
     *         was specified on construction.
     */
    public Namespace namespace()
    {
        return _namespace;
    }

    String beginAttribute()
    {
        return _beginAttribute;
    }
    
    private final String _name;
    private final Namespace _namespace;

    private final String _beginAttribute;
}
