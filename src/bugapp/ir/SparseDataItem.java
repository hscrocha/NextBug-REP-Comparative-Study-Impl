/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

/**
 *
 * @author Henrique
 */
public class SparseDataItem implements Comparable<SparseDataItem> {
    private int Index;
    private float Frequency;
    
    public SparseDataItem(){
        this.Index = -1;
        this.Frequency = 0;
    }
    
    public SparseDataItem(int Index, float Freq){
        this.Index = Index;
        this.Frequency = Freq;
    }

    public SparseDataItem(int Index){
        this.Index = Index;
        this.Frequency = 1;
    }
    
    /**
     * @return the Index
     */
    public int getIndex() {
        return Index;
    }

    /**
     * @param Index the Index to set
     */
    public void setIndex(int Index) {
        this.Index = Index;
    }

    /**
     * @return the Frequency
     */
    public float getFrequency() {
        return Frequency;
    }

    /**
     * @param Frequency the Frequency to set
     */
    public void setFrequency(float Frequency) {
        this.Frequency = Frequency;
    }
    
    /**
     * 
     * @param X 
     */
    public void addFrequency(float X){
        this.Frequency+=X;
    }
    
    public void multFrequency(float X){
        this.Frequency*=X;
    }
    
    public void incrementFrequency(){
        this.Frequency++;
    }
    
    public void logNormalizationFrequency(){
        this.Frequency = (float) (1.0 + Math.log(Frequency)/Math.log(2));
    }
    
    @Override
    public String toString(){
        return Integer.toString(Index)+" "+Float.toString(Frequency); 
    }

    @Override
    public int compareTo(SparseDataItem o) {
        //Sort by index
        return this.Index - o.Index;
    }
}
