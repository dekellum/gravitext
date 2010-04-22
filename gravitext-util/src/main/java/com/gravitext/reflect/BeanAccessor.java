/*
 * Copyright (c) 2007-2010 David Kellum
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

package com.gravitext.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides accessor methods to Java Beans.
 *
 * @author David Kellum
 */
public class BeanAccessor
{
    /**
     * Construct given bean instance to access.
     */
    public BeanAccessor( Object bean ) {
        _bean = bean;
    }

    /**
     * Set the specified property to value on the bean passed on
     * construction.
     * @throws BeanException if a suitable setter is not found for
     * this property, if the specified value could not be coerced to
     * the property type, or to wrap a multitude of exceptions which can
     * be thrown on coercion or invocation.
     */
    public void setProperty( String property, Object value )
        throws BeanException
    {
        Class<?> cls = _bean.getClass();

        String setterName = propertyToSetterName( property );

        Method setter = null;
        for( Method method : cls.getMethods() ) {
            if( method.getName().equals( setterName ) &&
                ( method.getParameterTypes().length == 1 ) ) {
                setter = method;
                break;
            }
        }

        if( setter == null ) {
            throw new BeanException( String.format(
                "No setter method found on bean class %s " +
                "for property name [%s].",
                cls.getName(), property ) );
        }

        Class<?> param = setter.getParameterTypes()[0];

        if( ( value == null ) && param.isPrimitive() ) {
            throw new BeanException( String.format(
                "Can't set primitive %s type property name [%s] to null.",
                param.getSimpleName(),
                property ) );
        }

        param = mapPrimitive( param );

        value = coerceObject( param, value );

        try {
            setter.invoke( _bean, value );
        }
        catch( IllegalArgumentException x ) {
            throw new BeanException( x );
        }
        catch( IllegalAccessException x ) {
            throw new BeanException( x );
        }
        catch( InvocationTargetException x ) {
            throw new BeanException( x.getCause() );
        }
    }

    /**
     * Attempts to coerce the specified value to the specified
     * type. If the value is a CharSequence it will be coerced via
     * {@link #coerceString coerceString()}. If the value is a
     * Number the target type is a type of Number, then
     * {@link #coerceNumber coerceNumber()} will be used.
     * @throws BeanException if the specified value could not be
     * coerced to the property type or to wrap a multitude of
     * exceptions which can be thrown on coercion (ex:
     * NumberFormatException).
     */
    public <T> T coerceObject( Class<T> type, Object value )
        throws BeanException
    {
        if( ( value != null ) && ! type.isInstance( value ) ) {

            if( value instanceof CharSequence ) {
                value = coerceString( type, value.toString() );
            }
            else if( ( value instanceof Number ) &&
                     ( Number.class.isAssignableFrom( type ) ) ) {
                value = coerceNumber( type.asSubclass( Number.class ),
                                      (Number) value );
            }
            else throw new BeanException
            ( "Unable to coerce value type " + value.getClass() +
              " to type " + type + '.' );
        }

        return type.cast( value );
    }

    /**
     * Attempts to coerce the specified string value to the specified
     * type. All of the primitive types, associated primitive wrapper
     * classes, additional JDK Number classes BigDecimal and
     * BigInteger, and Java enums are supported.
     * @throws BeanException if the specified value could not be
     * coerced to the property type or to wrap a multitude of
     * exceptions which can be thrown on coercion (ex:
     * NumberFormatException).
     */
    public <T> T coerceString( Class<T> type, String val )
        throws BeanException
    {
        Object res = null;

        if( type == String.class ) {
            res = val;
        }
        else if( type == Boolean.class ) {
            if     ( "true" .equalsIgnoreCase( val ) ) res = Boolean.TRUE;
            else if( "false".equalsIgnoreCase( val ) ) res = Boolean.FALSE;
            else throw new BeanException
            ( "Unable to parse value [" + val + "] as boolean." );
        }
        else if( type == Byte.class )    res = Byte.decode( val );
        else if( type == Character.class ) {
            if( val.length() == 1 ) {
                res = Character.valueOf( val.charAt( 0 ) );
            }
            else throw new BeanException
            ( "Unable to convert string [" + val + "] to (single) char." );
        }
        else if( Number.class.isAssignableFrom( type ) ) {
            try {
                if     ( type == Short.class )      res = Short.decode( val );
                else if( type == Integer.class )    res = Integer.decode( val );
                else if( type == Long.class )       res = Long.decode( val );
                else if( type == Float.class )      res = Float.valueOf( val );
                else if( type == Double.class )     res = Double.valueOf( val );
                else if( type == BigDecimal.class ) res = new BigDecimal( val );
                else if( type == BigInteger.class ) res = new BigInteger( val );
            }
            catch( NumberFormatException x ) {
                throw new BeanException( x );
            }
        }
        else if( type.isEnum() ) {
            res = coerceEnum( type.asSubclass( Enum.class ), val );
        }

        if( ( val != null ) && ( res == null ) ) {
            throw new BeanException( "Unable to coerce string to type "
                                     + type.getName() + '.' );
        }

        return type.cast( res );
    }

