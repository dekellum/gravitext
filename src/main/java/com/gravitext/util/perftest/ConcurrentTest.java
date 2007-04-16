package com.gravitext.util.perftest;

public interface ConcurrentTest
{
    public int runTest( int run, int seed ) throws Exception;
        
}
