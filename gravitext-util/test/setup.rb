require 'rubygems'
require 'bundler/setup'

require 'minitest/unit'
require 'minitest/autorun'

require 'rjack-logback'

module TestSetup
  include RJack
  Logback.config_console( :stderr => true, :thread => true )

  if ( ARGV & %w[ -v --verbose --debug ] ).empty?
    Logback.root.level = Logback::WARN
  else
    Logback.root.level = Logback::DEBUG
  end

  ARGV.delete( '--debug' )
end
