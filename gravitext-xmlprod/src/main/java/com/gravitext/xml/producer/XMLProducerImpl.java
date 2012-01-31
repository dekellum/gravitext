/*
 * Copyright (c) 2008-2012 David Kellum
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
import java.util.ArrayList;

/**
 * Internal implementation of the XMLProducer.
 *
 * @author  David Kellum
 */
final class XMLProducerImpl
{
    public XMLProducerImpl( final CharacterEncoder encoder )
    {
        _encoder = encoder;
        _out = encoder.output();
    }

    public void setIndent( final Indentor indentor )
    {
        _indentor = indentor;
    }

    public void putXMLDeclaration( final String encoding )
        throws IOException
    {
        trans( State.XML_DECLARATION );
        _out.append( "<?xml version=\"" );
        _out.append( _encoder.version().toString() );
        _out.append( "\" encoding=\"" );
        _out.append( encoding );
        _out.append( "\"?>" );
        newline(0);
    }

    public void putSystemDTD( final String name,
                              final CharSequence uri )
        throws IOException
    {
        trans( State.DOCTYPE_DECLARATION );
        _out.append( "<!DOCTYPE " );
        _out.append( name );
        _out.append( " SYSTEM \"" );
        _out.append( uri );
        _out.append( "\">" );
        newline(0);
    }

    public void putDTD( final CharSequence dtd )
        throws IOException
    {
        trans( State.DOCTYPE_DECLARATION );
        _out.append( dtd );
        newline(0);
    }

    public void startTag( final Tag tag ) throws IOException
    {
        closeTag();

        if( ( _state == State.ELEMENT ) ||
            ( _state == State.CHARS ) ||
            ( _state == State.COMMENT_INTERNAL ) ) {
            newline( _openTags.size() );
        }

        trans( State.START_TAG_OPEN );
        _out.append( tag.beginTag() );
        _openTags.add( tag );

        putNamespaceIfNotInScope( tag.namespace() );
    }

