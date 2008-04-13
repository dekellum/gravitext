package com.gravitext.xml.producer;

import java.io.IOException;

public final class XMLProducer
{
    public XMLProducer( Appendable out )
    {
        _impl = new XMLProducerImpl( new CharacterEncoder( out ) );
    }
    
    public XMLProducer( CharacterEncoder encoder )
    {
        _impl = new XMLProducerImpl( encoder );
    }
   

    public XMLProducer setIndent( Indentor indentor )
    {
        _impl.setIndent( indentor );
        return this;
    }
        
    
    public XMLProducer putXMLDeclaration( String encoding ) 
        throws IOException
    {
        _impl.putXMLDeclaration( encoding );
        return this;
    }

    public XMLProducer putDTD( CharSequence dtd )
        throws IOException
    {
        _impl.putDTD( dtd );
        return this;
    }
    
    public XMLProducer putSystemDTD( String name, CharSequence uri )
        throws IOException
    {
        _impl.putSystemDTD( name, uri );
        return this;
    }
    
    
    public XMLProducer startTag( Tag tag ) throws IOException
    {
        _impl.startTag( tag );
        return this;
    }
//  FIXME: Document callers responsibility to avoid repeated attribute names.

    public XMLProducer addAttr( Attribute attr, CharSequence value ) 
        throws IOException
    {
        _impl.addAttr( attr, value );
        return this;
    }

    public XMLProducer addAttr( Attribute attr, short value ) 
        throws IOException
    {
        _impl.addAttrSafe( attr, Short.toString( value ) );
        return this;
    }

    public XMLProducer addAttr( Attribute attr, int value ) 
        throws IOException
    {
        _impl.addAttrSafe( attr, Integer.toString( value ) );
        return this;
    }
    
    public XMLProducer addAttr( Attribute attr, long value ) 
        throws IOException
    {
        _impl.addAttrSafe( attr, Long.toString( value ) );
        return this;
    }
    
    public XMLProducer addAttr( Attribute attr, Enum<?> value ) 
        throws IOException
    {
        _impl.addAttrSafe( attr, value.name() );
        return this;
    }
 
    /**
     * Put an attribute on previously started tag with specified name
     * and value. Value will be encoded as necessary.
     */
    public XMLProducer addAttr( String name, CharSequence value )
    throws IOException
    {
        _impl.addAttr( name, value );
        return this;
    }

    public XMLProducer addAttr( String name, short value ) 
        throws IOException
    {
        _impl.addAttrSafe( name, Short.toString( value ) );
        return this;
    }
   
    public XMLProducer addAttr( String name, int value ) 
        throws IOException
    {
        _impl.addAttrSafe( name, Integer.toString( value ) );
        return this;
    }

    public XMLProducer addAttr( String name, long value ) 
        throws IOException
    {
        _impl.addAttrSafe( name, Long.toString( value ) );
        return this;
    }
    
    public XMLProducer addAttr( String name, Enum<?> value ) 
        throws IOException
    {
        _impl.addAttrSafe( name, value.name() );
        return this;
    }
    
    
    public XMLProducer addNamespace( Namespace ns ) throws IOException
    {
        _impl.addNamespace( ns );
        return this;
    }
    
    /**
     * @throws IOException from the underlying Appendable.
     * @throws CharEncodeException (an IOException) from the underlying
     *         CharacterEncoder.
     * @throws IllegalStateException indicating a programmatic violation of XML
     *         well-formedness.
     */
    public XMLProducer putChars( CharSequence text ) throws IOException
    {
        _impl.putChars( text );
        return this;
    }
    
    
    public XMLProducer putChars( short value ) throws IOException
    {
        _impl.putCharsSafe( Short.toString( value ) );
        return this;
    }
    
    public XMLProducer putChars( int value ) throws IOException
    {
        _impl.putCharsSafe( Integer.toString( value ) );
        return this;
    }
    
    public XMLProducer putChars( long value ) throws IOException
    {
        _impl.putCharsSafe( Long.toString( value ) );
        return this;
    }
    
    public XMLProducer putChars( Enum<?> value ) throws IOException
    {
        _impl.putCharsSafe( value.name() );
        return this;
    }
   
    public XMLProducer putComment( CharSequence comment ) throws IOException
    {
        _impl.putComment( comment );
        return this;
    }

    public XMLProducer endTag( Tag tag ) throws IOException
    {
        _impl.endTag( tag );
        return this;
    }

    public XMLProducer endTag() throws IOException
    {
        _impl.endTag( null );
        return this;
    }
    

    final XMLProducerImpl _impl;
}
