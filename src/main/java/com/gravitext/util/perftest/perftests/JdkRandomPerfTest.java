package com.gravitext.util.perftest.perftests;

import java.util.Random;

import com.gravitext.util.perftest.FastRandom;
import com.gravitext.util.perftest.PerformanceTest;

public class JdkRandomPerfTest implements PerformanceTest
{

    public int runTest( FastRandom random ) throws Exception
    {
        Random g = new Random( random.nextInt() );
        for( int i = 0; i < 10000; ++i ) g.nextInt(100);
        return ( ( g.nextInt() & 0x7fffffff ) % 3 );        
    }

    public void setVerbose( boolean doVerbose )
    {
    }

}
