
require 'java'
require 'gravitext-util'

module Gravitext

  # Provides a concurrent testing facility
  module Concurrent
    import 'com.gravitext.concurrent.TestExecutor'
    import 'com.gravitext.concurrent.TestFactory'
    import 'com.gravitext.concurrent.TestRunnable'

    # Returns the number of available processor cores on the host.
    def self.available_cores
      Java::java.lang.Runtime::runtime.available_processors
    end

    # Run TestRunnable instances created from (TestFactory)
    # test_factory concurrently with threads (one TestRunnable
    # per thread.).  The first of any Exceptions raised in a test
    # thread will be re-raised in the calling thread. Returns sum of
    # runIteration return counts.
    def self.execute_test_factory( test_factory, runs, 
                                   threads = available_cores )
      TestExecutor::run( test_factory, runs, threads )
    end

    # Run test_runnable_class instances concurrently in threads. The
    # test_runnable_class should take a instance of FastRandom in its
    # initialize(). The first of any Exceptions raised in a test
    # thread will be re-raised in the calling thread. Returns sum of
    # runIteration return counts.
    def self.execute_runnable( test_runnable_class, runs, 
                               threads = available_cores )
      TestExecutor::run( BasicTestFactory.new( test_runnable_class ), 
                         runs, threads )
    end

    # Run block concurrently in the specified number of threads. The
    # first of any Exceptions raised in block will be re-raised in the
    # calling thread. Returns sum of runIteration return counts.
    #
    # :call-seq:
    #   execute_test(runs,threads = available_cores) { |run,random| ... } -> Integer
    def self.execute_test( runs, threads = available_cores, &block )
      TestExecutor::run( BlockTestFactory.new( block ), runs, threads )
    end

    class BlockTestFactory
      include TestFactory

      attr_accessor :name
      
      def initialize( proc = nil, &block )
        @name = 'BlockTestFactory'
        @block = proc || block
      end

      def create_test_runnable( seed )
        BlockTestRunnable.new( seed, @block )
      end
    end

    class BlockTestRunnable
      include TestRunnable

      def initialize( seed, block )
        @block = block
        @random = Gravitext::Util::FastRandom.new( seed )
      end

      def run_iteration( run )
        @block.call( run, @random )
      end
    end

    class BasicTestFactory
      include TestFactory

      def initialize( test_class )
        @test_class = test_class
      end

      def name
        @test_class.name
      end

      def create_test_runnable( seed )
        @test_class.new( Gravitext::Util::FastRandom.new( seed ) )
      end
    end

  end

end
