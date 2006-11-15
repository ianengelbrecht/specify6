package edu.ku.brc.specify.datamodel;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * CollectingTrip generated by hbm2java
 */
public class CollectingTrip extends DataModelObjBase implements java.io.Serializable {

    // Fields    

     private Long collectingTripId;
     private String remarks;
     private Calendar startDate;
     private Short startDatePrecision;
     private String startDateVerbatim;
     private Calendar endDate;
     private Short endDatePrecision;
     private String endDateVerbatim;
     private Short startTime;
     private Short endTime;
     private Set<CollectingEvent> collectingEvents;


    // Constructors

    /** default constructor */
    public CollectingTrip()
    {
        // do nothing
    }
    
    /** constructor with id */
    public CollectingTrip(Long collectingTripId) {
        this.collectingTripId = collectingTripId;
    }
   
    
    // Initializer
    @Override
    public void initialize()
    {
        collectingTripId = null;
        remarks = null;
        startDate = null;
        startDatePrecision = null;
        startDateVerbatim = null;
        endDate = null;
        endDatePrecision = null;
        endDateVerbatim = null;
        startTime = null;
        endTime = null;
        collectingEvents = new HashSet<CollectingEvent>();
    }

    // End Initializer

    // Property accessors

    /**
     * 
     */
    public Long getCollectingTripId() {
        return this.collectingTripId;
    }
    
    public void setCollectingTripId(Long collectingTripId) {
        this.collectingTripId = collectingTripId;
    }

    @Override
    public Long getId()
    {
        return collectingTripId;
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
    public Calendar getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * 
     */
    public Short getStartDatePrecision() {
        return this.startDatePrecision;
    }
    
    public void setStartDatePrecision(Short startDatePrecision) {
        this.startDatePrecision = startDatePrecision;
    }

    /**
     * 
     */
    public String getStartDateVerbatim() {
        return this.startDateVerbatim;
    }
    
    public void setStartDateVerbatim(String startDateVerbatim) {
        this.startDateVerbatim = startDateVerbatim;
    }

    /**
     * 
     */
    public Calendar getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * 
     */
    public Short getEndDatePrecision() {
        return this.endDatePrecision;
    }
    
    public void setEndDatePrecision(Short endDatePrecision) {
        this.endDatePrecision = endDatePrecision;
    }

    /**
     * 
     */
    public String getEndDateVerbatim() {
        return this.endDateVerbatim;
    }
    
    public void setEndDateVerbatim(String endDateVerbatim) {
        this.endDateVerbatim = endDateVerbatim;
    }

    /**
     * 
     */
    public Short getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(Short startTime) {
        this.startTime = startTime;
    }

    /**
     * 
     */
    public Short getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(Short endTime) {
        this.endTime = endTime;
    }

    /**
     * 
     */
    public Set<CollectingEvent> getCollectingEvents() {
        return this.collectingEvents;
    }
    
    public void setCollectingEvents(Set<CollectingEvent> collectingEvents) {
        this.collectingEvents = collectingEvents;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.ui.forms.FormDataObjIFace#getTableId()
     */
    @Override
    public Integer getTableId()
    {
        return 87;
    }

}