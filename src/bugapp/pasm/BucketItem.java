/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import bugapp.ir.SparseDataItem;
import bugapp.ir.SparseDataVector;
import bugapp.persistence.entity.BugPath;
import bugapp.util.SetUtil;
import java.text.SimpleDateFormat;

/**
 *
 * @author Henrique
 */
public class BucketItem {
    private BugPath BugData;
    
    private SparseDataVector UnigramData; 
    private SparseDataVector BigramData;
    
    protected float MaxUnigramBM25Fext = -1; //For Normalization
    protected float MaxBigramBM25Fext = -1; //For Normalization
    
    public BucketItem(){
    }

    /**
     * @return the BugData
     */
    public BugPath getBugData() {
        return BugData;
    }

    /**
     * @param BugData the BugData to set
     */
    public void setBugData(BugPath BugData) {
        this.BugData = BugData;
    }

    /**
     * @return the NlData
     */
    public SparseDataVector getSumUnigramData() {
        return UnigramData;
    }

    /**
     * @param NlData the NlData to set
     */
    public void setSumUnigramData(SparseDataVector NlData) {
        this.UnigramData = NlData;
    }

    public float repDistance(BucketItem I){
        float sim = 0;
        
        sim += REP.getWeight(REP.UNIGRAM_INDEX) * (this.normalizedSimUnigramBM25Fext(I));
        sim += REP.getWeight(REP.BIGRAM_INDEX) * (this.normalizedSimBigramBM25Fext(I));
        sim += REP.getWeight(REP.PRODUCT_INDEX) * (this.isSameProduct(I)?1f:0f);
        sim += REP.getWeight(REP.COMPONENT_INDEX) * (this.isSameComponent(I)?1f:0f);
        sim += REP.getWeight(REP.TYPE_INDEX) * (this.isSameType(I)?1f:0f);
        sim += REP.getWeight(REP.PRIORITY_INDEX) * (1f/(1f+this.getPriorityDiference(I)));
        sim += REP.getWeight(REP.VERSION_INDEX) *(1f/(1f+this.getVersionDiference(I)));
        
        //normalize REP to 1 (weighted average)
        sim /= REP.getWeightSum();
        
        if(sim>1){ 
            //Only to test our normalization technique - Should **not** occur, unless there is a rounding float error.
            System.out.printf("sim:%f \n", sim);
            sim=1;
        }
        return (1f-sim); //distance is 1-similarity
    }
    
    public float distance(BucketItem I){
        //return NlData.cosineDistance(I.NlData);
        return distanceIdf(I);
    }

    protected float distanceIdf(BucketItem I){
        float d=0;
        float normA = 0, tempA;
        float normB = 0, tempB;
        float fb;
        
        for(SparseDataItem A : this.UnigramData.getItems()){
            tempA = A.getFrequency() * PasmFactory.idf(A);
            normA += tempA * tempA;
            fb = I.UnigramData.getFrequency(A.getIndex());
            if(fb!=0){
                d += fb * PasmFactory.idf(A) * tempA;
            }
        }
        for(SparseDataItem B : I.UnigramData.getItems()){
            tempB = B.getFrequency()*PasmFactory.idf(B);
            normB += tempB * tempB;
        }
        d =(float) (d / ( Math.sqrt(normA)*Math.sqrt(normB)));
        if(Float.isNaN(d)){
            d=0;
        }
        return 1-d;
    }
    
    public float normalizedSimUnigramBM25Fext(BucketItem Bi){
        return simBM25Fext(this.UnigramData, Bi.UnigramData, REP.getWeight(REP.K1_UNIGRAM_INDEX), REP.getWeight(REP.K3_UNIGRAM_INDEX), REP.getWeight(REP.BF_UNIGRAM_INDEX), PasmFactory.getAverageUnigramLength()) 
                / this.getMaxUnigramBM25Fext();
    }
    
