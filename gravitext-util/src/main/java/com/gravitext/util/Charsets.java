/*
 * Copyright (c) 2007-2010 David Kellum
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

package com.gravitext.util;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Convenient constants and utilities for character sets.
 * @author David Kellum
 */
public class Charsets
{
    public static final Charset ISO_8859_1 = Charset.forName( "ISO-8859-1" );
    public static final Charset UTF_8 = Charset.forName( "UTF-8" );
    public static final Charset ASCII = Charset.forName( "US-ASCII" );
    public static final Charset UTF_16 = Charset.forName( "UTF-16" );

    /**
     * Return Charset matching name or null if not found, or illegal name.
     */
    public static Charset lookup( String name )
    {
        Charset found = null;
        try {
            found = Charset.forName( name );
        }
        catch( IllegalCharsetNameException x ) {}
        catch( UnsupportedCharsetException x ) {}
        return found;
    }
}
