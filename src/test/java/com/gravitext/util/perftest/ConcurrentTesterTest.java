package com.gravitext.util.perftest;

import junit.framework.TestCase;

public class ConcurrentTesterTest extends TestCase
{
    public void testException() 
    {
        try {
            long count = ConcurrentTests.run(  new CTest(), 1000, 5 );
            fail( "Ran " + count + " without error" );
        } 
        catch( TestException x ) {
        }
    }

    
    public static class CTest implements ConcurrentTest 
    {
        public int runTest( int run, int seed ) throws TestException
        {
            FastRandom rand = new FastRandom( seed + run );
            if( rand.nextInt( 4 ) == 0 ) Thread.yield();
            if( run > 16 ) throw new TestException( "run: " + run );
            return run;
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
