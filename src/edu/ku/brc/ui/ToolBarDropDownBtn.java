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
package edu.ku.brc.ui;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Toolbar button derived from DropDownBtn, this provides a way to set menu items.
 *
 * @code_status Complete
 * 
 * @author rods
 *
 */
@SuppressWarnings("serial")
public class ToolBarDropDownBtn extends DropDownButton
{
    /**
     * Creates a toolbar item with label and icon and their positions.
     * @param label label of the toolbar item
     * @param icon the icon
     * @param textPosition the position of the text as related to the icon
     */
    public ToolBarDropDownBtn(final String label, 
                              final ImageIcon icon, 
                              final int textPosition, 
                              final boolean addArrowBtn)
    {
        super(label, icon, null, textPosition, addArrowBtn);
    }

    /**
     * Creates a toolbar item with label and icon and their positions.
     * @param label label of the toolbar item
     * @param icon the icon
     * @param toolTip the toolTip
     * @param textPosition the position of the text as related to the icon
     */
    public ToolBarDropDownBtn(final String label, 
                              final ImageIcon icon, 
                              final String toolTip, 
                              final int textPosition, 
                              final boolean addArrowBtn)
    {
        super(label, icon, toolTip, textPosition, addArrowBtn);
    }

    /**
     * Creates a toolbar item with label and icon and their positions and menu items to be added.
     * The Items MUST be of class JSeparator or JMenuItem.
     * @param label label of the toolbar item
     * @param icon the icon
     * @param vertTextPosition the position of the text as related to the icon
     * @param menus the list of menu items and separators
     */
    public ToolBarDropDownBtn(final String           label, 
                              final ImageIcon        icon, 
                              final int              vertTextPosition, 
                              final List<JComponent> menus)
    {
        super(label, icon, vertTextPosition, menus);      
     }

    /**
     * Creates a toolbar item with icon.
     * @param icon the icon for the button.
     */
    public ToolBarDropDownBtn(final ImageIcon icon)
    {
        super(icon, false);

    }
 }
