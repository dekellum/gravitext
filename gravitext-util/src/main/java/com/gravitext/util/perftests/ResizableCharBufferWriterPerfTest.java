package com.gravitext.util.perftests;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;
import com.gravitext.util.FastRandom;
import com.gravitext.util.ResizableCharBuffer;
import com.gravitext.util.ResizableCharBufferWriter;

public class ResizableCharBufferWriterPerfTest implements TestFactory
{
    public static final Class<?>[] TEST_CLASSES = new Class<?>[] {
        StringWriter.class,
        CharArrayWriter.class,
        ResizableCharBufferWriter.class
    };

    public ResizableCharBufferWriterPerfTest( Class<?> testClass )
    {
        _testClass = testClass;
    }

    public String name()
    {
        return _testClass.getSimpleName();
    }

    public TestRunnable createTestRunnable( int seed )
    {
        if( _testClass == StringWriter.class ) {
            return new TestRunnableBase( seed ) {
                public int runIteration( int run ) throws IOException
                {
                    final StringWriter writer
                        = new StringWriter( INITIAL_SIZE );
                    writeTestData( writer );
                    return writer.toString().length();
                }

            };
        }

        if( _testClass == CharArrayWriter.class ) {
            return new TestRunnableBase( seed ) {
                public int runIteration( int run ) throws IOException
                {
                    final CharArrayWriter writer
                        = new CharArrayWriter( INITIAL_SIZE );
                    writeTestData( writer );
                    return writer.toCharArray().length;
                }
            };

        }

        if( _testClass == ResizableCharBufferWriter.class ) {
            return new TestRunnableBase( seed ) {
                public int runIteration( int run ) throws IOException
                {
                    final ResizableCharBuffer buffer =
                        new ResizableCharBuffer( INITIAL_SIZE );
                    final ResizableCharBufferWriter writer =
                        new ResizableCharBufferWriter( buffer );
                    writeTestData( writer );
                    return buffer.flipAsCharBuffer().remaining();
                }
            };

        }

        throw new RuntimeException();
    }

    private static abstract class TestRunnableBase implements TestRunnable
    {
        TestRunnableBase( int seed )
        {
            _random = new FastRandom( seed );
        }

        final void writeTestData( final Writer writer ) throws IOException
        {
            for( int i = 0; i < 200; ++i ) {
                switch( _random.nextInt( 3 ) ) {
                case 0: writer.write( "a string " ); break;
                case 1: writer.write( 'x' ); break;
                case 2:
                    int l = _random.nextInt( WRITE_CHARS.length );
                    int o = _random.nextInt( WRITE_CHARS.length - l );
                    writer.write( WRITE_CHARS, o, l );

                }
            }
            writer.flush();
        }
        private final FastRandom _random;

        private static final char[] WRITE_CHARS =
            ( "Here are some chars to write with that are a fairly " +
                "long chunk but are otherwise completely pointless.\n" +
                "Here are some chars to write with that are a fairly " +
                "long chunk but are otherwise completely pointless.\n" +
                "Here are some chars to write with that are a fairly " +
                "long chunk but are otherwise completely pointless.\n" +
                "Here are some chars to write with that are a fairly " +
                "long chunk but are otherwise completely pointless.\n" +
                "Here are some chars to write with that are a fairly " +
                "long chunk but are otherwise completely pointless.\n"
            ).toCharArray();
    }

    private static final int INITIAL_SIZE = 4096;

    private final Class<?> _testClass;
}
