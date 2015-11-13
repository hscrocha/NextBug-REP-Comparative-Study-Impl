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
 * ClusterFactory.java
 *
 * Created on 29 de Maio de 2005, 19:40
 */

package brcluster;
import java.util.ArrayList;

/**
 *
 * @author  Henrique
 */
public class ClusterFactory {
    
    public static final int SIMPLE_LINK = 1;
    public static final int COMPLETE_LINK = 2;
    
    /** **/
    protected ArrayList<ClusterType> ClusterCollection;
    
    /** **/
    protected ArrayList<ClusterDataInterface> DataCollection;    
    
    /** Creates a new instance of ClusterFactory */
    public ClusterFactory(){
        this(new ArrayList<ClusterDataInterface>());
    }
    
    public ClusterFactory(ArrayList<ClusterDataInterface> Data){
                this.setDataCollection(Data);
    }
    
    /**
     * Performs the K-means clustering technic in the Data.
     * Approximate complexity order O(k*n*l) 
     *
     * @param K The number of Clusters (must be greater then 1). 
     * @param L How many times the k-means method will loop (may end before).
     *
     * @return How many times the k-means method did loop.
     *
     * @throws jClusterException - in case K<=1 or case K is greater than the Data.
     * @throws CloneNotSupportedException - in case the centers arent previously 
     *                                      initialize and the data isnt clonable.
     */
    public long kmeans(int K, long L) throws brClusterException, CloneNotSupportedException {
        int i, j;
        double MinDist; //The minimun distance between a cluster and the data
        double Dist; //Local distance between a cluster and the data
        boolean Changed; //Verify if any of the clusters centers have changed
        ClusterDataInterface Item;
        
        if(K<=1) throw new brClusterException("K-means clusters must be greater than 1.");
        if(K>getDataCollection().size()) throw new brClusterException("Number of clusters can't be greater than the data size."); 
        
        //In case the clusters havent been set
        if(ClusterCollection==null) ClusterCollection=new ArrayList<ClusterType>(); 
        for(j=getClusterCollection().size(); j<K; j++)
            getClusterCollection().add(new ClusterType());                
        
        //initialize clusters who has no center yet
        for(j=0; j<getClusterCollection().size(); j++){
            if(getClusterCollection().get(j).getCenter()==null)
                getClusterCollection().get(j).setCenter((ClusterDataInterface)getDataCollection().get(j).clone());
        }
        
        for(long h=0; h<L; h++){
            
            for(j=0; j<K; j++)
                getClusterCollection().get(j).clear(); //clean the clusters data
            
            //assign the data to the nearest cluster
            for(i=0; i<getDataCollection().size(); i++){
                
                Item=getDataCollection().get(i);
                MinDist=Item.distance(getClusterCollection().get(0).getCenter());
                Item.setCluster(getClusterCollection().get(0));
                for(j=1; j<K; j++){
                    Dist=Item.distance(getClusterCollection().get(j).getCenter());
                    if(Dist<MinDist){
                        MinDist=Dist;
                        Item.setCluster(getClusterCollection().get(j));
                    }
                }
                
                //The cluster closer to the data is alrady chosen here
                Item.getCluster().add(Item); //Adds this data to the cluster
            }
            
            //re-calculate the new cluster's centroids
            Changed=false;
            for(j=0; j<K; j++)
                Changed|=(getClusterCollection().get(j).calculateCenter());
            //Stop condition: clusters centroids havent changed
            if(!Changed) return h+1;
            
        }
        return L;
    }
    
    /**
     * Performs a hierarchic clustering according the the LinkMode parameter. 
     * Approximate complexity order O(n^2) 
     *
     * @param Clusters The number of clusters (must be greater than 1).
     * @param LinkMode The link mode to be used in this clustering (SIMPLE_LINK default).
     *
     * @throws jClusterException - in case the data types are incompatible.
     */
    public void hierarchical(int Clusters, int LinkMode) throws brClusterException{
        int i, j;
        ClusterType Ci, Cj;
        int Pos1, Pos2;
        double MinDist, Dist;
        int DistanceMeasure = ClusterType.MINIMUM_DISTANCE_MEASURE;
        
        if(Clusters<=1) throw new brClusterException("Hierarchical clusters must be greater than 1.");
        if(Clusters>getDataCollection().size()) throw new brClusterException("Number of clusters can't be greater than the data size."); 

        if(LinkMode==COMPLETE_LINK)
            DistanceMeasure = ClusterType.MAXIMUM_DISTANCE_MEASURE;
        
        int ClusterCount=getDataCollection().size();
                
        //Clear the clusters
        if(getClusterCollection()!=null)
            getClusterCollection().clear();
        else
            setClusterCollection(new ArrayList<ClusterType>());
        
        //Each data is assign to an individual cluster
        for(i=0; i<ClusterCount; i++){
            Ci=new ClusterType();
            Ci.add(getDataCollection().get(i));
            getClusterCollection().add(Ci);
        }
        
        //Until desired number of clusters is acheived
        while(ClusterCount>Clusters){

            Ci=getClusterCollection().get(0);
            Cj=getClusterCollection().get(1);
            MinDist=Ci.distance(Cj,DistanceMeasure);
            Pos1=0;
            Pos2=1;
            //Find the two closest clusters
            for(i=0; i<getClusterCollection().size()-1; i++){
                Ci=getClusterCollection().get(i);
                for(j=i+1; j<getClusterCollection().size(); j++){
                    Cj=getClusterCollection().get(j);
                    Dist=Ci.distance(Cj,DistanceMeasure);
                    if(Dist<MinDist){
                        Pos1=i;
                        Pos2=j;
                        MinDist=Dist;
                    }
                }                
            }
            
            //Join the closest clusters together
            Ci=getClusterCollection().get(Pos1);
            Cj=getClusterCollection().remove(Pos2);
            Ci.addAll(Cj);
            
            Cj.getDataSet().clear();
            Cj=null;
            ClusterCount--;
        }
    }
    

    /**
     * Gets the Cluster Collection. The clustering algorithms results are store in this dataset.
     *
     * @return The cluster collection.
     */
    public ArrayList<ClusterType> getClusterCollection() {
        return ClusterCollection;
    }

    /**
     * Sets the Cluster Collection. Usually used to set the initial clusters centers.
     *
     * @param ClusterCollection The cluster collection.
     */
    public void setClusterCollection(ArrayList<ClusterType> ClusterCollection) {
        this.ClusterCollection = ClusterCollection;
    }

    
    /**
     * Gets the data collection.
     * @return The data collection.
     */
    public ArrayList<ClusterDataInterface> getDataCollection() {
        return DataCollection;
    }
    
    /**
     * Sets the Data Collection. Usually used to set the initial unclustered data.
     * @param The data collection.
     */
    public void setDataCollection(ArrayList<ClusterDataInterface> DataCollection) {
        this.DataCollection = DataCollection;
    }
    
}
