#!/usr/bin/env jruby
#--
# Copyright (c) 2007-2010 David Kellum
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

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'test/unit'

require 'gravitext-util/perftest'

require 'java'

require 'rubygems'

require 'rjack-slf4j'
require 'rjack-logback' #Or, turn off logging: require 'rjack-slf4j/nop'

RJack::Logback.config_console( :level => RJack::Logback::DEBUG )

class TestPerfTest < Test::Unit::TestCase
  include Gravitext::PerfTest

  def test_listener
    factory = com.gravitext.perftest.tests.EmptyPerfTest.new
    harness = Harness.new( [ factory ] )
    harness.listener = LogListener.new( RJack::SLF4J[ 'TestPerfTest' ] )

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
