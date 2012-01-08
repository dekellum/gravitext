#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

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

require 'rubygems'

require 'minitest/unit'

require 'rjack-logback'
RJack::Logback.config_console

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'gravitext-util'

class TestUniMap < MiniTest::Unit::TestCase
  include Gravitext::HTMap

  TEST_KEYS = [ [ 'int_key',  Java::java.lang.Integer,      33 ],
                [ 'str_key',  Java::java.lang.String,       "string" ],
                [ 'cseq_key', Java::java.lang.CharSequence, "other" ],
                [ 'list_key', Java::java.util.List,         [ 1, 2, 3 ] ],
                [ 'date_key', Java::java.util.Date,         Time.now ],
                [ 'dbl_key',  Java::java.lang.Double,       3.1415 ],
                [ 'flt_key',  Java::java.lang.Float,        1.5625 ] ] #exact

  WRONG_TYPE_VALUE = Java::java.lang.RuntimeException.new

  TEST_KEYS.each do |a|
    a << UniMap.create_key( a[0], a[1] )
  end

  UniMap.define_accessors

  LOG = RJack::SLF4J[ name ]

  ## Manual tests

  # Full integer test for demonstration
  def test_int_key
    c = UniMap.new
    assert_nil( c.int_key )
    c.int_key = 7
    assert_equal( 7, c.int_key )
    c.int_key = nil
    assert_nil( c.int_key )
    assert_raises NameError, NativeException do
      c.int_key = WRONG_TYPE_VALUE
    end
  end

  # Java.util.Date comparison is tricky
  def test_date_key
    c = UniMap.new
    c.date_key = now = Time.now
    assert( c.date_key.equals( now ) )
  end

  # Define test_<type_key> for all not manually defined above
  TEST_KEYS.each do |name, vtype, test_value, key|
    get = name
    set = name + '='
    tmethod = "test_" + name
    unless method_defined?( tmethod )
      define_method( tmethod ) do
        c = UniMap.new
        assert_nil( c.send( get ) )
        assert_nil( c.send( set, test_value ) )
        assert_equal( test_value, c.send( get ) )
        assert_equal( test_value, c.get( key ) )
        assert_equal( test_value, c.send( set, nil ) )
        assert_nil( c.send( get ) )
        assert_raises NameError, NativeException do
          c.send( set, WRONG_TYPE_VALUE )
        end
        assert_nil( c.send( get ) )
      end
    end
  end

  def test_list_setters
    c = UniMap.new
    LOG.info( "UniMap setters defined: " +
              c.methods.sort.select { |m| m =~ /[^=]=$/ }.join( " " ) )
  end

end
