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

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DurationTest
{
    @Test
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
                        .divide( 1000000000000000000L ).toString() );
    }
}
