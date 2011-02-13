/*
 * Copyright (c) 2007-2011 David Kellum
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

/**
 * A runnable for a concurrent test. Instances are created via a
 * {@link TestFactory} and run by a single test thread. Thus
 * {@link #runIteration } may access class variables without synchronizing. A
 * common case is use of a FastRandom instance, initialized from the seed passed
 * to the TestFactory. The following test is prototypical:
 *
 * <code><pre>
 * new TestRunnable() {
 *     final FastRandom _random = new FastRandom( seed );
 *     public int runIteration( int run )
 *     {
 *         int c = _random.nextInt( 10000 ) + 10;
 *         List&lt;Integer> numbers = new ArrayList&lt;Integer>( c );
 *         for( int i = 0; i < c; ++i ) {
 *             numbers.add( _random.nextInt( 10000 ) );
 *         }
 *         Collections.sort( numbers );
 *         return numbers.get( 0 ); // return lowest
 *     }
 * }
 * </pre></code>
 *
 * @see com.gravitext.util.FastRandom
 * @author david
 */
public interface TestRunnable
{
    /**
     * Run a test iteration.
     * @param run a sequential run number starting from 1 to the number of runs
     *        tested
     * @return an arbitrary test result count (to be summed as test output)
     * @throws Exception on any error, which will terminate the test.
     */
    public int runIteration( int run ) throws Exception;

}
