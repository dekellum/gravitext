package com.gravitext.util.perftest;

public interface PerformanceTest
{
    public int runTest( FastRandom random ) throws Exception;

    public void setVerbose( boolean doVerbose );
}
