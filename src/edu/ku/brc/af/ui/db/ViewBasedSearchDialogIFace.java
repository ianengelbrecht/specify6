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
package edu.ku.brc.af.ui.db;

import java.util.List;

import javax.swing.JDialog;

/**
 * This interface represents a class that is used for searching for a data object. Objects implementing this interface 
 * are created by a factory imlpementing the ViewBasedDialogFactoryIFace interface. 
 * 
 *
 * @code_status Complete
 * 
 * @author rods
 *
 */
public interface ViewBasedSearchDialogIFace
{
    /**
     * Returns the dialog object for this interface (usually returns 'this')
     * @return the dialog object for this interface (usually returns 'this')
     */
    public abstract JDialog getDialog();
    
    /**
     * Returns whether the dialog was cancelled
     * @return whether the dialog was cancelled
     */
    public abstract boolean isCancelled();
    
    /**
     * Return the selected object 
     * @return the selected object 
     */
    public abstract Object getSelectedObject();
    
    /**
     * Return the selected objects (but only if setMultipleSelection is set to true).
     * @return the list of selected objects
     */
    public abstract List<Object> getSelectedObjects();
    
    /**
     * Sets the title of the dialog.
     * @param title the title (already localized)
     */
    public abstract void setTitle(String title);

    /**
     * Registers an interface that can be asked for the Query string and the results info.
     * @param builder the builder object
     */
    public abstract void registerQueryBuilder(ViewBasedSearchQueryBuilderIFace builder);
    
    /**
     * @param helpContext
     */
    public abstract void setHelpContext(String helpContext);
    
    /**
     * @param isMulti
     */
    public abstract void setMultipleSelection(boolean isMulti);
}
