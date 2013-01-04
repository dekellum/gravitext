/*
 * Copyright (c) 2007-2013 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.perftest.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;

/**
 * A sample performance test of sorting a list of integers via the
 * java.util.Collections sort.
 * @author David Kellum
 */
public class SortPerfTest implements TestFactory
{
    public String name()
    {
        return "JavaSort";
    }

    public TestRunnable createTestRunnable( final int seed )
    {
        return new TestRunnable() {
            final FastRandom _random = new FastRandom( seed );
            public int runIteration( int run )
            {
                int c = _random.nextInt( 10000 ) + 10;
                List<Integer> numbers = new ArrayList<Integer>( c );
                for( int i = 0; i < c; ++i ) {
                    numbers.add( _random.nextInt( 10000 ) );
                }
                Collections.sort( numbers );

                return numbers.get( 0 ); // return lowest
            }

        };
    }
}
