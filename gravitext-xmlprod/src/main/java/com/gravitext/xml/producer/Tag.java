/*
 * Copyright (c) 2008-2010 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.xml.producer;

/**
 * Immutable XML tag identifier.
 * @author David Kellum
 */
public final class Tag
{
    /**
     * Construct with name and default namespace.
     */
    public Tag( final String name )
    {
        this( name, null );
    }

    /**
     * Construct given (local) name and the specified namespace.
     */
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

    @Override
    public int hashCode()
    {
        int h = _name.hashCode();
        if( _namespace != null ) {
            h ^= _namespace.hashCode();
        }
        return h;
    }

    @Override
    public boolean equals( Object o )
    {
        if( ( o != null ) && ( o instanceof Tag ) ) {
            Tag ot = (Tag) o;
            if( ( namespace() == ot.namespace() ) ||
                ( ( namespace() != null ) &&
                    namespace().equals( ot.namespace() ) ) ) {
                return name().equals( ot.name() );
            }
        }
        return false;
    }

    /**
     * Return the local name of the tag.
     */
    public String name()
    {
        return _name;
    }

    /**
     * Return the Namespace of this Tag or null if no namespace was
     * specified on construction.
     */
    public Namespace namespace()
    {
        return _namespace;
    }

    /**
     * Return string representation of the tag.
     */
    @Override
    public String toString()
    {
        return ( beginTag() + '>' );
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
