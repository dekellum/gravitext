/*
 * Copyright 2007 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.perftest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gravitext.concurrent.TestFactory;
import com.gravitext.reflect.BeanAccessor;
import com.gravitext.reflect.BeanException;

/**
 * Standalone concurrent performance test driver.  For usage text,
 * execute this class (or the containing jar) without any arguments.
 *
 * @author David Kellum
 * @deprecated
 */
public class Harness
{
    public static void main( String[] args ) throws Exception
    {
        new Harness().run( args );
    }
    
   
    public void run( String[] args ) throws Exception
    {
        processArgs( args );
        
        System.out.println( "Testing with (-c)oncurrency: " + _threadCount );
                           
        if( _verbose ) {
            runVerbose();
        }
        else {
            warmup();
            runComparisons();
        }
    }

    public void run( List<TestFactory> tests )
    {
        _instances = tests;
        System.out.println( "Testing with (-c)oncurrency: " + _threadCount );
        warmup();
        runComparisons();
    }
    
    public void setThreadCount( int threads )
    {
        _threadCount = threads;
    }
    
    /**
     * Run the warmup iterations
     */
    private void warmup()
    {
        System.out.println( "==== Warmup Runs :" );
        System.out.print( PerformanceTester.header() );

        ArrayList<Runner> warmups = new ArrayList<Runner>();
        for( TestFactory t : _instances ) {
            Runner r;
            if( _warmCount == 0 ) {
                r = new AdaptiveRunner( t );
            }
            else {
                r = new Runner( t, _warmCount, _warmIterations );
            }
            r.setThreadCount( _threadCount );
            warmups.add( r );
        }
        boolean remaining = true;
        while( remaining ) {
            remaining = false;
            for( Runner warmup : warmups ) {
                warmup.run( null );
                if( warmup.hasMoreRuns() ) remaining = true;
            }
        }

        double minTP = Double.MAX_VALUE;
        double maxTP = Double.MIN_VALUE;
        for( Runner warmup : warmups ) {
            minTP = Math.min( minTP, warmup.prior().meanThroughput() );
            maxTP = Math.max( maxTP, warmup.prior().meanThroughput() );
        }

        if( _compCount == 0 ) {
            _compCount = (int) Math.min( _compTarget    * maxTP, 
                                         _compTargetMax * minTP );
        }
    }

    /**
     * Run the comparison iterations.
     */
    private void runComparisons()
    {
        System.out.println( "\n==== Comparison Runs :" );
        System.out.print( PerformanceTester.header() );
        
        ArrayList<Runner> comps = new ArrayList<Runner>();
        for( TestFactory t : _instances ) {
            Runner r = new Runner( t, _compCount, _compIterations );
            r.setThreadCount( _threadCount );
            comps.add( r );
        }
        PerformanceTester lastFirst = null;
        boolean remaining = true;
        while( remaining ) {
            remaining = false;
            PerformanceTester first = null;
            for( Runner comp : comps ) {
                comp.run( (first != null) ? first : lastFirst );
                if( first == null ) lastFirst = first = comp.prior();
                if( comp.hasMoreRuns() ) remaining = true;
            }
        }
    }
    
    /**
     * Run verbose single execution.
     * @throws Exception
     */
    private void runVerbose() throws Exception
    {
        boolean wrap = ( _instances.size() > 1 );
        for( TestFactory t : _instances ) {

            if( wrap ) System.out.println( 
                       "---- " + t.getClass().getName() + " ----" );

            t.createTestRunnable( 33 ).runIteration( 1 );
            
            if( wrap ) System.out.println();
        }
    }

    
    private void processArgs( String[] args ) 
        throws ClassNotFoundException, 
               InstantiationException, 
               IllegalAccessException,
               BeanException
    {
        int i = 0;  
        char flag = 0;
        ArrayList<String> commonArgs = new ArrayList<String>();

        TestFactory lastTest = null;
        
        while( i < args.length ) {

            if( args[i].startsWith("--") ) {
                if( lastTest == null ) commonArgs.add( args[i] );
                else applyArgument( lastTest, args[i] );
            }
            else if( flag != 0 ) {
                if( flag == 'c' )      _threadCount=Integer.parseInt(args[i]);
                else if( flag == 'w' ) _warmCount=Integer.parseInt(args[i]);
                else if( flag == 'r' ) _compCount=Integer.parseInt(args[i]);
                else {
                    System.err.println(
                        "ERROR: Invalid flag [-" + flag + "]." );
                    usage();
                }
                flag = 0;
            }
            else if( args[i].startsWith( "-" ) ) {
                if( args[i].length() != 2 ) usage();
                char f = args[i].charAt(1);
                if( f == 'v' ) { _verbose = true; }
                else flag = f;
            }
            else {
                Class<? extends TestFactory> ctClass =
                    Class.forName( args[i] ).asSubclass( TestFactory.class );
                lastTest = ctClass.newInstance();
                
                if( _verbose ) setVerbose( lastTest );
                
                for( String arg : commonArgs ) {
                    applyArgument( lastTest, arg );
                }
                
                _instances.add( lastTest );
            }
            i++;
        }
        if( flag != 0 ) usage();
        if( _instances.size() < 1 ) usage();
    }

    private void setVerbose( Object bean )
    {
        try {
            new BeanAccessor( bean ).setProperty( "verbose", true );
        }
        catch( BeanException x ) {
            // Ignore in verbose case.
        }
    }


    private void applyArgument( Object bean, String argument ) 
        throws BeanException
    {
        Matcher m = ARG_PATTERN.matcher( argument );
        if( m.find() ) {
            String property = m.group( 1 );
            Object value = m.group( 3 );
            if( value == null ) value = true;
            new BeanAccessor( bean ).setProperty( property, value );
        }
        else {
            System.err.println(
                "ERROR: Invalid bean argument [" + argument + "]." );
            usage();
        }
    }

    private static final Pattern ARG_PATTERN = 
        Pattern.compile( "^\\-\\-([^=\\s]+)(=(.+))?$" );
    
    private void usage()
    {
        System.err.println( 
    "Usage: " + getClass().getName() + " [-c] [-w] [-r] [-v]\n" +
    "       [--globalProp[=value]]... [<TestClass> --prop[=value]...] ... \n" +
    " -c threads : thread count.\n" +
    " -w count   : test run count per warmup iteration.\n" +
    " -r count   : test run count per comparison run iteration.\n" +
    " -v         : Verbose mode (TestClass.setVerbose(true)).\n" +
    " --globalProp[=value] : Set globalProp value on all TestClass instances.\n" +
    " --prop[=value]       : Set prop value on previous TestClass only.\n\n"
        );
        System.exit(1);
    }

    private int _threadCount = Runtime.getRuntime().availableProcessors();

    private int    _warmCount      = 0;
    private int    _warmIterations = 3;
    
    private int    _compCount      = 0;
    private int    _compIterations = 3;
    private double _compTarget     = 10.0d; //seconds
    private double _compTargetMax  = 60.0d; //seconds
    
    private boolean _verbose       = false;
        
    private List<TestFactory> _instances =
        new ArrayList<TestFactory>();
}
    
