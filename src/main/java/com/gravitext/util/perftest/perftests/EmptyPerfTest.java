package com.gravitext.util.perftest.perftests;

import com.gravitext.util.perftest.ConcurrentTest;

public class EmptyPerfTest implements ConcurrentTest
{

    public int runTest( int run, int seed ) throws Exception
    {
        return 1;
    }
}
