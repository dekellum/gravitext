#--
# Copyright (c) 2007-2013 David Kellum
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

require 'gravitext-util/version'
require 'java'

module Gravitext::DateSupport

  # Alias for java.util.Date
  JDate   = Java::java.util.Date

  # One second in milliseconds
  SEC_TO_MS  =                1_000

  # One minute in milliseconds
  MIN_TO_MS  =           60 * 1_000

  # One hour in milliseconds
  HOUR_TO_MS =      60 * 60 * 1_000

  # One day in milliseconds
  DAY_TO_MS  = 24 * 60 * 60 * 1_000

  # Return TimeDelta given Numeric seconds
  def d_secs( v )
    TimeDelta.new( SEC_TO_MS * v )
  end

  # Return TimeDelta given Numeric minutes
  def d_mins( v )
    TimeDelta.new( MIN_TO_MS * v )
  end

  # Return TimeDelta gioven Numeric hours
  def d_hours( v )
    TimeDelta.new( HOUR_TO_MS * v )
  end

  # Return TimeDelta gioven Numeric days
  def d_days( v )
    TimeDelta.new(  DAY_TO_MS * v )
  end

  # A Calendar-unaware, immutable, positive or negative time duration
  # optimized for Java's Integer milliseconds resolution and offering
  # various convenience methods for conversion.
  class TimeDelta

    # Return milliseconds as initialized
    attr_reader :msecs

    # New TimeDelta given Numeric, positive or negative
    # milliseconds. Optimized for Integer msecs.
    def initialize( msecs )
      @msecs = msecs
    end

    # True if other is a TimeDelta with equal msecs.
    def ==( other )
      other.is_a?( TimeDelta ) && ( @msecs == other.msecs )
    end

    # Compare this with other TimeDelta.
    def <=>( other )
      @msecs <=> other.msecs
    end

    # Return a new TimeDelta representing the sum of this and other
    # TimeDelta
    def +( other )
      TimeDelta.new( @msecs + other.msecs )
    end

    # Return a new TimeDelta representing the difference of this and
    # other TimeDelta.
    def -( other )
      TimeDelta.new( @msecs - other.msecs )
    end

    # Return hash code.
    def hash
      @msecs.hash
    end

    # Return delta as Integer seconds.
    def to_i
      ( @msecs / SEC_TO_MS ).to_i
    end

    # Return delta as Float seconds (with precision as initialized.)
    def secs
      @msecs / SEC_TO_MS_F
    end

    alias to_f secs

    # Return delta as Rational seconds.
    def to_r
      Rational( @msecs, SEC_TO_MS )
    end

    # Return delta as Float minutes.
    def mins
      @msecs / MIN_TO_MS_F
    end

    # Return delta as Float hours.
    def hours
      @msecs / HOUR_TO_MS_F
    end

    # Return delta as Float days.
    def days
      @msecs / DAY_TO_MS_F
    end

    # Return String representation of this TimeDelta using
    # "+/-(hh:)(mm:)(ss.uuu)" notation (where hour and minute
    # components are only provided when non-zero.
    def to_s
      ms, neg = @msecs < 0 ? [ -@msecs, true ] : [ @msecs, false ]
      secs   = ( ms % MIN_TO_MS ) / SEC_TO_MS_F
      mmins  = ( ms / MIN_TO_MS ).to_i
      hours, mins = mmins / 60, mmins % 60

      pad = ( secs < 10.0 ) ? '0' : '';

      if hours > 0
        hours = -hours if neg
        "%+d:%02d:%s%g" % [ hours, mins, pad, secs ]
      elsif mins > 0
        mins = -mins if neg
        "%+d:%s%g" % [ mins, pad, secs ]
      else
        secs = -secs if neg
        "%+g" % [ secs ]
      end
    end

    private

    SEC_TO_MS_F  = SEC_TO_MS.to_f  #:nodoc:
    MIN_TO_MS_F  = MIN_TO_MS.to_f  #:nodoc:
    HOUR_TO_MS_F = HOUR_TO_MS.to_f #:nodoc:
    DAY_TO_MS_F  = DAY_TO_MS.to_f  #:nodoc:

  end

  # Extensions to java.util.Date for some basic math operations and
  # and conversions.
  class JDate

    # Return conversion to ruby core Time (with millisecond precision).
    def to_ruby
      ems = self.time
      Time.at( ems/1000, ( ems % 1000 ) * 1000 )
    end

    # Return the difference between self and other. If other is a
    # JDate or ruby core Time, returns a TimeDelta. If other is a
    # TimeDelta or Numeric (seconds), return a new JDate.
    def -( other )
      case( other )
      when JDate
        TimeDelta.new( self.time - other.time )
      when TimeDelta
        JDate.new( self.time - other.msecs )
      when Numeric
        JDate.new( self.time - ( other * 1000 ).to_i )
      when Time
        TimeDelta.new( self.time -
                       ( ( other.to_i * 1000 ) + other.usec / 1000 ) )
      else
        raise TypeError, "Can't subract #{other.class} from JDate"
      end
    end

    # Return a new JDate representing the sum of self and other
    # TimeDelta or Numeric (seconds).
    def +( other )
      case( other )
      when TimeDelta
        JDate.new( self.time + other.msecs )
      when Numeric
        JDate.new( self.time + ( other * 1000 ).to_i )
      else
        raise TypeError, "Can't add #{other.class} to JDate"
      end
    end

    # Return age of self as a TimeDelta, by difference between the
    # current time and self (with millisecond resolution.) Will be
    # positive if self is "in the past".
    def age
      TimeDelta.new( JSystem.current_time_millis - self.time )
    end

    JSystem = Java::java.lang.System #:nodoc:

  end

end

# Ruby core Time extension
class Time

  # Return self, already ruby.
  def to_ruby
    self
  end

end
