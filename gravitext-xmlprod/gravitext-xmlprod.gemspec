# -*- ruby -*- encoding: utf-8 -*-

gem 'rjack-tarpit', '~> 2.0'
require 'rjack-tarpit/spec'

RJack::TarPit.specify do |s|
  require 'gravitext-xmlprod/version'

  s.version = Gravitext::XMLProd::VERSION

  s.add_developer( 'David Kellum', 'dek-oss@gravitext.com' )

  s.depend 'gravitext-util',        '~> 1.5.0'

  s.depend 'rjack-jdom',            '~> 1.1.0.0',   :dev
  s.depend 'minitest',              '~> 2.3',       :dev

  s.maven_strategy = :no_assembly
end
