package edu.ku.brc.specify.datamodel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;




/**
 * ExternalResource generated by hbm2java
 */
public class ExternalResource  implements java.io.Serializable {

    // Fields

     protected Integer externalResourceId;
     protected String mimeType;
     protected String fileName;
     protected Calendar fileCreatedDate;
     protected String remarks;
     protected String externalLocation;
     protected Date timestampCreated;
     protected Date timestampModified;
     protected String lastEditedBy;
     protected Set<AttributeIFace> attrs;
     protected Agent createdByAgent;
     protected Set<Agent> agents;
     protected Set<CollectionObject> collectionObjects;
     protected Set<CollectingEvent> collectinEvents;
     protected Set<Loan> loans;
     protected Set<Locality> localities;
     protected Set<Permit> permits;
     protected Set<Preparation> preparations;
     protected Set<Taxon> taxonomy;


    // Constructors

    /** default constructor */
    public ExternalResource() {
    }

    /** constructor with id */
    public ExternalResource(Integer externalResourceId) {
        this.externalResourceId = externalResourceId;
    }




    // Initializer
    public void initialize()
    {
        externalResourceId = null;
        mimeType = null;
        fileName = null;
        fileCreatedDate = null;
        remarks = null;
        externalLocation = null;
        timestampCreated = new Date();
        timestampModified = new Date();
        lastEditedBy = null;
        attrs = new HashSet<AttributeIFace>();
        createdByAgent = null;
        agents = new HashSet<Agent>();
        collectionObjects = new HashSet<CollectionObject>();
        collectinEvents = new HashSet<CollectingEvent>();
        loans = new HashSet<Loan>();
        localities = new HashSet<Locality>();
        permits = new HashSet<Permit>();
        preparations = new HashSet<Preparation>();
        taxonomy = new HashSet<Taxon>();
    }
    // End Initializer

    // Property accessors

    /**
     *
     */
    public Integer getExternalResourceId() {
        return this.externalResourceId;
    }

    public void setExternalResourceId(Integer externalResourceId) {
        this.externalResourceId = externalResourceId;
    }

    /**
     *
     */
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     *
     */
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *
     */
    public Calendar getFileCreatedDate() {
        return this.fileCreatedDate;
    }

    public void setFileCreatedDate(Calendar fileCreatedDate) {
        this.fileCreatedDate = fileCreatedDate;
    }

    /**
     *
     */
    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     *
     */
    public String getExternalLocation() {
        return this.externalLocation;
    }

    public void setExternalLocation(String externalLocation) {
        this.externalLocation = externalLocation;
    }

    /**
     *
     */
    public Date getTimestampCreated() {
        return this.timestampCreated;
    }

