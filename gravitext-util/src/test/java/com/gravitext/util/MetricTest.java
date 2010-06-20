/*
 * Copyright (c) 2007-2010 David Kellum
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

public class MetricTest
{
    @Test
    public void testPositives()
    {
        assertEquals( "999.9f",  Metric.format( 999.94e-15d ) );
        assertEquals( "1.000p",  Metric.format( 999.95e-15d ) );
        assertEquals( "999.9p",  Metric.format( 999.94e-12d ) );
        assertEquals( "1.000n",  Metric.format( 999.95e-12d ) );
        assertEquals( "999.9n",  Metric.format( 999.94e-9d ) );
        assertEquals( "1.000µ",  Metric.format( 999.95e-9d ) );
        assertEquals( "999.9µ",  Metric.format( 999.94e-6d ) );
        assertEquals( "1.000m",  Metric.format( 999.95e-6d ) );
        assertEquals( "10.00m",  Metric.format( 9999.5e-6d ) );
        assertEquals( "999.9m",  Metric.format( 999.94e-3d ) );
        assertEquals( " 1.000",  Metric.format( 999.95e-3d ) );
        assertEquals( " 9.999",  Metric.format( 9.9994d ) );
        assertEquals( "10.000",  Metric.format( 9.9995d ) );
        assertEquals( "99.999",  Metric.format( 99.9994d ) );
        assertEquals( "100.00",  Metric.format( 99.9995d ) );
        assertEquals( "999.99",  Metric.format( 999.994d ) );
        assertEquals( " 1,000",  Metric.format( 999.995d ) );
        assertEquals( "10,000",  Metric.format( 9999.94d ) );
        assertEquals( "10,000",  Metric.format( 9999.95d ) );
        assertEquals( "99,999",  Metric.format( 99999.4d ) );
        assertEquals( "100.0k",  Metric.format( 99999.5d ) );
        assertEquals( "999.9k",  Metric.format( 999.94e3d ) );
        assertEquals( "1.000M",  Metric.format( 999.95e3d ) );
        assertEquals( "999.9M",  Metric.format( 999.94e6d ) );
        assertEquals( "1.000G",  Metric.format( 999.95e6d ) );
    }

    @Test
    public void testNegatives()
    {
        assertEquals( " -999f",  Metric.format( -999.4e-15d ) );
        assertEquals( "-1.00p",  Metric.format( -999.5e-15d ) );
        assertEquals( " -999p",  Metric.format( -999.4e-12d ) );
        assertEquals( "-1.00n",  Metric.format( -999.5e-12d ) );
        assertEquals( " -999n",  Metric.format( -999.4e-9d ) );
        assertEquals( "-1.00µ",  Metric.format( -999.5e-9d ) );
        assertEquals( " -999µ",  Metric.format( -999.4e-6d ) );
        assertEquals( "-1.00m",  Metric.format( -999.5e-6d ) );
        assertEquals( "-10.0m",  Metric.format( -9999.5e-6d ) );
        assertEquals( " -999m",  Metric.format( -999.4e-3d ) );
        assertEquals( "-1.000",  Metric.format( -999.5e-3d ) );
        assertEquals( "-9.999",  Metric.format( -9.9994d ) );
        assertEquals( "-10.00",  Metric.format( -9.9995d ) );
        assertEquals( "-99.99",  Metric.format( -99.994d ) );
        assertEquals( "-100.0",  Metric.format( -99.995d ) );
        assertEquals( "-999.9",  Metric.format( -999.94d ) );
        assertEquals( "-1,000",  Metric.format( -999.95d ) );
        assertEquals( "-9,999",  Metric.format( -9999.4d ) );
        assertEquals( "-10.0k",  Metric.format( -9999.5d ) );
        assertEquals( "-99.9k",  Metric.format( -99940d ) );
        assertEquals( " -100k",  Metric.format( -99950d ) );
        assertEquals( " -999k",  Metric.format( -999.4e3d ) );
        assertEquals( "-1.00M",  Metric.format( -999.5e3d ) );
        assertEquals( " -999M",  Metric.format( -999.4e6d ) );
        assertEquals( "-1.00G",  Metric.format( -999.5e6d ) );
    }

    @Test
    public void testInteger()
    {
        assertEquals( "     1",  Metric.format( 1 ) );
        assertEquals( "    10",  Metric.format( 10 ) );
        assertEquals( "   999",  Metric.format( 999 ) );
        assertEquals( " 1,000",  Metric.format( 1000 ) );
        assertEquals( "10,000",  Metric.format( 10000 ) );
        assertEquals( "99,999",  Metric.format( 99999 ) );
        assertEquals( "100.0k",  Metric.format( 100000 ) );
        assertEquals( "1.000M",  Metric.format( 1000000 ) );
        assertEquals( "    -1",  Metric.format( -1 ) );
        assertEquals( "   -10",  Metric.format( -10 ) );
        assertEquals( "  -100",  Metric.format( -100 ) );
        assertEquals( "-1,000",  Metric.format( -1000 ) );
        assertEquals( "-9,999",  Metric.format( -9999 ) );
        assertEquals( "-10.0k",  Metric.format( -10000 ) );
        assertEquals( " -100k",  Metric.format( -99999 ) );
        assertEquals( " -100k",  Metric.format( -100000 ) );
        assertEquals( "-1.00M",  Metric.format( -1000000 ) );
    }

    @Test
    public void test0()
    {
        assertEquals( " 0.000", Metric.format( 0.0d ) );
        assertEquals( " 0.000", Metric.format( 1e-18d ) );
    }

    @Test
    public void testRandomSize()
    {
        Random r = new Random();

        StringBuilder b = new StringBuilder(6);
        for( int i = 1; i < 1000; ++i ) {
            b.setLength( 0 );
            double v = r.nextDouble() * Math.pow( 10d, r.nextInt( 31 ) - 15 );
            if( r.nextBoolean() ) v = -v;
            Metric.format( v, b );
            if( b.length() != 6 ) {
                fail( "Value: " + v + " ==> " + b );
            }
        }
    }

    @Test
    public void testDifferences()
    {
        assertEquals( "   N/A", Metric.formatDifference( Double.NaN ) );

        assertEquals( "2.100x", Metric.formatDifference( 1.1d ) );
        assertEquals( "50.33x", Metric.formatDifference( 49.33d ) );
        assertEquals( "101.6x", Metric.formatDifference( 100.6d ) );
        assertEquals( "10,000x", Metric.formatDifference( 9998.5d ) );

        assertEquals( "-23.0%", Metric.formatDifference( -0.23d ) );
        assertEquals( " -200%", Metric.formatDifference( -2d ) );
        assertEquals( "+13.2%", Metric.formatDifference( 0.1324d ) );
        assertEquals( "+1.48%", Metric.formatDifference( 0.01477d ) );
        assertEquals( "-1,000%", Metric.formatDifference( -9.996d ) );
    }
}
