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
import java.util.Collections;
import java.util.List;

import com.gravitext.xml.producer.Attribute;
import com.gravitext.xml.producer.Namespace;
import com.gravitext.xml.producer.Tag;

/**
 * Node representing a *ML Element with Tag, attributes and children.
 */
public final class Element extends Node
{
    public Element( Tag tag )
    {
        if( tag == null ) {
            throw new NullPointerException( getClass().getName() );
        }

        _tag = tag;
    }

    public Element( String name, Namespace ns )
    {
        this( new Tag( name, ns ) );
    }

    public Element( String name )
    {
        this( name, null );
    }

    @Override
    public boolean isElement()
    {
        return true;
    }

    @Override
    public Element asElement()
    {
        return this;
    }

    public Tag tag()
    {
        return _tag;
    }

    public String name()
    {
        return _tag.name();
    }

    public Namespace namespace()
    {
        return _tag.namespace();
    }

    public List<AttributeValue> attributes()
    {
        return _attributes;
    }

    /**
     * Return the specified attribute value or null is no match
     * AttributeValue is found.
     */
    public CharSequence attribute( Attribute attr )
    {
        for( AttributeValue av : _attributes ) {
            if( av.attribute().equals( attr ) ) {
                return av.value();
            }
        }
        return null;
    }

    /**
     * Return the attribute value with the given name and default Namespace, or
     * null if no matching AttributeValue is found.
     */
    public CharSequence attribute( String name )
    {
        for( AttributeValue av : _attributes ) {
            if( av.attribute().name().equals( name ) &&
                ( av.attribute().namespace() == null ) ) {
                return av.value();
            }
        }
        return null;
    }

    /**
     * Additional namespace declarations rooted at this element
     */
    public List<Namespace> namespaceDeclarations()
    {
        return _spaces;
    }

    public List<Node> children()
    {
        return _children;
    }

    /**
     * Set new tag for this element.
     */
    public void setTag( Tag tag )
    {
        _tag = tag;
    }

    /**
     * Replace all attributes on this Element with the specified list. Note
     * that no attempt is made to validate that AttributeValue's have unique
     * Attribute names.
     */
    public void setAttributes( List<AttributeValue> attributes )
    {
        _attributes = attributes;
    }

    /**
     * Set the specified attribute value, replacing any existing attribute if
     * found.
     * @return previous attribute value or null if not found.
     */
    public CharSequence setAttribute( AttributeValue avalue )
    {
        final int end = _attributes.size();
        for( int i = 0; i < end; ++i ) {
            if( _attributes.get(i).attribute().equals( avalue.attribute() ) ) {
                return _attributes.set( i, avalue ).value();
            }
        }
        addAttribute( avalue );
        return null;
    }

    /**
     * Set the specified attribute value, replacing any existing attribute if
     * found.
     * @return previous attribute value or null if not found.
     */
    public void setAttribute( Attribute attr, CharSequence value )
    {
        setAttribute( new AttributeValue( attr, value ) );
    }

    /**
     * Set the specified attribute value, replacing any existing attribute if
     * found.
     * @return previous attribute value or null if not found.
     */
    public void setAttribute( String name, CharSequence value )
    {
        setAttribute( new AttributeValue( new Attribute( name ), value ) );
    }

    /**
     * Add the specified attribute value, making no attempt to check if the
     * same attribute already exists.
     */
    public void addAttribute( AttributeValue avalue )
    {
        if( _attributes == EMPTY_ATTS ) {
            _attributes = new ArrayList<AttributeValue>(3);
        }

        _attributes.add( avalue );
    }

    /**
     * Add the specified attribute value, making no attempt to check if the
     * same attribute already exists.
     */
    public void addAttribute( Attribute attr, CharSequence value )
    {
        addAttribute( new AttributeValue( attr, value ) );
    }

    /**
     * Add the specified attribute value, making no attempt to check if the
     * same attribute already exists.
     */
    public void addAttribute( String name, CharSequence value )
    {
        addAttribute( new AttributeValue( new Attribute( name ), value ) );
    }

    /**
     * Remove the specified attribute value if found.
     * @return previous attribute value or null if not found.
     */
    public CharSequence removeAttribute( Attribute attr )
    {
        final int end = _attributes.size();
        for( int i = 0; i < end; ++i ) {
            if( _attributes.get( i ).attribute().equals( attr ) ) {
                return _attributes.remove( i ).value();
            }
        }
        return null;
    }

    /**
     * Remove the specified attribute value if found by name in the default
     * Namespace.
     * @return previous attribute value or null if not found.
     */
    public CharSequence removeAttribute( String name )
    {
        final int end = _attributes.size();
        for( int i = 0; i < end; ++i ) {
            final Attribute attr = _attributes.get( i ).attribute();
            if( attr.name().equals( name ) && ( attr.namespace() == null ) ) {
                return _attributes.remove( i ).value();
            }
        }
        return null;
    }

    /**
     * Add additional namespace declarations rooted at this element. Should not
     * include this elements namespace.
     */
    public void addNamespace( Namespace ns )
    {
        if( _spaces == EMPTY_NAMESPACES ) {
            _spaces = new ArrayList<Namespace>(3);
        }

        _spaces.add( ns );
    }

    /**
     * Remove specified namespace declaration if found.
     */
    public Namespace removeNamespace( Namespace ns )
    {
        return _spaces.remove( ns ) ? ns : null;
    }

    /**
     * Add child node to this element. Node is first detach()'ed, remove from
     * any prior parent.
     */
    public void addChild( Node node )
    {
        if( _children == EMPTY_CHILDREN ) {
            _children = new ArrayList<Node>(3);
        }

        node.detach();
        _children.add( node );
        node.setParent( this );
    }

    /**
     * Insert child node into the child list of this element at the specified
     * index position. Node is first detach()'ed, remove from any prior parent.
     * @throws IndexOutOfBoundsException on bad index
     */
    public void insertChild( int index, Node node )
    {
        if( _children == EMPTY_CHILDREN ) {
            _children = new ArrayList<Node>(3);
        }

        node.detach();
        _children.add( index, node );
        node.setParent( this );
    }

    void removeChild( Node node )
    {
        _children.remove( node );
    }

    private static final List<AttributeValue> EMPTY_ATTS =
        Collections.emptyList();
    private static final List<Node> EMPTY_CHILDREN = Collections.emptyList();
    private static final List<Namespace> EMPTY_NAMESPACES =
        Collections.emptyList();

    private Tag _tag;
    private List<AttributeValue> _attributes = EMPTY_ATTS;
    private List<Namespace> _spaces = EMPTY_NAMESPACES;
    private List<Node> _children = EMPTY_CHILDREN;
}
