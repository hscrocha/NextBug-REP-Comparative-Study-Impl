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
 * ClusterDataVector.java
 *
 * Created on 8 de Junho de 2005, 09:41
 */

package brcluster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Henrique
 */
public class ClusterDataVector extends ClusterDataType{
    
    public static final int DISTANCE_NORM1=1;
    public static final int DISTANCE_NORM2=2;
    public static final int DISTANCE_NORM_INFINITE=0;
    
    
    /** the vector components **/
    protected ArrayList<Number> Data;
    
    /** The distance measure **/
    protected static int DistanceMeasure = 2;
    
    /**
     * Constructs a new empty data vector.
     */
    public ClusterDataVector() {
        this(new ArrayList<Double>());
    }
    
    /**
     * Constructs a new data vector.
     *
     * @param Data  the data vector.
     */
    public ClusterDataVector(ArrayList<? extends Number> Data){
        super();
        this.setData((ArrayList<Number>)Data);
    }
    
    /**
     * Returns the distance (unsimilarity) between this and the object.
     * This object calculates the squared euclidian distance between two points.
     *
     * @param obj  The data to be compare with
     *
     * @return The distance value as a double primitive
     *
     * @throws jClusterException - in case the data types are incompatible.
     */
    @Override
    public double distance(ClusterDataInterface obj) throws brClusterException{
        if(obj instanceof ClusterDataVector){
            ClusterDataVector O=(ClusterDataVector)obj;
            double Distance=0;
            int i;
            ArrayList<Double> Aux=new ArrayList<Double>();
            
            for(i=0; i<this.getData().size(); i++){
                Aux.add(i,this.getData().get(i).doubleValue()-O.getData().get(i).doubleValue());
            }
            
            switch(getDistanceMeasure()){
                case 0:
                    for(i=0; i<Aux.size(); i++){
                        if(Distance<Aux.get(i).doubleValue())
                            Distance=Aux.get(i).doubleValue();
                    }
                    break;

                case 1:
                    for(i=0; i<Aux.size(); i++){
                        Distance+=java.lang.Math.abs(Aux.get(i).doubleValue());
                    }
                    break;
                    
                default:
                    for(i=0; i<Aux.size(); i++){
                        Distance+=java.lang.Math.pow(Aux.get(i).doubleValue(),2);
                    }
                    Distance=java.lang.Math.sqrt(Distance);
                    break;
            }
            
            return Distance;
        }
        else
            throw new brClusterException("Incompatible types.");
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
    public boolean equals(Object obj){
        if(obj==this)
            return true;
        else if(obj instanceof ClusterDataVector){
            ClusterDataVector O=(ClusterDataVector)obj;
            return getData().equals(O.getData());
        }
        else
            return false;
    }
    
    /**
     * Calculates the center of the Data and store in this.
     *
     * @param Data  The data.
     *     
     * @return True if the center has changed.
     */
    @Override
    public boolean calculateCenter(Collection<ClusterDataInterface> Data){
        
        ArrayList<Number> Minimum=new ArrayList<Number>();
        ArrayList<Number> Maximum=new ArrayList<Number>();
        ArrayList<Number> OldValues=(ArrayList<Number>)this.getData().clone();
        ClusterDataVector Item;
        int i;
        
        if(Data.isEmpty())
            return false;        
        
        Iterator<ClusterDataInterface> I=Data.iterator();
        Item=(ClusterDataVector)I.next();
        Minimum.addAll(Item.getData());
        Maximum.addAll(Item.getData());
        
        while(I.hasNext()){
            Item=(ClusterDataVector)I.next();
            for(i=0; i<Item.getData().size(); i++){
                if(Item.getData().get(i).doubleValue()>Maximum.get(i).doubleValue()){
                    Maximum.set(i,Item.getData().get(i));
                }
                else if(Item.getData().get(i).doubleValue()<Minimum.get(i).doubleValue()){
                    Minimum.set(i,Item.getData().get(i));
                }
            }
        }
        
        for(i=0; i<Maximum.size(); i++){
            this.getData().add(i,new Double(Minimum.get(i).doubleValue()+(Maximum.get(i).doubleValue()-Minimum.get(i).doubleValue())/2));
        }
                        
        if(OldValues.equals(this.getData()))
            return false;
        else
            return true;
    }
    
    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     */
    @Override
    public Object clone(){
         ClusterDataVector TheClone=new ClusterDataVector();
                  TheClone.setData((ArrayList<Number>) this.getData().clone());
         return TheClone;
    }
    
    /**
     * Returns the data vector
     *
     * @return the data vector
     */
    public ArrayList<Number> getData() {
        return Data;
    }
    
    /**
     * Sets the data vector
     *
     * @param Data the data vector.
     */
    public void setData(ArrayList<Number> Data) {
        this.Data = Data;
    }
    
    /**
     * Returns the distance measurement of this data.
     * This measurement can be:
     * - DISTANCE_NORM1: Use a norm 1 formulae ||X - Y||1 = Sum( abs(xi - yi) )
     * - DISTANCE_NORM2: Use a norm 2 formulae ||X - Y||2 = (Sum( (xi - yi)^2) )^1/2
     * - DISTANCE_NORM_INFINITE: Use a norm infinite formulae ||X - Y|| = Max ( abs(xi - yi) )
     *
     * @returns The distance measurement method
     */
    public int getDistanceMeasure() {
        return DistanceMeasure;
    }
    
    /**
     * Sets the distance measurement of this data.
     * This measurement can be:
     * - DISTANCE_NORM1: Use a norm 1 formulae ||X - Y||1 = Sum( abs(xi - yi) )
     * - DISTANCE_NORM2: Use a norm 2 formulae ||X - Y||2 = (Sum( (xi - yi)^2) )^1/2
     * - DISTANCE_NORM_INFINITE: Use a norm infinite formulae ||X - Y|| = Max ( abs(xi - yi) )
     *
     * @param DistanceMeasure The distance measurement method.
     */
    public void setDistanceMeasure(int DistanceMeasure) {
        ClusterDataVector.DistanceMeasure = DistanceMeasure;
    }
    
}
