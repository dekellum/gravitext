package com.gravitext.xml.tree;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gravitext.xml.producer.Indentor;
import com.gravitext.xml.producer.XMLProducer;

public class TreeUtils
{
    private static String produceString( Node root, Indentor indent )
        throws IOException
    {
        StringBuilder out = new StringBuilder( 128 );
        produce( root, indent, out );
        return out.toString();
    }

    private static void produce( Node root, Indentor indent, Appendable out )
        throws IOException
    {
        XMLProducer pd = new XMLProducer( out );
        pd.setIndent( indent );
        new NodeWriter( pd ).putTree( root );
    }

    public static Node saxParse( InputSource input )
        throws SAXException, IOException
    {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        SAXHandler handler = new SAXHandler();
        reader.setContentHandler( handler );
        reader.parse( input );
        return handler.root();
    }

    public static InputSource saxInputSource( String input )
    {
        return new InputSource( new StringReader( input ) );
    }

    public static String roundTripSAX( String input )
        throws SAXException, IOException
    {
        return roundTripSAX( input, Indentor.PRETTY );
    }

    public static String roundTripSAX( String input, Indentor indent )
        throws SAXException, IOException
    {
        Node root = saxParse( saxInputSource( input ) );

        return produceString( root, indent );
    }
}
