/* Copyright (C) 2023, Specify Collections Consortium
 * 
 * Specify Collections Consortium, Biodiversity Institute, University of Kansas,
 * 1345 Jayhawk Boulevard, Lawrence, Kansas, 66045, USA, support@specifysoftware.org
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
 * @author rods
 *
 * @code_status Alpha
 *
 */
public interface RecordSetItemIFace extends Comparable<RecordSetItemIFace>
{

    /**
     * @return the record ID of the record it references
     */
    public abstract Integer getRecordId();

    /**
     * @param recordId sets the record ID of the item it references
     */
    public abstract void setRecordId(final Integer recordId);
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(RecordSetItemIFace obj);
    
    /**
     * Clears the reference to the Parent RecordSetIFace
     */
    public abstract void clearParentReference();

}
