/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.pasm;

import bugapp.persistence.dao.BugsDAO;
import bugapp.persistence.entity.BugPath;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Henrique
 */
public class REP {
    
    public static final int UNIGRAM_INDEX = 0;
    public static final int BIGRAM_INDEX = 1;
    public static final int PRODUCT_INDEX = 2;
    public static final int COMPONENT_INDEX = 3;
    public static final int TYPE_INDEX = 4;
    public static final int PRIORITY_INDEX = 5;
    public static final int VERSION_INDEX = 6;
    public static final int K1_UNIGRAM_INDEX = 7;
    public static final int K3_UNIGRAM_INDEX = 8;
    public static final int K1_BIGRAM_INDEX = 9;
    public static final int K3_BIGRAM_INDEX = 10;
    public static final int BF_UNIGRAM_INDEX = 11;
    public static final int BF_BIGRAM_INDEX = 12;
    public static final int WSUM_UNIGRAM = 13;
    public static final int WDESC_UNIGRAM = 14;
    public static final int WSUM_BIGRAM = 15;
    public static final int WDESC_BIGRAM = 16;
    
    public static final int MaxIndexAddWeight = 7;
    
    private static float Weights[]=
           {0.9f, //0 Unigrams
            0.2f, //1 Bi-grams
            2f, //2 Product
            0f, //3 Component
            0.7f, //4 type
            0f, //5 priority
            0f, //6 version
            2f, //7 K1 unigram
            0f, //8 K3 unigram
            2f, //9 K1 bigram
            0f, //10 K3 bigram
            0.5f, //11 Bf unigram
            0.5f, //12 Bf bigram
            3f, //13 W Summary Unigram
            1f, //14 W Desc Unigram
            3f, //15 W Summary Bigram
            1f //16 W Desc Bigram
            };
    
    private static boolean TrainWeight[]={
            true, //0 Unigrams
            true, //1 Bi-grams
            true, //2 Product
            true, //3 Component
            true, //4 type
            true, //5 priority
            true, //6 version
            true, //7 K1 unigram
            true, //8 K3 unigram
            true, //9 K1 bigram
            true, //10 K3 bigram
            true, //11 Bf unigram
            true, //12 Bf bigram
            true, //13 W Summary Unigram
            true, //14 W Desc Unigram
            true, //15 W Summary Bigram
            true //16 W Desc Bigram
            };
    
    public static float getWeightSum(){
        float ws = 0;
        for(int i=0; i<MaxIndexAddWeight; i++){
            ws+=Weights[i];
        }
        return ws;
    }
    
    public static float distance(BucketItem I1, BucketItem I2){
        return I1.repDistance(I2);
    }
    
    public static void train(ArrayList<BugPath> lstData, int MaxData, int MaxLoops, float TunningRate) throws SQLException, Exception{
        PasmAdhoc Pasm = new PasmAdhoc();
        //Pasm.setDocumentCount(lstData.size());
        
        ArrayList<BucketItem> lstTrainingSet = new ArrayList<>();
        ArrayList<BucketItem> lstRelevant = new ArrayList<>();
        ArrayList<BucketItem> lstIrrelevant = new ArrayList<>();
        
        BucketItem Item, Rel, Irr; 
        
        System.out.println("REP Training - finding relevant & irrelevant sets");
        fillRelevantAndIrrelevant(Pasm, lstData, lstTrainingSet, lstRelevant, lstIrrelevant, MaxData);
        
        long milisBefore = System.currentTimeMillis();
        System.out.println("REP Training - Begin Training");
        for(int i=0; i<MaxLoops; i++){
            System.out.printf("REP Training - Loop #%d \n",i);
            if(i!=0){
                Pasm = new PasmAdhoc();
                remakeBuckets(Pasm, lstTrainingSet, lstRelevant, lstIrrelevant);
            }
            
            int[] shufIndex = shuffleIndexArray( lstTrainingSet.size() );
            for(int j=0; j<MaxData; j++){
                Item = lstTrainingSet.get( shufIndex[j] ); //get training item in random order
                Rel = lstRelevant.get( shufIndex[j] );
                Irr = lstIrrelevant.get( shufIndex[j] );
                for(int w=0; w<Weights.length; w++){ //for each free parameter
                    if (TrainWeight[w]) {
                        double dw = TunningRate * rnc(Item, Rel, Irr, w);
                        Weights[w] = (float) (-dw + Weights[w]);
                        //if(Weights[w]<0) Weights[w] = 0;
                        if (Float.isNaN(Weights[w])) {
                            Weights[w] = 0; //System.out.printf("NAN: %d \n",w);
                        }
                    }
                }
            }
        }
        
        long milisAfter = System.currentTimeMillis();
        System.out.println("=============================");
        System.out.printf("# Training Time (ms): %d \n", milisAfter-milisBefore);
        System.out.println("=============================");
        
        printWeights();
    }
    
    private static void remakeBuckets(PasmAdhoc Pasm, ArrayList<BucketItem> lstTrain, ArrayList<BucketItem> lstRel, ArrayList<BucketItem> lstIr) throws Exception{
        for(int i=0; i<lstTrain.size(); i++){
            Pasm.recalcNaturalLanguageForRep( lstTrain.get(i) );
            Pasm.recalcNaturalLanguageForRep( lstRel.get(i) );
            Pasm.recalcNaturalLanguageForRep( lstIr.get(i) );
        }
    }
    
