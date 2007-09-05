/**
 * Copyright (C) 2007  The University of Kansas
 *
 * [INSERT KU-APPROVED LICENSE TEXT HERE]
 * 
 */
package edu.ku.brc.specify.datamodel.busrules;

import static edu.ku.brc.ui.UIRegistry.getLocalizedMessage;

import org.apache.log4j.Logger;

import edu.ku.brc.dbsupport.DataProviderFactory;
import edu.ku.brc.dbsupport.DataProviderSessionIFace;
import edu.ku.brc.specify.datamodel.Taxon;
import edu.ku.brc.specify.datamodel.TaxonTreeDef;
import edu.ku.brc.specify.datamodel.TaxonTreeDefItem;

/**
 * A business rules class that handles various safety checking and housekeeping tasks
 * that must be performed when editing {@link Taxon} or
 * {@link TaxonTreeDefItem} objects.
 *
 * @author jstewart
 * @code_status Beta
 */
public class TaxonBusRules extends BaseTreeBusRules<Taxon, TaxonTreeDef, TaxonTreeDefItem>
{
    private static final Logger log = Logger.getLogger(TaxonBusRules.class);
    
    /**
     * Constructor.
     */
    public TaxonBusRules()
    {
        super(Taxon.class,TaxonTreeDefItem.class);
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
        if (dataObj instanceof Taxon)
        {
            return super.okToDeleteNode((Taxon)dataObj);
        }
        
        return false;
    }
    
    @Override
    public boolean hasNoConnections(Taxon taxon)
    {
        Integer id = taxon.getTreeId();
        if (id == null)
        {
            return true;
        }
        
        boolean noDeters = super.okToDelete("determination", "TaxonID",         id);
        boolean noCites  = super.okToDelete("taxoncitation", "TaxonID",         id);
        boolean noHyb1   = super.okToDelete("taxon",         "HybridParent1ID", id);
        boolean noHyb2   = super.okToDelete("taxon",         "HybridParent2ID", id);
        boolean noSyns   = super.okToDelete("taxon",         "AcceptedID",      id);

        boolean noConns = noDeters && noCites && noHyb1 && noHyb2 && noSyns;
        
        return noConns;
    }
    
    public boolean okToDeleteTaxon(Taxon taxon)
    {
        Integer id = taxon.getId();
        if (id == null)
        {
            return true;
        }
        
        boolean noDeters = super.okToDelete("determination", "TaxonID",         id);
        boolean noCites  = super.okToDelete("taxoncitation", "TaxonID",         id);
        boolean noHyb1   = super.okToDelete("taxon",         "HybridParent1ID", id);
        boolean noHyb2   = super.okToDelete("taxon",         "HybridParent2ID", id);
        boolean noSyns   = super.okToDelete("taxon",         "AcceptedID",      id);

        boolean okSoFar = noDeters && noCites && noHyb1 && noHyb2 && noSyns;
        
        if (okSoFar)
        {
            // now check the children

            DataProviderSessionIFace session = DataProviderFactory.getInstance().createSession();
            Taxon tmpT = session.load(Taxon.class, id);

            for (Taxon child: tmpT.getChildren())
            {
                if (!okToDeleteTaxon(child))
                {
                    // this child can't be deleted
                    // stop right here
                    okSoFar = false;
                    break;
                }
            }
            session.close();
        }
        
        return okSoFar;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.busrules.BaseTreeBusRules#beforeSave(java.lang.Object, edu.ku.brc.dbsupport.DataProviderSessionIFace)
     */
    @Override
    public void beforeSave(Object dataObj, DataProviderSessionIFace session)
    {
        super.beforeSave(dataObj, session);
        
        if (dataObj instanceof Taxon)
        {
            Taxon taxon = (Taxon)dataObj;
            beforeSaveTaxon(taxon, session);

            // this might not do anything (if no names need to be changed)
            super.updateFullNamesIfNecessary(taxon, session);
            
            return;
        }
        
        if (dataObj instanceof TaxonTreeDefItem)
        {
            beforeSaveTaxonTreeDefItem((TaxonTreeDefItem)dataObj);
            return;
        }
    }
    
    /**
     * Handles the {@link #beforeSave(Object)} method if the passed in {@link Object}
     * is an instance of {@link Taxon}.  The real work of this method is to
     * update the 'fullname' field of all {@link Taxon} objects effected by the changes
     * to the passed in {@link Taxon}.
     * 
     * @param taxon the {@link Taxon} being saved
     */
    protected void beforeSaveTaxon(Taxon taxon, @SuppressWarnings("unused") DataProviderSessionIFace session)
    {
        // if this node is "accepted" then make sure it doesn't point to an accepted parent
        if (taxon.getIsAccepted() == null || taxon.getIsAccepted().booleanValue() == true)
        {
            taxon.setAcceptedTaxon(null);
        }
        
        // if this node isn't a hybrid then make sure it doesn't point at hybrid "parents"
        if (taxon.getIsHybrid() == null || taxon.getIsHybrid().booleanValue() == false)
        {
            taxon.setHybridParent1(null);
            taxon.setHybridParent2(null);
        }
    }
    
    /**
     * Handles the {@link #beforeSave(Object)} method if the passed in {@link Object}
     * is an instance of {@link TaxonTreeDefItem}.  The real work of this method is to
     * update the 'fullname' field of all {@link Taxon} objects effected by the changes
     * to the passed in {@link TaxonTreeDefItem}.
     *
     * @param defItem the {@link TaxonTreeDefItem} being saved
     */
    protected void beforeSaveTaxonTreeDefItem(TaxonTreeDefItem defItem)
    {
        // This is a LONG process for some trees.  I wouldn't recommend doing it.  Can
        // we set these options before shipping the DB, then not let them change it ever again?
        // Or perhaps they can't change it if there are records at this level.
        
        log.warn("TODO: need to make a decision here");
        return;

        //super.beforeSaveTreeDefItem(defItem);
    }
}
