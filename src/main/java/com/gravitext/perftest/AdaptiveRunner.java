/**
 * 
 */
package com.gravitext.perftest;

import com.gravitext.concurrent.ConcurrentTest;

class AdaptiveRunner extends Runner
{
    AdaptiveRunner( ConcurrentTest ctest )
    {
        super( ctest );
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