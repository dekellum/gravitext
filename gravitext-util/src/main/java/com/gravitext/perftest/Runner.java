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

package com.gravitext.perftest;

import com.gravitext.concurrent.TestFactory;

/**
 * @deprecated
 */
class Runner
{
    Runner( TestFactory factory, int runCount, int iterations )
    {
        this( factory );
        _runCount = runCount;
        _iterations = iterations;
        _fixedSeed = true;
    }

    void run( PerformanceTester prior )
    {
        if( prior == null ) prior = _prior;
        
        PerformanceTester tester = 
            new PerformanceTester( _factory, _runCount, _threadCount );
        
        if( _fixedSeed ) tester.setSeed( 7936 * ( _iterations + 1234 ) );
            
        System.out.print( tester.formatStartLine() );
        tester.runTest();
        System.out.print( tester.formatResults( prior ) );

        _prior = tester;
        
        --_iterations;
    }

    boolean hasMoreRuns()
    {
        return ( _iterations > 0 );
    }

    PerformanceTester prior() 
    {
        return _prior;
    }
    
    void setThreadCount( int count )
    {
        _threadCount = count;
    }
    
    protected Runner( TestFactory factory )
    {
        _factory = factory;
    }
    
    protected final TestFactory _factory;
    protected int _runCount = 1;
    protected PerformanceTester _prior = null;

    private boolean _fixedSeed = false;
    private int _iterations = 0;
    private int _threadCount = Runtime.getRuntime().availableProcessors();
}