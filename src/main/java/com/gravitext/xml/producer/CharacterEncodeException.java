package com.gravitext.xml.producer;

import java.io.IOException;

/**
 * Thrown when a CharacterEncoder encounters a special character set to 
 * Mode.ERROR. 
 * @author David Kellum
 */
public class CharacterEncodeException extends IOException
{
    private static final long serialVersionUID = 1L;

    public CharacterEncodeException( String message )
    {
        super( message );
    }
}
