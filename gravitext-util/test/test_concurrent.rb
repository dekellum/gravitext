#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2007-2013 David Kellum
#
# Licensed under the Apache License, Version 2.0 (the "License"); you
# may not use this file except in compliance with the License.  You may
# obtain a copy of the License at
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

require 'java'
require 'thread'

class TestConcurrent < MiniTest::Unit::TestCase
  include Gravitext

  class RTestFactory
    include Gravitext::Concurrent
    include TestFactory
    include TestRunnable
    def name
      self.class.name
    end

    def create_test_runnable( seed )
      self.class.new
    end

    def run_iteration( run )
      run
    end
  end

  def test_ruby_test_factory
    tsum = Concurrent.execute_test_factory( RTestFactory.new, 100 )
    csum = (1..100).inject { |sum,i| sum + i }
    assert_equal( csum, tsum )
  end

  class AssertTestRunnable
    include Gravitext::Concurrent::TestRunnable
    include MiniTest::Assertions

    def initialize( random )
      @random = random
    end

    def run_iteration( run )
      Thread.pass if @random.next_int(3).zero?
      refute_equal( 101, run, "run == #{run}" )
      1
    end
  end

  def test_assert_runnable
    assert_raises( MiniTest::Assertion ) do
      Concurrent.execute_runnable( AssertTestRunnable, 1000, 7 )
    end
  end

  def test_assert_block
    assert_raises( MiniTest::Assertion ) do
      Concurrent.execute_test( 1000, 7 ) do |run, random|
        Thread.pass if random.next_int(3).zero?
        refute_equal( 101, run, "run == #{run}" )
        1
      end
    end
  end

  def test_block_runnable
    tsum = Concurrent.execute_test( 1000, 3 ) do |run,random|
      1 + random.next_int( run )
    end
    assert( tsum >= 1000, "#{tsum} < 1000" )
  end

end
