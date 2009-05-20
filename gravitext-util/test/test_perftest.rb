#!/usr/bin/env jruby

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'test/unit'

require 'gravitext-util/perftest'

require 'java'

require 'rubygems'

require 'slf4j'
require 'logback' #Or, turn off logging: require 'slf4j/nop'

Logback.config_console
Logback.root.level = Logback::DEBUG

class TestPerfTest < Test::Unit::TestCase
  include Gravitext::PerfTest

  def test_listener
    factory = com.gravitext.perftest.tests.EmptyPerfTest.new
    harness = Harness.new( [ factory ] )
    harness.listener = LogListener.new( SLF4J[ 'TestPerfTest' ] )

    harness.warmup_exec_target = 0.25
    harness.warmup_total_target = 0.5
    harness.warmup_tolerance = 1.0
    harness.final_exec_target = 0.25
    harness.final_iterations = 2

    sum = harness.execute.first
    assert_same( factory, sum.factory )
    assert( sum.runs_executed > 0 )
    assert( sum.duration.seconds > 0.0 )
  end

end

