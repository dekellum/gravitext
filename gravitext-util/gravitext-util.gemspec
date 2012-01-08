# -*- ruby -*- encoding: utf-8 -*-

gem 'rjack-tarpit', '~> 2.0'
require 'rjack-tarpit/spec'

RJack::TarPit.specify do |s|
  require 'gravitext-util/version'

  s.version  = Gravitext::Util::VERSION

  s.add_developer( 'David Kellum', 'dek-oss@gravitext.com' )

  s.depend 'rjack-slf4j',           '>= 1.5.8',  '< 1.7'
  s.depend 'rjack-logback',         '>= 0.9.18', '< 2.0'

  s.depend 'minitest',              '~> 2.3',       :dev

  s.maven_strategy = :no_assembly
end
