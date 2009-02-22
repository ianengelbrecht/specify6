/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.ku.brc.af.ui.forms.formatters;

import static edu.ku.brc.helpers.XMLHelper.xmlAttr;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author rods
 *
 * @code_status Alpha
 *
 * Created Date: Jan 17, 2007
 *
 * TODO: RSP: We should consider turning this class into a class hierarchy to remove all the type checks and switches 
 */
public class UIFieldFormatterField implements Cloneable
{
    public enum FieldType {numeric, alphanumeric, alpha, separator, year, anychar, constant}
                                                  
    private static final String ALPHA_NUM_SAMPLE = "Abc123abcdefg123456wxyz7890";
    private static final String ALPHA_SAMPLE     = "Abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_SAMPLE   = "1234567890";
    
    private static String alphaSample        = null;
    private static String numericSample      = null;
    private static String alphaNumericSample = null;
    
    
    protected FieldType type;
    protected int       size;
    protected String    value;
    protected boolean   incrementer;
    protected boolean   byYear;
    
    static {
        alphaSample        = buildSample(ALPHA_SAMPLE);
        numericSample      = buildSample(NUMERIC_SAMPLE);
        alphaNumericSample = buildSample(ALPHA_NUM_SAMPLE);
    }
    
    /**
     * Default constructor
     */
    public UIFieldFormatterField()
    {
        type        = FieldType.alphanumeric;
        size        = 1;
        value       = "";
        incrementer = false;
        byYear      = false;
    }
    
    /**
     * @param type
     * @param size
     * @param value
     * @param incrementer
     * @param byYear
     */
    public UIFieldFormatterField(final FieldType type, 
                                 final int       size, 
                                 final String    value, 
                                 final boolean   incrementer, 
                                 final boolean   byYear)
    {
        super();
        
        this.type        = type;
        this.size        = size;
        this.value       = value;
        this.incrementer = incrementer;
        this.byYear      = byYear;
        
        if (incrementer)
        {
            this.value = UIFieldFormatterMgr.getFormatterPattern(incrementer, null, size);
        }
    }
    
    /**
     * @param type
     * @param size
     * @param value
     * @param incrementer
     */
    public UIFieldFormatterField(final FieldType type, 
                                 final int       size, 
                                 final String    value, 
                                 final boolean   incrementer)
    {
        this(type, size, value, incrementer, false);
    }
    
    /**
     * Builds the maximum sample size string
     * @param sampText the sample text to build it form
     * @return the full max string
     */
    private static String buildSample(final String sampText)
    {
        StringBuilder sb = new StringBuilder(sampText);
        while (sb.length() < 255)
        {
            sb.append(sampText);
        }
        return sb.toString();
    }

    /**
     * Factory that creates a new UIFieldFormatterField from a formatting string
     * @param formattingString Formatting string that defines the field 
     * @return The UIFieldFormatter corresponding to the formatting string
     * @throws UIFieldFormattingParsingException (if formatting string is invalid)
     * 
     * Note: use one of the constructors to get a separator field
     */
    public static UIFieldFormatterField factory(final String   formattingString) throws UIFieldFormatterParsingException 
    {
    	UIFieldFormatterField field = new UIFieldFormatterField();
    	
    	//Pattern pattern = Pattern.compile("^(A+|a+|N+|\\#+|YEAR|Y{4}|Y{2}|M{2,3})$");
    	// restricting set of valid formats to those that can be applied to String for now
    	// will open up possibilities when supporting dates and numeric fields
    	Pattern pattern = Pattern.compile("^(A+|a+|N+|\\#+|YEAR|\"[^\"]*\")$");
    	Matcher matcher = pattern.matcher(formattingString);
    	
    	if (matcher.find()) 
    	{
    		String val = formattingString.substring(matcher.start(), matcher.end());
    		field.setValue(val);
    		field.setSize(val.length());
    		
    		char firstChar = val.charAt(0);
    		switch (firstChar) 
    		{
    		case 'A': 
    			field.setType(FieldType.alphanumeric); 
    			break;
    		case 'a': 
    			field.setType(FieldType.alpha); 
    			break;
    		case 'N': 
    			field.setType(FieldType.numeric);
    			field.setIncrementer(false);
    			break;
    		case '#': 
    			field.setType(FieldType.numeric);
    			field.setIncrementer(true);
    			break;
    		case 'Y': 
    			field.setType(FieldType.year); 
    			break;
    			
    		case '"':
    			field.setType(FieldType.constant);
    			break;
    			
    		// TODO: treat byyear case
    		}
    		
    	} 
    	else 
    	{
    		// didn't find a match: formatting code is invalid
    		throw new UIFieldFormatterParsingException("Invalid formatting string: " + formattingString, formattingString);
    	}
    	
    	return field;
    }
    
    
    public int getSize()
    {
        return size;
    }

    public FieldType getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public String getSample()
    {
    	String sample = "";

		if (type == FieldType.separator)
		{
			return value;
		}

		if (type == FieldType.alphanumeric)
		{
			sample = alphaNumericSample;
		}

		if (type == FieldType.alpha)
		{
			sample = alphaSample;
		}

		if (type == FieldType.numeric)
		{
			sample = numericSample;
			int dot = value.indexOf(".");
			if (dot > 0)
			{
				sample = sample.substring(0, dot) + "." + sample.substring(dot, sample.length());
			}
		}

		if (type == FieldType.year)
		{
			sample = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		}
		
		if (type == FieldType.constant)
		{
			return value.substring(1, value.length() - 1);
		}
		
		if (sample.length() == 0)
		{
			return "";
		}
		
		return sample.substring(0, value.length()); 
    }

    /**
     * @return whether the field is typed into or not.
     */
    public boolean isEntryField()
    {
        return !isIncrementer() && type != UIFieldFormatterField.FieldType.separator;
    }

    public boolean isIncrementer()
    {
        return incrementer;
    }
    
    /**
     * @return the byYear
     */
    public boolean isByYear()
    {
        return byYear;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Type["+type+"]  size["+size+"]  value["+value+"] incr["+incrementer+"]";
    }
    
    /**
     * Appends a presentation of itself in XML to the StringBuilder
     * @param sb the StringBuilder
     */
    public void toXML(StringBuilder sb)
    {
        sb.append("    <field");
        xmlAttr(sb, "type", type.toString());
        xmlAttr(sb, "size", size);
        
        if (type != FieldType.numeric)
        {
        	// XML encode any double quotes
            xmlAttr(sb, "value", value.replaceAll("\"", "&quot;"));
        }
        if (incrementer)
        {
            xmlAttr(sb, "inc", incrementer);
        }
        if (byYear)
        {
            xmlAttr(sb, "byyear", byYear);
        }
        sb.append("/>\n");
    }

	public void setType(FieldType type)
    {
        this.type = type;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setIncrementer(boolean incrementer)
    {
        this.incrementer = incrementer;
    }

    public void setByYear(boolean byYear)
    {
        this.byYear = byYear;
    }

    public boolean isCurrentYear()
    {
        return ((type == FieldType.year) && (value.equals("YEAR")));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
