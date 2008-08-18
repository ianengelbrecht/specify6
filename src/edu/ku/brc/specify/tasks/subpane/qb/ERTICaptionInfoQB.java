/*
     * Copyright (C) 2008  The University of Kansas
     *
     * [INSERT KU-APPROVED LICENSE TEXT HERE]
     *
     */
/**
 * 
 */
package edu.ku.brc.specify.tasks.subpane.qb;

import edu.ku.brc.af.ui.db.ERTICaptionInfo;
import edu.ku.brc.af.ui.db.PickListDBAdapterIFace;
import edu.ku.brc.af.ui.db.PickListItemIFace;
import edu.ku.brc.af.ui.forms.formatters.UIFieldFormatterIFace;
import edu.ku.brc.specify.dbsupport.RecordTypeCode;

/**
 * @author timbo
 *
 * @code_status Alpha
 *
 */
public class ERTICaptionInfoQB extends ERTICaptionInfo
{
    /**
     * A unique identifier for the column within the QB query which is independent of the column's caption.
     */
    protected final String colStringId;
    protected final PickListDBAdapterIFace pickList;
    
    public ERTICaptionInfoQB(String  colName, 
                           String  colLabel, 
                           boolean isVisible, 
                           UIFieldFormatterIFace uiFieldFormatter,
                           int     posIndex,
                           String colStringId,
                           PickListDBAdapterIFace pickList)
    {
        super(colName, colLabel, isVisible, uiFieldFormatter, posIndex);
        this.colStringId = colStringId;
        this.pickList = pickList;
    }

    /**
     * @return the colStringId;
     */
    public String getColStringId()
    {
        return colStringId;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.ui.db.ERTICaptionInfo#processValue(java.lang.Object)
     */
    @Override
    public Object processValue(Object value)
    {
        if (pickList instanceof RecordTypeCode)
        {
            PickListItemIFace item = ((RecordTypeCode )pickList).getItemByValue(value);
            if (item != null)
            {
                return item.getTitle();
            }
            return value.toString();
        }
        return super.processValue(value);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.ui.db.ERTICaptionInfo#getColClass()
     */
    @Override
    public Class<?> getColClass()
    {
        if (pickList instanceof RecordTypeCode)
        {
            return String.class;
        }
        return super.getColClass();
    }
    
    
}
