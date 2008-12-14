/*
 * Copyright 2007 David Kellum
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

package com.gravitext.concurrent;

import com.gravitext.util.FastRandom;

/**
 * Interface for a concurrent performance or thread safety test. Implementations 
 * of this interface should provide a default constructor for operability with 
 * the provided command line test Harness.
 * 
 * @author David Kellum
 * @deprecated
 */
public abstract class ConcurrentTest
    implements TestRunnable
{
    public int runIteration( int run ) throws Exception
    {
        return runTest( run, 33 );
    }

    /**
     * Run a test iteration. This method will be called by multiple threads 
     * concurrently. A result count of arbitrary meaning is returned, summed, 
     * and reported as a sanity check and to assure that no part of the test 
     * is optimized out. 
     * 
     * <p>The following is a prototypical performance test:</p>
     * <code><pre>
     * public int runTest( int run, int seed )
     * {
     *     FastRandom r = new FastRandom( seed + run );
     *     
     *     int c = r.nextInt( 10000 ) + 10;
     *     List&lt;Integer> numbers = new ArrayList&lt;Integer>( c );
     *     for( int i = 0; i < c; ++i ) {
     *         numbers.add( r.nextInt( 10000 ) );
     *     }
     *     Collections.sort( numbers );
     *   
     *     return numbers.get( 0 ); // return lowest
     * }
     * </pre></code>
     * 
     * @see FastRandom
     * @param run a sequential run number starting from 1 to the number of 
     *        runs tested
     * @param seed a fixed seed suitable for constructing a random number 
     *        generator
     * @return an arbitrary test result count
     * @throws Exception on any error, which will terminate the test.
     */
    public abstract int runTest( int run, int seed ) throws Exception;
}
