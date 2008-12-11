package com.gravitext.concurrent;

public interface TestRunnable
{
    /**
     * Run a test iteration. Multiple test threads will call this on multiple
     * thread local instances of the implementing class.  
     * 
     * FIXME:
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
     * @return an arbitrary test result count
     * @throws Exception on any error, which will terminate the test.
     */
    public int runIteration( int run ) throws Exception;

}
