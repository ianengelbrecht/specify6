/* Copyright (C) 2017, University of Kansas Center for Research
 * 
 * Specify Software Project, specify@ku.edu, Biodiversity Institute,
 * 1345 Jayhawk Boulevard, Lawrence, Kansas, 66045, USA
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package edu.ku.brc.dbsupport;

/**
 * Simple wrapper for Hibernate's StaleObjectStateException.
 * 
 * @author rods
 *
 * @code_status Complete
 *
 */
public class StaleObjectException extends Exception
{

    /**
     * Default Constructor.
     */
    public StaleObjectException()
    {
        // node code
    }

    /**
     * Constructs with a message.
     * @param message the message
     */
    public StaleObjectException(String message)
    {
        super(message);
    }

    /**
     * Constructs with a Throwable.
     * @param cause the Throwable
     */
    public StaleObjectException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs with a message and Throwable.
     * @param message the message
     * @param cause Throwable
     */
    public StaleObjectException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
