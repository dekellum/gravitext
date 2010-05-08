/*
 * Copyright (c) 2007-2010 David Kellum
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

package com.gravitext.htmap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class NestedGenericTest
{
    @Test
    public void testListKey()
    {
        KeySpace ks = new KeySpace();
        Key<List<Double>> dkey = ks.createListKey( "DKEY" );

        ArrayHTMap kmap = new ArrayHTMap( ks );
        List<Double> lvalue = new ArrayList<Double>();
        lvalue.add( 3.3 );

        kmap.set( dkey, new ArrayList<Double>( lvalue ) );

        assertEquals( lvalue, kmap.get( dkey ) );

        //List<String> ovalue = new ArrayList<String>();
        //Compile fails: kmap.set( dkey, ovalue );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGeneric()
    {
        KeySpace ks = new KeySpace();
        Key<Map<String,Double>> mkey = (Key<Map<String, Double>>)
            ks.createGeneric( "MKEY", Map.class );
        Map<String, Double> mvalue = new HashMap<String,Double>();
        mvalue.put( "length", 3.3 );

        ArrayHTMap kmap = new ArrayHTMap( ks );
        kmap.set( mkey, mvalue );

        assertEquals( mvalue, kmap.get( mkey ) );

        Key<Map<String,Double>> badKey = (Key<Map<String, Double>>)
        ks.createGeneric( "BAD_KEY", List.class );
            // List in error, not found on compile

        try {
            kmap.set( badKey, mvalue );
            fail();
        }
        catch( ClassCastException x ) { };
    }
}
