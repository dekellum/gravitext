package com.gravitext.concurrent;

/**
 * Interface for a concurrent performance or thread safety test. Implementations 
 * of this interface should provide a default constructor for operability with 
 * the provided command line test Harness.
 * 
 * @author David Kellum
 */
public interface ConcurrentTest
{
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
    public int runTest( int run, int seed ) throws Exception;
}
