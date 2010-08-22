/*
 * Copyright (c) 2010 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.gravitext.xml.tree;

import java.util.ArrayList;
import java.util.HashMap;

import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;
import com.gravitext.xml.producer.Tag;

/**
 * Cache of Namespaces and associated Tags and Attributes.
 */
public final class NamespaceCache
{
    /**
     * Find or create Namespace for the specified prefix and iri.
     * Returns null for a null or empty iri.
     */
    public Namespace namespace( String prefix, String iri )
    {
        if( ( iri == null ) || ( iri.isEmpty() ) ) {
            // http://www.w3.org/TR/REC-xml-names/#iri-use
            // Empty IRI is invalid namespace.
            return null;
        }

        if( ( prefix == null ) || prefix.isEmpty() ) {
            prefix = Namespace.DEFAULT;
        }

        NameSet nset = nset( iri );

        Namespace ns = null;

        for( Namespace nc : nset.spaces ) {
            if( nc.prefix().equals( prefix ) ) {
                ns = nc;
                break;
            }
        }
        if( ns == null ) {
            ns = new Namespace( prefix, iri );
            nset.spaces.add( ns );
        }
        return ns;
    }

    /**
     * Find or create tag by name in namespace.
     * @param ns may be null.
     */
    public Tag tag( final String name, final Namespace ns )
    {
        NameSet nset = nset( ns );
        Tag tag = nset.tags.get( name );
        if( tag == null ) {
            tag = new Tag( name, ns );
            nset.tags.put( name, tag );
        }
        return tag;
    }

    /**
     * Find or create attribute by name in namespace.
     * @param ns may be null.
     */
    public Attribute attribute( final String name, final Namespace ns )
    {
        NameSet nset = nset( ns );
        Attribute attribute = nset.attributes.get( name );
        if( attribute == null ) {
            attribute = new Attribute( name, ns );
            nset.attributes.put( name, attribute );
        }
        return attribute;
    }

    private NameSet nset( Namespace ns )
    {
        return nset( ( ns == null ) ? null : ns.nameIRI() );
    }

    private NameSet nset( String iri )
    {
        NameSet nset = _cache.get( iri );
        if( nset == null ) {
            nset = new NameSet();
            _cache.put( iri, nset );
        }
        return nset;
    }

    private final static class NameSet
    {
        final ArrayList<Namespace>    spaces = new ArrayList<Namespace>(2);
        final HashMap<String,Tag>       tags = new HashMap<String,Tag>(16);
        final HashMap<String,Attribute> attributes =
            new HashMap<String,Attribute>(16);
    }

    private final HashMap<String,NameSet> _cache =
        new HashMap<String,NameSet>( 8 );

}
