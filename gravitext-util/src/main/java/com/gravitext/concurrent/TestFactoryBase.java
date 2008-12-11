package com.gravitext.concurrent;

public abstract class TestFactoryBase implements TestFactory
{
    public String name()
    {
        return getClass().getSimpleName();
    }

}
