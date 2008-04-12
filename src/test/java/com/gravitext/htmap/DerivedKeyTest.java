package com.gravitext.htmap;

import junit.framework.TestCase;

// Experiment: Can derived key types be supported for compiler-based 
// scoping of keys/spaces? 

public class DerivedKeyTest extends TestCase
{
    static class BaseKey<V>
    {
        public BaseKey( String name, Class<V> valueType )
        {
        }
    }
    
    static class KeySpaceRegistry //<K>
    {
        public void register( BaseKey key ) //instead of K<T> create()
        {
        }
    }

    // Unsupported: Error "Type K is not generic"
    /*
    static class KeySpaceReg2<K extends BaseKey>
    {
        public <V> K<V> create( Class<V> valueType )
        {
            
        }
    }
    */
    
    
    // FINDINGS: Impossible to support Key sub-class and have
    // KeySpaceRegistry create type safe versions K<V>.  The approach below
    // using separate static registration block is the best available.
    

    static class DerivedKey<V> extends BaseKey<V>
    {
        public DerivedKey( String name, Class<V> valueType )
        {
            super( name, valueType );
        }

        public static final KeySpaceRegistry SPACE = new KeySpaceRegistry();
        
        public static final DerivedKey<Double> DBLKEY 
            = new DerivedKey<Double>( "DBLKEY", Double.class );
        
        static {
            SPACE.register( DBLKEY );
        }
    }

    public void test()
    {
    }
}
