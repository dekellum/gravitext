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

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

@JRubyClass( name="Gravitext::Util::RubySample" )
public final class RubySampleHelper extends RubyObject
{
    public RubySampleHelper( Ruby runtime, RubyClass klass )
    {
        super( runtime, klass );
    }

    @JRubyMethod( name = "string_to_ruby",
                  meta = true,
                  required = 1,
                  argTypes = { TextSampler.class } )
    public static IRubyObject stringToRuby( ThreadContext tc,
                                            IRubyObject klazz,
                                            IRubyObject ts )
    {
        return IOUtils.toRubyString( tc,
            ( (TextSampler) ts.toJava( TextSampler.class ) ).string() );
    }

    @JRubyMethod( name = "char_buffer_to_ruby",
                  meta = true,
                  required = 1,
                  argTypes = { TextSampler.class } )
    public static IRubyObject charBufferToRuby( ThreadContext tc,
                                                IRubyObject klazz,
                                                IRubyObject ts )
    {
        return IOUtils.toRubyString( tc,
            ( (TextSampler) ts.toJava( TextSampler.class ) ).charBuffer() );
    }

}
