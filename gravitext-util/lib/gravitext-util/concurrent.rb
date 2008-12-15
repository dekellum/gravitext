
require 'java'
require 'gravitext-util'

module Gravitext

  module Concurrent
    import 'com.gravitext.concurrent.TestExecutor'
    import 'com.gravitext.concurrent.TestFactory'
    import 'com.gravitext.concurrent.TestRunnable'
    
    def self.available_cores
      Java::java.lang.Runtime::runtime.available_processors
    end

    def self.execute_test_factory( test_factory, runs, 
                                   threads = available_cores )
      TestExecutor::run( test_factory, runs, threads )
    end

    def self.execute_runnable( test_runnable_class, runs, 
                               threads = available_cores )
      execute_test_factory( BasicTestFactory.new( test_runnable_class ), 
                            runs, threads )
    end

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
