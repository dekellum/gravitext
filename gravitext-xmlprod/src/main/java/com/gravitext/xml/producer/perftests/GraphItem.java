/*
 * Copyright (c) 2008-2010 David Kellum
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

import com.gravitext.util.FastRandom;

public final class GraphItem
{

    public GraphItem( FastRandom r )
    {
        int s = 0;
        _content = CHAR_DATA.substring( s = r.nextInt(101),
                                        s + r.nextInt(901));
        _score = r.nextInt();
        _value = r.nextInt();
        _name = CHAR_DATA.substring( s = r.nextInt(201), s + r.nextInt(21) );

        int count = r.nextInt(12);
        _list = new ArrayList<String>( count );
        for( int i = 0; i < count; ++i ) {
            _list.add( CHAR_DATA.substring( s = r.nextInt(201),
                                            s + r.nextInt(34) ) );
        }
    }

    public String getContent()
    {
        return _content;
    }

    public List<String> getList()
    {
        return _list;
    }

    public String getName()
    {
        return _name;
    }

    public int getScore()
    {
        return _score;
    }

    public int getValue()
    {
        return _value;
    }

    private String _content;
    private String _name;
    private int _score;
    private int _value;
    private List<String> _list;

    private static final String CHAR_DATA; // At least 1024 length

    static {
        StringBuilder b = new StringBuilder( 1024 + 256 );
        while( b.length() < 1024 ) {
            b.append(
           "Cérébrales here is some lengthy \"char & data\"   ");
        }
        CHAR_DATA = b.toString();
    }
}
