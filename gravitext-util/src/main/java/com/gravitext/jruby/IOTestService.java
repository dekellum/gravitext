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
import org.jruby.RubyModule;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.BasicLibraryService;

public class IOTestService implements BasicLibraryService
{
    public boolean basicLoad( Ruby runtime )
    {
        RubyModule util = runtime.
                            defineModule( "Gravitext" ).
                            defineModuleUnder( "Util" );

        RubyClass sclass = util.defineClassUnder( "RubySampler",
                                                  runtime.getObject(),
                                                  RS_ALLOCATOR );

        sclass.defineAnnotatedMethods( RubySampler.class );
        RubyClass hclass = util.defineClassUnder( "RubySampleHelper",
                                                  runtime.getObject(),
                                                  ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR );

        hclass.defineAnnotatedMethods( RubySampleHelper.class );

        return true;
    }

    private static ObjectAllocator RS_ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate( Ruby runtime, RubyClass klazz )
        {
            return new RubySampler( runtime, klazz );
        }
    };

}
