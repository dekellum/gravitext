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

package com.gravitext.htmap;

/**
 * An immutable associative array key that carries an associated value
 * class type.
 * @param <T> the associated value type. 
 * @see KeySpace
 * @author David Kellum
 */
public final class Key<T>
{
    /**
     * Package private constructor.
     * @param name should match assigned constant name.
     * @param valueType runtime class type of the value
     * @param space the {@code KeySpace} that created this Key
     * @param id {@code KeySpace} assigned unique id
     */
    Key( final String name,
         final Class<T> valueType, 
         final KeySpace space,
         final int id )
    {
        _name = name;
        _valueType = valueType; 
        _space = space;
        _id = id;
    }
    
    /**
     * Return the class type defined for values associated with this
     * Key.
     */
    public Class<T> valueType()
    {
        return _valueType;
    }
    
    /**
     * Return the name of this key.  Names are unique across all Keys
     * created from a single KeySpace.
     */
    public String name()
    {
        return _name;
    }
    
    /**
     * Return the name of this Key, as per {@link #name}.
     */
    @Override
    public String toString()
    {
        return _name;
    }

    /**
     * Return the numeric ID assigned for this Key. These are
     * guaranteed to be well packed and unique upon creation of the
     * Key by a KeySpace.
     */
    public int id()
    {
        return _id;
    }

    /**
     * Return the KeySpace in which this Key was created.
     */
    public KeySpace space()
    {
        return _space;
    }
    
    private final String _name;
    private final Class<T> _valueType;
    private final KeySpace _space;
    private final int _id;
}