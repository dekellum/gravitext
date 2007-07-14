package com.gravitext.htmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

import junit.framework.TestCase;

public class ArrayHTMapTest extends TestCase
{
    
    public void testOrdered()
    {
        KeySpace ks = new KeySpace();
        Key<Double> dkey = ks.create( "DKEY", Double.class );
        Key<String> skey = ks.create( "SKEY", String.class );

        ArrayHTMap kmap = new ArrayHTMap( ks );

        assertNull( kmap.get( dkey ) );
        assertNull( kmap.get( skey ) );
        
        assertEquals( 0, kmap.size() );
        
        assertNull( kmap.set( dkey, 3.4 ) );
        assertNull( kmap.set( skey, "string" ) );
        assertEquals( 2, kmap.size() );
                
        assertTrue( kmap.get( dkey ) == 3.4 );
        assertEquals( new String( "string" ), kmap.get( skey ) );
        
        assertTrue( kmap.remove( dkey ) == 3.4 );
        assertEquals( new String( "string" ), kmap.remove( skey ) );
        assertEquals( 0, kmap.size() );
       
        assertNull( kmap.get( dkey ) );
        assertNull( kmap.get( skey ) );
    }
    
    public void testExpansion() 
    {
        KeySpace ks = new KeySpace();
        ArrayHTMap kmap = new ArrayHTMap( ks );

        Key<Double> dkey = ks.create( "DKEY", Double.class );
        assertNull( kmap.get( dkey ) );
        assertNull( kmap.remove( dkey ) );
        assertEquals( 0, kmap.size() );

        Key<String> skey = ks.create( "SKEY", String.class );
        assertNull( kmap.remove( (Object) skey ) );
        assertNull( kmap.get( skey ) );
        assertEquals( 0, kmap.size() );

        assertNull( kmap.set( dkey, 3.4 ) );
        assertNull( kmap.set( skey, "string" ) );
        assertEquals( 2, kmap.size() );

        assertTrue( kmap.get( dkey ) == 3.4 );
        assertEquals( "string", kmap.get( skey ) );
        
        kmap.clear();
        assertEquals( 0, kmap.size() );
        assertNull( kmap.get( skey ) );
    }
    
    public void testNulls()
    {
        KeySpace ks = new KeySpace();
        ArrayHTMap kmap = new ArrayHTMap( ks );
        try {
            kmap.get( null );
            fail( "get( null ) succeeded!" );
        }
        catch( NullPointerException x ) {
            _log.debug( "Expected:", x );
        }

        Key<Double> dkey = ks.create( "DKEY", Double.class );
        try {
            kmap.set( dkey, null );
            fail( "set( key, null ) succeeded!" );
        }
        catch( NullPointerException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    public void testWrongValueType()
    {
        KeySpace ks = new KeySpace();
        ArrayHTMap kmap = new ArrayHTMap( ks );
        Key key = ks.create( "DKEY", Double.class );
        Object val = "goo";
        
        try {
            kmap.put( key, val ); //unchecked
            fail( "String inserted for Double value!");
        }
        catch( ClassCastException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    public void testWrongSpace()
    {
        KeySpace ks = new KeySpace();
        ArrayHTMap kmap = new ArrayHTMap( ks );
        KeySpace other = new KeySpace();
        Key<Double> key = other.create( "DKEY", Double.class );
        
        try {
            kmap.set( key, 3.4 );
            fail( "put() from wrong KeySpace succeeded!" );
        }
        catch( IllegalArgumentException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    public void testMapCompatibility()
    {
        KeySpace ks = new KeySpace();
        final Key<Double> dkey = ks.create( "DKEY", Double.class );
        final Key<String> skey = ks.create( "SKEY", String.class );

        ArrayHTMap kmap = new ArrayHTMap( ks );
        assertFalse( kmap.entrySet().iterator().hasNext() );
        assertFalse( kmap.keySet().iterator().hasNext() );
        assertFalse( kmap.values().iterator().hasNext() );
        assertEquals( 0, kmap.entrySet().size() );
        
        kmap.set( dkey, 3.4 );
        assertTrue( kmap.keySet().contains( dkey ) );
        assertTrue( kmap.values().contains( new Double( 3.4 ) ) );
        assertEquals( 1, kmap.entrySet().size() );
        assertFalse( kmap.keySet().contains( skey ) );
        assertFalse( kmap.values().contains( "string" ) );
        
        kmap.entrySet().add( new Map.Entry<Key,Object>() {
            public Key getKey() { return skey; }
            public Object getValue() { return "before"; }
            public Object setValue( Object value ) { return null; } 
        } );
        assertEquals( "before", kmap.get( skey ) );
        for( Map.Entry<Key, Object> e : kmap.entrySet() ) {
            if( e.getKey().equals( skey ) ) e.setValue( "string" );
        }
        assertEquals( "string", kmap.get( skey ) );
        
        assertTrue( kmap.keySet().contains( skey ) );
        assertFalse( kmap.values().contains( "before" ) );
        assertTrue( kmap.values().contains( "string" ) );
        
        ArrayHTMap copy = kmap.clone();
        assertEquals( kmap, copy );
        assertEquals( copy, kmap );

        copy = new ArrayHTMap( kmap );
        assertEquals( kmap, copy );
        assertEquals( copy, kmap );
        
        HashMap<Key,Object> hmap = new HashMap<Key,Object>( kmap );
        assertEquals( hmap, kmap );
        assertEquals( kmap, hmap );
        
        ArrayHTMap icopy = new ArrayHTMap( ks );
        for( Map.Entry<Key, Object> e : kmap.entrySet() ) {
            icopy.entrySet().add( e );
            assertTrue( kmap.entrySet().contains( e ) );
            assertTrue( e.equals( e ) );
            assertTrue( kmap.entrySet().remove( e ) );
        }
        assertEquals( 0, kmap.size() );
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
