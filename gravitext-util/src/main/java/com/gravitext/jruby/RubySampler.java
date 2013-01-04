/*
 * Copyright (c) 2007-2013 David Kellum
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
import org.jruby.runtime.Arity;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

@JRubyClass( name="Gravitext::Util::RubySample" )
public final class RubySampler extends RubyObject
{
    public RubySampler( Ruby runtime, RubyClass klass )
    {
        super( runtime, klass );
    }

    @JRubyMethod(name="new", meta = true, rest = true)
    public static IRubyObject rbNew( ThreadContext context,
                                     IRubyObject klazz,
                                     IRubyObject[] args ) {

        RubySampler s = (RubySampler) ((RubyClass)klazz).allocate();
        s.init(context, args);
        return s;
    }

    void init( ThreadContext context, IRubyObject[] args ) {
        Arity.checkArgumentCount(context.getRuntime(), args, 2, 2);

        int length = (Integer) args[0].toJava( Integer.class );
        int seed   = (Integer) args[1].toJava( Integer.class );

        _testSample = new TextSampler( length, seed );
    }

    @JRubyMethod( name = "string_to_ruby" )
    public IRubyObject stringToRuby( ThreadContext tc )
    {
        return IOUtils.toRubyString( tc, _testSample.string() );
    }

    @JRubyMethod( name = "char_buffer_to_ruby" )
    public IRubyObject charBufferToRuby( ThreadContext tc )
    {
        return IOUtils.toRubyString( tc, _testSample.charBuffer() );
    }

    private TextSampler _testSample;
}
