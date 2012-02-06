/*
 * Copyright (c) 2012 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.gravitext.jruby;

import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.Java;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import com.gravitext.htmap.HTMap;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.UniMap;

@JRubyClass( name="Gravitext::HTMap::HTMapHelper" )
public class HTMapHelper
{
    @SuppressWarnings("unchecked")
    @JRubyMethod( name = "set_map",
                  meta = true,
                  required = 3,
                  argTypes = { HTMap.class,
                               Key.class } )
    public static IRubyObject setMap( ThreadContext tc,
                                      IRubyObject klazz,
                                      IRubyObject m,
                                      IRubyObject k,
                                      IRubyObject v )
    {
        final HTMap map = (HTMap) m.toJava( HTMap.class );
        final Key key = (Key) k.toJava( Key.class );

        if( v.isNil() ) {
            return javaToRuby( tc, map.remove( key ) );
        }

        if( v instanceof RubyString ) {
            RubyString val = (RubyString) v;
            if( key.valueType() == String.class ) {
                return javaToRuby( tc,
                    map.put( key, IOUtils.stringFromRuby( val ) ) );
            }
            else if( key.valueType() == CharSequence.class ) {
                return javaToRuby( tc,
                    map.put( key, IOUtils.fromRubyString( val ) ) );
            }
        }

        return javaToRuby( tc, map.put( key, v.toJava( key.valueType() ) ) );
    }

    @SuppressWarnings("unchecked")
    @JRubyMethod( name = "get_map",
                  meta = true,
                  required = 2,
                  argTypes = { HTMap.class,
                               Key.class } )
    public static IRubyObject getMap( ThreadContext tc,
                                      IRubyObject klazz,
                                      IRubyObject m,
                                      IRubyObject k )
    {
        final HTMap map = (HTMap) m.toJava( HTMap.class );
        final Key key = (Key) k.toJava( Key.class );

        return javaToRuby( tc, map.get( key ) );
    }

    @SuppressWarnings("unchecked")
    @JRubyMethod( name = "remove_map",
                  meta = true,
                  required = 2,
                  argTypes = { HTMap.class,
                               Key.class } )
    public static IRubyObject removeMap( ThreadContext tc,
                                         IRubyObject klazz,
                                         IRubyObject m,
                                         IRubyObject k )
    {
        final HTMap map = (HTMap) m.toJava( HTMap.class );
        final Key key = (Key) k.toJava( Key.class );

        return javaToRuby( tc, map.remove( key ) );
    }

    @SuppressWarnings("unchecked")
    @JRubyMethod( name = "unimap_each",
                  meta = true,
                  required = 1,
                  argTypes = { UniMap.class } )
    public static IRubyObject unimap_each( ThreadContext tc,
                                           IRubyObject klazz,
                                           IRubyObject m,
                                           Block block )
    {
        final UniMap map = (UniMap) m.toJava( UniMap.class );
        for( Key k : UniMap.KEY_SPACE.keys() ) {
            Object value = map.get( k );
            if( value != null ) {
                block.call( tc,
                            Java.getInstance( tc.getRuntime(), k ),
                            javaToRuby( tc, value ) );
            }
        }
        return m;
    }

    private static IRubyObject javaToRuby( ThreadContext tc,
                                           final Object value )
    {
        if( value == null ) {
            return tc.getRuntime().getNil();
        }

        if( value instanceof CharSequence ) {
            return IOUtils.toRubyString( tc, (CharSequence) value );
        }

        return JavaUtil.convertJavaToRuby( tc.getRuntime(), value );
    }
}
