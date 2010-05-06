/*
 * Copyright (c) 2008-2010 David Kellum
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
import java.util.HashMap;

/**
 * Produces well-formed XML documents from a series of event methods.
 *
 * <p>The XML document is output to a supplied Appendable as it is
 * produced.  XML well-formed document constraints are enforced with a
 * IllegalStateException thrown from event methods.  See the
 * <a href="http://www.w3.org/TR/xml11/">XML 1.1 Specification</a> for
 * details of these constraints.</p>
 *
 * <p>Character data and attribute values are encoded as needed by the
 * supplied or constructed {@link CharacterEncoder}.  Indention or
 * "pretty printing" of the output is supported by a set
 * {@link Indentor}.</p>
 *
 * @author David Kellum
 */
public final class XMLProducer
{
    /**
     * Construct given Appendable to use for output and a default
     * CharacterEncoder.
     */
    public XMLProducer( final Appendable out )
    {
        _impl = new XMLProducerImpl( new CharacterEncoder( out ) );
    }

    /**
     * Construct given CharacterEncoder to use for output.
     */
    public XMLProducer( final CharacterEncoder encoder )
    {
        _impl = new XMLProducerImpl( encoder );
    }

    /**
     * Set the Indentor to use. The default is
     * {@link Indentor#LINE_BREAK}.
     */
    public XMLProducer setIndent( final Indentor indentor )
    {
        _impl.setIndent( indentor );
        return this;
    }

    /**
     * Put a XML declaration in the prolog, with the specified
     * encoding declaration, before the document element is started.
     */
    public XMLProducer putXMLDeclaration( final String encoding )
        throws IOException
    {
        _impl.putXMLDeclaration( encoding );
        return this;
    }

    /**
     * Put a system document type declaration (DTD reference) in
     * the prolog after any XML declaration and before the document
     * element is started.
     */
    public XMLProducer putSystemDTD( final String name,
                                     final CharSequence uri )
        throws IOException
    {
        _impl.putSystemDTD( name, uri );
        return this;
    }

    /**
     * Put a complete {@code &lt;!DOCTYPE[...]>} declaration in the
     * prolog after any XML declaration and before the document
     * element is started. This DTD reference may contain a complete
     * DTD subset and is not otherwise validated by this producer. Use
     * with caution.
     */
    public XMLProducer putDTD( final CharSequence dtd )
        throws IOException
    {
        _impl.putDTD( dtd );
        return this;
    }

    /**
     * Put start tag. The various addAttr() methods can then be called
     * to add attributes to this tag.
     */
    public XMLProducer startTag( final Tag tag ) throws IOException
    {
        _impl.startTag( tag );
        return this;
    }

    /**
     * Put start tag by name in default Namespace. The various addAttr() methods
     * can then be called to add attributes to this tag.
     */
    public XMLProducer startTag( final String name ) throws IOException
    {
        return startTag( name, null );
    }

    /**
     * Put start tag by name and Namespace. The various addAttr() methods
     * can then be called to add attributes to this tag.
     */
    public XMLProducer startTag( String name, Namespace ns ) throws IOException
    {
        _impl.startTag( cacheTag( name, ns ) );
        return this;
    }

