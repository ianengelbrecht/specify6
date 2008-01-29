/*
 * Copyright (C) 2007  The University of Kansas
 *
 * [INSERT KU-APPROVED LICENSE TEXT HERE]
 *
 */
package edu.ku.brc.ui.forms.formatters;

import java.util.List;

import edu.ku.brc.dbsupport.AutoNumberIFace;
import edu.ku.brc.ui.DateWrapper;
import edu.ku.brc.ui.forms.formatters.UIFieldFormatter.PartialDateEnum;
import edu.ku.brc.util.Pair;

/**
 * This interface describes how a formatter should be used by the form system. The InBound and OutBound methods
 * are mainly used for numeric values that need to have the leading zeroes stripped or appended so they are transparent 
 * for users, but are required for sorting.
 *  
 * @author rods
 *
 * @code_status Beta
 *
 * Created Date: Jul 10, 2007
 *
 */
public interface UIFieldFormatterIFace
{
    
    /**
     * @return whether it is a system formatter that cannot be deleted.
     */
    public abstract boolean isSystem();
    
    /**
     * @return the list of fields for the format.
     */
    public abstract List<UIFieldFormatterField> getFields();
    
    /**
     * Returns the format field for year. There should only ever be one, but if there is more than one
     * then it returns the last one or the one with isByYear set to true.
     * @return the year field.
     */
    public abstract UIFieldFormatterField getYear();

    /**
     * The unique name of the format as referenced by the form system.
     * @param name the name
     */
    public abstract void setName(final String name);

    /**
     * @return The unique name of the format as referenced by the form system.
     */
    public abstract String getTitle();

    /**
     * @return The unique name of the field it can be applied to (unless it is a date).
     */
    public abstract String getFieldName();

    /**
     * The human readable and possibly localized name of the format, used in the editor.
     * @param title the title
     */
    public abstract void setTitle(final String title);

    /**
     * @return The human readable and possibly localized name of the format, used in the editor.
     */
    public abstract String getName();

    /**
     * @return the class of data object that the format is intended for
     */
    public abstract Class<?> getDataClass();

    /**
     * @return true if it is the default formatter
     */
    public abstract boolean isDefault();

    /**
     * @param isDefault set this formatter as the default for a class of data objects.
     */
    public abstract void setDefault(boolean isDefault);

    /**
     * @return true if this formatter has an incrementer field.
     */
    public abstract boolean isIncrementer();

    /**
     * @return the character length of the entire format.
     */
    public abstract int getLength();

    /**
     * @return the pair of values where the first number is the index into the Fields list and the second
     * is the index of where the field's format ends.
     */
    public abstract Pair<Integer, Integer> getIncPosition();

    /**
     * @return the pair of values where the first number is the index into the Fields list and the second
     * is the index of where the field's format ends.
     */
    public abstract Pair<Integer, Integer> getYearPosition();

    /**
     * @return the string pattern that is used in the UI to tell the user how the value should be entered, the length should 
     * match that of the formtat, this string is autogenerated.
     */
    public abstract String toPattern();

    /**
     * Indicates whether the value should be formatted on the way 'out' of the UI before it is set into the data object.
     * @return whether the value should be formatted on the way 'out' of the UI before it is set into the data object
     */
    public abstract boolean isOutBoundFormatter();

    /**
     * Formats a value after retrieval from the UI before it goes to the data object
     * @param data the value to be formatted
     * @return the new formatted value
     */
    public abstract Object formatOutBound(final Object data);

    /**
     * @return true if this formatter can for/should format values before they get to the UI
     */
    public abstract boolean isInBoundFormatter();

    /**
     * Formats a value before it goes to the UI.
     * @param data the value to be formatted
     * @return the new formatted value
     */
    public abstract Object formatInBound(final Object data);

    /**
     * @return the class that is used for generating the next number in the sequence.
     */
    public abstract AutoNumberIFace getAutoNumber();

    /**
     * Sets the class that is used for generating the next number in the sequence.
     * @param autoNumber the autonukmber generator class
     */
    public abstract void setAutoNumber(AutoNumberIFace autoNumber);

    /**
     * Given a formatted string it returns the next formatted string in the progression.
     * @param value the current largest formatted string in the sequence
     * @return the next incremented value in the sequence
     */
    public abstract String getNextNumber(String value);

    /**
     * @return true if part of the format needs user input, false it is auto-generated.
     */
    public abstract boolean isUserInputNeeded();
    
    /**
     * Appends a presentation of itself in XML to the StringBuilder
     * @param sb the stringbuilder
     */
    public abstract void toXML(StringBuilder sb);
    
    //-----------------------------------------------------------------------
    // The Data Specific Methods
    //-----------------------------------------------------------------------
    
    /**
     * Quick way to find out if it is a date formatter.
     * @return true if date formatter
     */
    public abstract boolean isDate();

    /**
     * Quick way to find out if it is a numeric formatter.
     * @return true if numeric formatter
     */
    public abstract boolean isNumeric();

    /**
     * @return the dateWrapper the DateWrapper object for this formatter.
     */
    public abstract DateWrapper getDateWrapper();
    
    /**
     * Returns the type of Part date formatter it is.
     * @return the type of Part date formatter it is.
     */
    public abstract PartialDateEnum getPartialDateType();

}