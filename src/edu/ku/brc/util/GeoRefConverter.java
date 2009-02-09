package edu.ku.brc.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class GeoRefConverter implements StringConverter
{
    public static enum GeoRefFormat
    {
        DMS_PLUS_MINUS ("[\\+\\-]?\\d{1,3}[\\sd°|\\sd�]\\s?\\d{1,2}[\\s']\\s?\\d{1,2}(\\.\\d{0,}\\s*)?\"?")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDDMMSSStrToDDDDBD(orig);
            }
        },
        DM_PLUS_MINUS  ("[\\+\\-]?\\d{1,3}[\\sd°|\\sd�]\\s?\\d{1,2}(\\.\\d{0,}\\s*)?'?")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDDMMMMStrToDDDDBD(orig);
            }
        },
        D_PLUS_MINUS   ("[\\+\\-]?\\d{1,3}(\\.\\d{0,}\\s*)?[d°|d�]?")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDDDDStrToDDDDBD(orig);
            }
        },
        DMS_NSEW       ("\\d{1,3}[\\sd°|\\sd�]\\s?\\d{1,2}[\\s']\\s?\\d{1,2}(\\.\\d{0,})?\"?\\s?[NSEW]{1}.*")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDirectionalDDMMSSToDDDD(orig);
            }
        },
        DM_NSEW        ("\\d{1,3}[\\sd°|\\sd�]\\s?\\d{1,2}(\\.\\d{0,})?'?\\s?[NSEW]{1}.*")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDirectionalDDMMMMToDDDD(orig);
            }
        },
        D_NSEW         ("\\d{1,3}(\\.\\d{0,})?[d°|d�]?\\s?[NSEW]{1}.*")
        {
            @Override
            public BigDecimal convertToDecimalDegrees(String orig)
            {
                return LatLonConverter.convertDirectionalDDDDToDDDD(orig);
            }
        };
        
        public final String regex;
        
        GeoRefFormat(String regex)
        {
            this.regex = regex;
        }
        
        public boolean matches(String input)
        {
            return input.matches(regex);
        }
        
        public abstract BigDecimal convertToDecimalDegrees(String original);
    }
    
    public GeoRefConverter()
    {
        // nothing to do here
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.StringConverter#convert(java.lang.String, java.lang.String)
     */
    public String convert(final String original, final String destFormat) throws Exception
    {
    	return convert(original, destFormat, LatLonConverter.LATLON.Latitude/*dummy*/,
    			LatLonConverter.DEGREES_FORMAT.None);
    }

    public String convert(final String original, final String destFormat,
    		final LatLonConverter.LATLON llType, final LatLonConverter.DEGREES_FORMAT degFmt) 
    	throws Exception
    {
        if (original == null)
        {
            return null;
        }
        
        // first we have to 'discover' the original format
        // and convert to decimal degrees
        // then we convert to the requested format

        BigDecimal degreesPlusMinus = null;
        for (GeoRefFormat format: GeoRefFormat.values())
        {
            if (original.matches(format.regex))
            {
                degreesPlusMinus = format.convertToDecimalDegrees(original);
                break;
            }
        }
        
        int decimalFmtLen = 0;
        int decIndex = original.lastIndexOf('.');
        if (decIndex > -1 && original.length() > decIndex)
        {
            int end = original.length();
            while (!StringUtils.isNumeric(original.substring(decIndex+1,end)) && end >= 0)
            {
                end--;
            }
            decimalFmtLen = end - decIndex - 1;
        }
        
        // if we weren't able to find a matching format, throw an exception
        if (degreesPlusMinus == null)
        {
            throw new Exception("Cannot find matching input format");
        }
        
        if (destFormat == GeoRefFormat.DMS_PLUS_MINUS.name())
        {
            return LatLonConverter.convertToSignedDDMMSS(degreesPlusMinus, decimalFmtLen, degFmt);
        }
        else if (destFormat == GeoRefFormat.DM_PLUS_MINUS.name())
        {
            return LatLonConverter.convertToSignedDDMMMM(degreesPlusMinus, decimalFmtLen, degFmt);
        }
        else if (destFormat == GeoRefFormat.D_PLUS_MINUS.name())
        {
            return LatLonConverter.convertToSignedDDDDDD(degreesPlusMinus, decimalFmtLen, degFmt);
        }
        else if (destFormat == GeoRefFormat.D_NSEW.name())
        {
        	return LatLonConverter.convertToDDDDDD(degreesPlusMinus, degFmt, getDir(llType), decimalFmtLen, true);
        }
        else if (destFormat == GeoRefFormat.DM_NSEW.name())
        {
        	return LatLonConverter.convertToDDMMMM(degreesPlusMinus, degFmt, getDir(llType), decimalFmtLen, true);
        }
        else if (destFormat == GeoRefFormat.DMS_NSEW.name())
        {
        	return LatLonConverter.convertToDDMMSS(degreesPlusMinus, degFmt, getDir(llType), decimalFmtLen, true);
        }
        return null;
    }

    /**
     * @param llType
     * @return direction appropriate for llType.
     */
    protected LatLonConverter.DIRECTION getDir(LatLonConverter.LATLON llType)
    {
    	if (llType.equals(LatLonConverter.LATLON.Latitude))
    	{
    		return LatLonConverter.DIRECTION.NorthSouth;
    	}
    	else if (llType.equals(LatLonConverter.LATLON.Longitude))
    	{
    		return LatLonConverter.DIRECTION.EastWest;
    	}
    	//else ???
    	return LatLonConverter.DIRECTION.None;
    }
    
    /**
     * @param entry
     * @return the LatLonConverter FORMAT for entry.
     */
    public LatLonConverter.FORMAT getLatLonFormat(final String entry)
    {
        if (entry != null)
        {
            for (GeoRefFormat format: GeoRefFormat.values())
            {
                if (format.matches(entry))
                {
                    if (format.equals(GeoRefFormat.D_NSEW) || format.equals(GeoRefFormat.D_PLUS_MINUS))
                    {
                        return LatLonConverter.FORMAT.DDDDDD;
                    }
                    if (format.equals(GeoRefFormat.DM_NSEW) || format.equals(GeoRefFormat.DM_PLUS_MINUS))
                    {
                        return LatLonConverter.FORMAT.DDMMMM;
                    }
                    if (format.equals(GeoRefFormat.DMS_NSEW) || format.equals(GeoRefFormat.DMS_PLUS_MINUS))
                    {
                        return LatLonConverter.FORMAT.DDMMSS;
                    }
                }
            }
        }
        return LatLonConverter.FORMAT.None;
    }
    
    /**
     * @param args
     * @throws Exception 
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        String destFormat = GeoRefFormat.DMS_PLUS_MINUS.name();
        
        String[] inputStrings = new String[] {
                
                // +/- Deg Min Sec
                "//+/- Deg Min Sec",
                "0 0 0",
                "0 0 0.",
                "-32 45 16.8232",
                "-32d 45' 16.8232\"",
                "-32d45'16.8232\"",
                "-32°45'16.8232\"",
                "-32° 45' 16.82\"",
                "-32 45 16.82",
                "-32 45 6.8232",
                "-32 45 6.82",
                "-32 45 0.82",
                "-132 45 16.82151",
                "-132 45 6.82",
                "32 45 16.8232",
                "32 45 16.82",
                "32 45 6.8232",
                "32 45 6.82",
                "32 45 0.82",
                "132 45 16.82151",
                "132 45 6.82",
                
                // Deg Min Sec N/S/E/W
                "//Deg Min Sec N/S/E/W",
                "32 45 16.8232 N",
                "32 45 16.82 N",
                "32d45'16.82\" N",
                "32d45'16.82\"N",
                "32d 45' 16.82\" N",
                "32° 45' 16.82\" N",
                "32 45 16.82 N",
                "32 45 6.8232 N",
                "32 45 6.82 N",
                "32 45 0.82 N",
                "132 45 16.82151 N",
                "132 45 6.82 N",
                
                "32 45 16.8232 S",
                "32 45 16.82 S",
                "32 45 6.8232 S",
                "32 45 6.82 S",
                "32 45 0.82 S",
                "132 45 16.82151 S",
                "132 45 6.82 S",
                
                "32 45 16.8232 E",
                "32 45 16.82 E",
                "32 45 6.8232 E",
                "32 45 6.82 E",
                "32 45 0.82 E",
                "132 45 16.82151 E",
                "132 45 6.82 E",
                
                "32 45 16.8232 W",
                "32 45 16.82 W",
                "32 45 6.8232 W",
                "32 45 6.82 W",
                "32 45 0.82 W",
                "132 45 16.82151 W",
                "132 45 6.82 W",
                
                // +/- Deg Min
                "//+/- Deg Min",
                "0 0",
                "0 0.",
                "-32 16.8232",
                "-32 16.82",
                "-32° 16.82'",
                "-32°16.82",
                "-32d 16",
                "-32 16.82",
                "-32 6.8232",
                "-32 6.82",
                "-32 0.82",
                "-132 16.82151",
                "-132 6.82",
                "32 16.8232",
                "32 16.82",
                "32 6.8232",
                "32 6.82",
                "32 0.82",
                "132 16.82151",
                "132 6.82",
                
                // Deg Min N/S/E/W
                "//Deg Min N/S/E/W",
                "32 16.8232 N",
                "32 16.82 N",
                "32 6.8232 N",
                "32 6.82 N",
                "32 0.82 N",
                "132 16.82151 N",
                "132 6.82 N",
                
                "32 16.8232 S",
                "32 16.82 S",
                "32 6.8232 S",
                "32 6.82 S",
                "32 0.82 S",
                "132 16.82151 S",
                "132 6.82 S",
                
                "32 16.8232 E",
                "32 16.82 E",
                "32 6.8232 E",
                "32 6.82 E",
                "32 0.82 E",
                "132 16.82151 E",
                "132 6.82 E",
                
                "32 16.8232 W",
                "32 16.82 W",
                "32 6.8232 W",
                "32 6.82 W",
                "32 0.82 W",
                "132 16.82151 W",
                "132 6.82 W",
                
                // +/- Decimal Degrees
                "//+/- Decimal Degrees",
                "0",
                "0.",
                "-16.8232",
                "-16.8232°",
                "-16.82",
                "-6.8232",
                "-6.82",
                "-0.82",
                "-116.82151",
                "-116.82",
                "-1.82",
                "16.8232",
                "16.82",
                "6.8232",
                "6.82",
                "0.82",
                "116.82151",
                "116.82",
                "1.82",
                
                // Decimal Degrees N/S/E/W
                "//Decimal Degrees N/S/E/W",
                "16.8232 N",
                "16.82 N",
                "16.8232° N",
                "16.82° N",
                "16.8232°N",
                "16.82°N",
                "6.8232 N",
                "6.82 N",
                "0.82 N",
                "116.82151 N",
                "116.82 N",
                "1.82 N",
                
                "16.8232 S",
                "16.82 S",
                "6.8232 S",
                "6.82 S",
                "0.82 S",
                "116.82151 S",
                "116.82 S",
                "1.82 S",
                
                "16.8232 E",
                "16.82 E",
                "6.8232 E",
                "6.82 E",
                "0.82 E",
                "116.82151 E",
                "116.82 E",
                "1.82 E",
                
                "16.8232 W",
                "16.82 W",
                "6.8232 W",
                "6.82 W",
                "0.82 W",
                "116.82151 W",
                "116.82 W",
                "1.82 W",
                "41 43."
        };

        for (String input: inputStrings)
        {
            if (input.length()==0)
            {
                continue;
            }
            
            if (input.startsWith("//"))
            {
                System.out.println();
                System.out.println("----------------------------------");
                System.out.println("----------------------------------");
                System.out.println(input.substring(2));
                System.out.println("----------------------------------");
                System.out.println("----------------------------------");
                continue;
            }
            
            System.out.println("Input:             " + input);
            BigDecimal degreesPlusMinus = null;
            for (GeoRefFormat format: GeoRefFormat.values())
            {
                if (input.matches(format.regex))
                {
                    System.out.println("Format match:      " + format.name());
                    degreesPlusMinus = format.convertToDecimalDegrees(input);
                    break;
                }
            }
            
            // if we weren't able to find a matching format, throw an exception
            if (degreesPlusMinus == null)
            {
                System.out.println("No matching format found");
                System.out.println("----------------------------------");
                continue;
            }
            
            int decimalFmtLen = 0;
            int decIndex = input.lastIndexOf('.');
            if (decIndex > -1 && input.length() > decIndex)
            {
                decimalFmtLen = input.length() - decIndex;
            }

            
            String convertedVal = null;
            if (destFormat == GeoRefFormat.DMS_PLUS_MINUS.name())
            {
                convertedVal = LatLonConverter.convertToSignedDDMMSS(degreesPlusMinus, decimalFmtLen);
            }
            else if (destFormat == GeoRefFormat.DM_PLUS_MINUS.name())
            {
                convertedVal = LatLonConverter.convertToSignedDDMMMM(degreesPlusMinus, decimalFmtLen);
            }
            else if (destFormat == GeoRefFormat.D_PLUS_MINUS.name())
            {
                convertedVal = LatLonConverter.convertToSignedDDDDDD(degreesPlusMinus, decimalFmtLen);
            }
            
            System.out.println("Converted value:   " + convertedVal);
            System.out.println("----------------------------------");
        }

        GeoRefConverter converter = new GeoRefConverter();
        for (String input: inputStrings)
        {
            if (input.length()==0)
            {
                continue;
            }
            
            if (input.startsWith("//"))
            {
                System.out.println();
                System.out.println("----------------------------------");
                System.out.println("----------------------------------");
                System.out.println(input.substring(2));
                System.out.println("----------------------------------");
                System.out.println("----------------------------------");
                continue;
            }
            
            System.out.println("Input:             " + input);
            String decimalDegrees = converter.convert(input, GeoRefConverter.GeoRefFormat.D_PLUS_MINUS.name());
            System.out.println("Decimal degrees:   " + decimalDegrees);
        }
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
        System.out.println("----------------------------------");

        String problemString = "41 43 18.";
        System.out.println("input: " + problemString);
        String d   = converter.convert(problemString, GeoRefFormat.D_PLUS_MINUS.name());
        String dm  = converter.convert(problemString, GeoRefFormat.DM_PLUS_MINUS.name());
        String dms = converter.convert(problemString, GeoRefFormat.DMS_PLUS_MINUS.name());
        System.out.println(d + "   :   " + dm + "   :   " + dms);

        problemString = d;
        System.out.println("input: " + problemString);
        String d2   = converter.convert(problemString, GeoRefFormat.D_PLUS_MINUS.name());
        String dm2  = converter.convert(problemString, GeoRefFormat.DM_PLUS_MINUS.name());
        String dms2 = converter.convert(problemString, GeoRefFormat.DMS_PLUS_MINUS.name());
        System.out.println(d2 + "   :   " + dm2 + "   :   " + dms2);
        
        problemString = dm;
        System.out.println("input: " + problemString);
        String d3   = converter.convert(problemString, GeoRefFormat.D_PLUS_MINUS.name());
        String dm3  = converter.convert(problemString, GeoRefFormat.DM_PLUS_MINUS.name());
        String dms3 = converter.convert(problemString, GeoRefFormat.DMS_PLUS_MINUS.name());
        System.out.println(d3 + "   :   " + dm3 + "   :   " + dms3);
    }
}
