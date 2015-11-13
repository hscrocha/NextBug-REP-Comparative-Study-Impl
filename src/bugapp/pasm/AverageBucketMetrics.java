/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

/**
 *
 * @author Henrique
 */
public class AverageBucketMetrics {
    
    protected float IssueCount;
    protected float Components;
    protected float Developers;
    protected float DevLikelihood;
    protected float Overlap;
    protected float MaxOverlap;
    protected float ExternalOverlap;
    protected float ExternalOvCount;
    protected float Jaccard;
    protected float OverlapCount;
    protected float TaskCoeficient;
    protected float Similarity;
    protected float Feedback;

    protected float PrecCount;
    protected float Precision;
    protected float Likelihood;
    
    protected float RecCount;
    protected float Recall;
    protected float MaxRecall;
    
    protected float BaselineRecall;
    protected float BaselinePrecision;
    protected float BaselineLikelihood;
    
    public AverageBucketMetrics(){
        this.IssueCount=0;
        this.Components=0;
        this.Developers=0;
        this.DevLikelihood = 0;
        this.Similarity=0;
        this.Jaccard=0;
        this.Overlap=0;
        this.TaskCoeficient=0;
        this.MaxOverlap=0;
        this.ExternalOverlap = 0;
        this.ExternalOvCount = 0;
        this.OverlapCount=0;
        this.Precision=0;
        this.Recall=0;
        this.MaxRecall = 0;
        this.PrecCount = 0;
        this.RecCount = 0;
        this.BaselineRecall=0;
        this.BaselinePrecision=0;
        this.BaselineLikelihood=0;
        this.Feedback = 0;
    }

    /**
     * @return the IssueCount
     */
    public float getIssueCount() {
        return IssueCount;
    }

    /**
     * @param IssueCount the IssueCount to set
     */
    public void setIssueCount(float IssueCount) {
        this.IssueCount = IssueCount;
    }

    /**
     * @return the Components
     */
    public float getComponents() {
        return Components;
    }

    /**
     * @param Components the Components to set
     */
    public void setComponents(float Components) {
        this.Components = Components;
    }

    /**
     * @return the Developers
     */
    public float getDevelopers() {
        return Developers;
    }

    /**
     * @param Developers the Developers to set
     */
    public void setDevelopers(float Developers) {
        this.Developers = Developers;
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

    /**
     * @return the Similarity
     */
    public float getSimilarity() {
        return Similarity;
    }

    /**
     * @param Similarity the Similarity to set
     */
    public void setSimilarity(float Similarity) {
        this.Similarity = Similarity;
    }
    
    @Override
    public String toString(){
        return "[ issue="+IssueCount+", comp="+Components
                +", dev="+Developers+", devlik="+DevLikelihood+", sim="+Similarity
                +", ov="+Overlap+", jac="+Jaccard
                +", lik="+Likelihood+", rec="+Recall+", prec="+Precision+"]";
    }

    /**
     * @return the OverlapCount
     */
    public float getOverlapCount() {
        return OverlapCount;
    }

    /**
     * @param OverlapCount the OverlapCount to set
     */
    public void setOverlapCount(float OverlapCount) {
        this.OverlapCount = OverlapCount;
    }

    /**
     * @return the Precision
     */
    public float getPrecision() {
        return Precision;
    }

    /**
     * @param Precision the Precision to set
     */
    public void setPrecision(float Precision) {
        this.Precision = Precision;
    }

    /**
     * @return the Likelihood
     */
    public float getLikelihood() {
        return Likelihood;
    }

    /**
     * @param Likelihood the Likelihood to set
     */
    public void setLikelihood(float Likelihood) {
        this.Likelihood = Likelihood;
    }

    /**
     * @return the Recall
     */
    public float getRecall() {
        return Recall;
    }

    /**
     * @param Recall the Recall to set
     */
    public void setRecall(float Recall) {
        this.Recall = Recall;
    }

    /**
     * @return the PrecCount
     */
    public float getPrecCount() {
        return PrecCount;
    }

    /**
     * @param PrecCount the PrecCount to set
     */
    public void setPrecCount(float PrecCount) {
        this.PrecCount = PrecCount;
    }

    /**
     * @return the RecCount
     */
    public float getRecCount() {
        return RecCount;
    }

    /**
     * @param RecCount the RecCount to set
     */
    public void setRecCount(float RecCount) {
        this.RecCount = RecCount;
    }

    /**
     * @return the DevLikelihood
     */
    public float getDevLikelihood() {
        return DevLikelihood;
    }

    /**
     * @param DevLikelihood the DevLikelihood to set
     */
    public void setDevLikelihood(float DevLikelihood) {
        this.DevLikelihood = DevLikelihood;
    }

    /**
     * @return the ExternalOverlap
     */
    public float getExternalOverlap() {
        return ExternalOverlap;
    }

    /**
     * @param ExternalOverlap the ExternalOverlap to set
     */
    public void setExternalOverlap(float ExternalOverlap) {
        this.ExternalOverlap = ExternalOverlap;
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
     * @return the BaselineRecall
     */
    public float getBaselineRecall() {
        return BaselineRecall;
    }

    /**
     * @param BaselineRecall the BaselineRecall to set
     */
    public void setBaselineRecall(float BaselineRecall) {
        this.BaselineRecall = BaselineRecall;
    }

    /**
     * @return the BaselinePrecision
     */
    public float getBaselinePrecision() {
        return BaselinePrecision;
    }

    /**
     * @param BaselinePrecision the BaselinePrecision to set
     */
    public void setBaselinePrecision(float BaselinePrecision) {
        this.BaselinePrecision = BaselinePrecision;
    }

    /**
     * @return the BaselineLikelihood
     */
    public float getBaselineLikelihood() {
        return BaselineLikelihood;
    }

    /**
     * @param BaselineLikelihood the BaselineLikelihood to set
     */
    public void setBaselineLikelihood(float BaselineLikelihood) {
        this.BaselineLikelihood = BaselineLikelihood;
    }

    /**
     * @return the MaxRecall
     */
    public float getMaxRecall() {
        return MaxRecall;
    }

    /**
     * @param MaxRecall the MaxRecall to set
     */
    public void setMaxRecall(float MaxRecall) {
        this.MaxRecall = MaxRecall;
    }

    /**
     * @return the Feedback
     */
    public float getFeedback() {
        return Feedback;
    }

    /**
     * @param Feedback the Feedback to set
     */
    public void setFeedback(float Feedback) {
        this.Feedback = Feedback;
    }
            
}
