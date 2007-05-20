package com.gravitext.reflect;

public interface Coercer<T,V>
{
    <A extends T, X extends V> A coerce( Class<A> type, X value );
} 
