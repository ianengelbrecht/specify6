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
package edu.ku.brc.ui.forms;

import java.util.List;

/**
 * This interface represents the actions that can be performed on a data object by a business rules object.<br>
 * NOTE: This cannot be turned into a generic because of how it is called.
 *
 * @code_status Beta
 * 
 * @author rods
 *
 */
public interface BusinessRulesIFace
{

    /**
     * The status of the processing of the business rules.
     */
    public enum STATUS {None, OK, Warning, Error};
    
    /**
     * Processes the business rules for the data object.
     * @param dataObj the data object for rthe rules to be processed on.
     * @return the result status after processing the busniess rules.
     */
    public STATUS processBusinessRules(Object dataObj);
    
    /**
     * Returns a list of warnings and errors after processing the business rules.
     * @return a list of warnings and errors after processing the business rules.
     */
    public List<String> getWarningsAndErrors();
    
    /**
     * Returns a 
     * @param dataObj
     * @return
     */
    public List<BusinessRulesDataItem> getStandAloneDataItems(Object dataObj);
    
    /**
     * Some complex data object may have sub-object created that can be saved indepently of 
     * the main object itself. The business rules can determine this and ask fot the objects to be saved.
     * The implementation can optional ignore this method and choose to do nothing.
     * @param dataObj the data object to be checked.
     */
    public void saveStandAloneData(Object dataObj, List<BusinessRulesDataItem> list);
    
    /**
     * Asks if the object can be deleted.
     * @param dataObj the data object in question
     * @return true if it can be deleted, false if not
     */
    public boolean okToDelete(Object dataObj);
    
    
    /**
     * Returns a message for the user describing what was deleted (intended to be a single line of text).
     * @param dataObj the data object that will be or has been deleted but still continas its values
     * @return the single line text string
     */
    public String deleteMsg(Object dataObj);
    
}
