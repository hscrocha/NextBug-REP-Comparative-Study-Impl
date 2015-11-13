/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.pasm;

/**
 *
 * @author Henrique
 */
public class DisperseMetricEntity {
    
    private float AverageSimilarity;
    private float MaxSimilarity;
    private float AverageOverlap;
    private float MaxOverlap;
    private StringBuilder Details;
    
    public DisperseMetricEntity(){
        Details=new StringBuilder();
    }

    /**
     * @return the AverageSimilarity
     */
    public float getAverageSimilarity() {
        return AverageSimilarity;
    }

    /**
     * @param AverageSimilarity the AverageSimilarity to set
     */
    public void setAverageSimilarity(float AverageSimilarity) {
        this.AverageSimilarity = AverageSimilarity;
    }

    /**
     * @return the MaxSimilarity
     */
    public float getMaxSimilarity() {
        return MaxSimilarity;
    }

    /**
     * @param MaxSimilarity the MaxSimilarity to set
     */
    public void setMaxSimilarity(float MaxSimilarity) {
        this.MaxSimilarity = MaxSimilarity;
    }

    /**
     * @return the AverageOverlap
     */
    public float getAverageOverlap() {
        return AverageOverlap;
    }

    /**
     * @param AverageOverlap the AverageOverlap to set
     */
    public void setAverageOverlap(float AverageOverlap) {
        this.AverageOverlap = AverageOverlap;
    }

    /**
     * @return the MaxOverlap
     */
    public float getMaxOverlap() {
        return MaxOverlap;
    }

    /**
     * @param MaxOverlap the MaxOverlap to set
     */
    public void setMaxOverlap(float MaxOverlap) {
        this.MaxOverlap = MaxOverlap;
    }
    
    public void appendDetail(String Text){
        this.Details.append(Text);
    }
    
    public void appendDetail(int N){
        this.Details.append(N);
    }
    
    public void appendDetail(float F){
        this.Details.append(F);
    }
    
    public String getDetail(){
        return Details.toString();
    }
}
