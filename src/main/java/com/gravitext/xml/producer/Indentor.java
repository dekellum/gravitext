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
 * Immutable XML indentor for human readability.  
 * @author David Kellum
 */
public final class Indentor
{
    /**
     * Constant Indentor for no indentation or line breaks.
     */
    public static final Indentor COMPRESSED = new Indentor();

    /**
     * Constant Indentor for UNIX line breaks and no indentation.  
     */
    public static final Indentor LINE_BREAK = new Indentor( "" );

    /**
     * Constant Indentor with UNIX line breaks and single space
     * indentation.
     */
    public static final Indentor PRETTY = new Indentor( " " );
    

    public Indentor()
    {
        this( null );
    }
    
    /**
     * Construct Indentor using indent string for each indentation
     * level and the lineSeperator to use. The indent is typically
     * some number of tabs or spaces. Any non-null indent value
     * implies line-breaks as well.
     */
    public Indentor( final String indent )
    {
        this( indent, "\n" );
    }
    
    /**
     * Construct Indentor using indent string for each indentation
     * level and the lineSeperator to use. The indent is typically
     * some number of tabs or spaces. Any non-null indent value
     * implies line-breaks as well.
     */
    public Indentor( final String indent, final String lineSeparator )
    {
        _indent = indent;
        _lineSeparator = lineSeparator;
        
        if( _indent == null ) {
            _indentSize = -1;
            _indents = null;
        }
        else {
            _indentSize = _indent.length();
            StringBuilder b = 
                new StringBuilder( _lineSeparator.length() + 
                                   _indentSize * CACHE_INDENTS );
            b.append( _lineSeparator );
            if( _indentSize > 0 ) {
                for( int i = 0; i < CACHE_INDENTS; ++i ) b.append( _indent );
            }
            _indents = b.toString();
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
    
    public void indent( final Appendable out, final int level ) 
        throws IOException 
    {
        if( _indentSize == 0 ) { out.append( _lineSeparator ); }
        else if( _indentSize > 0 ) {
            int olev = Math.min( level, CACHE_INDENTS );
            out.append( _indents, 0, 
                        _lineSeparator.length() + ( olev * _indentSize ) );
            while( olev < level ) { //Remainder
                out.append( _indent );
                ++olev;
            }
        }
    }
    
    private final int _indentSize;
    private final String _indent;
    private final String _lineSeparator;
    private final String _indents;

    private static final int CACHE_INDENTS = 32;
}
