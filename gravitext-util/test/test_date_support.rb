#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2007-2013 David Kellum
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

require 'gravitext-util/date_support'

class TestDateSupport < MiniTest::Unit::TestCase
  include Gravitext::DateSupport

  def test_math
    now = JDate.new
    prior = ( now - ( d_mins( 20 ) + d_secs( 10.001 ) ) )
    assert_equal( prior, now - ( 20 * 60.0 + 10.001 ) )
    assert_kind_of( JDate, prior )
    assert_equal( now, prior + ( d_mins( 20 ) + d_secs( 10.001 ) ) )
    assert_equal( now, prior + ( 20 * 60.0 + 10.001 ) )
    assert_equal( ( d_mins( 20 ) + d_secs( 10.001 ) ), now - prior )
  end

  def test_ruby_math
    now = JDate.new
    assert_equal( 0.0, ( now - now.to_ruby ).secs )
  end

  def test_age
    prior = JDate.new - d_mins( 1 )
    assert_in_delta( 60.0, prior.age.secs, 1.0 )
  end

  def test_to_ruby
    now = JDate.new.to_ruby
    assert_kind_of( Time, now )
    assert_same( now, now.to_ruby )
    assert_in_delta( 0.0, Time.now - now, 0.01 )
  end

  def test_seconds
    assert_equal(  0.001, TimeDelta.new(     1 ).secs )
    assert_equal(  0.9,   TimeDelta.new(   900 ).secs )
    assert_equal( -9.98,  TimeDelta.new( -9980 ).secs )
    assert_equal( 70.9,   TimeDelta.new( 70900 ).secs )

    assert_equal( -0.9, d_secs( -0.9 ).secs )
    assert_equal( 70.9, d_secs( 70.9 ).secs )
  end

  def test_minutes
    assert_equal(  0.015, TimeDelta.new(   900 ).mins )
    assert_equal( -0.15,  TimeDelta.new( -9000 ).mins )
    assert_equal(  1.10,  TimeDelta.new( 66000 ).mins )

    assert_equal( -0.15, d_mins( -0.15  ).mins )
    assert_equal(  1.10, d_mins(  1.10  ).mins )
  end

  def test_hours
    assert_equal( 0.25, TimeDelta.new(  15 * 60 * 1_000 ).hours )
    assert_equal( -1.5, TimeDelta.new( -90 * 60 * 1_000 ).hours )

    assert_equal( 0.25, d_hours( 0.25 ).hours )
    assert_equal( -1.5, d_hours( -1.5 ).hours )
  end

  def test_seconds_to_s
    assert_equal( "+0.001", TimeDelta.new(    1 ).to_s )
    assert_equal( "+0.9",   TimeDelta.new(  900 ).to_s )
    assert_equal( "+0.999", TimeDelta.new(  999 ).to_s )
    assert_equal( "+1.9",   TimeDelta.new( 1900 ).to_s )
    assert_equal( "+9.98",  TimeDelta.new( 9980 ).to_s )
  end

  def test_minutes_to_s
    assert_equal( "+1:10.9", TimeDelta.new( 70900 ).to_s )
  end

  def test_hours_to_s
    assert_equal(  "+1:00:00.953",
                  TimeDelta.new(  1 * 60 * 60 * 1000 +  953 ).to_s )
    assert_equal( "+99:14:01.001",
                  TimeDelta.new( 99 * 60 * 60 * 1000 +
                                 14 * 60 * 1000 + 1001 ).to_s )
  end

  def test_seconds_neg_to_s
    assert_equal( "-0.001", TimeDelta.new(    -1 ).to_s )
    assert_equal( "-0.9",   TimeDelta.new(  -900 ).to_s )
    assert_equal( "-0.999", TimeDelta.new(  -999 ).to_s )
    assert_equal( "-1.9",   TimeDelta.new( -1900 ).to_s )
    assert_equal( "-9.98",  TimeDelta.new( -9980 ).to_s )
  end

  def test_minute_neg_to_s
    assert_equal( "-1:10.9", TimeDelta.new( -70900 ).to_s )
  end

  def test_hours_neg_to_s
    assert_equal(  "-1:00:00.953",
                  TimeDelta.new(  -1 * 60 * 60 * 1000 +  -953 ).to_s )
    assert_equal( "-99:14:01.001",
                  TimeDelta.new( -99 * 60 * 60 * 1000 +
                                 -14 * 60 * 1000 + -1001 ).to_s )
  end

end
