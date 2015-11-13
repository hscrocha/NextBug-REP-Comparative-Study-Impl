/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Henrique
 */
public class SparseDataVector {
    
    private int BugId;
    private HashMap<Integer, SparseDataItem> hsmData;
    
    public SparseDataVector(int BugId){
        this.BugId = BugId;
        hsmData=new HashMap<>();
    }
    
    public void increment(int Index){
        SparseDataItem Item = hsmData.get(Index);
        if(Item==null){
            Item = new SparseDataItem(Index);
        }
        else{
            Item.incrementFrequency();
        }
        hsmData.put(Index, Item);        
    }

    public void increment(int Index, float freq){
        SparseDataItem Item = hsmData.get(Index);
        if(Item==null){
            Item = new SparseDataItem(Index, freq);
        }
        else{
            Item.addFrequency(freq);
        }
        hsmData.put(Index, Item);        
    }
    
    public void set(int Index, float Freq){
        hsmData.put(Index, new SparseDataItem(Index, Freq));
    }

    public void applyWeight(float w){
        for(SparseDataItem Item : hsmData.values()){
            Item.multFrequency(w);
        }
    }
    
    public void logNormalization(){
        for(SparseDataItem Item : hsmData.values()){
            Item.logNormalizationFrequency();
            //hsmData.put(Item.getIndex(), Item);
        }
    }
    
    public float getFrequency(int Index){
        SparseDataItem Item = hsmData.get(Index);
        if(Item!=null){
            return Item.getFrequency();
        }
        else{
            return 0;
        }
    }
    
    public float get(int Index){
        return getFrequency(Index);
    }
    
    public Collection<SparseDataItem> getItems(){
        return hsmData.values();
    }
    
    public float length(){
        return (float)hsmData.values().size();
    }
    
    public String toArffDataLine(){
        if(hsmData.isEmpty()){
            return "";
        }

        StringBuilder stb=new StringBuilder("{");
        stb.append("0 ");
        stb.append( BugId );

        ArrayList<SparseDataItem> lstItems=new ArrayList<SparseDataItem>(hsmData.values());
        Collections.sort(lstItems);
        for(SparseDataItem Item : lstItems){
            stb.append(",");
            stb.append( Item.toString() );        
        }
        stb.append("}\n");
        return stb.toString();
    }

    public String toFileDataLine(){
        if(hsmData.isEmpty()){
            return "";
        }
        StringBuilder stb=new StringBuilder("[");
        stb.append( BugId );

        ArrayList<SparseDataItem> lstItems=new ArrayList<SparseDataItem>(hsmData.values());
        Collections.sort(lstItems);
        for(SparseDataItem Item : lstItems){
            stb.append(", ");
            stb.append( Item.toString() );        
        }
        stb.append("]\n");
        return stb.toString();
    }
    
    public float euclidianDistance(SparseDataVector Vet){
        float d=0;
        float f;
        
        //Calcula todos os pontos de A left join B
        for(SparseDataItem A : getItems()){
            f = A.getFrequency() - Vet.getFrequency( A.getIndex() ); // Ai - Bi
            d+=f*f;            
        }
        
        //Falta calcular os pontos B not in A
        for(SparseDataItem B : getItems()){
            f = this.getFrequency( B.getIndex() );
            if(f==0){
                //Se for diferente de 0, ele já foi calculado acima
                d+=B.getFrequency()*B.getFrequency(); //como Ai é zero, (Ai - Bi)^2 = (-Bi)^2 = Bi^2
            }
        }
        return (float)d; //Para otimizar, não fazer a raiz 
        //return (float)Math.sqrt(d);
    }
    
    public float cosineDistance(SparseDataVector Vet){
        float d=0;
        float normA = 0;
        float normB = 0;
        float fb;
        
        for(SparseDataItem A : getItems()){
            normA += A.getFrequency()*A.getFrequency();
            fb = Vet.getFrequency(A.getIndex());
            if(fb!=0){
                d += fb * A.getFrequency();
            }
        }
        for(SparseDataItem B : Vet.getItems()){
            normB += B.getFrequency()*B.getFrequency();
        }
        d =(float) (d / ( Math.sqrt(normA)*Math.sqrt(normB)));
        if(Float.isNaN(d)){
            d=0;
        }
        return 1-d;
    }
    
    @Override
    public SparseDataVector clone(){
        SparseDataVector Cloned = new SparseDataVector(BugId);
        Cloned.hsmData = (HashMap<Integer, SparseDataItem>)this.hsmData.clone();
        return Cloned;
    }
    
    /**
     * @return the BugId
     */
    public int getBugId() {
        return BugId;
    }
    
    protected void setBugId(int BugId){
        this.BugId = BugId;
    }

}
