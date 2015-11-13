/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import bugapp.ir.SparseDataVector;
import bugapp.persistence.entity.BugPath;

/**
 *
 * @author Henrique
 */
public class RecEntry implements Comparable<RecEntry> {
    
    private BucketItem Data;
    private float Distance;
    private byte SameDeveloper;
    private byte SameComponent;
    private float Overlap;
    private float Jaccard = 0;
    private float TaskCoeficient = 0;
    
    //For REP
    private byte SameProduct;
    protected byte SameType;
    protected int VersionDif;
    protected int PriorityDif;
    
    /**
     * @return the IssueData
     */
    public BugPath getIssueData() {
        return this.Data.getBugData();
    }

    /**
     * @return the ProcVector
     */
    public SparseDataVector getProcVector() {
        return this.Data.getSumUnigramData();
    }

    /**
     * @return the Distance
     */
    public float getDistance() {
        return Distance;
    }

    /**
     * @param Distance the Distance to set
     */
    public void setDistance(float Distance) {
        this.Distance = Distance;
    }
    
    public void setSimilarity(float Similarity){
        this.Distance = 1f - Similarity;
    }
    
    public float getSimilarity(){
        return 1f - Distance;
    }

    /**
     * @return the Overlap
     */
    public float getOverlap() {
        return Overlap;
    }

    /**
     * @param Overlap the Overlap to set
     */
    public void setOverlap(float Overlap) {
        this.Overlap = Overlap;
    }

    /**
     * @return the Jaccard
     */
    public float getJaccard() {
        return Jaccard;
    }

    /**
     * @param Jaccard the Jaccard to set
     */
    public void setJaccard(float Jaccard) {
        this.Jaccard = Jaccard;
    }
    
    public void setBucketItem(BucketItem B){
        this.Data = B;
    }
    
    public BucketItem getBucketItem(){
        return this.Data;
    }
    
    @Override
    public int compareTo(RecEntry o) {
        //int Dist = (int) ((this.getDistance() - o.getDistance())*100);
        int D1 = (int) ((this.getDistance()*1000000f));
        int D2 = (int) ((o.getDistance()*1000000f));
        return (D1-D2);
    }
   

    /**
     * @return the SameDeveloper
     */
    public byte getSameDeveloper() {
        return SameDeveloper;
    }

    /**
     * @param SameDeveloper the SameDeveloper to set
     */
    public void setSameDeveloper(byte SameDeveloper) {
        this.SameDeveloper = SameDeveloper;
    }

    public void setSameDeveloper(boolean same){
        this.SameDeveloper = same? (byte)1: (byte)0;
    }

    /**
     * @return the SameDeveloper
     */
    public byte getSameProduct() {
        return SameProduct;
    }

    /**
     * @param SameProduct the SameDeveloper to set
     */
    public void setSameProduct(byte SameProduct) {
        this.SameProduct = SameProduct;
    }

    public void setSameProduct(boolean same){
        this.SameProduct = same? (byte)1: (byte)0;
    }
    
    /**
     * @return the SameComponent
     */
    public byte getSameComponent() {
        return SameComponent;
    }

    /**
     * @param SameComponent the SameComponent to set
     */
    public void setSameComponent(byte SameComponent) {
        this.SameComponent = SameComponent;
    }
    
    public void setSameComponent(boolean same){
        this.SameComponent = same? (byte)1: (byte)0;
    }
    
    @Override
    public String toString(){
        StringBuilder stb=new StringBuilder();
        stb.append("(");
        stb.append(getSimilarity());
        stb.append(", id=");
        stb.append(getIssueData().getBugId());
        stb.append(", ");
        stb.append(getIssueData().getShortDesc());
        stb.append(", o=");
        stb.append(getOverlap());
        stb.append(", c=");
        stb.append(getSameComponent());
        stb.append(", d=");
        stb.append(getSameDeveloper());
        stb.append(", p=");
        stb.append(getPriorityDif());
        stb.append(", v=");
        stb.append(getVersionDif());
        stb.append(")");
        
        return stb.toString();
    }

    /**
     * @return the TaskCoeficient
     */
    public float getTaskCoeficient() {
        return TaskCoeficient;
    }

    /**
     * @param TaskCoeficient the TaskCoeficient to set
     */
    public void setTaskCoeficient(float TaskCoeficient) {
        this.TaskCoeficient = TaskCoeficient;
    }

    /**
     * @return the VersionDif
     */
    public int getVersionDif() {
        return VersionDif;
    }

    /**
     * @param VersionDif the VersionDif to set
     */
    public void setVersionDif(int VersionDif) {
        this.VersionDif = VersionDif;
    }

    /**
     * @return the PriorityDif
     */
    public int getPriorityDif() {
        return PriorityDif;
    }

    /**
     * @param PriorityDif the PriorityDif to set
     */
    public void setPriorityDif(int PriorityDif) {
        this.PriorityDif = PriorityDif;
    }

    /**
     * @return the SameType
     */
    public byte getSameType() {
        return SameType;
    }

    /**
     * @param SameType the SameType to set
     */
    public void setSameType(byte SameType) {
        this.SameType = SameType;
    }

    public void setSameType(boolean same){
        this.SameType = same? (byte)1: (byte)0;
    }
    
}
