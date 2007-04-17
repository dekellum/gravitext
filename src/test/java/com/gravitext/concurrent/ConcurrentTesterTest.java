package com.gravitext.concurrent;

import java.util.concurrent.atomic.AtomicIntegerArray;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.concurrent.ConcurrentTester;
import com.gravitext.concurrent.ConcurrentTests;
import com.gravitext.concurrent.FastRandom;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

public class ConcurrentTesterTest extends TestCase
{
    public void testException() 
    {
        ConcurrentTest ctest = new ConcurrentTest() {
            public int runTest( int run, int seed ) throws TestException
            {
                FastRandom rand = new FastRandom( seed + run );
                if( rand.nextInt( 4 ) == 0 ) Thread.yield();
                if( run == 990 ) throw new TestException( "run: " + run );
                return run;
            }
        };
        try {
            long count = ConcurrentTests.run( ctest, 1000, 5 );
            fail( "Ran " + count + " without error" );
        } 
        catch( TestException x ) {
        }
    }

    
    public void testJunitAssert()
    {
        ConcurrentTest ctest = new ConcurrentTest() {
            public int runTest( int run, int seed ) throws InterruptedException
            {
                if( ( run % 3 ) == 0 ) Thread.yield();
                assertFalse( "Fail on 101", run == 101 );
                return 1;
            }
        };
        ConcurrentTester tester = new ConcurrentTester( ctest, 1000, 7 );
        
        try {
            long count = tester.runTest();
            fail( "Ran " + count + " without error" );
        }
        catch( AssertionFailedError x ) {
        }
        assertTrue( tester.runsExecuted() >= 101 );
    }

    public void testRunCount() 
    {
        final int runs = 10000;
        final AtomicIntegerArray slots = new AtomicIntegerArray( runs + 10 );
        
        ConcurrentTest ctest = new ConcurrentTest() {
            public int runTest( int run, int seed ) throws TestException
            {
                 FastRandom r = new FastRandom( seed + run );
                 if( r.nextInt( 5 ) == 0 ) Thread.yield();
                 return slots.getAndIncrement( run );
            }
        };
        
        ConcurrentTester tester = new ConcurrentTester( ctest, runs, 3 );

        long count = tester.runTest();
        assertEquals( 0, count );
        assertEquals( runs, tester.runsExecuted() );
        
        for( int i = 1; i < slots.length(); ++i ) {
            assertEquals( "Run " + i,
                          ( i <= tester.runsExecuted() ) ? 1 : 0, 
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
