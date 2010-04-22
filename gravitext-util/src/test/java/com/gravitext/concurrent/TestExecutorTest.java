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

package com.gravitext.concurrent;

import java.util.concurrent.atomic.AtomicIntegerArray;

import com.gravitext.util.FastRandom;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestExecutorTest
{
    @Test
    public void testException()
    {
        TestFactory factory = new TestFactoryBase() {
            public TestRunnable createTestRunnable( final int seed )
            {
                return new TestRunnable() {
                    final FastRandom _random = new FastRandom( seed );
                    public int runIteration( int run ) throws Exception
                    {
                        if( _random.nextInt( 4 ) == 0 ) Thread.yield();
                        if( run == 990 ) {
                            throw new TestException( "run: " + run );
                        }
                        return run;
                    }
                };
            }
        };

        try {
            long count = TestExecutor.run( factory, 1000, 5 );
            fail( "Ran " + count + " without error" );
        }
        catch( TestException x ) {
        }
    }

    @Test
    public void testJunitAssert()
    {
        TestFactory factory = new TestFactoryBase() {
            public TestRunnable createTestRunnable( final int seed )
            {
                return new TestRunnable() {
                    public int runIteration( int run ) throws Exception
                    {
                       if( ( run % 3 ) == 0 ) Thread.yield();
                       assertFalse( "Fail on 101", run == 101 );
                       return run;
                    }
                };
            }
        };
        TestExecutor exec = new TestExecutor( factory, 1000, 7 );

        try {
            long count = exec.runTest();
            fail( "Ran " + count + " without error" );
        }
        catch( AssertionError x ) {
        }
        assertTrue( exec.runsExecuted() >= 101 );
    }

    @Test
    public void testRunCount()
    {
        final int runs = 10000;
        final AtomicIntegerArray slots = new AtomicIntegerArray( runs + 10 );

        TestFactory factory = new TestFactoryBase() {
            public TestRunnable createTestRunnable( final int seed )
            {
                return new TestRunnable() {
                    final FastRandom _random = new FastRandom( seed );
                    public int runIteration( int run ) throws Exception
                    {
                        if( _random.nextInt( 5 ) == 0 ) Thread.yield();
                        return slots.getAndIncrement( run );
                    }
                };
            }
        };
        TestExecutor exec = new TestExecutor( factory, runs, 3 );

        long count = exec.runTest();
        assertEquals( 0, count );
        assertEquals( runs, exec.runsExecuted() );

        for( int i = 1; i < slots.length(); ++i ) {
            assertEquals( "Run " + i,
                          ( i <= exec.runsExecuted() ) ? 1 : 0,
                          slots.get( i ) );
        }
    }

    private static class TestException extends RuntimeException
    {
        public TestException( String message )
        {
            super( message );
        }

        private static final long serialVersionUID = 1L;
    }

}
