/*
 * Copyright (c) 2008-2010 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.xml.producer.perftests;

import java.io.IOException;
import java.nio.CharBuffer;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;
import com.gravitext.xml.producer.CharacterEncoder;

public class StringBufferEncodePerfTest
    extends OutputPerfTest
    implements TestFactory
{
    public String name()
    {
        return ( _useCharBuffer ? "Encoder-CharBuffer" : "Encoder-String" );
    }

    public TestRunnable createTestRunnable( final int seed )
    {
        return new Runner( seed );
    }

    private final class Runner implements TestRunnable
    {
        public Runner( int seed )
        {
            _rnd = new FastRandom( seed );
        }

        public int runIteration( int run ) throws IOException
        {
            final TestOutput out = new TestOutput();

            final CharacterEncoder encoder =
                new CharacterEncoder( out.getWriter() );

            final boolean useCharBuffer = _useCharBuffer;

            for( int t = _rnd.nextInt( 50 ) + 51; t > 0; --t ) {
                int length = ( ( _rnd.nextInt( 5 ) + 1 ) *
                               ( _rnd.nextInt( 10 ) + 1 ) *
                               ( _rnd.nextInt( 20 ) + 1 ) +
                               _rnd.nextInt( 40 ) );

                int offset = _rnd.nextInt( CHAR_DATA.length - length );

                CharSequence cs;
                if( useCharBuffer ) {
                    cs = CharBuffer.wrap( CHAR_DATA, offset, length );
                }
                else {
                    cs = new String( CHAR_DATA, offset, length );
                }

                encoder.encodeCharData( cs );
            }

            out.flush();

            if( _doVerbose ) out.print();

            return out.size();
        }

        private final FastRandom _rnd;
    }

    public void setVerbose( boolean doVerbose )
    {
        _doVerbose = doVerbose;
    }

    public void setUseCharBuffer( boolean useCharBuffer )
    {
        _useCharBuffer = useCharBuffer;
    }

    private boolean _doVerbose   = false;

    private boolean _useCharBuffer = true;

    private static final char[] CHAR_DATA; //10000 bytes minimum

    static {
        StringBuilder b = new StringBuilder( 11000 );
        FastRandom random = new FastRandom( 1 );
        while( b.length() < ( 10000 ) ) {
            if( random.nextInt(10) == 0 ) {
                b.append( "[ Cérébrales escaped \"char & data\"] ");
            }
            else {
                b.append( "otherwise clean text " );
            }

        }
        CHAR_DATA = b.toString().toCharArray();
    }

}
