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

import java.util.Formatter;

import com.gravitext.concurrent.ConcurrentTest;
import com.gravitext.concurrent.ConcurrentTester;
import com.gravitext.concurrent.TestFactory;
import com.gravitext.util.Metric;

public class PerformanceTester extends ConcurrentTester
{
    /**
     * @deprecated
     */
    public PerformanceTester( ConcurrentTest ctest, int runCount, int threads )
    {
        super( ctest, runCount, threads );
    }

    public PerformanceTester( TestFactory factory, int runCount, int threads )
    {
        super( factory, runCount, threads );
    }

    public static CharSequence header()
    {
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%-20s %-6s %-7s %-6s %8s %10s(%6s) %-9s (%6s)\n",
                  "Test Class",
                  "Count",
                  "Time",
                  "R Sum",
                  "~R Value",
                  "Throughput",
                  "Change",
                  "~Latency",
                  "Change" );
        o.append( "---------------------------------------------" + 
                  "--------------------------------------------\n" );
        
        return o;
    }

    public CharSequence formatStartLine()
    {
        String name = factory().name();
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%-20s %6s ", name, Metric.format( runsTarget() ) );
        return o;
    }

    public double throughputChange( ConcurrentTester prior )
    {
        if( prior == null ) return Double.NaN;
        
        double c = meanThroughput();
        double p = prior.meanThroughput();
        
        return ( ( c - p ) / p );     
    }

    public double latencyChange( ConcurrentTester prior )
    {
        if( prior == null ) return Double.NaN;
        double c = meanLatency().seconds();
        double p = prior.meanLatency().seconds();
        
        return ( ( c - p ) / p );        
    }

    public CharSequence formatResults( ConcurrentTester prior )
    {
        StringBuilder o = new StringBuilder(128);
        Formatter f = new Formatter( o );
        f.format( "%7s %6s %6s/r %6sr/s (%6s) %7s/r (%6s)\n",
                  duration(),
                  Metric.format( (double) resultSum() ),
                  Metric.format( (double) resultSum() / runsExecuted() ),
                  Metric.format( meanThroughput() ),
                  Metric.formatDifference( throughputChange( prior ) ),     
                  meanLatency(),
                  Metric.formatDifference( latencyChange( prior ) ) );
        return o;
    }

}
