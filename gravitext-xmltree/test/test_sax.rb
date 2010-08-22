#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2010 David Kellum
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

require 'rubygems'

require 'test/unit'

#require 'rjack-logback'
#RJack::Logback.config_console

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'gravitext-xmltree'

class TestSax < Test::Unit::TestCase
  import "com.gravitext.xml.tree.TreeUtils"

  def test_basic
    xml = <<XML
<doc>
 <a>
  with white space
 </a>
</doc>
XML
    #show_tree xml
    assert_rt xml
  end

  def test_atts
    xml = <<XML
<doc>
 <a att1="a1value"/>&lt;
 <b att2="a2value"/>&gt;
</doc>
XML
    assert_rt xml
  end

  def test_ns_1
    xml = <<XML
<doc xmlns="foo" xmlns:s="bar" att1="a1value">
 <s:a s:att2="a2value"/>
</doc>
XML
    assert_rt xml
  end

  def test_ns_2
    xml = <<XML
<doc>
 <s:e xmlns:s="bar" s:att1="a1value"/>
 <s:e xmlns:s="bar" s:att2="a2value"/>
</doc>
XML
    show_tree xml
    assert_rt xml
  end

  def assert_rt( input )
    rt = TreeUtils::roundTripSTAX( input )
    assert_equal( input.rstrip, rt )
  end

  def show_tree( input )
    node = TreeUtils::staxParse( TreeUtils::staxSource( input ) )
    show_node( node )
  end

  def show_node( n, d = 0 )
    if n.name
      puts( ' ' * d + '<' + n.name + ' ' +
            n.namespace_declarations.to_a.map { |ns| 'ns:' + ns.nameIRI }.join( ' ' ) +
            ' ' + n.attributes.map { |av| av.attribute.name }.join( ' ' ) )
      n.children.each { |c| show_node( c, d+1 ) }
      puts( ' ' * d + '</' + n.name )
    else
      puts( ' ' * d + '[' + n.characters.to_s + ']' )
    end
  end

end
