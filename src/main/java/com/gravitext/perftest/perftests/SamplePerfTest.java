package com.gravitext.perftest.perftests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.concurrent.FastRandom;

public class SamplePerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed )
    {
        FastRandom r = new FastRandom( seed + run );
        
        // A time consuming operation, such as:
        int c = r.nextInt( 10000 ) + 10;
        List<Integer> numbers = new ArrayList<Integer>( c );
        for( int i = 0; i < c; ++i ) {
            numbers.add( r.nextInt( 10000 ) );
        }
        Collections.sort( numbers );
        
        return numbers.get( 0 ); // return lowest
    }

}
