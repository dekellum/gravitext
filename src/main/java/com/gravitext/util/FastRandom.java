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

package com.gravitext.util;

/**
 * A limited quality but high performance random number generator
 * suitable for performance testing. This class offers no thread
 * safety and is best used exclusively by each test thread. The
 * performance advantage over java.util.Random was found to be 34x for
 * Sun JDK 1.5.0_09 and 30x for Sun JDK 1.6.0_01 on Linux 2xCPU. The
 * generation cost for java.util.Random is potentially significant to
 * some performance test cases.
 *
 * The psuedorandom value generation is based on: George Marsaglia,
 * XorShift RNGs, Journal of Statistical Software 8(13), 2003
 *
 * @author David Kellum
 */
public final class FastRandom
{
    /**
     * Construct with FastRandom.generateSeed() as the seed.
     */    
    public FastRandom()
    {
        this( generateSeed() );
    }

    /**
     * Construct with the specified seed value.
     */
    public FastRandom( int seed )
    {
        _r = seed;
        nextInt(); // Avoid same seed being returned in first call to nextInt(). 
    }
    
    /**
     * Returns a psuedorandom value in the range [0,high).
     */
    public int nextInt( int high )
    {
        return nextInt( 0, high );
    }
    
    /**
     * Returns a psuedorandom value in the range [low,high).
     */
    public int nextInt( int low, int high )
    {
        int n = ( nextInt() % ( high - low ) );
        if( n < 0 ) n = -n;
        return ( n + low );
    }
    
    /**
     * Returns a psuedorandom integer using all 32-bits.
     */
    public int nextInt()
    {
        int r = _r;
        _r = r ^ ( r << 5 ) ^ ( r >>> 9 ) ^ ( r << 7 );
        return r;
    }
    
    /**
     * Generate a new seed derived from System.nanoTime() and a new
     * Object.hashCode().  Note this is a relatively slow operation
     * and should be avoided in performance intensive loops.
     */
    public static int generateSeed()
    {
        long t = System.nanoTime();
        return (int) ( t >>> 32 ) ^ (int) t ^ ( new Object() ).hashCode();
    }

    private int _r;
}
