#!/usr/bin/env jruby

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

# require 'test/unit'

require 'gravitext-util'

require 'gravitext-util/perftest'

require 'java'

#import 'com.gravitext.util.perftests.FastRandomPerfTest'
#tests = FastRandomPerfTest::Mode.values.map do |mode|
#  FastRandomPerfTest.new( mode )
#end

#tests.shift # remove first

#tests = [ com.gravitext.perftest.tests.EmptyPerfTest.new ]
#tests += tests

#harness = Gravitext::PerfTest::Harness.new( tests )
#puts( "Threads: #{harness.threads}" )

# harness.thread_count = 2
# harness.final_runs = 10000
#harness.execute


# FIXME: convert to test
