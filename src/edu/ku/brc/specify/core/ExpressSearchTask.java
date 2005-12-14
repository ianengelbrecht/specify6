/* Filename:    $RCSfile: ExpressSearchTask,v $
 * Author:      $Author: rods $
 * Revision:    $Revision: 1.1 $
 * Date:        $Date: 2005/10/19 19:59:54 $
 *
 * This library is free software; you can redistribute it and/or
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
package edu.ku.brc.specify.core;

import static edu.ku.brc.specify.ui.UICacheManager.getResourceString;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.FSDirectory;
import org.dom4j.Element;

import edu.ku.brc.specify.core.subpane.ExpressSearchIndexerPane;
import edu.ku.brc.specify.core.subpane.ExpressSearchResultsPane;
import edu.ku.brc.specify.core.subpane.SimpleDescPane;
import edu.ku.brc.specify.helpers.XMLHelper;
import edu.ku.brc.specify.plugins.MenuItemDesc;
import edu.ku.brc.specify.plugins.ToolBarItemDesc;
import edu.ku.brc.specify.ui.SubPaneIFace;
import edu.ku.brc.specify.ui.UICacheManager;

/**
 * This task will enable the user to index there database and preform express searches
 * 
 * @author rods
 *
 */
public class ExpressSearchTask extends BaseTask
{
    // Static Data Members
    private static Log log = LogFactory.getLog(ExpressSearchTask.class);
    
    public static final String EXPRESSSEARCH = "Express_Search";
    
    // Data Members
    protected File                         lucenePath = null;
    protected Hashtable<String, TableInfo> tables = new Hashtable<String, TableInfo>();
    protected JTextField                   searchText;
    protected JButton                      searchBtn;   
    

    /**
     * Deafult Constructor
     */
    public ExpressSearchTask()
    {
        super(EXPRESSSEARCH, getResourceString(EXPRESSSEARCH));
        
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.specify.core.Taskable#initialize()
     */
    public void initialize()
    {
        if (!isInitialized)
        {
            super.initialize(); // sets isInitialized to false
            
            lucenePath = getIndexDirPath();
            
            intializeTableInfo();
        }
    }
    
    /**
     * Helper function to return the path to the express search directory
     * @return return the path to the express search directory
     */
    public static File getIndexDirPath()
    {
        File path = new File(System.getProperty("user.home")+File.separator+"Specify"+File.separator+"index-dir");
        if (!path.exists())
        {
            if (!path.mkdirs())
            {
                String msg = "unable to create directory [" + path.getAbsolutePath() + "]";
                log.error(msg); 
                throw new RuntimeException(msg);
            }
        }
        return path;
    }
    
    /**
     * Collects information about all the tables that will be processed for the express search
     *
     */
    protected void intializeTableInfo()
    {
        try
        {
            Element esDOM = XMLHelper.readDOMFromConfigDir("express_search.xml");         // Describes the definitions of the full text search
            List tableItems = esDOM.selectNodes("/tables/table");
            for ( Iterator iter = tableItems.iterator(); iter.hasNext(); ) 
            {
                Element   tableElement = (Element)iter.next();
                Element   viewElement  = (Element)tableElement.selectSingleNode("detailView");
                String    sqlStr       = viewElement.selectSingleNode("sql").getText();
                String    idStr        = tableElement.attributeValue("id");
                String    iconName     = viewElement.attributeValue("icon");
                TableInfo table        = new TableInfo(idStr, tableElement.attributeValue("title"), sqlStr, iconName);
                
                List captionItems = viewElement.selectNodes("captions/caption");
                if (captionItems.size() > 0)
                {
                    Hashtable<String, String> colNameMappings = new Hashtable<String, String>();
                    for ( Iterator capIter = captionItems.iterator(); capIter.hasNext(); ) 
                    {
                        Element captionElement = (Element)capIter.next();
                        String    col  = captionElement.attributeValue("col");
                        String    text = captionElement.attributeValue("text");
                        colNameMappings.put(col.toLowerCase(), text);
                    }
                    table.setColNameMappings(colNameMappings);
                } else
                {
                    log.info("No Captions!");
                }
                
                tables.put(idStr, table);
            }  
            
        } catch (Exception ex)
        {
            log.error(ex);
        }    
    }
    
    /**
     * Check to see of the index has been run and then enables the express search controls
     *
     */
    public void checkForIndexer()
    {
        boolean exists = lucenePath.exists() && lucenePath.list().length > 0;
        searchBtn.setEnabled(exists);
        searchText.setEnabled(exists);
    }
    
    /**
     * Displays the config pane for the express search
     *
     */
    public void showIndexerPane()
    {
        ExpressSearchIndexerPane expressSearchIndexerPane = new ExpressSearchIndexerPane(this);
        UICacheManager.getInstance().getSubPaneMgr().addPane(expressSearchIndexerPane);       
    }
    
    /**
     * Performs the express search and returns the results
     * @param searchTerm the term to be searched
     */
    public void doQuery(final String searchTerm)
    {
        
        try
        {
            // Temporary for testing
            if (searchTerm.equals("indexme"))
            {
                showIndexerPane();
                return;
            }

            PhraseQuery  query = new PhraseQuery();
            //query.setSlop(slop);
            
            StringTokenizer st = new StringTokenizer(searchTerm);
            while (st.hasMoreTokens())
            {
                String termStr = st.nextToken();
                log.info(termStr);
                query.add(new Term("contents", termStr));  
            }
            IndexSearcher searcher = new IndexSearcher(FSDirectory.getDirectory(lucenePath, false));
            Hits hits = searcher.search(query);
            
            if (hits.length() == 0)
            {
                System.out.println("No Hits for ["+searchTerm+"]");
                return;
            } 
           
            for (int i=0;i<hits.length();i++)
            {
                Document  doc       = hits.doc(i);
                String    idStr     = doc.get("table");
                TableInfo tableInfo = tables.get(idStr);
                if (tableInfo == null)
                {
                    throw new RuntimeException("Bad id from search["+idStr+"]");
                }                
                tableInfo.getRecIds().add((Integer.parseInt(doc.get("id"))));
            }
        
            ExpressSearchResultsPane expressSearchPane = new ExpressSearchResultsPane(title, this);
            for (Enumeration<TableInfo> e=tables.elements();e.hasMoreElements();)
            {
                TableInfo tableInfo = e.nextElement();
                if (tableInfo.getRecIds().size() > 0)
                {
                    expressSearchPane.addSearchResults(tableInfo.getTitle(), tableInfo.getSql(), tableInfo.getIconName(), tableInfo.getColNameMappings());
                    tableInfo.getRecIds().clear();
                }
            }
            
            UICacheManager.getInstance().getSubPaneMgr().addPane(expressSearchPane);
            
        } catch (IOException ex)
        {
            log.error(ex);
        }
        
    }
    
    //-------------------------------------------------------
    // Taskable
    //-------------------------------------------------------
    
    /* (non-Javadoc)
     * @see edu.ku.brc.specify.core.BaseTask#getStarterPane()
     */
    @Override
    public SubPaneIFace getStarterPane()
    {
        return new SimpleDescPane(name, this, "This is the Express Search Pane");
    }

    //-------------------------------------------------------
    // Plugin Interface
    //-------------------------------------------------------
    
    /* (non-Javadoc)
     * @see edu.ku.brc.specify.plugins.TaskPluginable#getToolBarItems()
     */
    @Override
    public List<ToolBarItemDesc> getToolBarItems()
    {
        Vector<ToolBarItemDesc> list = new Vector<ToolBarItemDesc>();
        
        // Create Search Panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        JPanel     searchPanel = new JPanel(gridbag);
        JLabel     spacer      = new JLabel(" ");
        
        searchBtn   = new JButton(getResourceString("Search"));
        
        searchText  = new JTextField(10);
        
        searchText.setMinimumSize(new Dimension(50, searchText.getPreferredSize().height));
        
        ActionListener doQuery = new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                String text = searchText.getText();
                if (text != null && text.length() > 0)
                {
                    doQuery(text.toLowerCase());
                }
            }
        };
        
