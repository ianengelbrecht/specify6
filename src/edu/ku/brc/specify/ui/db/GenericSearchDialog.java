/*
 * Filename:    $RCSfile: GenericSearchDialog.java,v $
 * Author:      $Author: rods $
 * Revision:    $Revision: 1.3 $
 * Date:        $Date: 2005/10/20 12:53:02 $
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
package edu.ku.brc.specify.ui.db;

import static edu.ku.brc.specify.ui.UICacheManager.getResourceString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Hits;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.ku.brc.specify.core.NavBoxLayoutManager;
import edu.ku.brc.specify.datamodel.RecordSet;
import edu.ku.brc.specify.datamodel.RecordSetItem;
import edu.ku.brc.specify.dbsupport.HibernateUtil;
import edu.ku.brc.specify.tasks.ExpressResultsTableInfo;
import edu.ku.brc.specify.tasks.ExpressSearchTask;
import edu.ku.brc.specify.tasks.subpane.ExpressSearchResultsPaneIFace;
import edu.ku.brc.specify.tasks.subpane.ExpressTableResults;
import edu.ku.brc.specify.tasks.subpane.ExpressTableResultsBase;
import edu.ku.brc.specify.ui.UICacheManager;
import edu.ku.brc.specify.ui.forms.ViewFactory;
import edu.ku.brc.specify.ui.forms.ViewMgr;
import edu.ku.brc.specify.ui.forms.Viewable;
import edu.ku.brc.specify.ui.forms.persist.View;

/**
 * This is a "generic" or more specifically "configurable" search dialog class. This enables you to specify a form to be used to enter the search criteria
 * and then the search definition it is to use to do the search and display the results as a table in the dialog. The resulting class is to be passed in
 * on construction so the results of the search can actually yield a Hibernate object.
 *
 * @author rods
 *
 */
@SuppressWarnings("serial")
public class GenericSearchDialog extends JDialog implements ActionListener, ExpressSearchResultsPaneIFace
{
    private static Log log  = LogFactory.getLog(GenericSearchDialog.class);

    // Form Stuff
    protected View           formView = null;
    protected Viewable       form     = null;
    protected List<String>   fieldNames;
    
    // Members needed for creating results
    protected String         className;
    protected String         idFieldName;
    protected String         searchName;
    
    // UI
    protected boolean        isCancelled    = true;
    protected JButton        cancelBtn;
    protected JButton        okBtn;
    protected JTextField     searchText;

    protected JPanel         contentPanel;
    protected JScrollPane    scrollPane;
    protected JTable         table;

    protected JButton        searchBtn;
    protected Color          textBGColor    = null;
    protected Color          badSearchColor = new Color(255,235,235);

    protected Hashtable<String, ExpressResultsTableInfo> tables = new Hashtable<String, ExpressResultsTableInfo>();
    protected ExpressResultsTableInfo  tableInfo;
    protected ExpressTableResultsBase  etrb;

    protected RecordSet      recordSet      = null;
    protected String         sqlStr;

    protected Hashtable<String, Object> dataMap = new Hashtable<String, Object>();

