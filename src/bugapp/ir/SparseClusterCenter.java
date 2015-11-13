/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

import java.util.LinkedList;

/**
 *
 * @author Henrique
 */
public class SparseClusterCenter {
    
    private LinkedList<SparseDataVector> lstData = null;
    private SparseDataVector Center = null;
    private float AvgCenterDist = 0;
    
    public static final byte EUCLIDIAN_DISTANCE = 1;
    public static final byte COSINE_DISTANTE = 2;
    
    public SparseClusterCenter(){
        lstData = new LinkedList<SparseDataVector>();
    }
    
    public LinkedList<SparseDataVector> getData(){
        return lstData;
    }
    
    public void clearAssignedData(){
        lstData.clear();
    }
    
    public void add(SparseDataVector V){
        lstData.add(V);
    }
    
    public void setCenter(SparseDataVector V){
        Center = V.clone();
        Center.setBugId(0);
    }
    
    public SparseDataVector getCenter(){
        return Center;
    }
    
    public void join(SparseClusterCenter C){
        this.lstData.addAll(C.getData());
    }
    
    public float hierarchicalDistance(SparseClusterCenter C, byte HierarchicalLink){
        if(HierarchicalLink==1){
            //Single LINK
            float MinDist = 1;
            float Dist = 0;
            for(SparseDataVector Vi : this.lstData ){
                for(SparseDataVector Vj : C.lstData){
                    Dist=Vi.cosineDistance(Vj);
                    if(Dist<MinDist){
                        MinDist=Dist;
                    }
                }
            }
            return MinDist;
        }
        else if(HierarchicalLink==2){
            //Complete LINK
            float MaxDist = 0;
            float Dist = 0;
            for(SparseDataVector Vi : this.lstData ){
                for(SparseDataVector Vj : C.lstData){
                    Dist=Vi.cosineDistance(Vj);
                    if(Dist>MaxDist){
                        MaxDist=Dist;
                    }
                }
            }
            return MaxDist;
        }
        else{
            //Average LINK
            float AvgDist = 0;
            for(SparseDataVector Vi : this.lstData ){
                for(SparseDataVector Vj : C.lstData){
                    AvgDist+=Vi.cosineDistance(Vj);
                }
            }
            return AvgDist/(this.lstData.size()*C.lstData.size());
        }
    }
    
    public float distance(SparseDataVector V, byte DistanceMeasure){
        if(DistanceMeasure == EUCLIDIAN_DISTANCE){
            return this.Center.euclidianDistance(V);
        }
        else{
            return this.Center.cosineDistance(V);
        }
    }
    
    public boolean calcCenter(byte DistanceMeasure){
        SparseDataVector NewCenter = Center; 
        if(DistanceMeasure == EUCLIDIAN_DISTANCE){
            NewCenter = new SparseDataVector(0);
            for(SparseDataVector V : lstData){
                for(SparseDataItem Vi : V.getItems()){
                    if(NewCenter.getFrequency(Vi.getIndex())==0){
                        float freq=0;
                        for(SparseDataVector Q : lstData){
                            freq+=Q.getFrequency( Vi.getIndex() );
                        }
                        freq/=(float)lstData.size();
                        NewCenter.set(Vi.getIndex(), freq);
                    }
                }
            }
        }
        else{ //Cossine Distance - Medoids method
            float mindist = 1;
            float dist;
            
            for(SparseDataVector V : lstData){
                dist = 0;
                for(SparseDataVector Q : lstData){
                    dist += V.cosineDistance(Q);
                }
                dist/= (float)(lstData.size()-1);
                if(dist < mindist){
                    mindist = dist;
                    AvgCenterDist = dist;
                    NewCenter = V;
                }
            }
        }
        
        if(Center==NewCenter){
            return false;
        }
        else{
            Center = NewCenter;
            return true;
        }
    }

    /**
     * @return the AvgCenterDist
     */
    public float getAvgCenterDist() {
        return AvgCenterDist;
    }

    public void calcAvgCenterDist() {
        AvgCenterDist = 0;
        for (SparseDataVector Q : lstData) {
            AvgCenterDist += Center.cosineDistance(Q);
        }
        AvgCenterDist /= (float) (lstData.size() - 1);
    }
    
    public float getAvgCenterDistWithoutOutliers(){
        float n = 0;
        float Avg = 0;
        for (SparseDataVector Q : lstData) {
            float d = Center.cosineDistance(Q);
            if(d<1){
                Avg += d;
                n++;
            }
        }
        Avg /= n;
        return Avg;
    }

}
