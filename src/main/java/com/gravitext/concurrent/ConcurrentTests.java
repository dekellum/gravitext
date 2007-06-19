package com.gravitext.concurrent;

/**
 * Static utility class for ConcurrentTest instances. 
 * @author David Kellum
 */
public final class ConcurrentTests
{
    /**
     * Run specified test. The first of any Exceptions thrown in a test thread
     * from {@code ConcurrentTest.runTest()} will be re-thrown from this method
     * after wrapping as needed in a RuntimeException.
     * @param test the test instance
     * @param runs the total number of runs to test
     * @param threads the number of threads to concurrently run the test
     * @throws RuntimeException from ConcurrentTest.runTest()
     * @throws Error for example, when jUnit assertions are used in the
     *         ConcurrentTest
     * @return the sum of result counts returned from {@code test.runTest()}
     */
    public static long run( ConcurrentTest test, int runs, int threads )
    {
        return new ConcurrentTester( test, runs, threads ).runTest();
    }
}
