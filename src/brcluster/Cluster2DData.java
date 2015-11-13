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
 * Cluster2DData.java
 *
 * Created on 28 de Maio de 2005, 12:56
 */

package brcluster;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class Cluster2DData extends ClusterDataType {
    
    /** the x-value **/
    protected double X;
    /** the y-value **/
    protected double Y;
    
    /**
     * Constructs a new 2d data with x and y values set to 0.
     */
    public Cluster2DData() {
        this(0,0);
    }
    
    /**
     * Constructs a new 2d data.
     *
     * @param X  the x-value.
     * @param Y  the y-value.
     */
    public Cluster2DData(double X, double Y){
        super();
        this.X = X;
        this.Y = Y;
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
        if(obj instanceof Cluster2DData){
            Cluster2DData O=(Cluster2DData)obj;
            return ((this.X-O.X)*(this.X-O.X)+(this.Y-O.Y)*(this.Y-O.Y));
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
        else if(obj instanceof Cluster2DData){
            Cluster2DData O=(Cluster2DData)obj;
            if(this.X==O.X && this.Y==O.Y)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Returns the x-value.
     *
     * @return The x-value.
     */
    public double getX() {
        return X;
    }

   /**
    * Sets the x-value for this data.
    *
    * @param X  the new x-value.
    */
    public void setX(double X) {
        this.X = X;
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getY() {
        return Y;
    }

   /**
    * Sets the y-value for this data.
    *
    * @param Y  the new x-value.
    */
    public void setY(double Y) {
        this.Y = Y;
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
        
        double MinX, MinY, MaxX, MaxY;
        double OldX=this.getX(), OldY=this.getY();
        Cluster2DData Item;
        
        if(Data.isEmpty())
            return false;        
        
        Iterator<ClusterDataInterface> I=Data.iterator();
        Item=(Cluster2DData)I.next();
        MinX=MaxX=Item.getX();
        MinY=MaxY=Item.getY();
        while(I.hasNext()){
            Item=(Cluster2DData)I.next();
            
            if(Item.getX()>MaxX)
                MaxX=Item.getX();
            else if(Item.getX()<MinX)
                MinX=Item.getX();
            
            if(Item.getY()>MaxY)
                MaxY=Item.getY();
            else if(Item.getY()<MinY)
                MinY=Item.getY();
        } 
                
        this.setX(MinX+(MaxX-MinX)/2);
        this.setY(MinY+(MaxY-MinY)/2);
        
        if(OldX==this.getX() && OldY==this.getY())
            return false;
        else
            return true;
    }
    
    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     * 
     */
    @Override
    public Object clone(){
         Cluster2DData TheClone=new Cluster2DData();
         TheClone.setX(this.getX());
         TheClone.setY(this.getY());
         return TheClone;
    }

    
    /**
     * Returns a string representation of this data and 
     * its location in the (x, y) coordinate space. This 
     * method is intended to be used only for debugging purposes, 
     * and the content and format of the returned string may 
     * vary between implementations. The returned string may be 
     * empty but may not be null.
     *
     * @return A string representation of this data
     */
    @Override
    public String toString(){
        return "brcluster.Cluster2DData: [X="+getX()+", Y="+getY()+"]";
    }
       
};
