#--
# Copyright (c) 2007-2012 David Kellum
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

require 'gravitext-util/version'

# Magic loader hook -> HTMapService
require 'com/gravitext/jruby/HTMap'

module Gravitext::HTMap

  import 'com.gravitext.htmap.Key'
  import 'com.gravitext.htmap.UniMap'

  # Extension to com.gravitext.htmap.UniMap providing convenience
  # methods, including ruby accessors for keys registered in
  # UniMap::KEY_SPACE
  class UniMap

    class << self

      # Create a new key in UniMap::KEY_SPACE. Useful for ruby-only keys
      # or for keys passed as parameters to Java.
      # ==== Parameters
      # :name<~to_s>:: New key name (lower_case by convention)
      # :vtype<~java_class>:: Java class value type (default: java.lang.Object)
      # ==== Returns
      # com.gravitext.htmap.Key
      def create_key( name, vtype = Java::java.lang.Object )
        KEY_SPACE.create_generic( name, vtype.java_class )
      end

      # Define accessors for each key currently in the KEY_SPACE. To
      # define accessors for keys defined in java, statically reference
      # the containing class before calling this method. As Ruby's
      # define_method is not likely thread safe, invoke during
      # initialization in advance of starting threaded execution. May
      # be called multiple times.
      def define_accessors

        klist = []
        khash = {}
        KEY_SPACE.keys.each do |key|

          klist[ key.id ] = key.name.to_sym

          khash[ key.name ] = key
          khash[ key.name.to_sym ] = key

          getter = key.name.downcase
          unless method_defined?( getter )
            define_method( getter ) { get( key ) }
          end

          setter = getter + '='
          unless method_defined?( setter )
            define_method( setter ) { |value| set( key, value ) }
          end

        end

        @key_list = klist.freeze # key names array indexed by key.id
        @key_hash = khash.freeze # Hash of symbols,strings to Key
      end

      # Return cached symbol for the specified key (8bit optimized).
      # Must have define_accessors for the key first.
      def key_to_symbol( key )
        @key_list.at( key.id )
      end

      # Return Key for specified string name or symbol (8bit optimized)
      # Must have define_accessors for the key first.
      def str_to_key( str )
        @key_hash[ str ]
      end

      def deep_hash( m )
        case m
        when UniMap
          m.inject( {} ) do |o, (k, v)|
            o[ key_to_symbol( k ) ] = deep_hash( v )
            o
          end
        when Array
          m.map { |v| deep_hash( v ) }
        else
          m
        end
      end

    end

    def set( key, value )
      key = UniMap.str_to_key( key ) unless key.is_a?( Key )
      HTMapHelper.set_map( self, key, value )
    end

    def get( key )
      key = UniMap.str_to_key( key ) unless key.is_a?( Key )
      HTMapHelper.get_map( self, key )
    end

    def remove( key )
      key = UniMap.str_to_key( key ) unless key.is_a?( Key )
      HTMapHelper.remove_map( self, key )
    end

    alias :delete :remove

    def [](key)
      get( key )
    end

    def []=(key, val)
      set( key, val )
    end

    # Calls block with Key,value for each non-nil value
    def each( &block )
      HTMapHelper.unimap_each( self, &block )
    end

    def to_a
      map { |key,val| [ UniMap.key_to_symbol( key ), val ] }
    end

    # Returns self as Hash, with symbol names for keys
    def to_hash
      h = {}
      each do |key,val|
        h[ UniMap.key_to_symbol( key ) ] = val
      end
      h

      # FIXME: Drop? HTMapHelper.unimap_to_hash( self )
      # Hash[ to_a ]
    end

    # Like to_hash but converts all nested UniMaps to hashes as well.
    def deep_hash
      UniMap.deep_hash( self )
    end

    # Merge other (Hash or UniMap) and return new UniMap. Any nil
    # values from other will results in removing the associated key.
    def merge( other )
      clone.merge!( other )
    end

    # Merge other (Hash or UniMap) values to self. Any nil values from
    # other will results in removing key from self.
    def merge!( other )
      other.each do |k,v|
        set( k, v )
      end
      self
    end

    def to_json(*args)
      to_hash.to_json(*args)
    end

    end

  end
end