    /**
     * Constructs a search dialog from form infor and from search info
     * @param viewSetName the viewset name
     * @param viewName the form name from the viewset
     * @param searchName the search name, this is looked up by name in the "search_config.xml" file
     * @param title the title (should be already localized before passing in)
     * @param className the name of the class to be created from the selected results
     * @param idFieldName the name of the field in the clas that is the primary key which is filled in from the search table id
     * @throws HeadlessException an exception
     */
    public GenericSearchDialog(final String viewSetName, 
                               final String viewName, 
                               final String searchName,
                               final String title,
                               final String className,
                               final String idFieldName) throws HeadlessException
    {
        super((Frame)UICacheManager.get(UICacheManager.FRAME), title, true);
        
        this.className   = className;
        this.idFieldName = idFieldName;  
        this.searchName  = searchName;  

        Hashtable<String, ExpressResultsTableInfo> tmpTables = ExpressSearchTask.intializeTableInfo();
        for (Enumeration<ExpressResultsTableInfo> e=tmpTables.elements();e.hasMoreElements();)
        {
            ExpressResultsTableInfo tblInfo = e.nextElement();
            if (tblInfo.getName().equals(searchName))
            {
                tableInfo = tblInfo;
                tableInfo.setViewSQLOverridden(true);
                tables.put(tableInfo.getTableId(), tableInfo);
                sqlStr = tableInfo.getViewSql();
            }
        }

        createUI(viewSetName, viewName, title);
        
        setLocationRelativeTo((JFrame)(Frame)UICacheManager.get(UICacheManager.FRAME));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);


    }

    /**
     * Creates the Default UI
     *
     */
    protected void createUI(final String viewSetName, 
                            final String viewName, 
                            final String title)
    {
        searchText = new JTextField(30);
        searchBtn  = new JButton(getResourceString("Search"));
        ActionListener doQuery = new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                contentPanel.removeAll();

                form.getDataFromUI();

                StringBuilder strBuf = new StringBuilder(256);
                int cnt = 0;
                String[] columnNames = tableInfo.getColNames();
                for (String colName : columnNames)
                {
                  Object value  = dataMap.get(colName);
                  if (value != null)
                  {
                      String valStr = value.toString();
                      if (valStr.length() > 0)
                      {
                          if (cnt > 0)
                          {
                              strBuf.append(" OR ");
                          }
                          strBuf.append(" lower("+colName+") like '%"+valStr+"%'");
                          cnt++;
                      }
                  }

                }
                String fullSQL = sqlStr.replace("%s", strBuf.toString());
                tableInfo.setViewSql(fullSQL);
                log.info(fullSQL);
                setUIEnabled(false);
                addSearchResults(tableInfo, null);
            }
        };

        searchBtn.addActionListener(doQuery);
        searchText.addActionListener(doQuery);
        searchText.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (searchText.getBackground() != textBGColor)
                {
                    searchText.setBackground(textBGColor);
                }
            }
        });

        formView = ViewMgr.getView(viewSetName, viewName);
        if (formView != null)
        {
            form = ViewFactory.createFormView(null, formView, null, dataMap);
            add(form.getUIComponent(), BorderLayout.CENTER);

        } else
        {
            log.info("Couldn't load form with name ["+viewSetName+"] Id ["+viewName+"]");
        }
        
        fieldNames = new ArrayList<String>();
        form.getFieldNames(fieldNames);
        for (String fieldName : fieldNames)
        {
            Component comp = form.getComp(fieldName);
            if (comp instanceof JTextField)
            {
                ((JTextField)comp).addActionListener(doQuery);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));

        PanelBuilder    builder    = new PanelBuilder(new FormLayout("p,1dlu,p", "p,2dlu,p,2dlu,p"));
        CellConstraints cc         = new CellConstraints();

        //builder.addSeparator(getResourceString("AgentSearchTitle"), cc.xywh(1,1,3,1));
        builder.add(form.getUIComponent(), cc.xy(1,1));
        builder.add(searchBtn, cc.xy(3,1));

        panel.add(builder.getPanel(), BorderLayout.NORTH);
        contentPanel = new JPanel(new NavBoxLayoutManager(0,2));


        scrollPane = new JScrollPane(contentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(300,200));

        // Bottom Button UI
        cancelBtn = new JButton(getResourceString("Cancel"));
        okBtn = new JButton(getResourceString("OK"));

        okBtn.addActionListener(this);
        getRootPane().setDefaultButton(okBtn);

        ButtonBarBuilder btnBuilder = new ButtonBarBuilder();
        btnBuilder.addGlue();
        btnBuilder.addGriddedButtons(new JButton[] { cancelBtn, okBtn });

        cancelBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                setVisible(false);
            }
        });

        panel.add(btnBuilder.getPanel(), BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        updateUI();
    }

    /**
     * Updates the OK button (sets whether it is enabled by checking to see if there is a recordset
     */
    protected void updateUI()
    {
        okBtn.setEnabled(recordSet != null && recordSet.getItems().size() == 1);
        
    }

    protected void setUIEnabled(final boolean enabled)
    {
        form.getFieldNames(fieldNames);
        for (String fieldName : fieldNames)
        {
            Component comp = form.getComp(fieldName);
            if (comp instanceof JTextField)
            {
                ((JTextField)comp).setEnabled(enabled);
            }
        }
        searchBtn.setEnabled(enabled);
    }
    
    /**
     * Returns whether the dialog was cancelled
     * @return whether the dialog was cancelled
     */
    public boolean isCancelled()
    {
        return isCancelled;
    }
    
    /**
     * Return the selected object 
     * @return the selected object 
     */
    public Object getSelectedObject()
    {
        if (!isCancelled && recordSet != null && recordSet.getItems().size() > 0)
        {
            RecordSetItem item = (RecordSetItem)recordSet.getItems().iterator().next();
            try
            {
                log.info("getSelectedObject class["+className+"] idFieldName["+idFieldName+"] id["+item.getRecordId()+"]");
                
                Class classObj = Class.forName(className);
                SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                Session session = sessionFactory.openSession();
               
                Criteria criteria = session.createCriteria(classObj);
                criteria.add(Expression.eq(idFieldName, Integer.parseInt(item.getRecordId())));
                java.util.List list = criteria.list();
                session.close();
                
                if (list.size() == 1)
                {
                    return list.get(0);
                } else
                {
                    throw new RuntimeException("Why would more than one object be found in GenericSearchDialog?");
                }
            } catch (Exception ex)
            {
                log.error(ex);
            }

        }
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.tasks.subpane.ExpressSearchResultsPaneIFace#addSearchResults(edu.ku.brc.specify.tasks.ExpressResultsTableInfo, org.apache.lucene.search.Hits)
     */
    public void addSearchResults(final ExpressResultsTableInfo tableInfo, final Hits hits)
    {
        recordSet = null;
        
        updateUI();

        contentPanel.add(etrb = new ExpressTableResults(this, tableInfo, false));
        
        table = etrb.getTable();
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e)
                {
                    if (etrb != null && !e.getValueIsAdjusting())
                    {
                        recordSet = etrb.getRecordSet(false);
                        for (Object obj : recordSet.getItems())
                        {
                            RecordSetItem rsi = (RecordSetItem)obj;
                            System.out.println(rsi.getRecordId());
                        }

                    } else
                    {
                        recordSet = null;
                    }
                    updateUI();
                }});
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    okBtn.doClick(); //emulate button click
                }
            }
        });
        setUIEnabled(true);
        repaint();
    }


    /* (non-Javadoc)
     * @see edu.ku.brc.specify.tasks.subpane.ExpressSearchResultsPaneIFace#removeTable(edu.ku.brc.specify.tasks.subpane.ExpressTableResultsBase)
     */
    public void removeTable(ExpressTableResultsBase table)
    {
        contentPanel.remove(table);
        contentPanel.invalidate();
        contentPanel.doLayout();
        contentPanel.repaint();

        scrollPane.revalidate();
        scrollPane.doLayout();
        scrollPane.repaint();
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.specify.tasks.subpane.ExpressSearchResultsPaneIFace#revalidateScroll()
     */
    public void revalidateScroll()
    {
        contentPanel.invalidate();
        scrollPane.revalidate();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        // Handle clicks on the OK and Cancel buttons.
        isCancelled = e.getSource() == cancelBtn;
        setVisible(false);
    }

}