    /**
     * Put attribute with value on previous start tag. The value will
     * be encoded as necessary.  It is the callers responsibility to
     * avoid writing the same attribute more than once to a single
     * tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws CharacterEncodeException (an IOException) from the
     *         underlying CharacterEncoder.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final Attribute attr,
                                final CharSequence value )
        throws IOException
    {
        _impl.addAttr( attr, value, true );
        return this;
    }

    /**
     * Put attribute with short value on previous start tag. It is the
     * callers responsibility to avoid writing the same attribute more
     * than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final Attribute attr,
                                final short value )
        throws IOException
    {
        _impl.addAttr( attr, Short.toString( value ), false );
        return this;
    }

    /**
     * Put attribute with integer value on previous start tag. It is
     * the callers responsibility to avoid writing the same attribute
     * more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final Attribute attr,
                                final int value )
        throws IOException
    {
        _impl.addAttr( attr, Integer.toString( value ), false );
        return this;
    }

    /**
     * Put attribute with long value on previous start tag. It is
     * the callers responsibility to avoid writing the same attribute
     * more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final Attribute attr,
                                final long value )
        throws IOException
    {
        _impl.addAttr( attr, Long.toString( value ), false );
        return this;
    }

    /**
     * Put attribute with Enum.name() value on previous start tag. It
     * is the callers responsibility to avoid writing the same
     * attribute more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final Attribute attr,
                                final Enum<?> value )
        throws IOException
    {
        _impl.addAttr( attr, value.name(), false );
        return this;
    }

    /**
     * Put default namespace attribute name with value on previous
     * start tag. The value will be encoded as necessary.  It is the
     * callers responsibility to avoid writing the same attribute more
     * than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws CharacterEncodeException (an IOException) from the
     *         underlying CharacterEncoder.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final String name,
                                final CharSequence value )
        throws IOException
    {
        _impl.addAttr( name, value, true );
        return this;
    }

    /**
     * Put default namespace attribute name with short value on
     * previous start tag. It is the callers responsibility to avoid
     * writing the same attribute more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final String name,
                                final short value )
        throws IOException
    {
        _impl.addAttr( name, Short.toString( value ), false );
        return this;
    }

    /**
     * Put default namespace attribute name with short value on
     * previous start tag. It is the callers responsibility to avoid
     * writing the same attribute more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final String name,
                                final int value )
        throws IOException
    {
        _impl.addAttr( name, Integer.toString( value ), false );
        return this;
    }

    /**
     * Put default namespace attribute name with short value on
     * previous start tag. It is the callers responsibility to avoid
     * writing the same attribute more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final String name,
                                final long value )
        throws IOException
    {
        _impl.addAttr( name, Long.toString( value ), false );
        return this;
    }

    /**
     * Put default namespace attribute name with Enum.name() value on
     * previous start tag. It is the callers responsibility to avoid
     * writing the same attribute more than once to a single tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addAttr( final String name,
                                final Enum<?> value )
        throws IOException
    {
        _impl.addAttr( name, value.name(), false );
        return this;
    }

    /**
     * Put Namespace on previous start tag.  Any subsequent attributes
     * or tags of this namespace within the same scope will avoid
     * making the same declaration.  It is the callers responsibility
     * to avoid writing the same namespace more than once to a single
     * tag.
     * @throws IllegalStateException if not called after startTag().
     * @throws CharacterEncodeException (an IOException) from the
     *         underlying CharacterEncoder.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer addNamespace( final Namespace ns )
        throws IOException
    {
        _impl.addNamespace( ns );
        return this;
    }

    /**
     * Put character data into previously started element. The text
     * will be encoded as necessary.  The putChars() methods may be
     * called repeatedly.
     * @throws IllegalStateException character data can't be written
     *         at this location in the document.
     * @throws CharacterEncodeException (an IOException) from the
     *         underlying CharacterEncoder.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putChars( final CharSequence text )
        throws IOException
    {
        _impl.putChars( text, true );
        return this;
    }

    /**
     * Put short value as character data into previously started
     * element. The putChars() methods may be called repeatedly.
     * @throws IllegalStateException character data can't be written
     *         at this location in the document.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putChars( final short value ) throws IOException
    {
        _impl.putChars( Short.toString( value ), false );
        return this;
    }

    /**
     * Put integer value as character data into previously started
     * element. The putChars() methods may be called repeatedly.
     * @throws IllegalStateException character data can't be written
     *         at this location in the document.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putChars( final int value ) throws IOException
    {
        _impl.putChars( Integer.toString( value ), false );
        return this;
    }

    /**
     * Put long value as character data into previously started
     * element. The putChars() methods may be called repeatedly.
     * @throws IllegalStateException character data can't be written
     *         at this location in the document.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putChars( final long value ) throws IOException
    {
        _impl.putChars( Long.toString( value ), false );
        return this;
    }

    /**
     * Put Enum.name() value as character data into previously started
     * element. The putChars() methods may be called repeatedly.
     * @throws IllegalStateException character data can't be written
     *         at this location in the document.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putChars( final Enum<?> value ) throws IOException
    {
        _impl.putChars( value.name(), false );
        return this;
    }

    /**
     * Close the matching last opened tag. Tag instances are
     * identity compared: startTag(t1), endTag(t2), t1 == t2.
     * @throws IllegalStateException if there is no matching open tag.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer endTag( final Tag tag )
        throws IOException
    {
        _impl.endTag( tag );
        return this;
    }

    /**
     * Close the last opened tag.
     * @throws IllegalStateException if there is no open tag to close
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer endTag() throws IOException
    {
        _impl.endTag( null );
        return this;
    }

    /**
     * Close the matching last opened tag by name and default Namespace.
     * @throws IllegalStateException if there is no matching open tag.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer endTag( String name ) throws IOException
    {
        return endTag( name, null );
    }

    /**
     * Close the matching last opened tag by name and default Namespace.
     * @throws IllegalStateException if there is no matching open tag.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer endTag( String name, Namespace ns ) throws IOException
    {
        _impl.endTag( cacheTag( name, ns ) );
        return this;
    }

    /**
     * Put comment at the current location.
     * @throws IllegalStateException if a comment can't be written
     *         at this location in the document.
     * @throws CharacterEncodeException (an IOException) from the
     *         underlying CharacterEncoder.
     * @throws IOException from the underlying Appendable.
     */
    public XMLProducer putComment( final CharSequence comment )
        throws IOException
    {
        _impl.putComment( comment );
        return this;
    }

    private Tag cacheTag( String name, Namespace ns )
    {
        String iri = ( ns == null ) ? null : ns.nameIRI();
        HashMap<String,Tag> tags = _tagCache.get( iri );

        if( tags == null ) {
            tags = new HashMap<String,Tag>(11);
            _tagCache.put( iri, tags );
        }
        Tag t = tags.get( name );
        if( t == null ) {
            t = new Tag( name, ns);
            tags.put( name, t );
        }

        return t;
    }

    //Cached tags of: namespace URI -> tag name -> Tag1
    private HashMap<String,HashMap<String,Tag>> _tagCache =
        new HashMap<String,HashMap<String,Tag>>(5);

    private final XMLProducerImpl _impl;
}
