/* Filename:    $RCSfile: MainPanel.java,v $
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

import java.util.Vector;
import edu.ku.brc.specify.ui.*;

/**
 * Manages the task context of the UI. The task context is controlled by what tab is visible in the main pane
 * When tasks are registered they are asked for the NavBoxes and those are placed in the NavBox Manager. when
 * they are unregistered the NavBoxes are removed
 * 
 * Status: Finished
 * 
 * @author rods
 * 
 */
public class ContextMgr
{
    // Static Data Members
    private static ContextMgr instance = new ContextMgr();
    
    // Data Members
    protected Taskable         currentContext = null;
    protected Vector<Taskable> tasks          = new Vector<Taskable>();
    
    /**
     * Protected Constructor of Singleton
     *
     */
    protected ContextMgr()
    { 
    }
    
    /**
     * Returns singleton
     * @return returns singleton of Context Manager
     */ 
    public static ContextMgr getInstance()
    {
        return instance;
    }
    
    /**
     * Request for a change in context
     * @param task the task requesting the context
     */
    public void requestContext(Taskable task)
    {
        if (currentContext != null)
        {
            NavBoxMgr.getInstance().unregister(currentContext);
        }
        
        if (task != null)
        {
            NavBoxMgr.getInstance().register(task);
            register(task);
        }
        
        currentContext = task;
        
    }
    
    /**
     * Registers a task
     * @param task the task to be register
     */
    protected void register(Taskable task)
    {
        tasks.addElement(task);
    }
    
    /**
     *  Unregisters a task. Checks to see if it is the current task, if so then it unregisters the NavBoxes
     * @param task the task to be unregistered
     */
    public void unregister(Taskable task)
    {
        if (currentContext == task)
        {    
            NavBoxMgr.getInstance().unregister(currentContext);
            currentContext = null;
        }
        tasks.removeElement(task);
    }
}
