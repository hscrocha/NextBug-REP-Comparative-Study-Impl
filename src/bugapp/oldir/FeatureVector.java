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
import java.util.Iterator;

/**
 *
 * @author Henrique
 */
public class FeatureVector extends ClusterDataType {

    /**
     * @return the CalculateCenterOption
     */
    public static int getCalculateCenterOption() {
        return CalculateCenterOption;
    }

    /**
     * @param aCalculateCenterOption the CalculateCenterOption to set
     */
    public static void setCalculateCenterOption(int aCalculateCenterOption) {
        CalculateCenterOption = aCalculateCenterOption;
    }

    private ArrayList<FeatureVectorItem> lstFeatures;

    /** The distance measure **/
    private static int CalculateCenterOption = 1;
    public static final int CENTER_NORMAL = 1;
    public static final int CENTER_WEIGHTED = 2;
    
    public FeatureVector(){
        lstFeatures=new ArrayList<FeatureVectorItem>();
    }
    
    public void addFeature(String F){
        lstFeatures.add(new FeatureVectorItem(F));
    }
    
    public void addFeature(String F, double W){
        lstFeatures.add(new FeatureVectorItem(F,W));
    }
    
    public void addFeatureOrWeight(String F){
        for(FeatureVectorItem Item : lstFeatures){
            if(Item.getFeature().equals(F)){
                Item.addToWeight(1);
                return;
            }
        }
        addFeature(F);
    }
    
    public void addFeatureOrWeight(String F, double W){
        for(FeatureVectorItem Item : lstFeatures){
            if(Item.getFeature().equals(F)){
                Item.addToWeight(W);
                return;
            }
        }
        addFeature(F);
    }
    
    public String getFeature(int i){
        return lstFeatures.get(i).getFeature();
    }
    
    public double getFeatureWeight(int i){
        return lstFeatures.get(i).getWeight();
    }
    
    public int getFeatureIndex(String F){
        for(int i=0; i<lstFeatures.size(); i++){
            if(lstFeatures.get(i).getFeature().equals(F)) {
                return i;
            }
        }
        return -1;
    }

    public double getFeatureWeight(String F){
        for(FeatureVectorItem Item : lstFeatures){
            if(Item.getFeature().equals(F)){
                return Item.getWeight();
            }
        }
        return 0;
    }   
    
    public void setWeight(int i, double W){
        lstFeatures.get(i).setWeight(W);
    }
    
    public int getNumberOfFeatures(){
        return lstFeatures.size();
    }
    
    public double getNorm2(){
        double N=0;
        for(int i=0; i<lstFeatures.size(); i++){
            N+=Math.pow(lstFeatures.get(i).getWeight(), 2);
        }
        return Math.sqrt(N);
    }

    @Override
    public double distance(ClusterDataInterface obj) throws brClusterException {
        if(obj instanceof FeatureVector){
            double sim=0, wd;
            FeatureVector V=(FeatureVector)obj;
            
            for(FeatureVectorItem Item : lstFeatures){
                wd=V.getFeatureWeight(Item.getFeature());
                if(wd>0){
                    sim+=wd*Item.getWeight();
                }
            }
            sim/=( this.getNorm2()*V.getNorm2() );
            
            return 1-sim;
        }
        else{
            throw new brClusterException("Incompatible types in FeatureVector.distance().");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this)
            return true;
        else if(obj instanceof FeatureVector){
            FeatureVector P=(FeatureVector)obj;
            return lstFeatures.equals(P.lstFeatures);
        }
        else{
            return false;
        } 
    }

    @Override
    public Object clone() {
        FeatureVector Cloned=new FeatureVector();
        Cloned.lstFeatures = (ArrayList<FeatureVectorItem>) lstFeatures.clone();
        return Cloned;
    }

    @Override
    public boolean calculateCenter(Collection<ClusterDataInterface> Data) {
        if(CalculateCenterOption==CENTER_NORMAL){
            return centerNormal(Data);
        }
        else{
            return centerWeighted(Data);
        }
    }
    
    protected boolean centerNormal(Collection<ClusterDataInterface> Data){
        FeatureVector Item;
        FeatureVector oldV=(FeatureVector)this.clone();
        FeatureVector min;
        FeatureVector max;
        int i, indexMin;

        if(Data.isEmpty())
            return false;        

        Iterator<ClusterDataInterface> I=Data.iterator();
        Item=(FeatureVector)I.next();
        min=(FeatureVector)Item.clone();
        max=(FeatureVector)Item.clone();
        
        lstFeatures.clear();
        while(I.hasNext()){
            Item=(FeatureVector)I.next();
            for(i=0; i<Item.getNumberOfFeatures(); i++){
                indexMin = min.getFeatureIndex(Item.getFeature(i));
                if(indexMin<0){
                    //Não existia essa dimensão (feature) no vetor
                    //adiciona-la tanto no mínimo quanto no máximo
                    min.addFeature(Item.getFeature(i), 0);
                    max.addFeature(Item.getFeature(i), Item.getFeatureWeight(i));
                }
                else{
                    if(Item.getFeatureWeight(i)<min.getFeatureWeight(indexMin)){
                        min.setWeight(indexMin, Item.getFeatureWeight(i));
                    }
                    else if(Item.getFeatureWeight(i)>max.getFeatureWeight(indexMin)){
                        max.setWeight(indexMin, Item.getFeatureWeight(i));
                    }
                }
            }
        }
        Item=null; //gc
        
        //Calcula o novo centro do cluster
        FeatureVectorItem med=null;
        for(i=0; i<max.getNumberOfFeatures(); i++){
            med = new FeatureVectorItem();
            med.setFeature(min.getFeature(i));
            med.setWeight(min.getFeatureWeight(i)+(max.getFeatureWeight(i)-min.getFeatureWeight(i))/2);
            lstFeatures.add(med);
        }
        med=null; //gc
        
        //Reseta vars para ajudar no GC
        min=null;
        max=null;
        
        //Verifica se o centro mudou de lugar
        if(oldV.equals(this))
            return false;
        else
            return true;
    }

    protected boolean centerWeighted(Collection<ClusterDataInterface> Data){
        return false;
    }
    
    @Override
    public String toString(){
        StringBuilder stb=new StringBuilder();
        stb.append("\nFeatureVector(");
        for(FeatureVectorItem item : lstFeatures){
            stb.append(item.getFeature());
            stb.append("=");
            stb.append(item.getWeight());
            stb.append(", ");
        }
        stb.append(")\n");
        return stb.toString();
    }
}
