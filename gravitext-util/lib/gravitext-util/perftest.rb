#--
# Copyright (c) 2007-2010 David Kellum
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

require 'gravitext-util'

require 'ostruct'
require 'java'

module Gravitext

  # Concurrent performance testing facility
  module PerfTest

    module CalcUtil #:nodoc: all

      NaN = Java::java.lang.Double::NaN

      def throughput_change( exec, prior = nil )
        if prior
          p = prior.mean_throughput
          ( exec.mean_throughput - p ) / p
        else
          NaN
        end
      end

      def latency_change( exec, prior = nil )
        if prior
          p = prior.mean_latency.seconds
          ( exec.mean_latency.seconds - p ) / p
        else
          NaN
        end
      end
    end

    # Concurrent performance testing harness with support for adaptive
    # warmup and comparison on multiple TestFactory instances.
    class Harness
      include CalcUtil
      import 'com.gravitext.concurrent.TestExecutor'
      import 'com.gravitext.util.Duration'

      # Number of threads to test with (default: available cores)
      attr_accessor :thread_count

      # Target duration of warmup comparison iterations in seconds (default:8s)
      attr_accessor :warmup_exec_target

      # Minimum warmup time in seconds for each test factory (default: 25s)
      attr_accessor :warmup_total_target

      # Secondary warmup requirement: consectutive iteration
      # throughputs within tollerance (default: 0.05)
      attr_accessor :warmup_tolerance

      # Number of final comparison iterations (default: 3)
      attr_accessor :final_iterations

      # Number of runs in final comparisons (default: computed from
      # warmup and final_exec_target)
      attr_accessor :final_runs

      # Target duration of final comparison iterations in seconds (default: 10s)
      attr_accessor :final_exec_target

      # Test executation progress is sent to listener, see PrintListener.
      attr_accessor :listener

      # Do per run timing for mean latency. (default: true)
      attr_accessor :do_per_run_timing

      # Maximum difference in counts for final run counts to be
      # alligned (default: 3.0)
      attr_accessor :max_align_ratio

      # Initialize given array of com.gravitext.concurrent.TestFactory
      # instances.
      def initialize( factories )
        @factories = factories
        @thread_count = Concurrent::available_cores

        @warmup_exec_target = 8.0
        @warmup_total_target = 25
        @warmup_tolerance = 0.05

        @final_iterations = 3
        @final_runs = nil
        @final_exec_target = 10.0

        @max_align_ratio = 3.0
        @do_per_run_timing = true

        @listener = PrintListener.new
      end

      def execute
        @listener.begin( self, @factories )

        finals = warmup

        run_counts = if @final_runs
                       Array.new( @factories.size, @final_runs )
                     else
                       counts = @factories.map do |factory|
                         exec = finals.detect { |e| e.factory == factory }
                         ( exec.mean_throughput * @final_exec_target ).to_i
                       end
                       align_counts( counts )
                     end

        results = execute_comparisons( run_counts, @final_iterations )
        sums = sum_results( results ) if @final_iterations > 1

        @listener.comparisons_end( self, sums )
        sums
      end

      def warmup

        @listener.warmups_begin( self )

        states = @factories.map do |factory|
          s = OpenStruct.new
          s.factory = factory
          s.prior = nil
          s.warm_time = 0.0
          s
        end

        first = true
        finals = []
        until states.empty? do

          @listener.warmup_next_series( self ) unless first

          states,done = states.partition do |s|

            # Cleanup before each run
            Java::java.lang.System::gc
            Java::java.lang.Thread::yield

            runs = if s.prior
                     ( @warmup_exec_target * s.prior.runs_executed ) /
                       s.prior.duration.seconds
                   else
                     1
                   end

            executor = create_executor( s.factory, runs.to_i )
            @listener.warmup_start_run( executor )
            executor.run_test
            @listener.warmup_complete_run( executor, s.prior )

            # Test throughput change, and increment warm_time
            s.warm_time += executor.duration.seconds
            tchange = throughput_change( executor, s.prior )
            s.prior = executor

            ( ( s.warm_time < @warmup_total_target ) ||
              ( tchange.abs > @warmup_tolerance ) )
          end
          finals += done.map { |s| s.prior }
          first = false
        end

        @listener.warmups_end( finals )
        finals
      end

      def align_counts( counts )
        mean = ( counts.inject { |sum,c| sum + c } ) / counts.size

        # Round to 2-significant digits
        f = 1
        ( f *= 10 ) while ( mean / f ) > 100
        mean = ( mean.to_f / f ).round * f

        if ( mean.to_f / counts.min ) > @max_align_ratio
          counts
        else
          Array.new( counts.size, mean )
        end
      end

      def execute_comparisons( run_counts, iterations )

        results = Array.new( @factories.size ) { [] }

        @listener.comparisons_begin( self, run_counts )

        iterations.times do |iteration|
          @listener.comparison_next_series( self ) unless iteration.zero?

          Java::java.lang.System::gc
          Java::java.lang.Thread::yield

          @factories.each_index do |f|
            executor = create_executor( @factories[f], run_counts[f] )
            @listener.comparison_start_run( executor )
            executor.run_test
            @listener.comparison_complete_run( executor, results[0].last )
            results[f] << executor
          end
        end

        results
      end

      def sum_results( results )
        sums = []

        results.each do |runs|
          sum = OpenStruct.new
          sum.factory            = runs.first.factory
          sum.runs_target        = 0
          sum.duration           = 0.0
          sum.result_sum         = 0
          sum.runs_executed      = 0
          sum.mean_throughput    = 0.0
          sum.mean_latency       = 0.0

          runs.each do |exec|
            sum.runs_target     += exec.runs_target
            sum.duration        += exec.duration.seconds
            sum.result_sum      += exec.result_sum
            sum.runs_executed   += exec.runs_executed
            sum.mean_throughput += exec.mean_throughput
            sum.mean_latency    += exec.mean_latency.seconds
          end

          sum.duration           = Duration.new( sum.duration )
          sum.mean_throughput   /= runs.size
          sum.mean_latency       = Duration.new( sum.mean_latency / runs.size )

          sums << sum
        end

        sums
      end

      def create_executor( factory, runs )
        executor = TestExecutor.new( factory, runs, @thread_count )
        executor.do_per_run_timing = @do_per_run_timing
        executor
      end

    end

    # Listen for various events from the Harness and print results to console
    class PrintListener
      include CalcUtil
      import 'com.gravitext.util.Metric'

      # Status is written via out << (default $stdout)
      def initialize( out = $stdout )
        @out = out
      end

      def begin( harness, factories )
        @out << "Concurrent testing: #{harness.thread_count} threads."
        new_line
        @nwidth = ( factories.map { |f| f.name.length } << 4 ).max
      end

      def warmups_begin( harness )
        @out << ( "Warmup min %gs (change tolerance: %g) per test:" %
                  [ harness.warmup_total_target, harness.warmup_tolerance ] )
        new_line
        print_header
      end

      def warmup_start_run( executor )
        print_result_start( executor )
      end

      def warmup_complete_run( executor, prior )
        print_result( executor, prior )
      end

      def warmup_next_series( harness )
        print_separator
      end

      def warmups_end( final_executors )
        new_line
      end

      def comparisons_begin( harness, run_counts )
        @out << ( "Comparison runs (%d iterations):" %
                  [ harness.final_iterations ] )
        new_line
        print_header
      end

      def comparison_next_series( harness )
        print_separator
      end

      def comparison_start_run( executor )
        print_result_start( executor )
      end

      def comparison_complete_run( executor, prior )
        print_result( executor, prior )
      end

      def comparisons_end( harness, executor_sums )
        print_separator( '=' )
        executor_sums.each_index do |s|
          print_result_start( executor_sums[s] )
          print_result( executor_sums[s], ( executor_sums.first unless s.zero? ) )
        end
      end

      def new_line
        @out << "\n"
      end

      def print_header
        @out << ( "%-#{@nwidth}s %-6s %-7s %-6s %8s %10s(%6s) %-9s (%6s)" %
                  [ "Test",
                    "Count",
                    "Time",
                    "R Sum",
                    "~R Value",
                    "Throughput",
                    "Change",
                    "~Latency",
                    "Change" ] )
        new_line
        print_separator( '=' )
      end

      def print_separator( char = '-' )
        @out << ( char * ( @nwidth + 69 ) )
        new_line
      end

      def print_result_start( exec, out = @out )
        out << ( "%-#{@nwidth}s %6s " %
                 [ exec.factory.name,
                   Metric::format( exec.runs_target ) ] )
      end

      def print_result( exec, prior = nil, out = @out )
        out << ( "%7s %6s %6s/r %6sr/s (%6s) %7s/r (%6s)" %
                 [ exec.duration,
                   Metric::format( exec.result_sum ),
                   Metric::format( exec.result_sum.to_f /
                                   exec.runs_executed ),
                   Metric::format( exec.mean_throughput ),
                   Metric::format_difference( throughput_change(exec,prior) ),
                   exec.mean_latency,
                   Metric::format_difference( latency_change(exec,prior) ) ] )
        new_line
      end

    end

    # Derivation of PrintListener for consise debug log output
    class LogListener < PrintListener

      # Send <<() to log.debug
      class LogWriter
        def initialize( log )
          @log = log
        end
        def <<( msg )
          @log.debug( msg )
        end
      end

      def initialize( logger )
        super( LogWriter.new( logger ) )
      end

      alias :orig_result_start :print_result_start
      def print_result_start( exec ); end

      # Print run start and result output on single log line
      def print_result( exec, prior = nil )
        line = ""
        orig_result_start( exec, line )
        super( exec, prior, line )
        @out << line
      end

      def new_line; end
      def print_separator( char = '-' ); end

    end

  end
end
