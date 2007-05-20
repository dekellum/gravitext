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
    public BeanAccessor( Object bean ) {
        _bean = bean;
    }

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

    @SuppressWarnings("unchecked")
    public <T extends Enum> T coerceEnum( Class<T> type, String value ) 
        throws BeanException
    {
        try {
            return Enum.valueOf( type, value );
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
