
require 'gravitext-util/version'
require 'java'

module Gravitext
  module Util
    require File.join( LIB_DIR, "gravitext-util-#{ VERSION }.jar" )

    import 'com.gravitext.util.FastRandom'
  end

  require 'gravitext-util/concurrent'
end
