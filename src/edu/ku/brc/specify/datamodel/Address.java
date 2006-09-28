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




/**

 */
public class Address extends DataModelObjBase implements java.io.Serializable {

    // Fields

    protected Long           addressId;
    protected String            address;
    protected String            address2;
    protected String            city;
    protected String            state;
    protected String            country;
    protected String            postalCode;
    protected String            remarks;
    protected Agent             agent;

    // From Agent
    protected Boolean           isPrimary;
    protected String            phone1;
    protected String            phone2;
    protected String            fax;
    protected String            roomOrBuilding;
     

    // Constructors

    /** default constructor */
    public Address() {
    }

    /** constructor with id */
    public Address(Long addressId) {
        this.addressId = addressId;
    }

    // Initializer
    @Override
    public void initialize()
    {
        addressId = null;
        address = null;
        address2 = null;
        city = null;
        state = null;
        country = null;
        postalCode = null;
        remarks = null;
        timestampModified = null;
        timestampCreated = new Date();
        lastEditedBy = null;
        agent = null;
        
        // Agent
        isPrimary = false;
        phone1 = null;
        phone2 = null;
        fax = null;
        roomOrBuilding = null;
        
    }
    // End Initializer

    // Property accessors

    /**
     *      * PrimaryKey
     */
    public Long getAddressId() {
        return this.addressId;
    }

    /**
     * Generic Getter for the ID Property.
     * @returns ID Property.
     */
    @Override
    public Long getId()
    {
        return this.addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     *      * Address as it should appear on mailing labels
     */
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     */
    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     *
     */
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     */
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     */
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    // Agent

    /**
     *
     */
    public String getPhone1() {
        return this.phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     *
     */
    public String getPhone2() {
        return this.phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     *
     */
    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     *
     */
    public String getRoomOrBuilding() {
        return this.roomOrBuilding;
    }

    public void setRoomOrBuilding(String roomOrBuilding) {
        this.roomOrBuilding = roomOrBuilding;
    }
    

    /**
     *      * Is the agent currently located at this address?
     */
    public Boolean getIsPrimary() {
        return this.isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

}
