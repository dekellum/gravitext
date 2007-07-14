package com.gravitext.htmap.perftests;

import java.util.Map;

import com.gravitext.htmap.HashHTMap;
import com.gravitext.htmap.Key;

/**
 * HashHTMap performance test.
 * @author David Kellum
 */
public class HashHTMapPerfTest extends ArrayHTMapPerfTest
{
    @Override
    protected Map<Key, Object> createMap()
    {
        return new HashHTMap( TOTAL_KEYS );
    }
}
