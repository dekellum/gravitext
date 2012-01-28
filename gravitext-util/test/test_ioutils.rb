#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2007-2012 David Kellum
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

# Magic loader hook -> IOTestService
require 'com/gravitext/jruby/IOTest'

class TestIOUtils < MiniTest::Unit::TestCase
  include Gravitext::Util

  import 'com.gravitext.jruby.TextSampler'

  def test_text_sampler
    ts = TextSampler.new( 300, 2 )

    assert_instance_of( String, ts.string )
    # IF to UTF-8 bytes, will be longer than 300
    assert_operator( ts.string.length, :>, 300 )

    assert_instance_of( String, ts.string_sequence )
    assert_operator( ts.string_sequence.length, :>, 300 )

    if JRUBY_VERSION =~ /^1.7/
      assert_instance_of( String, ts.char_buffer )
      assert_instance_of( String, ts.char_buffer_sequence )
    else
      # In Jruby < 1.7, CharBuffer isn't converted to String until
      # to_s
      assert_equal( 'java.nio.HeapCharBuffer',
                    ts.char_buffer.java_class.name )
      assert_equal( 'java.nio.HeapCharBuffer',
                    ts.char_buffer_sequence.java_class.name )
    end
    assert_operator( ts.char_buffer.to_s.length, :>, 300 )
    assert_operator( ts.char_buffer_sequence.to_s.length, :>, 300 )

    assert_operator( ts.byte_list.to_s.length, :>, 300 )

    s = String.from_java_bytes( ts.bytes )
    assert_instance_of( String, s )
    assert_operator( s.length, :>, 300 )
  end

  def test_ruby_sampler
    rs = RubySampler.new( 300, 2 )
    assert_instance_of( String, rs.char_buffer_to_ruby )
    assert_operator( rs.char_buffer_to_ruby.length, :>, 300 )

    assert_instance_of( String, rs.string_to_ruby )
    assert_operator( rs.string_to_ruby.length, :>, 300 )
  end

  def test_ruby_sample_helper
    ts = TextSampler.new( 300, 2 )
    rs = RubySampleHelper

    assert_instance_of( String, rs.char_buffer_to_ruby( ts ) )
    assert_operator( rs.char_buffer_to_ruby( ts ).length, :>, 300 )

    assert_instance_of( String, rs.string_to_ruby( ts ) )
    assert_operator( rs.string_to_ruby( ts ).length, :>, 300 )
  end

end
