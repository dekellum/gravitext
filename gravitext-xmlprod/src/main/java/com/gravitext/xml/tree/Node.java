/*
 * Copyright (c) 2008-2011 David Kellum
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

import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.HTAccess;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

/**
 * Generic XML Tree Node.
 */
public abstract class Node
    implements HTAccess
{
    public static final KeySpace KEY_SPACE = new KeySpace();

    /**
     * Return this nodes parent, or null if there is no parent (root
     * element.)
     */
    public final Element parent()
    {
        return _parent;
    }

    /**
     * Detach this node from its parent, if attached.
     */
    public final void detach()
    {
        if( _parent != null ) {
            _parent.removeChild( this );
            _parent = null;
        }
    }

    /**
     * Return true if this is an element.
     */
    public boolean isElement()
    {
        return false;
    }

    /**
     * Return this Node cast as Element, or null if this is not an Element.
     */
    public Element asElement()
    {
        return null;
    }

    /**
     * Return true if this node is Characters.
     */
    public boolean isCharacters()
    {
        return false;
    }

    /**
     * Return characters if this is Characters, or all contained
     * character data if this is an Element. Null may be returned in
     * the later case.
     */
    public abstract CharSequence characters();

    /**
     * Set characters if this is a Characters node.
     * @throws UnsupportedOperationException unless isCharacters().
     */
    public void setCharacters( CharSequence characters )
    {
        throw new UnsupportedOperationException( "Not a Characters node" );
    }

    public <T, V extends T> T set( Key<T> key, V value )
    {
        if( _props == EMPTY_PROPS ) {
            _props = new ArrayHTMap( KEY_SPACE );
        }

        if( value != null ) {
            return _props.set( key, value );
        }
        return remove( key );
    }

    public <T> T get( Key<T> key )
    {
        return _props.get( key );
    }

    public <T> T remove( Key<T> key )
    {
        return _props.remove( key );
    }

    protected final void setParent( Element parent )
    {
        _parent = parent;
    }

    protected static final ArrayHTMap EMPTY_PROPS = new ArrayHTMap( KEY_SPACE );
    protected static final KeySpace COMPAT_KEY_SPACE = new KeySpace();

    private ArrayHTMap _props = EMPTY_PROPS;
    private Element _parent = null;
}
