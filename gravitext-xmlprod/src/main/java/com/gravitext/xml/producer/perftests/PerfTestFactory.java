package com.gravitext.xml.producer.perftests;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.concurrent.TestRunnable;

public class PerfTestFactory implements TestFactory
{
    public static enum Serializer {
        JAXP,
        JDOM,
        STAX,
        XMLPROD
    };

    public PerfTestFactory( Serializer serializer )
    {
        _serializer = serializer; 
    }
    
    public String name()
    {
        return _serializer.toString();
    }

    public void setDoEncode( boolean doEncode )
    {
        _doEncode = doEncode;
    }

    public void setEncoding( String encoding )
    {
        _encoding = encoding;
    }
    
    public void setVerbose( boolean doVerbose )
    {
        _doVerbose = doVerbose;
    }
    
    public void setUseWriter( boolean useWriter )
    {
        _useWriter = useWriter;
    }
    
    
    public TestRunnable createTestRunnable( int seed )
    {
        SerializePerfTest r = null;
        switch( _serializer ) {
        case JAXP:    r = new JaxpPerfTest(); break; 
        case JDOM:    r = new JDomPerfTest(); break;
        case STAX:    r = new StaxPerfTest(); break;        
        case XMLPROD: r = new ProducerPerfTest();
        }

        r.setVerbose( _doVerbose );
        r.setUseWriter( _useWriter );
        r.setEncoding( _encoding );
        r.setDoEncode( _doEncode );

        r.setSeed( seed );
        
        return r;
    }
    
    private boolean _doVerbose   = false;
    private boolean _useWriter   = true;
    private boolean _doEncode = false;
    private String _encoding = "ISO-8859-1";
    
    private final Serializer _serializer;
}
