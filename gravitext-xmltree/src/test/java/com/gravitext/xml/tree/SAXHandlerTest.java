package com.gravitext.xml.tree;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.gravitext.xml.producer.Indentor;
import static org.junit.Assert.*;
import static com.gravitext.xml.tree.TreeUtils.*;


public class SAXHandlerTest
{
    @Test
    public void test() throws SAXException, IOException
    {
        String input = "<a>b</a>";
        assertEquals( input, roundTripSAX( input, Indentor.COMPRESSED ) );
    }
}
