#--
# Copyright (c) 2012 David Kellum
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

require 'gravitext-xmlprod'

# Magic loader hook -> XMLService
require 'com/gravitext/xml/jruby/XML'

module Gravitext::XMLProd

  def parse_tree( input )
    XMLHelper.stax_parse_string( input )
  end

  module_function :parse_tree

  class Element

    # Shorthand for attribute accessed either by name (default
    # Namespace) or Attribute object.
    def []( name )
      attribute( name )
    end

    def characters
      XMLHelper.element_characters( self )
    end

    # Return Array of child elements matching tag and/or further
    # constrained where yielding to block returns true.
    def select( tag = nil )
      tag = Tag.new( tag, Tag.WILDCARD_NS ) unless tag.nil? || tag.is_a?( Tag )
      if block_given?
        children.select do |c|
          c.element? && ( tag.nil? || c.tag == tag ) && yield( c )
        end
      else
        children.select do |c|
          c.element? && ( c.tag == tag )
        end
      end
    end

    # Return first child element matching tag and/or where yielding to
    # block returns true. Without a block is equivalent to
    # first_element.
    def find( tag = nil )
      tag = Tag.new( tag, Tag.WILDCARD_NS ) unless tag.nil? || tag.is_a?( Tag )
      if block_given?
        children.find do |c|
          c.element? && ( tag.nil? || c.tag == tag ) && yield( c )
        end
      else
        first_element( tag )
      end
    end

    # Serialize self to String of XML, using specified Indentor and
    # QuoteMark to use for attributes.
    def to_xml( indentor = Indentor::COMPRESSED, qmark = QuoteMark::DOUBLE )
      XMLHelper.write_element( self, indentor, qmark )
    end

  end

end