    public void addAttr( final Attribute attr,
                         final CharSequence value,
                         final boolean encode )
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException(
                "XMLProducer: Can only addAttr() after startTag()." );
        }

        putNamespaceIfNotInScope( attr.namespace() );

        _out.append( attr.beginAttribute() );

        _out.append( _encoder.quoteMark().literal );

        if( encode ) _encoder.encodeAttrValue( value );
        else _out.append( value );

        _out.append( _encoder.quoteMark().literal );
    }

    public void addAttr( final String name,
                         final CharSequence value,
                         final boolean encode )
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException(
                "XMLProducer: Can only addAttr() after startTag()." );
        }

        _out.append( ' ' );
        _out.append( name );
        _out.append( '=' );
        _out.append( _encoder.quoteMark().literal );
        if( encode ) _encoder.encodeAttrValue( value );
        else _out.append( value );
        _out.append( _encoder.quoteMark().literal );
    }

    public void implyNamespace( final Namespace ns )
    {
        if( !ns.isXML() ) {
            _nScopes.add( new NScope( ns, _openTags.size() - 1 ) );
        }
    }

    public void addNamespace( final Namespace ns ) throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException(
                "XMLProducer: Can only addNamespace() after startTag()." );
        }
        if( !ns.isXML() ) {
            _out.append( ns.beginDecl() );
            _out.append( _encoder.quoteMark().literal );
            _encoder.encodeAttrValue( ns.nameIRI() );
            _out.append( _encoder.quoteMark().literal );

            _nScopes.add( new NScope( ns, _openTags.size() - 1 ) );
        }
    }

    public void putChars( final CharSequence data,
                          final boolean encode )
        throws IOException
    {
        // Test for empty to avoid non-canonical <t></t> instead of <t/>
        if( data.length() > 0 ) {
            closeTag();
            trans( State.CHARS );
            if( encode ) _encoder.encodeCharData( data );
            else _out.append( data );
        }
    }

    public void putComment( final CharSequence comment )
        throws IOException
    {
        closeTag();

        if( _state.ordinal() < State.ELEMENT.ordinal() ) {
            trans( State.COMMENT_PROLOG );
        }
        else if( _state.ordinal() < State.END.ordinal() ) {
            trans( State.COMMENT_INTERNAL );
            newline( _openTags.size() );
        }
        else trans( State.COMMENT_END );

        _out.append( "<!--" );
        _encoder.encodeComment( comment );
        _out.append( "-->" );
        if( _openTags.size() == 0 ) newline(0);
    }

    /**
     * @param tag matching start tag or null to close last open tag.
     */
    public void endTag( final Tag tag ) throws IOException
    {
        if( _openTags.size() == 0 ) {
            throw new IllegalStateException(
            "XMLProducer: Attempt to endTag() with no matching startTag()." );
        }

        int depth = _openTags.size() - 1;
        Tag openTag = _openTags.remove( depth );
        if( ( tag != null ) && ( tag != openTag ) ) {
            throw new IllegalStateException(
                "XMLProducer: Attempt to end " + tag + " while "
                + openTag + " is open." );
        }

        // Remove any namespace scopes closed with this tag.
        int i = _nScopes.size();
        while( i-- > 0 ) {
            if( _nScopes.get( i ).depth == depth ) _nScopes.remove( i );
            else break;
        }

        if( ( _state == State.ELEMENT ) ||
            ( _state == State.COMMENT_INTERNAL ) ) newline( depth );
        boolean empty = ( _state == State.START_TAG_OPEN );

        trans( State.ELEMENT );

        if( empty ) _out.append( "/>" );
        else _out.append( openTag.endTag() );

        if( depth == 0 ) {
            trans( State.END );
            newline( 0 );
        }
    }

    private static enum State
    {
        BEGIN,
        XML_DECLARATION,
        COMMENT_PROLOG,
        DOCTYPE_DECLARATION,
        START_TAG_OPEN,
        COMMENT_INTERNAL,
        ELEMENT,
        CHARS,
        END,
        COMMENT_END
    }

    /* Allowed state transitions TRANS[current][next] */
    private static final boolean[][] ALLOWED_STATE_TRANSITIONS = {
    /*current | ------------------------------- next ------------------------------- |*/
    /*------- | BEGIN  XML    COMPRO DOCTPE START  COMINT ELMENT CHARS  END   COMEND |*/
    /*BEGIN */{ false, true,  true,  true,  true,  false, false, false, false, false },
    /*XML   */{ false, false, true,  true,  true,  false, false, false, false, false },
    /*COMPRO*/{ false, false, true,  true,  true,  false, false, false, false, false },
    /*DOCTPE*/{ false, false, true,  false, true,  false, false, false, false, false },
    /*START */{ false, false, false, false, false, true,  true,  false, false, false },
    /*COMINT*/{ false, false, false, false, true,  true,  true,  true,  false, false },
    /*ELMENT*/{ false, false, false, false, true,  true,  true,  true,  true,  false },
    /*CHARS */{ false, false, false, false, true,  true,  true,  true,  false, false },
    /*END   */{ false, false, false, false, false, false, false, false, false, true  },
    /*COMEND*/{ false, false, false, false, false, false, false, false, false, true  }
    };

    private static final class NScope
    {
        NScope( final Namespace namespace, final int depth )
        {
            this.namespace = namespace;
            this.depth = depth;
        }

        final Namespace namespace;
        final int depth;
    }

    private void putNamespaceIfNotInScope( final Namespace ns )
        throws IOException
    {
        if( ns != null && !ns.isXML() ) {
            int i = _nScopes.size();
            while( i-- > 0 ) {
                if( _nScopes.get( i ).namespace == ns ) return;
            }
            addNamespace( ns );
        }
    }

    private void closeTag() throws IOException
    {
        if( _state == State.START_TAG_OPEN ) {
            trans( State.ELEMENT );
            _out.append( '>' );
        }
    }

    private void newline( final int level ) throws IOException
    {
        _indentor.indent( _out, level );
    }

    private void trans( final State next )
    {
        // Test state transition and throw exception if invalid.

        if( ALLOWED_STATE_TRANSITIONS[ _state.ordinal() ][ next.ordinal() ] ) {
            _state = next;
        }
        else {
            throw new IllegalStateException
            ( "XMLProducer: Can't transition from " + _state
                    + " to " + next + '.' );
        }
    }

    private State _state = State.BEGIN;
    private final Appendable _out;
    private final CharacterEncoder _encoder;

    private Indentor _indentor = Indentor.LINE_BREAK;

    private final ArrayList<Tag>    _openTags = new ArrayList<Tag>(16);
    private final ArrayList<NScope> _nScopes  = new ArrayList<NScope>(8);
}
