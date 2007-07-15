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

package com.gravitext.perftest.tests;

import com.gravitext.concurrent.ConcurrentTest;

/**
 * Empty performance test useful for determining base test overhead.
 * @author David Kellum
 */
public class EmptyPerfTest implements ConcurrentTest
{
    public int runTest( int run, int seed )
    {
        return _returnValue;
    }
    
    public void setReturnValue( int rvalue )
    {
        _returnValue = rvalue;
    }

    private int _returnValue = 1;
}
