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
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.HashHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

import static org.junit.Assert.*;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class HashHTMapTest
{
    @Test   
    public void testOrdered()
    {
        KeySpace ks = new KeySpace();
        Key<Double> dkey = ks.create( "DKEY", Double.class );
        Key<String> skey = ks.create( "SKEY", String.class );

        HashHTMap map = new HashHTMap();

        assertNull( map.get( dkey ) );
        assertNull( map.get( skey ) );
        
        assertEquals( 0, map.size() );
        
        assertNull( map.set( dkey, 3.4 ) );
        assertNull( map.set( skey, "string" ) );
        assertEquals( 2, map.size() );
                
        assertTrue( map.get( dkey ) == 3.4 );
        assertEquals( new String( "string" ), map.get( skey ) );
        
        assertTrue( map.remove( dkey ) == 3.4 );
        assertEquals( new String( "string" ), map.remove( skey ) );
        assertEquals( 0, map.size() );
       
        assertNull( map.get( dkey ) );
        assertNull( map.get( skey ) );
    }

    @Test   
    public void testNulls()
    {
        HashHTMap map = new HashHTMap();
        try {
            map.set( null, "goo" );
            fail( "set( key, null ) succeeded!" );
        }
        catch( NullPointerException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    @Test    
    public void testWrongValueType()
    {
        KeySpace ks = new KeySpace();
        HashHTMap map = new HashHTMap();
        Key key = ks.create( "DKEY", Double.class );
        Object val = "goo";
        
        try {
            map.put( key, val ); //unchecked
            fail( "String inserted for Double value!");
        }
        catch( ClassCastException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    @Test
    public void testMapCompatibility()
    {
        KeySpace ks = new KeySpace();
        final Key<Double> dkey = ks.create( "DKEY", Double.class );
        final Key<String> skey = ks.create( "SKEY", String.class );

        HashHTMap map = new HashHTMap();
        assertFalse( map.entrySet().iterator().hasNext() );
        assertFalse( map.keySet().iterator().hasNext() );
        assertFalse( map.values().iterator().hasNext() );
        assertEquals( 0, map.entrySet().size() );
        
        map.set( dkey, 3.4 );
        assertTrue( map.keySet().contains( dkey ) );
        assertEquals( 1, map.entrySet().size() );
        assertFalse( map.keySet().contains( skey ) );
        assertFalse( map.values().contains( "string" ) );
        assertTrue( map.values().contains( new Double( 3.4 ) ) );
        
        /*
        map.entrySet().add( new Map.Entry<Key,Object>() {
            public Key getKey() { return skey; }
            public Object getValue() { return "before"; }
            public Object setValue( Object value ) { return null; } 
        } );
        */
        map.set( skey, "before" );
        assertEquals( "before", map.get( skey ) );

        for( Map.Entry<Key, Object> e : map.entrySet() ) {
            if( e.getKey().equals( skey ) ) e.setValue( "string" );
        }
        assertEquals( "string", map.get( skey ) );
        assertTrue( map.keySet().contains( skey ) );
        assertFalse( map.values().contains( "before" ) );
        assertTrue( map.values().contains( "string" ) );
        
        HashHTMap copy = (HashHTMap) map.clone();
        assertEquals( map, copy );
        assertEquals( copy, map );

        copy = new HashHTMap( map );
        assertEquals( map, copy );
        assertEquals( copy, map );
        
        HashMap<Key,Object> hmap = new HashMap<Key,Object>( map );
        assertEquals( hmap, map );
        assertEquals( map, hmap );
        
        ArrayHTMap icopy = new ArrayHTMap( ks );
        for( Map.Entry<Key, Object> e : map.entrySet() ) {
            icopy.entrySet().add( e );
            assertTrue( map.entrySet().contains( e ) );
            assertTrue( e.equals( e ) );
            //assertTrue( map.entrySet().remove( e ) );
        }
        //assertEquals( 0, map.size() );
        assertEquals( icopy, copy );
        assertEquals( copy, icopy );
        
        copy.entrySet().clear();
        Iterator<Map.Entry<Key, Object>> iter = icopy.entrySet().iterator();
        while( iter.hasNext() ) {
            assertNotNull( iter.next() );
            iter.remove();
        }
        assertEquals( icopy, copy );
    }

    private Logger _log = LoggerFactory.getLogger( getClass() );
}
