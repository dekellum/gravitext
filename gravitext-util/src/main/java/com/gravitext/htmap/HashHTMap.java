/*
 * Copyright 2007 David Kellum
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

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Heterogeneous Type-safe {@code HTMap} implementation based on a
 * {@code HashMap}.  A given {@code HashHTMap} instance may contain
 * keys from more than one {@link KeySpace} (since the KeySpace is
 * not used for lookup).  Null keys are not supported as per 
 * {@code HTMap}. Null values are supported.
 *
 * <p>HashHTMap instances are not internally synchronized, as per the
 * underlying {@code HashMap}.</p>
 *
 * @see Key
 * @author David Kellum
 */
@SuppressWarnings("unchecked")
public class HashHTMap 
    extends HashMap<Key, Object>
    implements HTMap
{
    public HashHTMap()
    {
        super();
    }

    public HashHTMap( int expectedMaxSize )
    {
        super( expectedMaxSize );
    }

    public HashHTMap( Map<? extends Key, ? extends Object> m )
    {
        super( (1 + (m.size() / 3)) * 4 );
        putAll( m );
    }
    
    @Override
    public Set<Entry<Key, Object>> entrySet()
    {
        return new EntrySet( super.entrySet() );
    }
    
    /**
     * {@inheritDoc}
     * @throws NullPointerException if key is null
     */
    public <T> T get( Key<T> key )
    {
        return cast( key, super.get( key ) );
    }

    /**
     * {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if key is null
     */
    public <T, V extends T> T set( Key<T> key, V value )
    {
        checkValue( key, value );
        return cast( key, super.put( key, value ) );
    }

    @Override
    public Object put( Key key, Object value )
    {
        checkValue( key, value );
        return super.put( key, value );
    }

    @Override
    public void putAll( Map<? extends Key, ? extends Object> m )
    {
        for (Map.Entry<? extends Key, ? extends Object> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException if key is null
     */
    public <T> T remove( Key<T> key )
    {
        return cast( key, super.remove( key ) );
    }
    
    private final class EntrySet
        implements Set<Entry<Key, Object>>
    {
        public EntrySet( Set<Entry<Key, Object>> base )
        {
            _baseES = base;
        }
        
        public void clear()
        {
            _baseES.clear();
        }

        public boolean contains( Object o )
        {
            return _baseES.contains( o );
        }

        public boolean containsAll( Collection<?> c )
        {
            return _baseES.containsAll( c );
        }

        public boolean equals( Object o )
        {
            return _baseES.equals( o );
        }

        public int hashCode()
        {
            return _baseES.hashCode();
        }

        public boolean isEmpty()
        {
            return _baseES.isEmpty();
        }

        public boolean remove( Object o )
        {
            return _baseES.remove( o );
        }

        public boolean removeAll( Collection<?> c )
        {
            return _baseES.removeAll( c );
        }

        public boolean retainAll( Collection<?> c )
        {
            return _baseES.retainAll( c );
        }

        public int size()
        {
            return _baseES.size();
        }

        public Object[] toArray()
        {
            return _baseES.toArray();
        }

        public <T> T[] toArray( T[] a )
        {
            return _baseES.toArray( a );
        }

        public boolean add( Entry<Key, Object> e )
        {
            checkValue( e.getKey(), e.getValue() );
            return _baseES.add( e );
        }

        public boolean addAll( Collection<? extends Entry<Key, Object>> c )
        {
            boolean changed = false;
            for( Entry<Key, Object> e : c ) {
                if( add( e ) ) changed = true;
            }
            return changed;
        }
        
        public Iterator<Entry<Key, Object>> iterator()
        {
            return new EntryIterator( _baseES.iterator() );
        }

        private Set<Entry<Key, Object>> _baseES;
    }
    
    private final class EntryIterator
        implements Iterator<Entry<Key, Object>>
    {
        public EntryIterator( Iterator<Entry<Key, Object>> base )
        {
            _baseIter = base;
        }

        public boolean hasNext()
        {
            return _baseIter.hasNext();
        }

        public Entry<Key, Object> next()
        {
            return new EntryWrapper( _baseIter.next() );
        }

        public void remove()
        {
            _baseIter.remove();
        }

        private Iterator<Entry<Key, Object>> _baseIter;
    }
    
    private final class EntryWrapper
        implements Entry<Key, Object>
    {
        public EntryWrapper( Entry<Key, Object> base )
        {
            _baseEntry = base;
        }

        @Override
        public boolean equals( Object o )
        {
            return _baseEntry.equals( o );
        }

        public Key getKey()
        {
            return _baseEntry.getKey();
        }


        public Object getValue()
        {
            return _baseEntry.getValue();
        }

        @Override
        public int hashCode()
        {
            return _baseEntry.hashCode();
        }

        public Object setValue( Object value )
        {
            checkValue( _baseEntry.getKey(), value );
            return _baseEntry.setValue( value );
        }

        private Entry<Key, Object> _baseEntry;
    }
    
    private void checkValue( Key key, Object value )
    {
        if( ! key.valueType().isInstance( value ) ) {
            throw new ClassCastException( String.format( 
            "Value type %s not assignable to Key '%s' with value type %s.", 
                value.getClass().getName(), 
                key.name(),
                key.valueType().getName() ) );
        }
    }
    
    private <T> T cast( Key<T> key, Object value )
    {
        return (T) value;
    }

    private static final long serialVersionUID = 8648040230253776031L;
}
