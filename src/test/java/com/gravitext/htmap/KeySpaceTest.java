package com.gravitext.htmap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.concurrent.ConcurrentTests;
import com.gravitext.util.FastRandom;
import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

import junit.framework.TestCase;

public class KeySpaceTest extends TestCase
{
    public void testLength()
    {
        KeySpace ks = new KeySpace();
        assertEquals( 0, ks.size() );
        ks.create( "KEY1", String.class );
        assertEquals( 1, ks.size() );
    }
    
    public void testDuplicateName()
    {
        KeySpace ks = new KeySpace();
        ks.create( "KEY1", String.class );
        try {
            ks.create( "KEY1", Float.class );
            fail( "Second KEY1 allowed!" );
        }
        catch( IllegalArgumentException x ) {
            _log.debug( "Expected:", x );
        }
    }
    
    public void testConcurrentCreation() 
    {
        int count = 0;
        
        for( int r = 1; r < 17; ++r ) {
            count += ConcurrentTests.run( new ConcurrentCreater(), 101, r );
        }
        
        _log.debug( "Completed threaded run with {} iterations.", count );
    }

    private static class ConcurrentCreater implements ConcurrentTest 
    {
        public int runTest( int run, int seed ) throws Exception
        {
            FastRandom rand = new FastRandom( seed + run );

            int count = rand.nextInt( 10 );
            
            Key[] lkeys = new Key[ count ];

            ArrayHTMap kmap = new ArrayHTMap( _ks );
                        
            for( int i = 0; i < count; ++i ) {
                lkeys[i] = _ks.create( "k-" + run + '.' + i, String.class );
                kmap.put( lkeys[i], lkeys[i].toString() );
                if( rand.nextInt( 5 ) == 0 ) Thread.yield();
            }

            for( int i = 0; i < count; ++i ) {
                if( rand.nextInt( 5 ) == 0 ) Thread.yield();
                assertEquals( lkeys[i].name(), 
                              kmap.get( (Object) lkeys[i] ) );
            }

            Map<Key, Object> copy = new HashMap<Key, Object>( kmap );
            if( rand.nextInt( 3 ) == 0 ) Thread.yield();
            assertEquals( copy, kmap );
            
            return count;
        }
        private final KeySpace _ks = new KeySpace();
    }

    
    private Logger _log = LoggerFactory.getLogger( getClass() );
}
