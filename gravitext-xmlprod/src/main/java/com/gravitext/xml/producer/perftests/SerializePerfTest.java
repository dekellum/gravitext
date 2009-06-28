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

package com.gravitext.xml.producer.perftests;

import java.util.ArrayList;
import java.util.List;

import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;
import com.gravitext.xml.producer.Indentor;

public abstract class SerializePerfTest
    extends OutputPerfTest
    implements TestRunnable
{
    public int runIteration( int run ) throws Exception
    {
        List<GraphItem> g = nextGraph();

        TestOutput out = new TestOutput();

        if( _doSerialize ) serializeGraph( g, out );

        out.flush();

        if( _doVerbose ) out.print();

        if( _doSerialize ) return out.size();

        return ( ( _random.nextInt() & 0x7fffffff ) % 3 );
    }

    public void setVerbose( boolean doVerbose )
    {
        _doVerbose = doVerbose;
    }

    public boolean useWriter()
    {
        return _useWriter;
    }

    public void setUseWriter( boolean useWriter )
    {
        _useWriter = useWriter;
    }

    public void setIndent( Indentor indentor )
    {
        _indentor = indentor;
    }

    public Indentor getIndent()
    {
        return _indentor;
    }

    public void setSeed( int seed )
    {
        _random = new FastRandom( seed );
    }

    protected abstract void serializeGraph( List<GraphItem> graph,
                                            TestOutput out )
        throws Exception;

    private final List<GraphItem> nextGraph()
    {
        int count = _random.nextInt( 4, 15 );
        ArrayList<GraphItem> graph = new ArrayList<GraphItem>( count );

        for( int i = 0; i < count; ++i ) {
            graph.add( new GraphItem( _random ) );
        }

        return graph;
    }

    private boolean _doVerbose   = false;
    private boolean _doSerialize = true; // vs. test generation time exclusively

    private boolean _useWriter   = true;

    private Indentor _indentor   = Indentor.LINE_BREAK;

    private FastRandom _random = new FastRandom( 33 );
}
