/*
 * Copyright (c) 2007-2011 David Kellum
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

package com.gravitext.htmap.perftests;

import java.util.Map;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;

import com.gravitext.util.FastRandom;
import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.HashHTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;

/**
 * ArrayHTMap performance test.
 * @author David Kellum
 */
public class HTMapPerfTest implements TestFactory
{
    public static final Class<?>[] TEST_CLASSES = new Class<?>[] {
        ArrayHTMap.class,
        HashHTMap.class
    };

    public String name()
    {
        return _testClass.getSimpleName();
    }

    public HTMapPerfTest( Class<?> testClass )
    {
        _testClass = testClass;
    }

    public TestRunnable createTestRunnable( int seed )
    {
        if( _testClass == ArrayHTMap.class ) {
            return new TestRunnableBase( seed ) {
                public int runIteration( int run )
                {
                    return readWrite( new ArrayHTMap( KEY_SPACE ) );
                }
            };
        }

        if( _testClass == HashHTMap.class ) {
            return new TestRunnableBase( seed ) {
                public int runIteration( int run )
                {
                    return readWrite( new HashHTMap( TOTAL_KEYS ) );
                }
            };
        }

        throw new RuntimeException();
    }

    private static abstract class TestRunnableBase implements TestRunnable
    {
        TestRunnableBase( int seed )
        {
            _random = new FastRandom( seed );
        }

        public final int readWrite( Map<Key,Object> map )
        {
            int found = 0;
            for( int t = 0; t < PASSES; ++t ) {
                int i = _random.nextInt( TOTAL_KEYS );
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

        private final FastRandom _random;
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
    private final Class<?> _testClass;

}
