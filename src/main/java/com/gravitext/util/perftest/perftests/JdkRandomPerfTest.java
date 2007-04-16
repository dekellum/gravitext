package com.gravitext.util.perftest.perftests;

import java.util.Random;

import com.gravitext.util.perftest.ConcurrentTest;

public class JdkRandomPerfTest implements ConcurrentTest
{

    public int runTest( int run, int seed ) throws Exception
    {
        Random g = new Random( run + seed );
        for( int i = 0; i < 10000; ++i ) g.nextInt(100);
        return ( ( g.nextInt() & 0x7fffffff ) % 3 );        
    }
}
