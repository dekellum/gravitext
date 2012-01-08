/*
 * Copyright (c) 2008-2012 David Kellum
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
