/* Copyright (C) 2017, University of Kansas Center for Research
 * 
 * Specify Software Project, specify@ku.edu, Biodiversity Institute,
 * 1345 Jayhawk Boulevard, Lawrence, Kansas, 66045, USA
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package edu.ku.brc.specify.datamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

/**

 */
@Entity
@org.hibernate.annotations.Entity(dynamicInsert=true, dynamicUpdate=true)
@org.hibernate.annotations.Proxy(lazy = false)
@Table(name = "loanagent", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "Role", "LoanID", "AgentID" }) 
        })
@org.hibernate.annotations.Table(appliesTo="loanagent", indexes =
    {   @Index (name="LoanAgDspMemIDX", columnNames={"DisciplineID"})
    })
public class LoanAgent extends DisciplineMember implements java.io.Serializable {

    // Fields    

     protected Integer loanAgentId;
     protected String  role;
     protected String  remarks;
     protected Loan    loan;
     protected Agent   agent;


    // Constructors

    /** default constructor */
    public LoanAgent() {
        //
    }
    
    /** constructor with id */
    public LoanAgent(Integer loanAgentId) {
        this.loanAgentId = loanAgentId;
    }
   
    
    

    // Initializer
    @Override
    public void initialize()
    {
        super.init();
        loanAgentId = null;
        role = null;
        remarks = null;
        loan = null;
        agent = null;
    }
    // End Initializer


    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.DataModelObjBase#forceLoad()
     */
    @Override
    public void forceLoad()
    {
        agent.getId();
        loan.getId();
    }
    
    // Property accessors

    /**
     * 
     */
    @Id
    @GeneratedValue
    @Column(name = "LoanAgentID", unique = false, nullable = false, insertable = true, updatable = true)
    public Integer getLoanAgentId() {
        return this.loanAgentId;
    }

    /**
     * Generic Getter for the ID Property.
     * @returns ID Property.
     */
    @Transient
    @Override
    public Integer getId()
    {
        return this.loanAgentId;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.ui.forms.FormDataObjIFace#getDataClass()
     */
    @Transient
    @Override
    public Class<?> getDataClass()
    {
        return LoanAgent.class;
    }
    
    public void setLoanAgentId(Integer loanAgentId) {
        this.loanAgentId = loanAgentId;
    }

    /**
     *      * Role the agent played in the loan
     */
    @Column(name = "Role", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
    public String getRole() {
        return this.role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 
     */
    @Lob
    @Column(name = "Remarks", length = 4096)
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     *      * ID of loan agent at AgentID played a role in
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "LoanID", unique = false, nullable = false, insertable = true, updatable = true)
    public Loan getLoan() {
        return this.loan;
    }
    
    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    /**
     *      * Address of agent
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "AgentID", unique = false, nullable = false, insertable = true, updatable = true)
    public Agent getAgent() {
        return this.agent;
    }
    
    public void setAgent(Agent agent) {
        this.agent = agent;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.DataModelObjBase#getParentTableId()
     */
    @Override
    @Transient
    public Integer getParentTableId()
    {
        return Loan.getClassTableId();
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.datamodel.DataModelObjBase#getParentId()
     */
    @Override
    @Transient
    public Integer getParentId()
    {
        return loan != null ? loan.getId() : null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.ui.forms.FormDataObjIFace#getTableId()
     */
    @Override
    @Transient
    public int getTableId()
    {
        return getClassTableId();
    }
    
    /**
     * @return the Table ID for the class.
     */
    public static int getClassTableId()
    {
        return 53;
    }


}
