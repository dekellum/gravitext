/*
 * Copyright (C) 2008-2009 David Kellum
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
package com.gravitext.htmap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * Common use generic HTAccess container. Wraps an {@link ArrayHTMap}
 * based on single static KEY_SPACE. The set/put methods are
 * overridden to call remove(key) when passed a null value.
 *
 * @author David Kellum
 */
public final class UniMap
    implements HTAccess, Map<Key,Object>
{
    public static final KeySpace KEY_SPACE = new KeySpace();

    public UniMap()
    {
        _map = new ArrayHTMap( KEY_SPACE );
    }

    /**
     * Copy constructor, creates a shallow copy of other.
     */
    public UniMap( UniMap other )
    {
        _map = other._map.clone();
    }

    /**
     *  {@inheritDoc}
     */
    public <T> T get( Key<T> key )
    {
        return _map.get( key );
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#get(Object)
     */
    public Object get( Object key )
    {
        return _map.get( key );
    }
    
    /**
     * {@inheritDoc}  In this implementation, if a null value is given then
     * the key is removed.
     */
    public <T, V extends T> T set( Key<T> key, V value )
    {
        if( value != null ) {
            return _map.set( key, value );
        }
        else {
            return remove( key );
        }
    }

    /**
     * {@inheritDoc}  In this implementation, if a null value is given then
     * the key is removed.
     */
    @SuppressWarnings("unchecked")
    public Object put( Key key, Object value )
    {
        if( value != null ) {
            return _map.put( key, value );
        }
        else {
            return remove( key );
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> T remove( Key<T> key )
    {
        return _map.remove( key );
    }

    /**
     * Return a shallow copy of this UniMap.
     */
    public UniMap clone()
    {
        return new UniMap( this );
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#clear()
     */
    public void clear()
    {
        _map.clear();
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#containsKey(java.lang.Object)
     */
    public boolean containsKey( Object key )
    {
        return _map.containsKey( key );
    }

    /**
     * @see java.util.AbstractMap#containsValue(java.lang.Object)
     */
    public boolean containsValue( Object value )
    {
        return _map.containsValue( value );
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#entrySet()
     */
    @SuppressWarnings("unchecked")
    public Set<Entry<Key, Object>> entrySet()
    {
        return _map.entrySet();
    }

    /**
     * @see java.util.AbstractMap#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        return _map.equals( o );
    }

    /**
     * @see java.util.AbstractMap#hashCode()
     */
    public int hashCode()
    {
        return _map.hashCode();
    }

    /**
     * @see java.util.AbstractMap#isEmpty()
     */
    public boolean isEmpty()
    {
        return _map.isEmpty();
    }

    /**
     * @see java.util.AbstractMap#keySet()
     */
    @SuppressWarnings("unchecked")
    public Set<Key> keySet()
    {
        return _map.keySet();
    }

    /**
     * Merge all key/values from other to this.
     */
    @SuppressWarnings("unchecked")
    public void putAll( Map<? extends Key, ? extends Object> other )
    {
        _map.putAll( other );
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#remove(java.lang.Object)
     */
    public Object remove( Object key )
    {
        return _map.remove( key );
    }

    /**
     * @see com.gravitext.htmap.ArrayHTMap#size()
     */
    public int size()
    {
        return _map.size();
    }

    /**
     * @see java.util.AbstractMap#toString()
     */
    public String toString()
    {
        return _map.toString();
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    public Collection<Object> values()
    {
        return _map.values();
    }

    private final ArrayHTMap _map;

}
