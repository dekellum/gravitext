/*
 * Copyright (c) 2012 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.gravitext.jruby;

import java.nio.CharBuffer;

import org.jruby.util.ByteList;

import com.gravitext.util.Charsets;
import java.nio.ByteBuffer;

public final class TextSampler
{
    public TextSampler( int length, int offset )
    {
        _length = length;
        _offset = Math.abs( offset ) % CHAR_DATA.length;
    }

    public String string()
    {
        return new String( CHAR_DATA, nextOffset(), _length );
    }

    public CharSequence stringSequence()
    {
        return new String( CHAR_DATA, nextOffset(), _length );
    }

    public CharBuffer charBuffer()
    {
        return CharBuffer.wrap( CHAR_DATA, nextOffset(), _length );
    }

    public CharSequence charBufferSequence()
    {
        return CharBuffer.wrap( CHAR_DATA, nextOffset(), _length );
    }

    public int length()
    {
        return charBuffer().length();
    }

    public ByteBuffer byteBuffer()
    {
        CharBuffer cbuff = CharBuffer.wrap( CHAR_DATA, nextOffset(), _length );
        ByteBuffer b = Charsets.UTF_8.encode( cbuff );
        return b;
    }

    public byte[] bytes()
    {
        CharBuffer cbuff = CharBuffer.wrap( CHAR_DATA, nextOffset(), _length );
        ByteBuffer b = Charsets.UTF_8.encode( cbuff );
        byte[] out = new byte[ b.remaining() ];
        b.get( out );
        return out;
    }

    public ByteList byteList()
    {
        return IOUtils.toByteList( CharBuffer.wrap( CHAR_DATA,
                                                    nextOffset(),
                                                    _length ) );
    }

    private int nextOffset()
    {
        ++_offset;
        if( ( _offset + _length ) >= CHAR_DATA.length ) {
            _offset = 0;
        }
        return _offset;
    }

    private static final char[] CHAR_DATA; // 32K

    static {
        StringBuilder b = new StringBuilder( 1024 * 33 );
        while( b.length() < ( 1024 * 32 ) ) {
            b.append(
           "Cérébrales here is some lengthy chardata.   " );
        }
        CHAR_DATA = b.toString().toCharArray();
    }

    private final int _length;
    private int _offset;
}
