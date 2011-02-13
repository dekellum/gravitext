/*
 * Copyright (c) 2008-2011 David Kellum
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
 * Immutable XML attribute identifier.
 * @author David Kellum
 */
public class Attribute
{
    /**
     * Construct with name in default namespace.
     */
    public Attribute( final String name )
    {
        this( name, null );
    }

    /**
     * Construct with name and namespace.
     */
    public Attribute( final String name, final Namespace ns )
    {
        if( name == null ) throw new NullPointerException( "name" );

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

    public final String name()
    {
        return _name;
    }

    /**
     * Returns the Namespace associated with this Tag or null if no
     * Namespace was specified on construction.
     */
    public final Namespace namespace()
    {
        return _namespace;
    }

    final String beginAttribute()
    {
        return _beginAttribute;
    }

    private final String _name;
    private final Namespace _namespace;

    private final String _beginAttribute;
}
