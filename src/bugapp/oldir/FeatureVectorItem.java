/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.oldir;

/**
 *
 * @author Henrique
 */
public class FeatureVectorItem {
    private String Feature;
    private double Weight;
    
    public FeatureVectorItem(){
        Feature="";
        Weight=0;
    }
    
    public FeatureVectorItem(String Word){
        this.Feature=Word;
        this.Weight=1;
    }
    
    public FeatureVectorItem(String Word, double Weight){
        this.Feature=Word;
        this.Weight=Weight;
    }
    

    /**
     * @return the Feature
     */
    public String getFeature() {
        return Feature;
    }

    /**
     * @param Feature the Feature to set
     */
    public void setFeature(String Feature) {
        this.Feature = Feature;
    }

    /**
     * @return the Weight
     */
    public double getWeight() {
        return Weight;
    }

    /**
     * @param Weight the Weight to set
     */
    public void setWeight(double Weight) {
        this.Weight = Weight;
    }
    
    public void addToWeight(double Value){
        this.Weight+=Value;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj==this){
            return true;
        }
        else if(obj instanceof FeatureVectorItem){
            FeatureVectorItem F=(FeatureVectorItem)obj;
            return this.Feature.equals(F.Feature) && this.Weight==F.Weight;
        }
        else{
            return false;
        }
    }
    
    @Override
    public Object clone(){
        FeatureVectorItem C=new FeatureVectorItem();
        C.setFeature(this.Feature);
        C.setWeight(this.Weight);
        return C;
    }
}
