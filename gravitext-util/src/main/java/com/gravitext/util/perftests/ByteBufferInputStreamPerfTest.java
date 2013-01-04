/*
 * Copyright (c) 2007-2013 David Kellum
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

package com.gravitext.util.perftests;

import static com.gravitext.util.Charsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.ByteBufferInputStream;
import com.gravitext.util.FastRandom;

public class ByteBufferInputStreamPerfTest implements TestFactory
{
    public static final Class<?>[] TEST_CLASSES = new Class<?>[] {
        java.io.ByteArrayInputStream.class,
        com.gravitext.util.ByteArrayInputStream.class,
        ByteBufferInputStream.class
    };

    public ByteBufferInputStreamPerfTest( Class<?> testClass )
    {
        _testClass = testClass;
    }

    public void setReadOne( boolean readOne )
    {
        _readOne = readOne;
    }

    public String name()
    {
        String n = _testClass.getName().replaceAll( "(\\w)\\w+\\.", "$1." );
        if( _readOne ) n = n + "-1";
        return n;
    }

    public TestRunnable createTestRunnable( int seed )
    {
        if( _testClass == ByteBufferInputStream.class ) {
            return new TestRunnableBase( seed, _readOne ) {
                public int runIteration( int run ) throws IOException
                {
                    ByteBuffer b = wrapInput();
                    ByteBufferInputStream in = new ByteBufferInputStream( b );
                    return readTestData( in );
                }
            };
        }
        if( _testClass == java.io.ByteArrayInputStream.class ) {
            return new TestRunnableBase( seed, _readOne ) {
                public int runIteration( int run ) throws IOException
                {
                    ByteBuffer b = wrapInput();
                    java.io.ByteArrayInputStream in =
                        new java.io.ByteArrayInputStream
                        ( b.array(),
                          b.arrayOffset() + b.position(),
                          b.remaining() );
                    return readTestData( in );
                }
            };
        }
        if( _testClass == com.gravitext.util.ByteArrayInputStream.class ) {
            return new TestRunnableBase( seed, _readOne ) {
                public int runIteration( int run ) throws IOException
                {
                    ByteBuffer b = wrapInput();
                    com.gravitext.util.ByteArrayInputStream in =
                        new com.gravitext.util.ByteArrayInputStream
                        ( b.array(),
                          b.arrayOffset() + b.position(),
                          b.remaining() );
                    return readTestData( in );
                }
            };
        }

        throw new RuntimeException();
    }

    private static abstract class TestRunnableBase implements TestRunnable
    {
        TestRunnableBase( int seed, boolean readOne )
        {
            _random = new FastRandom( seed );
            _readOne = readOne;
        }

        final int readTestData( final InputStream in ) throws IOException
        {
            int count = 0;

            if( _readOne ) {
                while( true ) {
                    if( in.read() == -1 ) break;
                    else ++count;
                }
            }
            else {
                while( true ) {
                    final byte[] b = new byte[ _random.nextInt( 2048 ) + 1 ];
                    final int length = in.read( b );
                    if( length < 1 ) break;
                    else count += Math.min( length, b.length );
                }
            }

            in.close();
            return count;
        }

        final ByteBuffer wrapInput()
        {
            int oset = _random.nextInt( 2 * 1024 + 1 );

            return ByteBuffer.wrap( BYTE_INPUT,
                                    oset,
                                    _random.nextInt(BYTE_INPUT.length - oset) );
        }
        private final boolean _readOne;
        private final FastRandom _random;

        private static final byte[] BYTE_INPUT;

        static {
            ByteBuffer s = UTF_8.encode(
                "The quick brown fox jumps over the lazy dog. " );
            ByteBuffer in = ByteBuffer.allocate( s.remaining() * 2000 );
            for( int i = 0; i < 2000; ++i ) {
                in.put( s );
                s.rewind();
            }
            BYTE_INPUT = in.array();
        }
    }
    private final Class<?> _testClass;
    private boolean _readOne = false;
}
