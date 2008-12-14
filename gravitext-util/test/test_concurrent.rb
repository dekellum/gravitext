#!/usr/bin/env jruby

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'test/unit'

require 'gravitext-util/concurrent'

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
    tsum = Concurrent.execute_test( RTestFactory.new, 100 ) 
    csum = (1..100).inject { |sum,i| sum + i }
    assert_equal( csum, tsum )
  end

  class AssertTestFactory
    include Test::Unit::Assertions
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
      Thread.pass if ( run % 3 ).zero?
      assert_not_equal( 101, run, "run == #{run}" )
      1
    end
  end

  def test_unit_assert
    assert_raise( Test::Unit::AssertionFailedError ) do 
      Concurrent.execute_test( AssertTestFactory.new, 1000, 7 ) 
    end
  end
end
