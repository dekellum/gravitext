#--
# Copyright (c) 2008-2012 David Kellum
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

require 'gravitext-xmlprod/version'
require 'java'

require 'gravitext-util'

module Gravitext::XMLProd

  require File.join( LIB_DIR, "gravitext-xmlprod-#{ VERSION }.jar" )

  java_import 'com.gravitext.xml.producer.CharacterEncoder'
  java_import 'com.gravitext.xml.producer.Indentor'
  java_import 'com.gravitext.xml.producer.Attribute'
  java_import 'com.gravitext.xml.producer.Namespace'
  java_import 'com.gravitext.xml.producer.Tag'

  QuoteMark = CharacterEncoder::QuoteMark

  java_import 'com.gravitext.xml.tree.Characters'
  java_import 'com.gravitext.xml.tree.Element'
  java_import 'com.gravitext.xml.tree.Node'

end
