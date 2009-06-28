package com.gravitext.util.perftests;

import java.util.Random;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;

public class FastRandomPerfTest implements TestFactory
{
    public enum Mode
    {
        JDK_SHARED,
        JDK_PER_THREAD,
        FAST
    }

    public FastRandomPerfTest( Mode mode )
    {
        _mode = mode;
    }

    public void setIterations( int count )
    {
        _iterations = count;
    }

    public String name()
    {
        return _mode.name();
    }

    public TestRunnable createTestRunnable( final int seed )
    {
        switch( _mode ) {
        case JDK_SHARED :
            return new TestRunnable() {
                public int runIteration( int run )
                {
                    for( int i = _iterations; i > 0; --i ) {
                        _sharedRandom.nextInt( 100 );
                    }
                    return _sharedRandom.nextInt( 3 );
                }
            };
        case JDK_PER_THREAD:
            return new TestRunnable() {
                final Random _random = new Random( seed );
                public int runIteration( int run )
                {
                    for( int i = _iterations; i > 0; --i ) {
                        _random.nextInt( 100 );
                    }
                    return _random.nextInt( 3 );
                }
            };

        case FAST:
            return new TestRunnable() {
                final FastRandom _random = new FastRandom( seed );
                public int runIteration( int run )
                {
                    for( int i = _iterations; i > 0; --i ) {
                        _random.nextInt( 100 );
                    }
                    return _random.nextInt( 3 );
                }
            };
        }
        throw new RuntimeException();
    }

    private final Mode _mode;
    private int _iterations = 10000;

    private final Random _sharedRandom = new Random();
}
