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

package edu.ku.brc.dbsupport;

import static edu.ku.brc.helpers.XMLHelper.getAttr;

import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.ku.brc.helpers.XMLHelper;
import edu.ku.brc.ui.forms.BusinessRulesIFace;
import edu.ku.brc.ui.forms.GenericBusRules;
import edu.ku.brc.ui.forms.formatters.UIFieldFormatterIFace;
import edu.ku.brc.util.DatamodelHelper;

/**
 * This manages all the tables and maps names to ids and can create queries for
 * recordsets. (This needs to be updated for all the tables. XXX - Meg??????).
 * <br>
 * TODO Many of the searches are linear and should be converted to bininary searches.
 * 
 * @code_status Betra
 * 
 * @author rods
 * 
 */
public class DBTableIdMgr
{
    // Static Data Members
	protected static final Logger log = Logger.getLogger(DBTableIdMgr.class);
    
    protected static  DBTableIdMgr instance = null;
    
    // Data Members
    protected Hashtable<Integer, DBTableInfo> hash   = new Hashtable<Integer, DBTableInfo>();
    protected Vector<DBTableInfo>             tables = new Vector<DBTableInfo>();
    protected boolean                         isFullSchema = true;

    /**
     * Can now be created as a standalone class to read in other types of Schema Definitions (i.e. Workbench Schema).
     */
    public DBTableIdMgr(final boolean isFullSchema)
    {
        this.isFullSchema = isFullSchema;
    }
    
    /**
     * Returns the singleton.
     * @return the singleton.
     */
    public static DBTableIdMgr getInstance()
    {
        if (instance == null)
        {
            instance = new DBTableIdMgr(true);
            instance.initialize();
        }
        return instance;
    }

    /**
     * Reads in datamodel input file and populates the hashtable of teh
     * DBTableMgr with DBTableInfo
     */
    protected void initialize()
    {
        initialize(new File(DatamodelHelper.getDatamodelFilePath()));
    }

