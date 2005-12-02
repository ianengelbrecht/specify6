/* Filename:    $RCSfile: IconEntry.java,v $
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
package edu.ku.brc.specify.ui;

import java.awt.Image;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ku.brc.specify.ui.IconManager.IconSize;

public class IconEntry
{
    private static Log log = LogFactory.getLog(IconManager.class);
    
    private String name;
    private Hashtable<Integer, ImageIcon> icons = new Hashtable<Integer, ImageIcon>();
    
    /**
     * 
     * @param name the name of the icon entry
     */
    public IconEntry(final String name)
    {
        this.name = name;
    }


    /**
     * Returns an icon for a given size in pixels
     * @param size the size of the icon
     * @return the icon for that size in pixels
     */
    public ImageIcon getIcon(final Integer size)
    {
        ImageIcon icon = icons.get(size);
        //if (icon == null)
        //{
        //    icon = icons.get(size);
        //}
        return icon;
    }
    
    /**
     * Adds an icon of a particular size
     * @param size the size in pixels
     * @param icon the icon to be added
     */
    public void add(final Integer size, final ImageIcon icon)
    {
        icons.put(size, icon);
    }


    /**
     * Gets a scaled icon and if it doesn't exist it creates one and scales it
     * @param iconSize the standard size in pixels
     * @param scaledIconSize the new scaled size in pixels
     * @return the scaled icon
     */
    public ImageIcon getScaledIcon(final IconSize iconSize, final IconSize scaledIconSize)
    {
        ImageIcon icon = icons.get(iconSize.size());
        if (icon != null)
        {
            ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(scaledIconSize.size(), scaledIconSize.size(), Image.SCALE_SMOOTH));
            if (scaledIcon != null)
            {
                icons.put(scaledIconSize.size(), scaledIcon);
                return scaledIcon;
            } else
            {
                
                log.error("Can't scale icon ["+iconSize+"] to ["+scaledIconSize+"]");
            }
            
        } else
        {
            log.error("Couldn't find icon ["+iconSize+"] to scale to ["+scaledIconSize+"]");
        }
        return null;
    }
}
