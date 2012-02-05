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

require File.join( File.dirname( __FILE__ ), "setup" )

require 'gravitext-util'
require 'json'

class TestUniMap < MiniTest::Unit::TestCase
  include Gravitext::HTMap

  TEST_KEYS = [ [ 'int',  Java::java.lang.Integer,      33 ],
                [ 'str',  Java::java.lang.String,       "string" ],
                [ 'cseq', Java::java.lang.CharSequence, "other" ],
                [ 'date', Java::java.util.Date,         Time.now ],
                [ 'dbl',  Java::java.lang.Double,       3.1415 ],
                [ 'flt',  Java::java.lang.Float,        1.5625 ], #exact
                [ 'list', Java::java.util.List,         [ 1, 2, 3 ] ],
                [ 'sub',  Java::com.gravitext.htmap.UniMap, UniMap.new ],
                [ 'any',  Java::java.lang.Object,       { :foo => 'bar' } ] ]

  WRONG_TYPE_VALUE = Java::java.lang.RuntimeException.new

  TEST_KEYS.each do |a|
    a << UniMap.create_key( a[0], a[1] )
  end

  UniMap.define_accessors

  LOG = RJack::SLF4J[ name ]

  ## Manual tests

  # Full integer test for demonstration
  def test_int
    c = UniMap.new
    assert_nil( c.int )
    c.int = 7
    assert_equal( 7, c.int )
    c.int = nil
    assert_nil( c.int )
    assert_raises( TypeError ) do
      c.int = WRONG_TYPE_VALUE
    end
  end

  # Java.util.Date comparison is tricky
  def test_date
    c = UniMap.new
    c.date = now = Time.now
    assert( c.date.equals( now ) )
  end

  # Define test_<key> for all not manually defined above
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
        assert_raises( TypeError ) do
          c.send( set, WRONG_TYPE_VALUE )
        end unless key.name == 'any'
        assert_nil( c.send( get ) )
      end
    end
  end

  def test_list_setters
    c = UniMap.new
    LOG.info( "UniMap setters defined: " +
              c.methods.sort.select { |m| m =~ /[^=]=$/ }.join( " " ) )
  end

  # Only tests below use this setup of a nested @sample structure.
  def setup
    @sample = UniMap.new
    TEST_KEYS.each do |k,_,v|
      @sample.send( "#{k}=", v ) if v
    end
    @sample.list = (1..2).map do |i|
      UniMap.new.tap do |m|
        m.int = i
        m.str = i.to_s
      end
    end

    @sample.sub = @sample.dup
  end

  def test_to_hash
    h = @sample.to_hash

    assert_equal( 33, h[ :int ], "to_hash should have symbol keys" )
    assert_nil( h[ 'int' ],      "to_hash should not have string keys" )
    assert_equal( Hash[ @sample.to_a ], h, "to_a, to_hash agree" )
  end

  def test_is_a_hash
    assert_kind_of( Hash, @sample )
    assert( @sample.is_a?( Hash ) )
  end

  def test_membership
    assert( @sample.has_key?( :int ) )
    assert( @sample.include?( 'int' ) )
    assert( @sample.key?( :str ) )
    assert( @sample.member?( :str ) )
  end

  def test_brackets
    assert_equal( 33, @sample[ 'int' ] )
    assert_equal( 33, @sample[ :int  ] )

    assert_equal( 66, @sample[ :int ] = 66 )
    assert_equal( 66, @sample[ :int ] )
  end

  def test_merge
    m = @sample.merge( :int => 66, 'str' => "new" )
    assert_equal( 33,       @sample.int )
    assert_equal( "string", @sample.str )
    assert_equal( 66,    m.int )
    assert_equal( "new", m.str )

    n = @sample.merge( m )
    assert_equal( 66,    n.int )
    assert_equal( "new", n.str )
  end

  def test_merge_bang
    @sample.merge!( :int => 66, :str => "new" )
    assert_equal( 66,    @sample.int )
    assert_equal( "new", @sample.str )
  end

  def test_json

    assert_equal( @sample.deep_hash.to_json, @sample.to_json )
    refute_match( /int1/, @sample.to_json )

    json = JSON.pretty_generate( @sample )
    pass

    LOG.debug { "JSON.pretty_generate:\n#{json}" }

    s = @sample.clone
    s.remove( :date ) #would otherwise roundtrip as string
    s.delete( :sub )  #also has a date

    assert_equal( s.deep_hash,
                  JSON.parse( s.to_json, :symbolize_names => true ) )

  end

end
