package com.gravitext.perftest.tests;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.util.FastRandom;

/**
 * Performance test of FastRandom.
 * @author David Kellum
 */
public class FastRandomPerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed )
    {
        FastRandom g = new FastRandom( seed + run );
        for( int i = 0; i < 10000; ++i ) g.nextInt(100);
        return ( ( g.nextInt() & 0x7fffffff ) % 3 );
    }
}
