package com.gravitext.concurrent;

import java.text.NumberFormat;

public final class Metric
{
    
    public static void format( double value, StringBuilder out )
    {     
        char p = 0;
        double v = value;
        
        for( Range r : RANGES ) {
            if( v >= r.pos || v <= r.neg ) {
                p = r.postfix;
                v *= r.fac;
                break;
            }
        }

        int fdigits = 3;
        if( v >= 0.0d ) {
            if( p == 0 ) { // 99,999 9,999 999.99, 99.999, 9.999 
                if      ( v >= 999.995 ) fdigits = 0;
                else if ( v >= 99.9995 ) fdigits = 2;           
            }
            else { //9,999x, 999.9x, 99.99x, 9.999x
                if      ( v >= 999.95 ) fdigits = 0;
                else if ( v >= 99.995 ) fdigits = 1;           
                else if ( v >= 9.995 )  fdigits = 2;
            }
        }
        else {
            if( p == 0 ) { // -9,999 -999.9, -99.99 -9.999 
                if      ( v <= -999.95 ) fdigits = 0;
                else if ( v <= -99.995 ) fdigits = 1;
                else if ( v <= -9.9995 ) fdigits = 2;           
            }
            else { //-999x, -99.9x, -9.99x
                if      ( v <= -99.95 ) fdigits = 0;
                else if ( v <= -9.995 ) fdigits = 1;           
                else fdigits = 2;
            }
        }

        NumberFormat f = NumberFormat.getNumberInstance();
        f.setMinimumFractionDigits( fdigits );
        f.setMaximumFractionDigits( fdigits );
        f.setGroupingUsed( true );
        
        String vf = f.format( v );

        if( vf.length() == ( (p == 0) ? 5 : 4 ) ) out.append( ' ' );
        out.append( vf );
        if( p != 0 ) out.append( p );
    }
    
    public static void formatDifference( double value, StringBuilder out )
    {
        if( Double.isNaN( value ) ) {
            out.append( "N/A" );
            return;
        }
        
        double r = value;
        char symbol;
        if( r > 1.0d ) {
            r += 1.0d;
            symbol = 'x';
        }
        else {
            r *= 100d;
            symbol = '%';
        }
        
        int fdigits = 2;
        if( r >= 100d || r <= -100d ) fdigits = 0;
        else if( r >= 10d || r <= -10d ) fdigits = 1;
        
        NumberFormat f = NumberFormat.getNumberInstance();
        f.setMinimumFractionDigits( fdigits );
        f.setMaximumFractionDigits( fdigits );
        
        if( r > 0d && symbol == '%' ) out.append('+');
        out.append( f.format( r ) );
        out.append( symbol );
    }
    
    public static String format( double value )
    {
        StringBuilder b = new StringBuilder( 16 );
        format( value, b );
        return b.toString();
    }

    public static String formatDifference( double value )
    {
        StringBuilder b = new StringBuilder( 16 );
        formatDifference( value, b );
        return b.toString();
    }
    private static final class Range 
    {
        Range( double pos, double neg, double fac, char postfix )
        {
            this.pos = pos;
            this.neg = neg;
            this.fac = fac;
            this.postfix = postfix;
        }

        double pos;
        double neg;
        double fac; 
        char postfix;
    }
    
    private static final Range[] RANGES = new Range[] {
        new Range(   999.95e12d,  -999.5e12d,  1e-15d, 'P' ),
        new Range(   999.95e9d,   -999.5e9d,   1e-12d, 'T' ),
        new Range(   999.95e6d,   -999.5e6d,   1e-9d,  'G' ),
        new Range(   999.95e3d,   -999.5e3d,   1e-6d,  'M' ),
        new Range( 99999.5e0d,   -9999.5e0d,   1e-3d,  'k' ),
        new Range(   999.95e-3d,  -999.5e-3d,  1d,     '\0'),
        new Range(   999.95e-6d,  -999.5e-6d,  1e+3d,  'm' ),
        new Range(   999.95e-9d,  -999.5e-9d,  1e+6d,  'µ' ),
        new Range(   999.95e-12d, -999.5e-12d, 1e+9d,  'n' ),
        new Range(   999.95e-15d, -999.5e-15d, 1e+12d, 'p' ),
        new Range(   999.95e-18d, -999.5e-18d, 1e+15d, 'f' ),
    };
}
