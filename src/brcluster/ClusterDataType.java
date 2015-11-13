//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// brCluster - The easy, generic and free data clustering library
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Copyright (C) 2005  Henrique Santos Camargos Rocha
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// For more information or contact, visit the project page on the sourceforge
// http://sourceforge.net/projects/brcluster/
//
// [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
// in the United States and other countries.]
//

/*
 * ClusterDataType.java
 *
 * Created on 27 de Maio de 2005, 17:30
 */

package brcluster;
import java.util.Collection;

/**
 * A class that encapsulates most methods from the ClusterDataInterface.
 */
public abstract class ClusterDataType implements ClusterDataInterface{
    
    /** The Cluster who owns this data **/
    protected ClusterType Cluster; //The clusters which this data belongs to
    //protected Vector<FuzzyClusterType> FuzzyClusters; 
    
    /** Creates a new instance of ClusterDataType */
    public ClusterDataType() {
        Cluster=null;
    }
    
    /**
     * Returns the distance (unsimilarity) between this and the object.
     *
     * @param obj  The data to be compare with
     *
     * @return The distance value as a double primitive
     *
     * @throws jClusterException in case the parameter is incompatible with this class.
     */
    @Override
    public abstract double distance(ClusterDataInterface obj) throws brClusterException;
        
    
    /**
     * Returns the cluster who owns this data. Note: if the
     * data has multiple clusters then the result is undefined.
     *
     * @return The cluster who owns this data (<code>null</code> permitted).
     */
    @Override
    public ClusterType getCluster(){
        return Cluster;
    }
    
   /**
     * Sets the cluster who owns this data. 
     *
     * @param C The cluster who owns this data (<code>null</code> permitted).
     */
    @Override
    public void setCluster(ClusterType C){
        Cluster=C;        
    }
    
    /**
     * Tests if this object is equal to another.
     *
     * @param obj  the object to test against for equality (<code>null</code>
     *             permitted).
     *
     * @return A boolean.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     * 
     * @throws CloneNotSupportedException not thrown by this class, but 
     *         subclasses may differ.
     */
    @Override
    public abstract Object clone();
    
    /**
     * Calculates the center of the Data and store in this.
     *
     * @param Data  The data.
     *     
     * @return True if the center has changed.
     */
    @Override
    public abstract boolean calculateCenter(Collection<ClusterDataInterface> Data);
    
    
}
