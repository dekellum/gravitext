/*
 * Copyright (c) 2007-2010 David Kellum
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gravitext.concurrent.TestExecutor;
import com.gravitext.concurrent.TestFactoryBase;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;
import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

import static org.junit.Assert.*;
import org.junit.Test;

public class KeySpaceTest
{
    @Test
    public void testLength()
    {
        KeySpace ks = new KeySpace();
        assertEquals( 0, ks.size() );
        ks.create( "KEY1", String.class );
        assertEquals( 1, ks.size() );
    }

    @Test
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

    @Test
    public void testConcurrentCreation()
    {
        int count = 0;

        for( int r = 1; r < 17; ++r ) {
            count += TestExecutor.run( new ConcurrentCreater(), 101, r );
        }
        _log.debug( "Completed threaded run with {} iterations.", count );
    }

    private static class ConcurrentCreater extends TestFactoryBase
    {
        public TestRunnable createTestRunnable( final int seed )
        {
            return new TestRunnable() {
                final FastRandom _rand = new FastRandom( seed );
                public int runIteration( int run )
                {
                    int count = _rand.nextInt( 10 );

                    Key[] lkeys = new Key[ count ];

                    ArrayHTMap kmap = new ArrayHTMap( _ks );

                    for( int i = 0; i < count; ++i ) {
                        lkeys[i] = _ks.create( "k-" + run + '.' + i,
                                               String.class );
                        kmap.put( lkeys[i], lkeys[i].toString() );
                        if( _rand.nextInt( 5 ) == 0 ) Thread.yield();
                    }

                    for( int i = 0; i < count; ++i ) {
                        if( _rand.nextInt( 5 ) == 0 ) Thread.yield();
                        assertEquals( lkeys[i].name(),
                                      kmap.get( (Object) lkeys[i] ) );
                    }

                    Map<Key, Object> copy = new HashMap<Key, Object>( kmap );
                    if( _rand.nextInt( 3 ) == 0 ) Thread.yield();
                    assertEquals( copy, kmap );

                    return count;

                }
            };
        }
        private final KeySpace _ks = new KeySpace();
    }

    private Logger _log = LoggerFactory.getLogger( getClass() );
}
