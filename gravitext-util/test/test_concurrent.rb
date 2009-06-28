#!/usr/bin/env jruby

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'test/unit'

require 'gravitext-util'

require 'java'
require 'thread'

class TestConcurrent < Test::Unit::TestCase
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
    include Test::Unit::Assertions

    def initialize( random )
      @random = random
    end

    def run_iteration( run )
      Thread.pass if @random.next_int(3).zero?
      assert_not_equal( 101, run, "run == #{run}" )
      1
    end
  end

  def test_assert_runnable
    assert_raise( Test::Unit::AssertionFailedError ) do
      Concurrent.execute_runnable( AssertTestRunnable, 1000, 7 )
    end
  end

  def test_assert_block
    assert_raise( Test::Unit::AssertionFailedError ) do
      Concurrent.execute_test( 1000, 7 ) do |run, random|
        Thread.pass if random.next_int(3).zero?
        assert_not_equal( 101, run, "run == #{run}" )
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
