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

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'gravitext-xmltree'

class TestSax < Test::Unit::TestCase
  import 'com.gravitext.xml.tree.TreeUtils'
  import 'com.gravitext.xml.producer.Indentor'

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

  def test_sax
    TEST_XML.each do | name, xml |
      root = TreeUtils::saxParse( TreeUtils::saxInputSource( xml ) )
      assert_equal( xml.rstrip,
                    TreeUtils::produceString( root, Indentor::COMPRESSED ),
                    name.to_s + ":\n" + show_node( root )  )
    end
  end

  def test_stax
    TEST_XML.each do | name, xml |
      root = TreeUtils::staxParse( TreeUtils::staxSource( xml ) )
      assert_equal( xml.rstrip,
                    TreeUtils::produceString( root, Indentor::COMPRESSED ),
                    name.to_s + ":\n " + show_node( root )  )
    end
  end

  def show_node( n, d = 0, out = "" )
    if n.name
      out << ( ' ' * d + '<' + n.name + ' ' +
               n.namespace_declarations.to_a.map { |ns| 'ns:' + ns.nameIRI }.join( ' ' ) +
               ' ' + n.attributes.map { |av| av.attribute.name }.join( ' ' ) )
      n.children.each { |c| show_node( c, d+1, out ) }
      out << ( ' ' * d + '</' + n.name )
    else
      out << ( ' ' * d + '[' + n.characters.to_s + ']' )
    end
    out
  end

end
