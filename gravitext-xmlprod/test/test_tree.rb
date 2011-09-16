#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2008-2011 David Kellum
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
require 'minitest/unit'

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'gravitext-xmlprod'

class TestTree < MiniTest::Unit::TestCase
  import 'com.gravitext.xml.tree.TreeUtils'
  import 'com.gravitext.xml.tree.SAXUtils'
  import 'com.gravitext.xml.tree.StAXUtils'
  import 'com.gravitext.xml.producer.Indentor'
  import 'com.gravitext.xml.producer.Tag'

  TEST_XML = {}

  TEST_XML[ :basic ] = <<XML
<doc>
 <a>
  with &lt;>entities
 </a>
</doc>
XML

  TEST_XML[ :atts ] = <<XML
<doc>
 <a att1="a1value"/>
 <b att2="a2value"/>
</doc>
XML

  TEST_XML[ :namespace_1 ] = <<XML
<doc xmlns="foo" xmlns:s="bar" att1="a1value">
 <s:a s:att2="a2value"/>
</doc>
XML

  TEST_XML[ :namespace_2 ] = <<XML
<doc>
 <s:e xmlns:s="bar" s:att1="a1value"/>
 <s:e xmlns:s="bar" s:att2="a2value"/>
</doc>
XML

  TEST_XML[ :namespace_3 ] = <<XML
<doc xmlns="top">
 <o:outer xmlns:o="out" o:att1="a1value">
  <inner o:att1="a2value"/>
 </o:outer>
</doc>
XML

  TEST_XML[ :namespace_4 ] = <<XML
<doc xmlns="top">
 <outer xmlns="out" att1="a1value">
  <i:inner xmlns:i="top" att1="a2value"/>
 </outer>
</doc>
XML

  TEST_XML[ :namespace_5 ] = <<XML
<doc xmlns:s="ns" a1="v1" s:a2="v2"/>
XML

  TEST_XML[ :namespace_xml ] = <<XML
<doc>
 <content type="html" xml:lang="en" xml:base="http://www.huffingtonpost.com/thenewswire"/>
</doc>
XML

  TEST_XML.each do | name, xml |
    define_method( "test_sax_#{name}" ) do
      assert_xml( xml, SAXUtils::saxParse( SAXUtils::saxInput( xml ) ) )
    end
    define_method( "test_stax_#{name}" ) do
      assert_xml( xml, StAXUtils::staxParse( StAXUtils::staxInput( xml ) ) )
    end
  end

  def assert_xml( xml, root )
    assert_equal( xml.rstrip,
                  TreeUtils::produceString( root, Indentor::COMPRESSED ),
                  show_node( root ) )
  end

  def test_find
    xml = <<XML
<doc>
 <o1>
  <i1>text</i1>
  <i2>next</i2>
 </o1>
 <o1/>
 <o2>
  <o2>
   <i3/>
  </o2>
 </o2>
</doc>
XML
    root = StAXUtils::staxParse( StAXUtils::staxInput( xml ) )
    assert_equal( 'text', first( root, :o1, :i1 ).characters )
    assert_equal( 'next', first( root, :o1, :i2 ).characters )

    assert(     first( root, :o2 ) )
    assert(     first( root, :o2, :o2, :i3 ) )

    assert_nil( first( root, :o2, :i1 ) )
    assert_nil( first( root, :o3 ) )
  end

  def first( root, *tags )
    root.first_element( *( tags.map { |s| Tag.new( s.to_s ) } ) )
  end

  def show_node( n, d = 0, out = "" )
    if n.asElement
      out << ( ' ' * d + '<' + n.name + ' ' +
               Array( n.namespace_declarations ).map { |ns|
                 'ns:%s=%s' % [ ns.prefix.to_s, ns.nameIRI ] }.join( ' ' ) +
               ' ' + n.attributes.map { |av| av.attribute.name }.join( ' ' ) )
      n.children.each { |c| show_node( c, d+1, out ) }
      out << ( ' ' * d + '</' + n.name )
    else
      out << ( ' ' * d + '[' + n.characters.to_s + ']' )
    end
    out
  end

end
