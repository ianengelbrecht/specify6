/**
 * Copyright (C) 2007  The University of Kansas
 *
 * [INSERT KU-APPROVED LICENSE TEXT HERE]
 * 
 */
package edu.ku.brc.specify.datamodel.busrules;

import static edu.ku.brc.ui.UICacheManager.getLocalizedMessage;
import edu.ku.brc.specify.datamodel.Taxon;

/**
 *
 * @code_status Alpha
 * @author jstewart
 */
public class TaxonBusRules extends BaseBusRules
{
    public TaxonBusRules()
    {
        super(Taxon.class);
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.busrules.BaseBusRules#getDeleteMsg(java.lang.Object)
     */
    @Override
    public String getDeleteMsg(Object dataObj)
    {
        return getLocalizedMessage("TAXON_DELETED", ((Taxon)dataObj).getName());
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.busrules.BaseBusRules#okToDelete(java.lang.Object)
     */
    @Override
    public boolean okToDelete(Object dataObj)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
