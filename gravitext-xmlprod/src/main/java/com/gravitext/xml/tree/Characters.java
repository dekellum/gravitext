package com.gravitext.xml.tree;

import com.gravitext.htmap.Key;

public final class Characters extends Node
{
    public static final Key<CharSequence> CONTENT =
        COMPAT_KEY_SPACE.create( "content", CharSequence.class );

    public Characters( CharSequence chars )
    {
        _chars = chars;
    }

    public CharSequence characters()
    {
        return _chars;
    }

    public void setCharacters( CharSequence chars )
    {
        _chars = chars;
    }

    public <T> T get( Key<T> key )
    {
        if( key == CONTENT ) {
            return key.valueType().cast( characters() );
        }
        return super.get( key );
    }

    public <T> T remove( Key<T> key )
    {
        if( key == CONTENT ) {
            CharSequence old = characters();
            setCharacters("");
            return key.valueType().cast( old );
        }
        return super.remove( key );
    }

    public <T, V extends T> T set( Key<T> key, V value )
    {
        if( key == CONTENT ) {
            CharSequence old = characters();
            setCharacters( CONTENT.valueType().cast( value ) );
            return key.valueType().cast( old );
        }
        return super.set( key, value );
    }

    private CharSequence _chars = null;

}
