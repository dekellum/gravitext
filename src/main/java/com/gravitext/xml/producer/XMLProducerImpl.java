package com.gravitext.xml.producer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Produces well-formed XML documents from series of event/stream
 * style method calls.
 *
 * <p>The XML document is output to a supplied Appendable as it is being
 * produced by calls to the event methods.  Various
 * well-formed XML document constraints are guaranteed with an
 * IllegalStateException being thrown in the event of a violation. See
 * the <a href="http://www.w3.org/TR/REC-xml">XML 1.0
 * Specification</a> for details of these constraints.
 *
 * <p>All character data and attribute values are encoded as needed as
 * part of the corresponding event calls. This is done via methods in
 * the XML utility class.
 *
 * <p>Indentation for human readability is inserted into the output by
 * default. This can be turned off or otherwise adjusted with the
 * various set methods below after construction.  
 *
 * @author  David Kellum
 */
final class XMLProducerImpl
{
    /**
     * Construct given Appendable to use for output.
     */
    public XMLProducerImpl( CharacterEncoder encoder )
    {
        _encoder = encoder;
        _out = encoder.output();
    }
       
    /**
     * Set the indentor to use.
     */
    public void setIndent( Indentor indentor )
    {
        _indentor = indentor.clone();
    }
     
    /**
     * Put a XML declaration into the prolog with the specified
     * encoding declaration before the document element is started.  
     */
    public void putXMLDeclaration( String encoding )
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
    
    // FIXME: encoding constants?
    
    /**
     * Put a document type declaration (DTD reference) in prolog after
     * any XML declartation and before the document element is
     * started.  
     */
    public void putSystemDTD( String name, CharSequence uri ) 
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
    
    public void putDTD( CharSequence dtd ) 
        throws IOException
    {
        trans( State.DOCTYPE_DECLARATION );
        _out.append( dtd );
        newline(0);
    }
   
        
    /**
     * Put a start tag with the specified name.
     */
    public void startTag( Tag tag ) throws IOException
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

    /**
     * Put an attribute on previously started tag with specified name
     * and value. Value will be encoded via XML.EncodeAttr() as
     * necessary.
     */
    public void addAttr( Attribute attr, CharSequence value ) 
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException( 
                "XMLProducer: Can only putAttribute() after putStartTag()." );
        }
        
        putNamespaceIfNotInScope( attr.namespace() );
        
        _out.append( attr.beginAttribute() );
        _encoder.encodeAttrValue( value );
        _out.append( '"' );
    }

    public void addAttrSafe( Attribute attr, String value ) 
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException( 
                "XMLProducer: Can only putAttribute() after putStartTag()." );
        }
        
        putNamespaceIfNotInScope( attr.namespace() );
        
        _out.append( attr.beginAttribute() );
        _out.append( value );
        _out.append( '"' );
    }

    
    public void addAttr( String name, CharSequence value ) 
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) { 
            throw new IllegalStateException(
                "XMLProducer: Can only putAttribute() after putStartTag()." ); 
        }

        _out.append( ' ' );
        _out.append( name );
        _out.append( "=\"" );
        _encoder.encodeAttrValue( value );
        _out.append( '"' );
    }
    
    public void addAttrSafe( String name, String value ) 
        throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException( 
                "XMLProducer: Can only putAttribute() after putStartTag()." );
        }

        _out.append( ' ' );
        _out.append( name );
        _out.append( "=\"" );
        _out.append( value );
        _out.append( '"' );
    }
   
    public void addNamespace( Namespace ns ) throws IOException
    {
        if( _state != State.START_TAG_OPEN ) {
            throw new IllegalStateException( 
                "XMLProducer: Can only putNamespace() after putStartTag()." );
        }
        _out.append( ns.beginDecl() );
        _encoder.encodeAttrValue( ns.nameIRI() );
        _out.append( '"' );
        
        _nScopes.add( new NScope( ns, _openTags.size() - 1 ) );
    }
    
    /**
     * Put character data, encoded as necessary.
     */
    public void putChars( CharSequence data ) throws IOException
    {
        // Test for empty to avoid non-canonical <t></t> instead of <t/>
        if( data.length() > 0 ) {
            closeTag();
            trans( State.CHARS );
            _encoder.encodeCharData( data );
        }
    }
    
    public void putCharsSafe( String data ) throws IOException
    {
        // Test for empty to avoid non-canonical <t></t> instead of <t/>
        if( data.length() > 0 ) {
            closeTag();
            trans( State.CHARS );
            _out.append( data );
        }
    }
    
    public void putComment( CharSequence comment ) throws IOException
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
     * Close a previously opened start tag.
     */
    public void endTag( Tag tag ) throws IOException
    {
        if( _openTags.size() == 0 ) {
            throw new IllegalStateException( 
                "XMLProducer: Attempt to end tag with no matching start tag." );
        }

        int depth = _openTags.size() - 1; 
        Tag openTag = _openTags.remove( depth );
        if( ( tag != null ) && ( tag != openTag ) ) {
            throw new IllegalStateException(
                "XMLProducer: Attempt to end tag " + tag + " while " 
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
    
    /* Allowed state transitions TRANS[current][next]                     */
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
        NScope(final Namespace namespace, final int depth)
        {
            this.namespace = namespace;
            this.depth = depth;
        }

        final Namespace namespace;
        final int depth;
    }


    private void putNamespaceIfNotInScope( Namespace ns ) throws IOException
    {
        if( ns != null ) {
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

    private void newline( int level ) throws IOException
    {
        _indentor.indent( _out, level );
    }

    private void trans( State next ) 
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
    private Appendable _out;
    private CharacterEncoder _encoder;
    
    private Indentor _indentor = Indentor.LINE_BREAK.clone();
    
    private ArrayList<Tag>    _openTags = new ArrayList<Tag>(16);
    private ArrayList<NScope> _nScopes  = new ArrayList<NScope>(8);
}
