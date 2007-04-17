package com.gravitext.concurrent;

import java.util.concurrent.TimeUnit;

import com.gravitext.concurrent.Duration;

import junit.framework.TestCase;


public class DurationTest extends TestCase
{
    public void test()
    {
        assertEquals( "1.235ms", 
                      new Duration( 1234567, TimeUnit.NANOSECONDS )
                          .toString() );
        assertEquals( " 1,235s",
                new Duration( 1234567, TimeUnit.MILLISECONDS )
                    .toString() );

        assertEquals( "12.345s",
                new Duration( 12345, TimeUnit.MILLISECONDS )
                    .toString() );
        
        assertEquals( "7.000ns", 
                      new Duration( 7, TimeUnit.NANOSECONDS ).toString() );
        assertEquals( "5.000ms",
                      new Duration( 5000000000000000000L,
                                    TimeUnit.MILLISECONDS )
                        .subdivide( 1000000000000000000L ).toString() );
    }
}
