package com.gravitext.util.perftest.perftests;

import com.gravitext.util.perftest.FastRandom;
import com.gravitext.util.perftest.PerformanceTest;

public class FastRandomPerfTest implements PerformanceTest
{

    public int runTest( FastRandom random ) throws Exception
    {
        FastRandom g = new FastRandom( random.nextInt() );
        for( int i = 0; i < 10000; ++i ) g.nextInt(100);
        return ( ( g.nextInt() & 0x7fffffff ) % 3 );
    }

    public void setVerbose( boolean doVerbose )
    {
    }

}
