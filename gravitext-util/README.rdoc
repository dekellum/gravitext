= gravitext-util

* http://gravitext.rubyforge.org
* http://gravitext.com/oss/gravitext-util
* http://github.com/dekellum/gravitext

== Description

A collection of core java utilities with ruby adapters for JRuby.

* A concurrent (thread safety) testing facility for java JUnit,
  ruby Test::Unit, or other test harnesses.

* A concurrent performance testing facility for java/JRuby with simple
  test wiring in ruby.

* A Heterogeneous Type-safe Map implementation in java with
  dynamically generated ruby accessors.

* A set of core java utility classes (resizable buffers, a stopwatch,
  SI unit formatting, fast random number generator), in support of the
  above and of general utility.

== Dependencies

* Java 1.5+
* JRuby 1.1.6+
* rjack-slf4j[http://rjack.rubyforge.org/slf4j] and
  rjack-logback[http://rjack.rubyforge.org/logback] gems for testing.

== License

Copyright (c) 2007-2009 David Kellum

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License.  You
may obtain a copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied.  See the License for the specific language governing
permissions and limitations under the License.
