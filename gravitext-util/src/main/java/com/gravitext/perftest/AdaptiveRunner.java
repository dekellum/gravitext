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
class AdaptiveRunner extends Runner
{
    AdaptiveRunner( TestFactory factory )
    {
        super( factory );
    }
           
    @Override
    void run( PerformanceTester altPrior )
    {
        PerformanceTester prior = prior();
        super.run( altPrior );
        PerformanceTester current = prior(); 

        _warmTime += current.duration().seconds();
        _tchange = current.throughputChange( prior );
        _runCount = (int) ( ( _warmTarget * _warmInterval ) * _runCount
                    * 1.25d
                    / current.duration().seconds() );
    }
    
    @Override
    boolean hasMoreRuns()
    {
        return ( ( _warmTime < _warmTarget ) || 
                 ( _tchange < -_warmTolerance ) || 
                 ( _tchange > _warmTolerance ) );
    }
    
    private double _warmTime = 0d;
    private double _tchange = Double.NaN;

    private double _warmTarget     = 40.0d; //seconds
    private double _warmInterval   = 1.0d / 4.0d; //~10 second intervals
    private double _warmTolerance  = 0.05d; // +/- 5%
}