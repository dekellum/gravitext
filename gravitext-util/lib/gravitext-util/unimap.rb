#--
# Copyright (C) 2008-2009 David Kellum
#
# Licensed under the Apache License, Version 2.0 (the "License"); you
# may not use this file except in compliance with the License.  You
# may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.  See the License for the specific language governing
# permissions and limitations under the License.
#++

module Gravitext

  module HTMap

    import 'com.gravitext.htmap.UniMap'

    # Extension to com.gravitext.htmap.UniMap providing convenience
    # methods, including ruby accessors for keys registered in
    # UniMap::KEY_SPACE
    class UniMap

      # Types requiring explicit construction on set
      EXPLICIT_CTOR_TYPES = [ Java::java.lang.Integer,
                              Java::java.lang.Double,
                              Java::java.lang.Float ]

      # Create a new key in UniMap::KEY_SPACE. Useful for ruby-only keys
      # or for keys passed as parameters to Java.
      # ==== Parameters
      # :name<~to_s>:: New key name (lower_case by convention)
      # :vtype<~java_class>:: Java class value type (default: java.lang.Object)
      # ==== Returns
      # com.gravitext.htmap.Key
      def self.create_key( name, vtype = Java::java.lang.Object )
        KEY_SPACE.create_generic( name, vtype.java_class )
      end

      # Define accessors for each key currently in the KEY_SPACE. To
      # define accessors for keys defined in java, statically reference
      # the containing class before calling this method. As Ruby's
      # define_method is not likely thread safe, invoke during
      # initialization in advance of starting threaded execution. May
      # be called multiple times.
      def self.define_accessors
        KEY_SPACE.keys.each do |key|

          getter = key.name.downcase
          unless method_defined?( getter )
            define_method( getter ) { get( key ) }
          end

          setter = getter + '='
          unless method_defined?( setter )
            vtype = key.value_type
            ctype = EXPLICIT_CTOR_TYPES.find { |ct| vtype == ct.java_class }
            if ctype
              define_method( setter ) do |value|
                set( key, ( ctype.new( value ) if value ) )
              end
            else
              define_method( setter ) do |value|
                set( key, value )
              end
            end
          end

        end
      end

    end

  end
end