    /**
     * Reads in datamodel input file and populates the hashtable of teh
     * DBTableMgr with DBTableInfo
     */
    public void initialize(final File inputFile)
    {
		log.debug("Reading in datamodel file: " + inputFile.getAbsolutePath() + " to create and populate DBTableMgr"); //$NON-NLS-1$ //$NON-NLS-2$
		String classname = null;
		try
		{
			//FileInputStream fileInputStream = new FileInputStream(inputFile);
			SAXReader       reader          = new SAXReader();
			reader.setValidation(false);
            
			//Document doc          = reader.read(fileInputStream);
			//Element  databaseNode = doc.getRootElement();
			String xmlStr = FileUtils.readFileToString(inputFile);
	        Element  databaseNode = XMLHelper.readStrToDOM4J(xmlStr);


			if (databaseNode != null)
			{
				for (Iterator<?> i = databaseNode.elementIterator("table"); i.hasNext();) //$NON-NLS-1$
				{
					Element tableNode = (Element) i.next();
					classname = tableNode.attributeValue("classname"); //$NON-NLS-1$
                    
					String  tablename      = tableNode.attributeValue("table"); //$NON-NLS-1$
					int     tableId        = Integer.parseInt(tableNode.attributeValue("tableid")); //$NON-NLS-1$
                    boolean isSearchable   = XMLHelper.getAttr(tableNode, "searchable", false); //$NON-NLS-1$

					String primaryKeyField = null;
                    
					// iterate through child elements of id nodes, there should only be 1
					for (Iterator<?> i2 = tableNode.elementIterator("id"); i2.hasNext();) //$NON-NLS-1$
					{
						Element idNode = (Element) i2.next();
						primaryKeyField = idNode.attributeValue("name"); //$NON-NLS-1$
					}  

					if (classname == null)
                    {
						log.error("classname is null; check input file"); //$NON-NLS-1$
                    }
					if (tablename == null)
                    {
						log.error("tablename is null; check input file"); //$NON-NLS-1$
                    }
					if (isFullSchema && primaryKeyField == null)
                    {
						log.error("primaryKeyField is null; check input file table["+tablename+"]"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
					//log.debug("Populating hashtable ID["+tableId+"]for class: " + classname+" "+ inputFile.getName());
                    
                    DBTableInfo tblInfo = new DBTableInfo(tableId, classname, tablename, primaryKeyField, tableNode.attributeValue("abbrv")); //$NON-NLS-1$
                    tblInfo.setIsSearchable(isSearchable);
                    tblInfo.setBusinessRuleName(XMLHelper.getAttr(tableNode, "businessrule", null)); //$NON-NLS-1$
                    
                    if (hash.get(tableId) != null)
                    {
                        log.error("Table ID used twice["+tableId+"]"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
					hash.put(tableId, tblInfo); 
                    tables.add(tblInfo);
                    
                    Element idElement = (Element)tableNode.selectSingleNode("id"); //$NON-NLS-1$
                    if (idElement != null)
                    {
                        tblInfo.setIdColumnName(getAttr(idElement, "column", null)); //$NON-NLS-1$
                        tblInfo.setIdFieldName(getAttr(idElement,  "name", null)); //$NON-NLS-1$
                        tblInfo.setIdType(getAttr(idElement,       "type", null)); //$NON-NLS-1$
                    }
                    
                    Element displayElement = (Element)tableNode.selectSingleNode("display"); //$NON-NLS-1$
                    if (displayElement != null)
                    {
                        tblInfo.setDefaultFormName(getAttr(displayElement,  "view", null)); //$NON-NLS-1$
                        tblInfo.setUiFormatter(getAttr(displayElement,      "uiformatter", null)); //$NON-NLS-1$
                        tblInfo.setDataObjFormatter(getAttr(displayElement, "dataobjformatter", null)); //$NON-NLS-1$
                        tblInfo.setSearchDialog(getAttr(displayElement,     "searchdlg", null)); //$NON-NLS-1$
                        tblInfo.setNewObjDialog(getAttr(displayElement,     "newobjdlg", null)); //$NON-NLS-1$
                        
                    } else
                    {
                        tblInfo.setDefaultFormName(""); //$NON-NLS-1$
                        tblInfo.setUiFormatter(""); //$NON-NLS-1$
                        tblInfo.setDataObjFormatter(""); //$NON-NLS-1$
                        tblInfo.setSearchDialog(""); //$NON-NLS-1$
                        tblInfo.setNewObjDialog(""); //$NON-NLS-1$
                    }
                    
                    for (Iterator<?> ir = tableNode.elementIterator("relationship"); ir.hasNext();) //$NON-NLS-1$
                    {
                        Element irNode = (Element) ir.next();
                        DBRelationshipInfo tblRel = new DBRelationshipInfo(
                                                            irNode.attributeValue("relationshipname"), //$NON-NLS-1$
                                                            getRelationshipType(irNode.attributeValue("type")), //$NON-NLS-1$
                                                            irNode.attributeValue("classname"), //$NON-NLS-1$
                                                            irNode.attributeValue("columnname"), //$NON-NLS-1$
                                                            irNode.attributeValue("othersidename"), //$NON-NLS-1$
                                                            irNode.attributeValue("jointable"), //$NON-NLS-1$
                                                            getAttr(irNode, "required", false), //$NON-NLS-1$
                                                            getAttr(irNode, "updatable", false)); //$NON-NLS-1$
                        tblInfo.getRelationships().add(tblRel);
                    }
                    
                    for (Iterator<?> ir = tableNode.elementIterator("field"); ir.hasNext();) //$NON-NLS-1$
                    {
                        Element irNode = (Element) ir.next();
                        
                        int len = -1;
                        String lenStr = irNode.attributeValue("length"); //$NON-NLS-1$
                        if (StringUtils.isNotEmpty(lenStr) && StringUtils.isNumeric(lenStr))
                        {
                            len = Integer.parseInt(lenStr);
                        }
                        DBFieldInfo fieldInfo = new DBFieldInfo(tblInfo,
                                                                irNode.attributeValue("column"), //$NON-NLS-1$
                                                                irNode.attributeValue("name"), //$NON-NLS-1$
                                                                irNode.attributeValue("type"), //$NON-NLS-1$
                                                                len,
                                                                getAttr(irNode, "required", false), //$NON-NLS-1$
                                                                getAttr(irNode, "updatable", false), //$NON-NLS-1$
                                                                getAttr(irNode, "unique", false), //$NON-NLS-1$
                                                                getAttr(irNode, "indexed", false)); //$NON-NLS-1$
                        tblInfo.addField(fieldInfo);
                    }
                    //Collections.sort(tblInfo.getFields());
				}
			} else
			{
				log.error("Reading in datamodel file.  SAX parser got null for the root of the document."); //$NON-NLS-1$
			}

		} catch (java.lang.NumberFormatException numEx)
		{
			log.error("Specify datamodel input file: " + DatamodelHelper.getDatamodelFilePath() //$NON-NLS-1$
					+ " failed to provide valid table id for class/table:" + classname); //$NON-NLS-1$
			log.error(numEx);
		} catch (Exception ex)
		{
			log.error(ex);
			ex.printStackTrace();
		}
        Collections.sort(tables);
        log.debug("Done Reading in datamodel file: " + DatamodelHelper.getDatamodelFilePath()); //$NON-NLS-1$
	}
    
    /**
     * Cleanups internal state.
     */
    public void cleanUp()
    {
        for (DBTableInfo ti : hash.values())
        {
            ti.cleanUp();
        }
        hash.clear();
    }
    
    /**
     * Returns the full collection of Tables. 
     * @return a collection of DBTableInfo objects
     */
    public Vector<DBTableInfo> getTables()
    {
        return tables;
    }

	/**
	 * Returns the defualt form name for a given table ID.
	 * @param id the ID of a table
	 * @return the default form name
	 */
	public String getDefaultFormNameById(final int id)
	{
		// for now the default name will
		DBTableInfo tableInfo = hash.get(id);
		if (tableInfo != null)
		{
			String defaultFormName = tableInfo.getDefaultFormName();
			if (StringUtils.isEmpty(defaultFormName))
			{
			    return tableInfo.getClassObj().getSimpleName();
			}
			return defaultFormName;
		}
		return null;
	}

	/**
	 * This looks it up by table name (not Object name) the look up is case
	 * insensitive.
	 * @param name the name
	 * @return the id of the table
	 */
	public int getIdByShortName(final String name)
	{
		for (DBTableInfo tableInfo : hash.values())
		{
			String tableName = tableInfo.getName();
			int inx = tableName.lastIndexOf('.');

			tableName = inx > -1 ? tableName.substring(inx + 1) : tableName;
			if (tableName.equalsIgnoreCase(name))
			{
				return tableInfo.getTableId();
			}
		}
		throw new RuntimeException("Couldn't find table id for table name[" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

    /**
     * This looks it up by fully specified class name the look up is case
     * sensitive.
     * 
     * @param className the full class name
     * @return the id of the table
     */
    public int getIdByClassName(final String className)
    {
        for (DBTableInfo tableInfo : hash.values())
        {
            if (tableInfo.getClassName().equalsIgnoreCase(className))
            {
                return tableInfo.getTableId();
            }
        }
        throw new RuntimeException("Couldn't find table id for table name[" + className + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This looks it up by fully specified class name the look up is case
     * sensitive.
     * 
     * @param className the full class name
     * @return the id of the table
     */
    public DBTableInfo getByClassName(final String className)
    {
        for (DBTableInfo tableInfo : hash.values())
        {
            if (tableInfo.getClassName().equalsIgnoreCase(className))
            {
                return tableInfo;
            }
        }
        return null;
    }

    /**
     * This looks it up by specified class (no path) name the look up is case sensitive.
     * 
     * @param className the full class name
     * @return the id of the table
     */
    public DBTableInfo getByShortClassName(final String shortClassName)
    {
        // for now just use a brute force linear search
        for (DBTableInfo tableInfo : hash.values())
        {
            if (tableInfo.getShortClassName().equalsIgnoreCase(shortClassName))
            {
                return tableInfo;
            }
        }
        throw new RuntimeException("Couldn't find table id for table name[" + shortClassName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the Info Object By Id.
     * @param tableId the id to look up
     * @return the table info object
     */
    public DBTableInfo getInfoById(final String tableId)
    {
        return getInfoById(Integer.parseInt(tableId));
    }

    /**
     * Returns the Info Object By Id.
     * @param tableId the id to look up
     * @return the table info object
     */
    public DBTableInfo getInfoById(final int tableId)
    {
        if (hash.get(tableId) == null)
        {
            log.error("Couldn't find tableId["+tableId+"]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return hash.get(tableId);
    }
    
    /**
     * Returns the Info Object By table name (the all lowercase name of the table).
     * @param tableName the name of the table
     * @return the table info object
     */
    public DBTableInfo getInfoByTableName(final String tableName)
    {
        // for now just use a brute force linear search
        for (DBTableInfo tblInfo : hash.values())
        {
            if (tblInfo.getName().equals(tableName))
            {
                return tblInfo;
            }
        }
        return null;
    }
    
	/**
	 * Creates a Query object for a table from a recordset, it uses an "in" clause.
    * @param recordSet the recordset containing the record ids
	 * @return a query object
	 */
	public String getQueryForTable(final RecordSetIFace recordSet)
	{
		DBTableInfo tableInfo = hash.get(recordSet.getDbTableId());
		if (tableInfo != null)
		{
			StringBuffer strBuf = new StringBuffer("from "); //$NON-NLS-1$
			strBuf.append(tableInfo.getName());
			strBuf.append(" in class "); //$NON-NLS-1$
			strBuf.append(tableInfo.getShortClassName());
			strBuf.append(" where "); //$NON-NLS-1$
			strBuf.append(tableInfo.getName());
			strBuf.append('.');
			strBuf.append(tableInfo.getPrimaryKeyName());
			strBuf.append(getInClause(recordSet));
			log.debug(strBuf.toString());
            return strBuf.toString();
		}
		return null;
	}

	/**
	 * Creates a Query object for a table from a single Record ID.
	 * @param recordId a single Record Id
	 * @return a query object
	 */
	public String getQueryForTable(final int tableId, final long recordId)
	{
		DBTableInfo tableInfo = hash.get(tableId);
		if (tableInfo != null)
		{
            StringBuffer strBuf = new StringBuffer("from "); //$NON-NLS-1$
			strBuf.append(tableInfo.getName());
			strBuf.append(" in class "); //$NON-NLS-1$
			strBuf.append(tableInfo.getShortClassName());
			strBuf.append(" where "); //$NON-NLS-1$
			strBuf.append(tableInfo.getName());
			strBuf.append('.');
			strBuf.append(tableInfo.getPrimaryKeyName());
			strBuf.append(" = " + recordId); //$NON-NLS-1$
			log.debug(strBuf.toString());
            return strBuf.toString();
        }
        return null;
	}

	/**
	 * Returns an "in" clause for a recordset
	 * 
	 * @param recordSet the recordset of ids
	 * @return a string "in" clause
	 */
	public String getInClause(final RecordSetIFace recordSet)
	{
		if (recordSet != null)
		{
			StringBuffer strBuf = new StringBuffer(" in ("); //$NON-NLS-1$
			List<RecordSetItemIFace> items = recordSet.getOrderedItems();
			if (items == null)
			{
				throw new RuntimeException("RecordSet items is null!"); //$NON-NLS-1$
			}
			int i = 0;
			for (RecordSetItemIFace rsi : items)
			{
				if (i > 0)
				{
					strBuf.append(","); //$NON-NLS-1$
				}
				strBuf.append(rsi.getRecordId());
				i++;
			}
			strBuf.append(")"); //$NON-NLS-1$
			return strBuf.toString();
		}
        // else
        return ""; //$NON-NLS-1$
	}
    
    /**
     * Converts a String to an Enum for Relationship Type
     * @param relTypeStr the string
     * @return the relationship type
     */
    public static DBRelationshipInfo.RelationshipType getRelationshipType(final String relTypeStr)
    {
        if (relTypeStr.equals("one-to-many")) //$NON-NLS-1$
        {
            return DBRelationshipInfo.RelationshipType.OneToMany;
            
        } else if (relTypeStr.equals("many-to-one")) //$NON-NLS-1$
        {
            return DBRelationshipInfo.RelationshipType.ManyToOne;
            
        } else if (relTypeStr.equals("many-to-many")) //$NON-NLS-1$
        {
            return DBRelationshipInfo.RelationshipType.ManyToMany;
            
        } else if (relTypeStr.equals("one-to-one")) //$NON-NLS-1$
        {
            return DBRelationshipInfo.RelationshipType.OneToOne;
            
        } else if (relTypeStr.equals("zero-to-one")) //$NON-NLS-1$
        {
            return DBRelationshipInfo.RelationshipType.ZeroOrOne;
        }
        return null;
    }
    
    /**
     * Returns a business rule object for a given class.
     * @param data the data that might have a business rule class
     * @return the business rule object or null
     */
    public BusinessRulesIFace getBusinessRule(Object data)
    {
        if (data != null)
        {
            return getBusinessRule(data.getClass());
        }
        return null;
    }

    /**
     * Returns a business rule object for a given class name.
     * @param classOfObj the class to look up
     * @return the business rule object or null
     */
    public BusinessRulesIFace getBusinessRule(String className)
    {
        try
        {
            return getBusinessRule(Class.forName(className));
            
        } catch (Exception ex)
        {
            //log.error(ex); // this isn't an error
        }
        return null;
    }

    /**
     * Returns a business rule object for a given class.
     * @param classOfObj the class to look up
     * @return the business rule object or null
     */
    public BusinessRulesIFace getBusinessRule(Class<?> classOfObj)
    {
        DBTableInfo ti = getByClassName(classOfObj.getName());
        if (ti != null)
        {
            String br = ti.getBusinessRuleName();
            if (StringUtils.isNotEmpty(br))
            {
                try
                {
                    return (BusinessRulesIFace)Class.forName(br).newInstance();
                    
                } catch (Exception ex)
                {
                    log.error("Bad Business Rule class name["+br+"]"); //$NON-NLS-1$ //$NON-NLS-2$
                    log.error(ex);
                }
            }
        }
        return new GenericBusRules();
    }
    
    /**
     * @return an iterator for Tables that only returns 'visible ones'
     */
    public DBInfoVisibleIterator<DBTableInfo> getVisableTabless()
    {
        return new DBInfoVisibleIterator<DBTableInfo>(tables);
    }
    
    /**
     * Returns the UIFieldFormatterIFace for a given Table Class and field Name.
     * @param tableClass the name of the class to look up
     * @param fieldName the field name
     * @return null or the formatter
     */
    public static UIFieldFormatterIFace getFieldFormatterFor(final Class<?> tableClass, final String fieldName)
    {
        DBTableInfo ti = DBTableIdMgr.getInstance().getByShortClassName(tableClass.getSimpleName());
        if (ti != null)
        {
            DBFieldInfo fi = ti.getFieldByName(fieldName);
            if (fi != null)
            {
                return fi.getFormatter();
            }
        }
        return null;
    }

}
