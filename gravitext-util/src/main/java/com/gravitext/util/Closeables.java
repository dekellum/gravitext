/*
 * Copyright (c) 2007-2010 David Kellum
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

package com.gravitext.util;

import java.io.IOException;

/**
 * Utilities for Closeable instances.
 */
public class Closeables
{
    /**
     * Close target if it implements {@link com.gravitext.util.Closeable}
     * @return true if close was called successfully.
     */
    public static boolean closeIf( Object target )
    {
        if( ( target != null ) && ( target instanceof Closeable ) ) {
            ( (Closeable) target ).close();
            return true;
        }
        return false;
    }

    /**
     * Close target it if implements either {@link com.gravitext.util.Closeable}
     * or {@link java.io.Closeable}.
     * @return true if close was called successfully.
     * @throws IOException from {@link java.io.Closeable#close()}
     */
    public static boolean closeIfAny( Object target ) throws IOException
    {
        if( closeIf( target ) ) return true;

        if( ( target != null ) && ( target instanceof java.io.Closeable ) ) {
            ( (java.io.Closeable) target ).close();
            return true;
        }
        return false;
    }
}