    /**
     * Coerces the specified Number value to an alternative specified
     * Number type via the standard Number defined conversion methods.
     * @throws BeanException if the specified value could not be
     * coerced to the property type or to wrap a multitude of
     * exceptions which can be thrown on coercion (ex:
     * NumberFormatException).
     */
    public <T extends Number> T coerceNumber( Class<T> type, Number val )
        throws BeanException
    {
        if     ( type == Byte.class )    val = new Byte( val.byteValue() );
        else if( type == Short.class )   val = new Short( val.shortValue() );
        else if( type == Integer.class ) val = new Integer( val.intValue() );
        else if( type == Long.class )    val = new Long( val.shortValue() );
        else if( type == Float.class )   val = new Float( val.floatValue() );
        else if( type == Double.class )  val = new Double( val.doubleValue() );
        else if( type == BigInteger.class ) {
            val = BigInteger.valueOf( val.longValue() );
        }
        else if( type == BigDecimal.class ) {
            try {
                val = BigDecimal.valueOf( val.doubleValue() );
            }
            catch( NumberFormatException x ) {
                throw new BeanException( x );
            }
        }
        else throw new BeanException( "Unsupported Number type: " +
                                      type.getName() );

        return type.cast( val );
    }

    /**
     * Coerces the specified String value to the specified enum type
     * via the {@link java.lang.Enum#valueOf(java.lang.Class, java.lang.String) Enum.valueOf()}
     * method.
     * @throws BeanException wrapping an IllegalArgumentException if
     * the string value does not match a Enum constant of the
     * specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum> T coerceEnum( Class<T> type, String value )
        throws BeanException
    {
        try {
            return type.cast( Enum.valueOf( type, value ) );
        }
        catch( IllegalArgumentException x ) {
            throw new BeanException( x );
        }
    }

    /**
     * Map a primitive type to its associated object wrapper class.
     * @return if cls.isPrimitive() return the associated Object wrapper Class,
     *         else return cls.
     */
    public static Class<?> mapPrimitive( Class<?> cls )
    {
        if( cls.isPrimitive() ) {
            if     ( cls == Boolean.TYPE )   cls = Boolean.class;
            else if( cls == Byte.TYPE )      cls = Byte.class;
            else if( cls == Character.TYPE ) cls = Character.class;
            else if( cls == Short.TYPE )     cls = Short.class;
            else if( cls == Integer.TYPE )   cls = Integer.class;
            else if( cls == Long.TYPE )      cls = Long.class;
            else if( cls == Float.TYPE )     cls = Float.class;
            else if( cls == Double.TYPE )    cls = Double.class;
            else if( cls == Void.TYPE )      cls = Void.class;
            else throw new IllegalStateException( "Unknown primitive type: "
                                                  + cls.getName() );
        }

        return cls;
    }

    /**
     * Convert a property name to a setter method name by capitalizing
     * the first character and prefixing with "set".
     */
    public static String propertyToSetterName( String propName )
    {
        return ( "set" + Character.toUpperCase( propName.charAt(0) )
                 + propName.substring(1) );
    }

    private Object _bean;
}
