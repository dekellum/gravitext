package com.gravitext.reflect;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class BeanTestCase extends TestCase
{
    
    
    public void test() 
        throws IntrospectionException, 
               IllegalArgumentException, 
               IllegalAccessException, 
               InvocationTargetException
    {
        PropertyDescriptor pd = new PropertyDescriptor( "stringProp", 
                                                        TestBean.class );
        Method setter = pd.getWriteMethod();
        
        TestBean bean = new TestBean();
        
        setter.invoke( bean, new Object[] { "value" } );
        
        assertEquals( "value", bean.getStringProp() );
        
        pd = new PropertyDescriptor( "booleanProp", TestBean.class );
        setter = pd.getWriteMethod();
        
        setter.invoke( bean, true );
        
        assertEquals( true, bean.isBooleanProp() );
    }
    
    
    private static final class TestBean
    {
        
        public boolean isBooleanProp()
        {
            return _booleanProp;
        }

        public void setBooleanProp( boolean booleanProp )
        {
            _booleanProp = booleanProp;
        }

        public String getStringProp()
        {
            return _stringProp;
        }

        public void setStringProp( String stringProp )
        {
            _stringProp = stringProp;
        }
        
        private String _stringProp = null;
        private boolean _booleanProp = false;
    }
}