    public void setTimestampCreated(Date timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    /**
     *
     */
    public Date getTimestampModified() {
        return this.timestampModified;
    }

    public void setTimestampModified(Date timestampModified) {
        this.timestampModified = timestampModified;
    }

    /**
     *
     */
    public String getLastEditedBy() {
        return this.lastEditedBy;
    }

    public void setLastEditedBy(String lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    /**
     *
     */
    public Set<AttributeIFace> getAttrs() {
        return this.attrs;
    }

    public void setAttrs(Set<AttributeIFace> attrs) {
        this.attrs = attrs;
    }

    /**
     *
     */
    public Agent getCreatedByAgent() {
        return this.createdByAgent;
    }

    public void setCreatedByAgent(Agent createdByAgent) {
        this.createdByAgent = createdByAgent;
    }

    /**
     *
     */
    public Set<Agent> getAgents() {
        return this.agents;
    }

    public void setAgents(Set<Agent> agents) {
        this.agents = agents;
    }

    /**
     *
     */
    public Set<CollectionObject> getCollectionObjects() {
        return this.collectionObjects;
    }

    public void setCollectionObjects(Set<CollectionObject> collectionObjects) {
        this.collectionObjects = collectionObjects;
    }

    /**
     *
     */
    public Set<CollectingEvent> getCollectinEvents() {
        return this.collectinEvents;
    }

    public void setCollectinEvents(Set<CollectingEvent> collectinEvents) {
        this.collectinEvents = collectinEvents;
    }

    /**
     *
     */
    public Set<Loan> getLoans() {
        return this.loans;
    }

    public void setLoans(Set<Loan> loans) {
        this.loans = loans;
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

    /**
     *
     */
    public Set<Permit> getPermits() {
        return this.permits;
    }

    public void setPermits(Set<Permit> permits) {
        this.permits = permits;
    }

    /**
     *
     */
    public Set<Preparation> getPreparations() {
        return this.preparations;
    }

    public void setPreparations(Set<Preparation> preparations) {
        this.preparations = preparations;
    }

    /**
     *
     */
    public Set<Taxon> getTaxonomy() {
        return this.taxonomy;
    }

    public void setTaxonomy(Set<Taxon> taxonomy) {
        this.taxonomy = taxonomy;
    }





    // Add Methods

    public void addAttr(final ExternalResourceAttr attr)
    {
        this.attrs.add(attr);
        attr.setExternalResource(this);
    }

    /* These are the other side of the many-to-many so I don't they are needed)
    public void addAgent(final Agent agent)
    {
        this.agents.add(agent);
        agent.setExternalResource(this);
    }

    public void addCollectionObject(final CollectionObject collectionObject)
    {
        this.collectionObjects.add(collectionObject);
        collectionObject.setExternalResource(this);
    }

    public void addCollectinEvent(final CollectingEvent collectinEvent)
    {
        this.collectinEvents.add(collectinEvent);
        collectinEvent.setExternalResource(this);
    }

    public void addLoan(final Loan loan)
    {
        this.loans.add(loan);
        loan.setExternalResource(this);
    }

    public void addLocalities(final Locality localities)
    {
        this.localities.add(localities);
        localities.setExternalResource(this);
    }

    public void addPermit(final Permit permit)
    {
        this.permits.add(permit);
        permit.setExternalResource(this);
    }

    public void addPreparation(final Preparation preparation)
    {
        this.preparations.add(preparation);
        preparation.setExternalResource(this);
    }

    public void addTaxonomy(final Taxon taxonomy)
    {
        this.taxonomy.add(taxonomy);
        taxonomy.setExternalResource(this);
    }*/

    // Done Add Methods

    // Delete Methods

    public void removeAttr(final ExternalResourceAttr attr)
    {
        this.attrs.remove(attr);
        attr.setExternalResource(null);
    }

    /* These are the other side of the many-to-many so I don't they are needed)

    public void removeAgent(final Agent agent)
    {
        this.agents.remove(agent);
        agent.setExternalResource(null);
    }

    public void removeCollectionObject(final CollectionObject collectionObject)
    {
        this.collectionObjects.remove(collectionObject);
        collectionObject.setExternalResource(null);
    }

    public void removeCollectinEvent(final CollectingEvent collectinEvent)
    {
        this.collectinEvents.remove(collectinEvent);
        collectinEvent.setExternalResource(null);
    }

    public void removeLoan(final Loan loan)
    {
        this.loans.remove(loan);
        loan.setExternalResource(null);
    }

    public void removeLocalities(final Locality localities)
    {
        this.localities.remove(localities);
        localities.setExternalResource(null);
    }

    public void removePermit(final Permit permit)
    {
        this.permits.remove(permit);
        permit.setExternalResource(null);
    }

    public void removePreparation(final Preparation preparation)
    {
        this.preparations.remove(preparation);
        preparation.setExternalResource(null);
    }

    public void removeTaxonomy(final Taxon taxonomy)
    {
        this.taxonomy.remove(taxonomy);
        taxonomy.setExternalResource(null);
    }
    */

    // Delete Add Methods
}
