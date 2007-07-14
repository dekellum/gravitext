package com.gravitext.perftest.tests;

import java.util.Random;

import com.gravitext.concurrent.ConcurrentTest;

/**
 * Performance test of java.util.Random.
 * @author David Kellum
 */
public class JdkRandomPerfTest implements ConcurrentTest
{

    public int runTest( int run, int seed )
    {
        Random g = new Random( run + seed );
        for( int i = 0; i < 10000; ++i ) g.nextInt(100);
        return ( ( g.nextInt() & 0x7fffffff ) % 3 );        
    }
}
