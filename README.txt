= Gravitext Project

* http://rubyforge.org/projects/gravitext
* http://github.com/dekellum/gravitext

== Description

The Gravitext Project provides a collection of core java components
packaged as gems for JRuby. The source tree contains the source for
several distinct gems (per rubyforge conventions), in independent
directories.

=== gravitext-util

A collection of core java utilities with ruby adapters for JRuby.  

* A concurrent (thread safety) testing facility for java JUnit,
  ruby Test::Unit, or other test harnesses.

* A concurrent performance testing facility for java/JRuby with simple
  test wiring in ruby.

* A Heterogeneous Type-safe Map implementation in java.

* A set of core java utility classes (resizable buffers, a stopwatch,
  SI unit formatting, fast random number generator), in support of the
  above and general use.

=== gravitext-xmlprod

* A fast and simple XML streaming production package.

== Source

=== RubyForge

 % git remote add rubyforge gitosis@rubyforge.org:gravitext.git

Create package under gravitext project:

    (rubyforge login)
    rubyforge create_package gravitext <package-name>

Release a gem (via Hoe/Rakefile): 

    VERSION=x.y.z jrake release publish_docs post_news

Upload docs to rubyforge:

    rsync -auP --exclude '*~' www/ dekellum@rubyforge.org:/var/www/gforge-projects/gravitext


=== github

 % git remote add origin git@github.com:dekellum/gravitext.git 

== License

Copyright (C) 2007-2009 David Kellum

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License.  You
may obtain a copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied.  See the License for the specific language governing
permissions and limitations under the License.
