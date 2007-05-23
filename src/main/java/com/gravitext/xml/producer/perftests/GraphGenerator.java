package com.gravitext.xml.producer.perftests;

import java.util.ArrayList;
import java.util.List;

import com.gravitext.concurrent.FastRandom;

public final class GraphGenerator
{
    GraphGenerator( FastRandom random )
    {
        _r = random;
    }
    
    public List<GraphItem> nextGraph()
    {
        int count = _r.nextInt( 4, 15 );
        ArrayList<GraphItem> graph = new ArrayList<GraphItem>( count );
        
        for( int i = 0; i < count; ++i ) {
            graph.add( new GraphItem( _r ) );
        }
        
        return graph;
    }
    
    final FastRandom _r;
}