    public float normalizedSimBigramBM25Fext(BucketItem Bi){
        return simBM25Fext(this.BigramData, Bi.BigramData, REP.getWeight(REP.K1_BIGRAM_INDEX), REP.getWeight(REP.K3_BIGRAM_INDEX), REP.getWeight(REP.BF_BIGRAM_INDEX), PasmFactory.getAverageBigramLength() )
                / this.getMaxBigramBM25Fext();
    }
    
    public float simUnigramBM25Fext(BucketItem Bi){
        return simBM25Fext(this.UnigramData, Bi.UnigramData, REP.getWeight(REP.K1_UNIGRAM_INDEX), REP.getWeight(REP.K3_UNIGRAM_INDEX), REP.getWeight(REP.BF_UNIGRAM_INDEX), PasmFactory.getAverageUnigramLength());
    }
    
    public float simBigramBM25Fext(BucketItem Bi){
        return simBM25Fext(this.BigramData, Bi.BigramData, REP.getWeight(REP.K1_BIGRAM_INDEX), REP.getWeight(REP.K3_BIGRAM_INDEX), REP.getWeight(REP.BF_BIGRAM_INDEX), PasmFactory.getAverageBigramLength() );
    }
    
    public static float simBM25Fext(SparseDataVector QueryNgram, SparseDataVector DocumentNgram, float K1, float K3, float Bf, float AvgLength){
        float d=0;
        float dtf, qtf, normD=1, normQ=1;
        
        if(DocumentNgram!=null && QueryNgram!=null){
            for (SparseDataItem Doc : DocumentNgram.getItems()) {
                //dtf = Doc.getFrequency();
                dtf = REP.calcTFD(Doc.getFrequency(), Bf, DocumentNgram.length(), AvgLength);
                //normD += dtf*dtf; //less strict, however much faster
                //normD += dtf*dtf*PasmFactory.idf(Doc)*PasmFactory.idf(Doc)*(K1+1)*(K1+1); //More math strict, however slower
                qtf = QueryNgram.getFrequency(Doc.getIndex());
                if (qtf != 0) {
                    //normB += btf*btf;
                    d += REP.calcTermBM25Fext(PasmFactory.idf(Doc), dtf, qtf, K1, K3);
                    //d += PasmFactory.idf(A) * atf/(atf+REP.getK1()) * ((REP.getK3()+1)*btf)/(REP.getK3()+btf);
                }
                //maxTf = atf>btf?atf:btf;
                //normA += REP.calcTermBM25Fext(PasmFactory.idf(A), maxTf, maxTf, K1, K3);
            }
//            for(SparseDataItem Q : QueryNgram.getItems()){
//                qtf = Q.getFrequency();
//                normQ += qtf*qtf; //less strict, however much faster
//                //normQ += qtf*qtf*(K3+1)*(K3+1); //More math strict, however slower    
//            }
        }
        //Normalization now is performed outside this method, for "repDistance" using the simBM25F
        //d =(float) (d/((K1+1)*(K1+1)*normD+(K3+1)*(K3+1)*normQ)); //less strict, however much faster
        //d =(float) (d/(Math.sqrt(normD)*Math.sqrt(normQ))); //More math strict, however slower    
        if(Float.isNaN(d) || Float.isInfinite(d)) {
            d=0;
        }
        //Normalization is now performed outside this method
//        if(d>1){ /
//            //Only to test our normalization technique - Should **not** occur! Ever!
//            System.out.printf("d:%f, a.size:%d, b.size: %d \n", d, DocumentNgram.getItems().size(), QueryNgram.getItems().size());
//            d=1;
//        }
        return d;
    }
    
