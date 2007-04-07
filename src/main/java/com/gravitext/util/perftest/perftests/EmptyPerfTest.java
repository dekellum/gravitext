package com.gravitext.util.perftest.perftests;

import com.gravitext.util.perftest.FastRandom;
import com.gravitext.util.perftest.PerformanceTest;

public class EmptyPerfTest implements PerformanceTest
{

    public int runTest( FastRandom random ) throws Exception
    {
        return 1;
    }

    public void setVerbose( boolean doVerbose )
    {
    }

}
