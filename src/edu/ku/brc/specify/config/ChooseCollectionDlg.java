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
/**
 * 
 */
package edu.ku.brc.specify.config;

import static edu.ku.brc.ui.UIRegistry.getResourceString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.ku.brc.specify.datamodel.Collection;
import edu.ku.brc.ui.CustomDialog;
import edu.ku.brc.ui.IconManager;
import edu.ku.brc.ui.UIRegistry;
import edu.ku.brc.util.Pair;

/**
 * Dialog used to select a Collection using the standard icons, not the icons that the user may have choosen.
 * 
 * @author rods
 *
 * @code_status Beta
 *
 * Created Date: Jul 17, 2008
 *
 */
public class ChooseCollectionDlg extends CustomDialog
{
    protected JList list;
    protected List<Collection>             collectionList;
    protected Hashtable<String, ImageIcon> iconHash = new Hashtable<String, ImageIcon>();
    
    /**
     * @param frame
     * @param title
     * @param isModal
     * @param contentPanel
     * @throws HeadlessException
     */
    public ChooseCollectionDlg(List<Collection> collectionList) throws HeadlessException
    {
        super((Frame)UIRegistry.getTopWindow(), getResourceString("CHOOSE_COLLECTION_TITLE"), true, OK_BTN, null);
        
        this.collectionList = collectionList;
        
        DefaultListModel model = new DefaultListModel();
        list = new JList(model);
        
        List<Pair<String, ImageIcon>> disciplinesList = IconManager.getListByType("disciplines", IconManager.IconSize.Std16); //$NON-NLS-1$
        Collections.sort(disciplinesList, new Comparator<Pair<String, ImageIcon>>() {
            public int compare(Pair<String, ImageIcon> o1, Pair<String, ImageIcon> o2)
            {
                String s1 = UIRegistry.getResourceString(o1.first);
                String s2 = UIRegistry.getResourceString(o2.first);
                return s1.compareTo(s2);
            }
        });
        
        for (Pair<String, ImageIcon> p : disciplinesList)
        {
            iconHash.put(p.first, p.second);
        }
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.ui.CustomDialog#createUI()
     */
    @Override
    public void createUI()
    {
        super.createUI();
        
        PanelBuilder    pb = new PanelBuilder(new FormLayout("f:p:g", "f:p:g,8px"));
        CellConstraints cc = new CellConstraints();
        
        for (Collection col : collectionList)
        {
            ((DefaultListModel)list.getModel()).addElement(col);
        }
        
        JScrollPane sp = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pb.add(sp, cc.xy(1, 1));
        
        list.addMouseListener(new MouseAdapter() {
            //@Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                
                if (e.getClickCount() == 2)
                {
                    okBtn.doClick();
                }
            }
        });
        
        list.addListSelectionListener(new ListSelectionListener() {
            //@Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    okBtn.setEnabled(list.getSelectedIndex() > -1);
                }
            }
        });
        
        DefaultListCellRenderer renderer = new DefaultListCellRenderer()
        {
            //@Override
            public Component getListCellRendererComponent(JList listArg,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus)
            {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Collection col = (Collection)value;
                label.setText(col.toString());
                
                DisciplineType disciplineType = DisciplineType.getDiscipline(DisciplineType.STD_DISCIPLINES.valueOf(col.getDiscipline().getName()));
                label.setIcon(iconHash.get(disciplineType.getDisciplineType().toString()));
                
                ImageIcon imgIcon = IconManager.getIcon(col.getDiscipline().getName(), IconManager.IconSize.Std24);
                if (imgIcon == null)
                {
                    imgIcon = IconManager.getIcon("Blank", IconManager.IconSize.Std24);
                }
                label.setIcon(imgIcon);
                return label;
            }
            
        };
        list.setCellRenderer(renderer);
        
        contentPanel = pb.getPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        pack();
        
        okBtn.setEnabled(false);
    }
    
    public void setSelectedIndex(final int index)
    {
        list.setSelectedIndex(index);
    }
    /**
     * @return
     */
    public Collection getSelectedObject()
    {
        return (Collection)list.getSelectedValue();
    }

}
