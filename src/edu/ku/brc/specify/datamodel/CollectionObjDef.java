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
package edu.ku.brc.specify.datamodel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;




/**

 */
public class CollectionObjDef extends DataModelObjBase implements java.io.Serializable 
{

    protected static CollectionObjDef currentCollectionObjDef = null;
    
    // Fields

     protected Long collectionObjDefId;
     protected String name;
     protected String discipline;
     protected DataType dataType;
     protected Set<CatalogSeries> catalogSeries;
     protected SpecifyUser specifyUser;
     protected Set<AttributeDef> attributeDefs;
     protected GeographyTreeDef geographyTreeDef;
     protected GeologicTimePeriodTreeDef geologicTimePeriodTreeDef;
     protected LocationTreeDef locationTreeDef;
     protected TaxonTreeDef taxonTreeDef;
     protected Set<Locality> localities;
     protected Set<AppResourceDefault> appResourceDefaults;

    // Constructors

    /** default constructor */
    public CollectionObjDef() {
    }

    /** constructor with id */
    public CollectionObjDef(Long collectionObjDefId) {
        this.collectionObjDefId = collectionObjDefId;
    }

    public static CollectionObjDef getCurrentCollectionObjDef()
    {
        return currentCollectionObjDef;
    }

    public static void setCurrentCollectionObjDef(CollectionObjDef currentCollectionObjDef)
    {
        CollectionObjDef.currentCollectionObjDef = currentCollectionObjDef;
    }

    // Initializer
    public void initialize()
    {
        collectionObjDefId = null;
        name = null;
        discipline = null;
        dataType = null;
        timestampModified = null;
        timestampCreated = new Date();
        catalogSeries = new HashSet<CatalogSeries>();
        specifyUser = null;
        attributeDefs = new HashSet<AttributeDef>();
        geographyTreeDef = null;
        geologicTimePeriodTreeDef = null;
        locationTreeDef = null;
        taxonTreeDef = null;
        localities = new HashSet<Locality>();
        appResourceDefaults = new HashSet<AppResourceDefault>();
    }
    // End Initializer

    // Property accessors

    /**
     *
     */
    public Long getCollectionObjDefId() {
        return this.collectionObjDefId;
    }

    /**
     * Generic Getter for the ID Property.
     * @returns ID Property.
     */
    public Long getId()
    {
        return this.collectionObjDefId;
    }

    public void setCollectionObjDefId(Long collectionObjDefId) {
        this.collectionObjDefId = collectionObjDefId;
    }

    /**
     *
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
    *
    */
    public String getDiscipline()
    {
        return discipline;
    }

    public void setDiscipline(String discipline)
    {
        this.discipline = discipline;
    }

    /**
     *
     */
    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     *
     */
    public Set<CatalogSeries> getCatalogSeries() {
        return this.catalogSeries;
    }

    public void setCatalogSeries(Set<CatalogSeries> catalogSeries) {
        this.catalogSeries = catalogSeries;
    }

    /**
     *
     */
    public SpecifyUser getSpecifyUser() {
        return this.specifyUser;
    }

    public void setSpecifyUser(SpecifyUser specifyUser) {
        this.specifyUser = specifyUser;
    }

    /**
     *
     */
    public Set<AttributeDef> getAttributeDefs() {
        return this.attributeDefs;
    }

    public void setAttributeDefs(Set<AttributeDef> attributeDefs) {
        this.attributeDefs = attributeDefs;
    }

    /**
     *
     */
    public GeographyTreeDef getGeographyTreeDef() {
        return this.geographyTreeDef;
    }

    public void setGeographyTreeDef(GeographyTreeDef geographyTreeDef) {
        this.geographyTreeDef = geographyTreeDef;
    }

    /**
     *
     */
    public GeologicTimePeriodTreeDef getGeologicTimePeriodTreeDef() {
        return this.geologicTimePeriodTreeDef;
    }

    public void setGeologicTimePeriodTreeDef(GeologicTimePeriodTreeDef geologicTimePeriodTreeDef) {
        this.geologicTimePeriodTreeDef = geologicTimePeriodTreeDef;
    }

    /**
     *
     */
    public LocationTreeDef getLocationTreeDef() {
        return this.locationTreeDef;
    }

    public void setLocationTreeDef(LocationTreeDef locationTreeDef) {
        this.locationTreeDef = locationTreeDef;
    }

    /**
     *      * @hibernate.one-to-one
     */
    public TaxonTreeDef getTaxonTreeDef() {
        return this.taxonTreeDef;
    }

    public void setTaxonTreeDef(TaxonTreeDef taxonTreeDef) {
        this.taxonTreeDef = taxonTreeDef;
    }

    /**
     *
     */
    public Set<Locality> getLocalities() {
        return this.localities;
    }

    public void setLocalities(Set<Locality> localities) {
        this.localities = localities;
    } 

    public Set<AppResourceDefault> getAppResourceDefaults()
    {
        return appResourceDefaults;
    }

    public void setAppResourceDefaults(Set<AppResourceDefault> appResourceDefaults)
    {
        this.appResourceDefaults = appResourceDefaults;
    }

/**
	 * toString
	 * @return String
	 */
  public String toString() {
	  StringBuffer buffer = new StringBuffer(128);

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("name").append("='").append(getName()).append("' ");
      buffer.append("]");

      return buffer.toString();
	}




    // Add Methods

    public void addCatalogSeries(final CatalogSeries catalogSeriesArg)
    {
        this.catalogSeries.add(catalogSeriesArg);
        catalogSeriesArg.getCollectionObjDefItems().add(this);
    }

    public void addAttributeDefs(final AttributeDef attributeDef)
    {
        this.attributeDefs.add(attributeDef);
        attributeDef.setCollectionObjDef(this);
    }

    public void addLocalities(final Locality localitiesArg)
    {
        this.localities.add(localitiesArg);
        localitiesArg.getCollectionObjDefs().add(this);
    }

    // Done Add Methods

    // Delete Methods

    public void removeCatalogSeries(final CatalogSeries catalogSeriesArg)
    {
        this.catalogSeries.remove(catalogSeriesArg);
        catalogSeriesArg.getCollectionObjDefItems().remove(this);
    }

    public void removeAttributeDefs(final AttributeDef attributeDef)
    {
        this.attributeDefs.remove(attributeDef);
        attributeDef.setCollectionObjDef(null);
    }

    public void removeLocalities(final Locality localitiesArg)
    {
        this.localities.remove(localitiesArg);
        localitiesArg.getCollectionObjDefs().remove(this);
       ;
    }

    // Delete Add Methods
}
