/*
 * Copyright (c) 2008-2012 David Kellum
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
 * Immutable XML namespace identifier.
 * @author David Kellum
 */
public class Namespace
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

        _nameIRI = nameIRI;
        _prefix  = prefix;

        // Flag all prefixes starting with xml, i.e xml:lang, xml:base, "xmlns"
        // Should not declare these.
        _isXML   = prefix.startsWith( "xml" );

        StringBuilder qName = new StringBuilder(64);
        qName.append( " xmlns" ); //Note leading space.
        if( !isDefault() ) {
            qName.append( ':' ).append( _prefix );
        }
        qName.append( '=' );
        _beginDecl = qName.toString();
    }

    /**
     * Shorthand for new Namespace( DEFAULT, iri ).
     */
    public Namespace( final String nameIRI )
    {
        this( DEFAULT, nameIRI );
    }

    @Override
    public final int hashCode()
    {
        return _nameIRI.hashCode();
    }

    @Override
    public final boolean equals( Object o )
    {
        if( ( o != null ) && ( o instanceof Namespace ) ) {
            return _nameIRI.equals( ((Namespace) o)._nameIRI );
        }
        return false;
    }

    /**
     * Return the prefix as constructed or the default (empty) prefix.
     */
    public final String prefix()
    {
        return _prefix;
    }

    /**
     * Return the non-empty IRI as constructed.
     */
    public final String nameIRI()
    {
        return _nameIRI;
    }

    /**
     * Return true if this is the default Namespace declaration with a
     * DEFAULT (empty) prefix.
     */
    public final boolean isDefault()
    {
        return ( _prefix == DEFAULT );
    }

    public final boolean isXML()
    {
        return _isXML;
    }

    final String beginDecl()
    {
        return _beginDecl;
    }

    private final String _prefix;
    private final String _nameIRI;
    private final String _beginDecl;
    private final boolean _isXML;
}
