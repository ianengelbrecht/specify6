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

import java.util.Calendar;
import java.util.Date;




/**

 */
public class ExchangeIn extends DataModelObjBase implements java.io.Serializable {

    // Fields    

     protected Long exchangeInId;
     protected Calendar exchangeDate;
     protected Short quantityExchanged;
     protected String descriptionOfMaterial;
     protected String remarks;
     protected String text1;
     protected String text2;
     protected Float number1;
     protected Float number2;
     protected Boolean yesNo1;
     protected Boolean yesNo2;
     protected Agent agentReceivedFrom;
     protected Agent agentCatalogedBy;


    // Constructors

    /** default constructor */
    public ExchangeIn() {
    }
    
    /** constructor with id */
    public ExchangeIn(Long exchangeInId) {
        this.exchangeInId = exchangeInId;
    }
   
    
    

    // Initializer
    public void initialize()
    {
        exchangeInId = null;
        exchangeDate = null;
        quantityExchanged = null;
        descriptionOfMaterial = null;
        remarks = null;
        text1 = null;
        text2 = null;
        number1 = null;
        number2 = null;
        timestampCreated = new Date();
        timestampModified = null;
        lastEditedBy = null;
        yesNo1 = null;
        yesNo2 = null;
        agentReceivedFrom = null;
        agentCatalogedBy = null;
    }
    // End Initializer

    // Property accessors

    /**
     *      * Primary key
     */
    public Long getExchangeInId() {
        return this.exchangeInId;
    }

    /**
     * Generic Getter for the ID Property.
     * @returns ID Property.
     */
    public Long getId()
    {
        return this.exchangeInId;
    }
    
    public void setExchangeInId(Long exchangeInId) {
        this.exchangeInId = exchangeInId;
    }

    /**
     *      * Date exchange was received
     */
    public Calendar getExchangeDate() {
        return this.exchangeDate;
    }
    
    public void setExchangeDate(Calendar exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    /**
     *      * Number of items received
     */
    public Short getQuantityExchanged() {
        return this.quantityExchanged;
    }
    
    public void setQuantityExchanged(Short quantityExchanged) {
        this.quantityExchanged = quantityExchanged;
    }

    /**
     * 
     */
    public String getDescriptionOfMaterial() {
        return this.descriptionOfMaterial;
    }
    
    public void setDescriptionOfMaterial(String descriptionOfMaterial) {
        this.descriptionOfMaterial = descriptionOfMaterial;
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
     *      * User definable
     */
    public String getText1() {
        return this.text1;
    }
    
    public void setText1(String text1) {
        this.text1 = text1;
    }

    /**
     *      * User definable
     */
    public String getText2() {
        return this.text2;
    }
    
    public void setText2(String text2) {
        this.text2 = text2;
    }

    /**
     *      * User definable
     */
    public Float getNumber1() {
        return this.number1;
    }
    
    public void setNumber1(Float number1) {
        this.number1 = number1;
    }

    /**
     *      * User definable
     */
    public Float getNumber2() {
        return this.number2;
    }
    
    public void setNumber2(Float number2) {
        this.number2 = number2;
    }


    /**
     *      * User definable
     */
    public Boolean getYesNo1() {
        return this.yesNo1;
    }
    
    public void setYesNo1(Boolean yesNo1) {
        this.yesNo1 = yesNo1;
    }

    /**
     *      * User definable
     */
    public Boolean getYesNo2() {
        return this.yesNo2;
    }
    
    public void setYesNo2(Boolean yesNo2) {
        this.yesNo2 = yesNo2;
    }

    /**
     *      * Agent ID of organization that sent material
     */
    public Agent getAgentReceivedFrom() {
        return this.agentReceivedFrom;
    }
    
    public void setAgentReceivedFrom(Agent agentReceivedFrom) {
        this.agentReceivedFrom = agentReceivedFrom;
    }

    /**
     *      * Agent ID of person recording the exchange
     */
    public Agent getAgentCatalogedBy() {
        return this.agentCatalogedBy;
    }
    
    public void setAgentCatalogedBy(Agent agentCatalogedBy) {
        this.agentCatalogedBy = agentCatalogedBy;
    }





    // Add Methods

    // Done Add Methods

    // Delete Methods

    // Delete Add Methods
}
