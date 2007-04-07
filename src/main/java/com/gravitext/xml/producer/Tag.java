package com.gravitext.xml.producer;

/**
 * Immutable XML tag identifier. 
 * @author David Kellum
 */
public final class Tag
{
    public Tag( final String name )
    {
        this( name, null );
    }
    
    public Tag( final String name, final Namespace ns )
    {
        if( name == null ) throw new NullPointerException("name");
        if( name.length() == 0 ) throw new IllegalArgumentException
        ("Name must be non-empty.");
        //FIXME: Test other name validations here?
        _name = name;
        _namespace = ns;
        
        StringBuilder qName = new StringBuilder(64);
        if( ( _namespace != null ) && (! _namespace.isDefault() ) ) {
            qName.append( _namespace.prefix() ).append( ':' );
        }
        qName.append( _name );
        
        _beginTag = "<" + qName;
        _endTag = "</" + qName + ">";
    }

    public String name()
    {
        return _name;
    }

    @Override
    public String toString()
    {
        return ( beginTag() + '>' ); 
    }
    
    /**
     * @return the Namespace associated with this Tag or null if no Namespace
     *         was specified on construction.
     */
    public Namespace namespace()
    {
        return _namespace;
    }

    String beginTag()
    {
        return _beginTag;
    }

    String endTag()
    {
        return _endTag;
    }
    
    private final String _name;
    private final Namespace _namespace;

    private final String _beginTag;
    private final String _endTag;
    
}
