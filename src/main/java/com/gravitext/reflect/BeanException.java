package com.gravitext.reflect;

/**
 * Checked exception for various BeanAccessor errors. 
 * @author David Kellum
 */
public class BeanException extends Exception
{
    public BeanException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public BeanException( String message )
    {
        super( message );
    }

    public BeanException( Throwable cause )
    {
        super( cause );
    }

    private static final long serialVersionUID = 1L;
}