        searchBtn.addActionListener(doQuery);
        searchText.addActionListener(doQuery);
        
        c.weightx = 1.0;
        gridbag.setConstraints(spacer, c);
        searchPanel.add(spacer);
        
        c.weightx = 0.0;
        gridbag.setConstraints(searchText, c);
        searchPanel.add(searchText);
        
        searchPanel.add(spacer);
        
        gridbag.setConstraints(searchBtn, c);
        searchPanel.add(searchBtn);
        
        list.add(new ToolBarItemDesc(searchPanel));
        
        checkForIndexer();
        
        return list;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.plugins.TaskPluginable#getMenuItems()
     */
    @Override
    public List<MenuItemDesc> getMenuItems()
    {
        return new Vector<MenuItemDesc>();
    }
    
    
    //-----------------------------------------------------------------
    // Inner Class that represents all the search results for a table
    //-----------------------------------------------------------------
    class TableInfo
    {
        protected String tableId;
        protected String title;
        protected String sqlStr;
        protected String iconName = null;
        protected Vector<Integer> recIds = new Vector<Integer>();
        protected Hashtable<String, String> colNameMappings = null;
        
        public TableInfo(final String tableId, final String title, final String sqlStr, final String iconName)
        {
            this.tableId  = tableId;
            this.title    = title;
            this.sqlStr   = sqlStr;
            this.iconName = iconName;
        }

        public String getTitle()
        {
            return title;
        }
        
        public String getSql()
        {
            
            return sqlStr.replace("%s", getRecIdList());
        }
        
        public Vector<Integer> getRecIds()
        {
            return recIds;
        }

        public void setRecIds(Vector<Integer> recIds)
        {
            this.recIds = recIds;
        }

        public String getTableId()
        {
            return tableId;
        }
        
        public String getRecIdList()
        {
            StringBuffer idsStr = new StringBuffer();
            for (int i=0;i<recIds.size();i++)
            {
                if (i > 0) idsStr.append(',');
                idsStr.append(recIds.elementAt(i).toString());
            }
            return idsStr.toString();
        }

        public String getIconName()
        {
            return iconName;
        }
        
        /**
         * Sets the hashtable for mapping names from resultset column names to more human readable names
         * @param colNameMappings the hash of name mappings
         */
        public void setColNameMappings(final Hashtable<String, String> colNameMappings)
        {
            this.colNameMappings = colNameMappings;
        }

        public Hashtable<String, String> getColNameMappings()
        {
            return colNameMappings;
        }
       
    }

}
