package com.gravitext.htmap.perftests;

import java.util.Map;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.util.FastRandom;
import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

/**
 * ArrayHTMap performance test.
 * @author David Kellum
 */
public class ArrayHTMapPerfTest implements ConcurrentTest
{
    public final int runTest( int run, int seed ) throws Exception
    {
        final FastRandom r = new FastRandom( seed + run );
        
        int found = 0;

        final Map<Key,Object> map = createMap();

        for( int t = 0; t < PASSES; ++t ) {
            int i = r.nextInt( TOTAL_KEYS );
            Key key = KEYS[i];
            Object value = map.get( key );
            if( value == null ) {
                map.put( key, VALUES[i] );
            }
            else {
                ++found;
                if( value != VALUES[i] ) {
                    throw new IllegalStateException
                    ( "value of " + key + " = " + value );
                }
                map.remove( key );
            }
        }
        
        return found;
    }

    protected Map<Key,Object> createMap()
    {
        return new ArrayHTMap( KEY_SPACE );
    }

    protected static final int TOTAL_KEYS = 40; 
    protected static final int PASSES = 120;
    
    protected static final KeySpace KEY_SPACE = new KeySpace();
    
    protected static final Key[] KEYS; 
    protected static final String[] VALUES;

    static
    {
        KEYS = new Key[TOTAL_KEYS];
        VALUES = new String[TOTAL_KEYS];
        
        for( int i = 0; i < TOTAL_KEYS; ++i ) {
            KEYS[i] = KEY_SPACE.create( "key-" + i, String.class );
            VALUES[i] = ( "value-" + i );
        }
    }
    
}
