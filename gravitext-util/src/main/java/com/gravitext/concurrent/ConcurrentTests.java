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

/**
 * Static utility class for ConcurrentTest instances. 
 * @author David Kellum
 */
public final class ConcurrentTests
{
    /**
     * Run specified test. The first of any Exceptions thrown in a test thread
     * from {@code ConcurrentTest.runTest()} will be re-thrown from this method
     * after wrapping as needed in a RuntimeException.
     * @param test the test instance
     * @param runs the total number of runs to test
     * @param threads the number of threads to concurrently run the test
     * @throws RuntimeException from ConcurrentTest.runTest()
     * @throws Error, for example, when jUnit assertions fail in the
     *         ConcurrentTest
     * @return the sum of result counts returned from {@code test.runTest()}
     * @deprecated Use TestExecutor instead
     */
    public static long run( ConcurrentTest test, int runs, int threads )
    {
        ConcurrentTester tester = new ConcurrentTester( test, runs, threads );
        tester.setDoPerRunTiming( false );
        return tester.runTest();
    }
}
