package com.gravitext.util.perftest;

public final class ConcurrentTests
{
    public static long run( ConcurrentTest test, int runs, int threads )
    {
        return new ConcurrentTester( test, runs, threads ).runTest();
    }
}
