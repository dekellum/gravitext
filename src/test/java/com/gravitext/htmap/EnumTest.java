package com.gravitext.htmap;

import junit.framework.TestCase;


// Experiment: Can enums be derived as with our Key classes?
public class EnumTest extends TestCase
{
    public enum Base
    {
        FOO
    }
    
    public enum Derived //Unsupported: extends Base
    {
        BAR
    }
    
    public void test()
    {
        
    }
}