    private static void fillRelevantAndIrrelevant(PasmAdhoc Pasm, ArrayList<BugPath> lstData, ArrayList<BucketItem> lstTrain, ArrayList<BucketItem> lstRel, ArrayList<BucketItem> lstIr, int MaxData) throws SQLException, Exception{
        int[] shufIndex = shuffleIndexArray( lstData.size() );
        for(int i=0; i<lstData.size(); i++){
            System.out.printf("REP Training - Fill method i:%d, t-set:%d \n", i, lstTrain.size());
            BugPath Data = lstData.get( shufIndex[i] );
            ArrayList<BugPath> lst = BugsDAO.getBugFilesOpennedWhenForRep(Data);
            BugPath Rel = null, Irr = null;
            float OvRel=-1, OvIrr=1;
            
            for(BugPath Bp : lst){
                float ov = Data.overlap(Bp);
                if(ov < PasmAdhoc.getOverlapThreshold() ){
                    //Maybe an Irrelevant Bug
                    if(ov < OvIrr){
                        Irr = Bp;
                        OvIrr = ov;
                    }
                }
                else{
                    //Maybe a Relevant Bug
                    if(ov > OvRel){
                        Rel = Bp;
                        OvRel = ov;
                    }
                }
            }
            
            if(Rel!=null && Irr!=null){
                lstTrain.add( Pasm.createRepBucketItem(Data) );
                lstRel.add( Pasm.createRepBucketItem(Rel) );
                lstIr.add( Pasm.createRepBucketItem(Irr) );
                
                if(lstTrain.size()>=MaxData) return;
            }
        }
    }
    
    private static double rnc(BucketItem Main, BucketItem Rel, BucketItem Irr, int WeightIndex) {
        float simRel, simIrr;
        
        simRel = similarityPerWeight(Main, Rel, WeightIndex);
        simIrr = similarityPerWeight(Main, Irr, WeightIndex);
        
        double simDif = simIrr - simRel;
        
        double u = 1+Math.exp(simDif);
        double du = Math.exp(simDif)* simDif;
        //double fx = Math.log1p( ex );
        //double dx = Math.log1p( ex ) * ex * (simDif);
        double dx = du * 1.0 / (u * Math.log(2));
        if(Double.isNaN(dx)){
            System.out.printf("w: %d, sr: %f, si: %f, sdiff: %f, u: %f, du: %f, dx: %f \n", WeightIndex, simRel, simIrr, simDif, u, du, dx);
        }
        return dx;
    }
    
    private static float similarityPerWeight(BucketItem I1, BucketItem I2, int WeightIndex){
        switch(WeightIndex){
            case UNIGRAM_INDEX:
                return I1.normalizedSimUnigramBM25Fext(I2)*Weights[WeightIndex];
                
            case BIGRAM_INDEX:
                return I1.normalizedSimBigramBM25Fext(I2)*Weights[WeightIndex];
                
            case PRODUCT_INDEX:
                return (I1.isSameProduct(I2)?1:0)*Weights[WeightIndex];
                    
            case COMPONENT_INDEX:
                return (I1.isSameComponent(I2)?1:0)*Weights[WeightIndex];
                
            case TYPE_INDEX:
                return (I1.isSameType(I2)?1:0)*Weights[WeightIndex];
                
            case PRIORITY_INDEX:
                return (1f/(1f+I1.getPriorityDiference(I2)))*Weights[WeightIndex];
                
            case VERSION_INDEX:
                return (1f/(1f+I1.getVersionDiference(I2)))*Weights[WeightIndex];
                
            case K1_UNIGRAM_INDEX:
            case K3_UNIGRAM_INDEX:
            case BF_UNIGRAM_INDEX:
            case WSUM_UNIGRAM:
            case WDESC_UNIGRAM:
                return I1.normalizedSimUnigramBM25Fext(I2)*Weights[UNIGRAM_INDEX];

            case K1_BIGRAM_INDEX:
            case K3_BIGRAM_INDEX:
            case BF_BIGRAM_INDEX:
            case WSUM_BIGRAM:
            case WDESC_BIGRAM:
                return I1.normalizedSimBigramBM25Fext(I2)*Weights[BIGRAM_INDEX];

            default:
                return 0;
        }
    }
    
    public static float getWeight(int index){
        return Weights[index];
    }
    
    private static void setTrainWeight(boolean train, int... indexes){
        for(int i=0; i<indexes.length; i++){
            TrainWeight[ indexes[i] ]=train;
        }
    }
    
    public static void fixWeight(int... indexes){
        setTrainWeight(false, indexes);
    }
    
    public static void unfixWeight(int... indexes){
        setTrainWeight(true, indexes);
    }
    
    private static int[] shuffleIndexArray(int Max){
        int[] shufIndex = new int[Max];
        //filling the array
        for(int i=0; i<Max; i++){
            shufIndex[i]=i;
        }
        
        //shuffling the array
        Random rand = new Random(System.currentTimeMillis());
        int aux, s1, s2;
        for(int i=0; i<Max/2; i++){
            s1 = rand.nextInt(Max);
            s2 = rand.nextInt(Max);
            if(s1!=s2){
                aux = shufIndex[s1];
                shufIndex[s1]=shufIndex[s2];
                shufIndex[s2]=aux;
            }
        }
        
        return shufIndex;
    }
    
    public static float calcTermBM25Fext(float Idf, float DocTf, float Qtf, float k1, float k3){
        if(Qtf==0 || DocTf==0) return 0;
        else if(k3 == 0) return Idf * (DocTf/(DocTf+k1));
        else return Idf * (DocTf/(DocTf+k1)) * (((k3+1)*Qtf)/(k3+Qtf));        
    }
    
    public static float calcTFD(float DocF, float bf, float length, float averageLength){
        return DocF / (1 - bf + bf*length/averageLength );
    }
    
    public static void printWeights(){
        System.out.printf("REP Weights: %s \n", Arrays.toString(Weights));
    }
    
    public static String weightsToString(){
        return Arrays.toString(Weights);
    }
    
}
