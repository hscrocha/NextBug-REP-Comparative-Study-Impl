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
 * ClusterType.java
 *
 * Created on 27 de Maio de 2005, 17:32
 */

package brcluster;
import java.util.ArrayList;

/**
 *
 * @author  Henrique
 */
public class ClusterType {
    
    public static final int MINIMUM_DISTANCE_MEASURE = 1;
    public static final int MAXIMUM_DISTANCE_MEASURE = 2;
    //public static final int MEDIAN_DISTANCE_MEASURE = 3;
    
    /** The data that belongs to this cluster **/
    protected ArrayList<ClusterDataInterface> DataSet;
    
    /** This cluster center (<code>null</code> permitted)**/
    protected ClusterDataInterface Center;
    
    /** The membership values for the clustering data (Used only in Fuzzy-Clustering)**/
    //protected ArrayList<Double> DataMembership;
    
        
    /** 
     * Constructs a new ClusterType without any data or center 
     */
    public ClusterType() {
        this(new ArrayList<ClusterDataInterface>(),null);
    }
    
    /**
     * Constructs a new ClusterType with the specified center
     *
     * @param Center  The center of this cluster
     */
    public ClusterType(ClusterDataInterface Center){
        this(new ArrayList<ClusterDataInterface>(),Center);
    }
    
    /**
     * Constructs a new ClusterType with the specified data and center
     *
     * @param Dataset The cluster data.
     * @param Center  The cluster center (<code>null</code> permitted). 
     */    
    public ClusterType(ArrayList<ClusterDataInterface> Dataset, ClusterDataInterface Center){
         this.setDataSet(Dataset);
         this.setCenter(Center); 
         //DataMembership=new ArrayList<Double>();
    }
    
    /**
     * Adds an item to this cluster dataset
     *
     * @param Item The item to be added. (<code>null</code> not permitted).
     */
    public void add(ClusterDataInterface Item){
        getDataSet().add(Item);
    }
    
    /**
     * Adds fuzzy item with a membership value to this cluster dataset.
     *
     * @param Item The item to be added. (<code>null</code> not permitted).
     * @param membership The membership value of this data in the clusters.
     */
    /*public void add(ClusterDataInterface Item, double membership){
        getDataSet().add(Item);
        DataMembership.add(membership);
    }*/
    
    /**
     * Adds all the data of the Cluster to this dataset.
     *
     * @param Obj The cluster who's data will be copy from.
     */
    public void addAll(ClusterType Obj){
        this.getDataSet().addAll(Obj.getDataSet());
    }
    
    /**
     * Removes all elements from the dataset.
     */
    public void clear(){
        getDataSet().clear();
    }
    
    /**
     * Returns the data item from the specified index.
     *
     * @param Index the index of element to return.
     *
     * @return ClusterDataInterface at the specified index.
     *
     * @throws ArrayIndexOutOfBoundsException - index is out of range (index < 0 || index >= size()).
     */
    public ClusterDataInterface get(int Index) throws ArrayIndexOutOfBoundsException {
        return getDataSet().get(Index);
    }
    
    /**
     * Returns how many data items this cluster has.
     *
     * @return The number items the dataset has.
     */
    public int size(){
        return getDataSet().size();
    }
    
    /**
     * Calculate this cluster center based on the DataSet
     *
     * @return True if the cluster center has changed.
     *
     * @throws NullPointerException - in case the cluster center is null.
     */
    public boolean calculateCenter() throws NullPointerException{
        return getCenter().calculateCenter(getDataSet());            
    }

    /**
     * Returns DataSet as a ArrayList.
     *
     * @return The DataSet as a ArrayList.
     */
    public ArrayList<ClusterDataInterface> getDataSet() {
        return DataSet;
    }

    /**
     * Sets the DataSet.
     * @param DataSet The dataset.
     */
    public void setDataSet(ArrayList<ClusterDataInterface> DataSet) {
        this.DataSet = DataSet;
    }

    /**
     * Returns this cluster center.
     *
     * @return The cluster center.
     */
    public ClusterDataInterface getCenter() {
        return Center;
    }
    
    /**
     * Sets this cluster center. Note: Recommended to set the cluster centers
     * in the algorithms k-means and fuzzy c-means.
     *
     * @return This cluster center.
     */
    public void setCenter(ClusterDataInterface Center) {
        this.Center = Center;
    }
    
    /**
     * Gets the distance between two clusters. The way that
     * the distance is processed depends on the measurement
     * parameter.
     *
     * @param Obj  The cluster to be compare with.
     * @param measurement  The measurement operation (MINIMUM_DISTANCE_MEASURE default).
     *
     * @return The distance between the clusters as a double primitive.
     *
     * @throws jClusterException - in case the data types are incompatible.
     */
    public double distance(ClusterType Obj, int measurement) throws brClusterException{

        double Dist=0;
        
        switch(measurement){
            
            case MAXIMUM_DISTANCE_MEASURE:
                Dist=maximumDistance(Obj);
                break;
                
            default: //MINIMUM_DISTANCE_MEASURE
                Dist=minimumDistance(Obj);
                break;
        };
        return Dist;
    }
    
    /**
     * Gets the minimum distance between the clusters data
     *
     * @param Obj  The cluster to be compare with.
     * @throws jClusterException - in case the data types are incompatible.
     */
    protected double minimumDistance(ClusterType Obj) throws brClusterException{
        
        int i,j;
        ClusterDataInterface ThisItem, ObjItem;
        double Dist;
        double MinDist=this.get(0).distance(Obj.get(0));
        
        for(i=0; i<this.size(); i++){
            ThisItem=this.get(i);
            for(j=0; j<Obj.size(); j++){
                ObjItem=Obj.get(j);
                Dist=ThisItem.distance(ObjItem);
                if(Dist<MinDist){
                    MinDist=Dist;
                }
            }            
        }
        return MinDist;
    }
    
   /**
     * Gets the maximum distance between the clusters data
     *
     * @param Obj  The cluster to be compare with.
     * @throws jClusterException - in case the data types are incompatible.
     */
    protected double maximumDistance(ClusterType Obj) throws brClusterException{
        
        int i,j;
        ClusterDataInterface ThisItem, ObjItem;
        double Dist;
        double MaxDist=this.get(0).distance(Obj.get(0));
        
        for(i=0; i<this.size(); i++){
            ThisItem=this.get(i);
            for(j=0; j<Obj.size(); j++){
                ObjItem=Obj.get(j);
                Dist=ThisItem.distance(ObjItem);
                if(Dist>MaxDist){
                    MaxDist=Dist;
                }
            }            
        }
        return MaxDist;
    }
    
    /**
     * Returns a string representation of this cluter and 
     * it's center string representation according to its subclass. 
     * This method is intended to be used only for debugging 
     * purposes, and the content and format of the returned string
     * may vary between implementations. The returned string may 
     * be empty but may not be null.
     *
     * @return A string representation of this cluster
     */
    @Override
    public String toString(){
        return "brCluster.ClusterType [Center:"+Center.toString()+"]";
    }
    
    
}
