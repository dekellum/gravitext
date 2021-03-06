#!/usr/bin/env jruby
#.hashdot.profile += jruby-shortlived

#--
# Copyright (c) 2008-2013 David Kellum
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

require File.join( File.dirname( __FILE__ ), "setup" )

require 'gravitext-xmlprod'
require 'gravitext-xmlprod/extensions'

class TestTree < MiniTest::Unit::TestCase
  include Gravitext::XMLProd

  import 'com.gravitext.xml.tree.TreeUtils'
  import 'com.gravitext.xml.tree.SAXUtils'
  import 'com.gravitext.xml.tree.StAXUtils'
  import 'javax.xml.stream.XMLStreamException'

  TEST_XML = {}

  TEST_XML[ :basic ] = <<XML
<doc>
 <a>
  with &lt;>entities
 </a>
</doc>
XML

  TEST_XML[ :atts ] = <<XML
<doc xmlns="foo">
 <a att1="a1value"/>
 <b att2="a2value"/>
 <b att2="a2value.1"/>
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

  TEST_XML[ :namespace_xml_2 ] = <<XML
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <body>
  <p>hello</p>
 </body>
</html>
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
    assert_equal( xml.rstrip, root.to_xml, show_node( root ) )
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
    root = parse_tree( xml )
    assert_equal( 'text', first( root, :o1, :i1 ).characters )
    assert_equal( 'next', first( root, :o1, :i2 ).characters )

    assert(     first( root, :o2 ) )
    assert(     first( root, :o2, :o2, :i3 ) )

    assert_nil( first( root, :o2, :i1 ) )
    assert_nil( first( root, :o3 ) )
  end

  def test_find_namespace
    root = SAXUtils::saxParse( SAXUtils::saxInput( TEST_XML[ :namespace_1 ] ) )
    aelm = root.find( 'a' )
    assert( aelm, "found using WILDCARD_NS" )
    assert_equal( "bar", aelm.tag.namespace.nameIRI, "found the right s:a tag" )
  end

  def test_select_attribute
    root = parse_tree( TEST_XML[ :atts ] )
    # Note: Default NS (foo) is here irrelevent
    avals = root.select( 'b' ) { |e| e['att2'] }
    assert_equal( 2, avals.length, "element with att2 found" )

    att = Attribute.new( 'att2' )
    assert_equal( %w[ a2value a2value.1 ],
                  avals.map { |e| e[ att ] },
                  "Matching default NS attribute" )
  end

  def test_select_attribute_ns
    root = parse_tree( TEST_XML[ :namespace_2 ] )
    att = Attribute.new( 'att2', Namespace.new( 'bar' ) )
    # Note: NS prefix is irrelevant
    assert_equal( 1, root.select { |e| e[ att ] == 'a2value' }.length )
  end

  def test_recurse
    xml = <<XML
<doc>
 <o1 a='1'>
  <i1>text</i1>
  <i2>next</i2>
 </o1>
 <o1 a='2'/>
 <o1>
  <o2>
   <i1 a='3'>other</i1>
   <i3/>
  </o2>
 </o1>
</doc>
XML
    root = parse_tree( xml )

    assert_equal( 'text', root.find_r( 'i1' ).characters )
    assert_equal( 'next', root.find_r( 'i2' ).characters )
    assert_equal( 'other',
                  root.find_r( 'i1' ) { |e| e['a'] == '3' }.characters )

    assert_equal( %w[ 1 2 3 ],
                  root.select_r { |e| e['a'] }.map { |e| e['a'] } )
  end

  def test_invalid_xml_error
    assert_raises( XMLStreamException ) do
      parse_tree( "<doc><open></doc>" )
    end

    assert_raises( XMLStreamException ) do
      parse_tree( "" )
    end
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
