/*
 * Copyright 2007 David Kellum
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

package com.gravitext.xml.producer;

import java.io.IOException;

/**
 * XML indentor for human readability.  
 * @author David Kellum
 */
public final class Indentor implements Cloneable
{
    /**
     * Constant Indentor for no indentation or line breaks.
     */
    public static final Indentor COMPRESSED = new Indentor( null );

    /**
     * Constant Indentor for line breaks but no indentation.  
     */
    public static final Indentor LINE_BREAK = new Indentor( "" );

    /**
     * Constant Indentor with line breaks and single space
     * indentation.
     */
    public static final Indentor PRETTY = new Indentor( " " );
    
    // FIXME: Indentor keeps an internal cached representation and is
    // not synchronized. Thus each producer should use a new or cloned
    // Indentor instance.

    //FIXME: Add support for non-UNIX default line-break on request

    /**
     * Construct Indentor using indent string for each indentation
     * level (typically some number of tabs and spaces.) Any non-null
     * indent value implies line-breaks as well.
     */
    public Indentor( String indent )
    {
        if( indent == null ) {
            _indentSize = -1;
            _indents = null;
        }
        else {
            _indentSize = indent.length();
            StringBuilder b = new StringBuilder( 1 + _indentSize * 16 );
            b.append( '\n' );
            if( _indentSize > 0 ) {
                for( int i = 0; i < 16; ++i ) b.append( indent );
            }
            _indents = b.toString();
        }
    }
    
    @Override
    public Indentor clone()
    {
        try {
            return (Indentor) super.clone();
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Return true if this Indentor is equivalent to COMPRESSED
     * constant.
     */
    public boolean isCompressed()
    {
        return ( _indentSize == -1 );
    }
    
    /**
     * Return true if this Indentor is equivalent to LINE_BREAKS
     * constant.
     */
    public boolean isLineBreak()
    {
        return ( _indentSize == 0 );
    }
    
    void indent( final Appendable out, final int level ) 
        throws IOException 
    {
        if( _indentSize == 0 ) out.append( '\n' );
        else if( _indentSize > 0 ) {
            int len = 1 + ( level * _indentSize );
            while( len > _indents.length() ) {
                _indents = _indents + _indents.substring(1);
            }
            out.append( _indents, 0, len );
        }
    }
    
    private final int _indentSize;
    private String _indents;
 }
