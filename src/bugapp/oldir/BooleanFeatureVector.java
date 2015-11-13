/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.oldir;

import brcluster.ClusterDataInterface;
import brcluster.ClusterDataType;
import brcluster.brClusterException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Henrique
 */
public class BooleanFeatureVector extends ClusterDataType {
    
    private ArrayList<String> lstFeatures;
    
    public BooleanFeatureVector(){
        lstFeatures=new ArrayList<String>();
    }
    
    public void addFeature(String F){
        lstFeatures.add(F);
    }
    
    public String getFeature(int i){
        return lstFeatures.get(i);
    }
    
    public boolean hasFeature(String F){
        return lstFeatures.contains(F);
    }   
    
    public int getNumberOfFeatures(){
        return lstFeatures.size();
    }

    @Override
    public double distance(ClusterDataInterface obj) throws brClusterException {
        if(obj instanceof BooleanFeatureVector){
            double sim=0;
            BooleanFeatureVector V=(BooleanFeatureVector)obj;
            
            for(String Feature : lstFeatures){
                if(V.hasFeature(Feature)){
                    sim+=1;
                }
            }
            
            sim/=((double)getNumberOfFeatures());
            
            return 1-sim;
        }
        else{
            throw new brClusterException("Incompatible types in BooleanFeatureVector.distance().");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this)
            return true;
        else if(obj instanceof BooleanFeatureVector){
            BooleanFeatureVector P=(BooleanFeatureVector)obj;
            return lstFeatures.equals(P.lstFeatures);
        }
        else{
            return false;
        } 
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean calculateCenter(Collection<ClusterDataInterface> Data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
