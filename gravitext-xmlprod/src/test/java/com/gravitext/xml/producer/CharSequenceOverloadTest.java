package com.gravitext.xml.producer;

import java.util.Date;

import junit.framework.TestCase;

public class CharSequenceOverloadTest extends TestCase
{

    boolean foo( CharSequence s )
    {
        return true;
    }
    boolean foo( Object o )
    {
        return false;
    }

    interface IAlt
    {
    }

    class Bar implements IAlt, CharSequence
    {

        public char charAt( int index )
        {
            return 0;
        }

        public int length()
        {
            return 0;
        }

        public CharSequence subSequence( int start, int end )
        {
            return null;
        }
    };

    public void test()
    {
        assertFalse( foo( new Date() ) );
        assertTrue( foo( "somestring" ) );

        assertTrue( foo( new Bar() ) );

        // But CharSequence interface can be hidden.
        IAlt ialt = new Bar();
        assertFalse( foo( ialt ) );
    }

}
