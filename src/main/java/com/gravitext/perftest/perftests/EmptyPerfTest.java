package com.gravitext.perftest.perftests;

import com.gravitext.concurrent.ConcurrentTest;

public class EmptyPerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed ) throws Exception
    {
        return _returnValue;
    }
    
    public void setReturnValue( int rvalue )
    {
        _returnValue = rvalue;
    }

    private int _returnValue = 1;
}
