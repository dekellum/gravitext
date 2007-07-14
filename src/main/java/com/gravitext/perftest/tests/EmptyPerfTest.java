package com.gravitext.perftest.tests;

import com.gravitext.concurrent.ConcurrentTest;

/**
 * Empty performance test useful for determining base test overhead.
 * @author David Kellum
 */
public class EmptyPerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed )
    {
        return _returnValue;
    }
    
    public void setReturnValue( int rvalue )
    {
        _returnValue = rvalue;
    }

    private int _returnValue = 1;
}
