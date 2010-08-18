package com.gravitext.xml.tree;

import com.gravitext.xml.producer.Attribute;


/**
 * Immutable XML attribute/value pair.
 * @author David Kellum
 *
 */
public class AttributeValue
{
    public AttributeValue( Attribute attribute, CharSequence value )
    {
        _attribute = attribute;
        _value = value;
    }
    public Attribute attribute()
    {
        return _attribute;
    }

    public CharSequence value()
    {
        return _value;
    }
    private final Attribute _attribute;
    private final CharSequence _value;
}
