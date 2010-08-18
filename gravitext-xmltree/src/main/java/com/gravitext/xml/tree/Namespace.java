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

package com.gravitext.xml.tree;

/**
 * Immutable XML namespace identifier.
 * @author David Kellum
 */
public final class Namespace
{
    /**
     * The default (empty) namespace prefix.
     */
    public static final String DEFAULT = new String( "" );

    /**
     * Construct given prefix and IRI.
     * @param nameIRI non-empty/non-null namespace IRI (URL,urn, etc.)
     * @param prefix DEFAULT or a non-empty/non-null prefix string.
     */
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

    @Override
    public int hashCode()
    {
        return _nameIRI.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if( ( o != null ) && ( o instanceof Namespace ) ) {
            return _nameIRI.equals( ((Namespace) o)._nameIRI );
        }
        return false;
    }

    /**
     * Return the prefix as constructed or the default (empty) prefix.
     */
    public String prefix()
    {
        return _prefix;
    }

    /**
     * Return the non-empty IRI as constructed.
     */
    public String nameIRI()
    {
        return _nameIRI;
    }

    /**
     * Return true if this is the default Namespace declaration with a
     * DEFAULT (empty) prefix.
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
