/**
 * 
 */
package edu.ku.brc.specify.datamodel;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Administrator
 *
 */
public class CommonNameTxCitation extends DataModelObjBase
{
    protected Integer commonNameTxCitationId;
    protected String remarks;
    protected String text1;
    protected String text2;
    protected Float number1;
    protected Float number2;
    protected Boolean yesNo1;
    protected Boolean yesNo2;
    protected ReferenceWork referenceWork;
    protected CommonNameTx commonNameTx;

    
    /**
     * Default constructor 
     */
    public CommonNameTxCitation()
    {
    	//do nothing
    }

	
    /**
	 * @return the dnaSequencingRunCitationId
	 */
    @Id
    @GeneratedValue
    @Column(name = "CommonNameTxCitationID", unique = false, nullable = false, insertable = true, updatable = true)
	public Integer getCommonNameTxCitationId()
	{
		return commonNameTxCitationId;
	}



	/**
	 * @return the remarks
	 */
    @Lob
    @Column(name = "Remarks", unique = false,nullable = true, insertable = true, updatable = true, length = 4096)
	public String getRemarks()
	{
		return remarks;
	}



	/**
	 * @return the text1
	 */
    @Column(name = "Text1", unique = false,nullable = true, insertable = true, updatable = true, length = 300)
	public String getText1()
	{
		return text1;
	}



	/**
	 * @return the text2
	 */
    @Column(name = "Text2", unique = false,nullable = true, insertable = true, updatable = true, length = 300)
	public String getText2()
	{
		return text2;
	}



	/**
	 * @return the number1
	 */
    @Column(name = "Number1", unique = false, nullable = true, insertable = true, updatable = true)
	public Float getNumber1()
	{
		return number1;
	}


	/**
	 * @return the number2
	 */
    @Column(name = "Number2", unique = false, nullable = true, insertable = true, updatable = true)
	public Float getNumber2()
	{
		return number2;
	}


	/**
	 * @return the yesNo1
	 */
    @Column(name="YesNo1",unique=false,nullable=true,updatable=true,insertable=true)
	public Boolean getYesNo1()
	{
		return yesNo1;
	}



	/**
	 * @return the yesNo2
	 */
    @Column(name="YesNo2",unique=false,nullable=true,updatable=true,insertable=true)
	public Boolean getYesNo2()
	{
		return yesNo2;
	}



	/**
	 * @return the referenceWork
	 */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ReferenceWorkID", unique = false, nullable = false, insertable = true, updatable = true)
	public ReferenceWork getReferenceWork()
	{
		return referenceWork;
	}



	/**
	 * @return the sequencingRun
	 */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "CommonNameTxID", unique = false, nullable = false, insertable = true, updatable = true)
	public CommonNameTx getCommonNameTx()
	{
		return commonNameTx;
	}



	/**
     * @return the Table ID for the class.
     */
	@Transient
    public static int getClassTableId()
    {
    	return 134;
    }
    
	/* (non-Javadoc)
	 * @see edu.ku.brc.specify.datamodel.DataModelObjBase#getDataClass()
	 */
	@Override
	@Transient
	public Class<?> getDataClass()
	{
		return CommonNameTxCitation.class;
	}

	/* (non-Javadoc)
	 * @see edu.ku.brc.specify.datamodel.DataModelObjBase#getId()
	 */
	@Override
	@Transient
	public Integer getId()
	{
		return commonNameTxCitationId;
	}

	/* (non-Javadoc)
	 * @see edu.ku.brc.specify.datamodel.DataModelObjBase#getTableId()
	 */
	@Override
	@Transient
	public int getTableId()
	{
		return getClassTableId();
	}

	
	/**
	 * @param commonNameTxCitationId the commonNameTxCitationId to set
	 */
	public void setCommonNameTxCitationId(Integer commonNameTxCitationId)
	{
		this.commonNameTxCitationId = commonNameTxCitationId;
	}


	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/**
	 * @param text1 the text1 to set
	 */
	public void setText1(String text1)
	{
		this.text1 = text1;
	}


	/**
	 * @param text2 the text2 to set
	 */
	public void setText2(String text2)
	{
		this.text2 = text2;
	}


	/**
	 * @param number1 the number1 to set
	 */
	public void setNumber1(Float number1)
	{
		this.number1 = number1;
	}


	/**
	 * @param number2 the number2 to set
	 */
	public void setNumber2(Float number2)
	{
		this.number2 = number2;
	}


	/**
	 * @param yesNo1 the yesNo1 to set
	 */
	public void setYesNo1(Boolean yesNo1)
	{
		this.yesNo1 = yesNo1;
	}


	/**
	 * @param yesNo2 the yesNo2 to set
	 */
	public void setYesNo2(Boolean yesNo2)
	{
		this.yesNo2 = yesNo2;
	}


	/**
	 * @param referenceWork the referenceWork to set
	 */
	public void setReferenceWork(ReferenceWork referenceWork)
	{
		this.referenceWork = referenceWork;
	}


	/**
	 * @param commonNameTx the commonNameTx to set
	 */
	public void setCommonNameTx(CommonNameTx commonNameTx)
	{
		this.commonNameTx = commonNameTx;
	}


	/* (non-Javadoc)
	 * @see edu.ku.brc.specify.datamodel.DataModelObjBase#initialize()
	 */
	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub
		commonNameTxCitationId = null;
		remarks = null;
		text1 = null;
		text2 = null;
		number1 = null;
		number2 = null;
		yesNo1 = null;
		yesNo2 = null;
		referenceWork = null;
		commonNameTx = null;
	}

}
