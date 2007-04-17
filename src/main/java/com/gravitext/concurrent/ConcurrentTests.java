package com.gravitext.concurrent;

public final class ConcurrentTests
{
    public static long run( ConcurrentTest test, int runs, int threads )
    {
        return new ConcurrentTester( test, runs, threads ).runTest();
    }
}