    public float taskCoeficient(BucketItem Bi){
        float over = 0, intersect,  min;

        if(this.getBugData().getPaths().isEmpty() || Bi.getBugData().getPaths().isEmpty()){
            over = -1;
        }
        else if(this != Bi) {
            intersect = SetUtil.intersection(this.getBugData().getPaths(), Bi.getBugData().getPaths());
            min = (float)Bi.getBugData().getPaths().size();
            over = intersect / min;
        }
        return over;
    }
    
    
    public float overlap(BucketItem Bi){
        float over = 0, intersect,  min;

        if(this.getBugData().getPaths().isEmpty() || Bi.getBugData().getPaths().isEmpty()){
            over = -1;
        }
        else if(this != Bi) {
            intersect = SetUtil.intersection(this.getBugData().getPaths(), Bi.getBugData().getPaths());
            min = SetUtil.min(this.getBugData().getPaths(), Bi.getBugData().getPaths()); //min - overlap
            //min = (float)Bi.getBugData().getPaths().size(); // |F2| - novo coeficiente de reuso de contexto
            
            over = intersect / min;
        }
        return over;
    }
    
    public float jaccard(BucketItem Bi){
        float jac = 0, intersect, union;
        
        if (this != Bi && !Bi.getBugData().getPaths().isEmpty()) {
            intersect = SetUtil.intersection(this.getBugData().getPaths(), Bi.getBugData().getPaths());
            union = SetUtil.union(intersect, this.getBugData().getPaths(), Bi.getBugData().getPaths());
            jac = intersect / union;
        }

        return jac;
    }
    
    public boolean isSameDevel(BucketItem Bi){
        return this.getBugData().isSameDeveloper(Bi.getBugData());
        //return this.getBugData().getDeveloperId() == Bi.getBugData().getDeveloperId();
    }
    
    public boolean isSameComponent(BucketItem Bi){
        return this.getBugData().isSameComponent(Bi.getBugData());
        //return this.getBugData().getComponentId() == Bi.getBugData().getComponentId();
    }
    
    public boolean isSameType(BucketItem Bi){
        return this.getBugData().isSameType(Bi.getBugData());
    }
    
    public boolean isSameProduct(BucketItem Bi){
        return this.getBugData().isSameProduct(Bi.getBugData());
    }
    
    public int getVersionDiference(BucketItem Bi){
        return this.getBugData().versionDif(Bi.getBugData());
    }

    public int getPriorityDiference(BucketItem Bi){
        return this.getBugData().priorityDif(Bi.getBugData());
    }
    
    @Override
    public String toString(){
        StringBuilder stb=new StringBuilder();
        stb.append("[Id=");
        stb.append(BugData.getBugId());
        stb.append(", Dt(");
        stb.append(SimpleDateFormat.getDateInstance().format(BugData.getDtCreation()));
        stb.append(",");
        stb.append(SimpleDateFormat.getDateInstance().format(BugData.getDtClose()));
        stb.append("), Cp=");
        stb.append(BugData.getComponentId());
        stb.append(", Dv=");
        stb.append(BugData.getDeveloperId());
        stb.append(", ");
        stb.append(BugData.getShortDesc());
        stb.append(", Path=");
        stb.append(BugData.getPaths().toString());
        stb.append("]");
        return stb.toString();
    }

    /**
     * @return the BigramData
     */
    public SparseDataVector getBigramData() {
        return BigramData;
    }

    /**
     * @param BigramData the BigramData to set
     */
    public void setBigramData(SparseDataVector BigramData) {
        this.BigramData = BigramData;
    }

    /**
     * @return the MaxUnigramBM25Fext
     */
    public float getMaxUnigramBM25Fext() {
        if(MaxUnigramBM25Fext<0){
            MaxUnigramBM25Fext = this.simUnigramBM25Fext(this);
        }
        return MaxUnigramBM25Fext;
    }

    /**
     * @return the MaxBigramBM25Fext
     */
    public float getMaxBigramBM25Fext() {
        if(MaxBigramBM25Fext<0){
            MaxBigramBM25Fext = this.simBigramBM25Fext(this);
        }
        return MaxBigramBM25Fext;
    }
    
}
