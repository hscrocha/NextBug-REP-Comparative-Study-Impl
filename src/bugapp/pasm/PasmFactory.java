/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import au.com.bytecode.opencsv.CSVWriter;
import bugapp.ir.SparseDataItem;
import bugapp.ir.SparseDataVector;
import bugapp.persistence.entity.BugPath;
import bugapp.stem.BugAppStemmingFilter;
import bugapp.stem.StemmingSnow;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Henrique
 */
public class PasmFactory {
    
    private static HashMap<String, Integer> hsmWords = new HashMap<>();
    private static HashMap<Integer, Float> hsmDocFreq = new HashMap<>(); //F //Não preciso do F para os cálculos
    private static HashMap<Integer, Float> hsmDocNumber = new HashMap<>(); //ni(F)
    private int WordCount;
    private static int DocCount;

    private static float TotalUnigramLength;
    private static float TotalBigramLength;
    
    //private int BiGramCount;
    //private static HashMap<String, Integer> hsmBiWords = new HashMap<>();
    //private static HashMap<Integer, Float> hsmBiDocFreq = new HashMap<>();
    //private static HashMap<Integer, Float> hsmBiDocNumber = new HashMap<>();

    private LinkedList<Bucket> lstOpenBuckets;
    private LinkedList<Bucket> lstClosedBuckets;

    protected float DistanceThreshold = 0.7f;
    private int RebucketCount;
    
    public PasmFactory(){
        hsmWords.clear();
        hsmDocFreq.clear();
        hsmDocNumber.clear();
        WordCount = 1;
        DocCount = 0;
        //BiGramCount = 1;
        TotalUnigramLength = 0;
        TotalBigramLength = 0;
        
        lstOpenBuckets = new LinkedList<Bucket>();
        lstClosedBuckets = new LinkedList<Bucket>();
    }
    
    public void pasm(ArrayList<BugPath> lstBugs) throws Exception {
        RebucketCount=0;
        BucketItem I;
        setDocumentCount(lstBugs.size());
        for(BugPath Bp : lstBugs){
            I = createBucketItem(Bp);
            pasm(I,true);
        }
        
        if(!Bucket.isInfinityBucket()){
            closeRemainingBuckets();
        }
        
        if(RebucketCount>0){
            System.out.printf("Rebucket count %d%n",RebucketCount);
        }
    }
    
    private void pasm(BucketItem Data, boolean recluster){
        Iterator<Bucket> It = lstOpenBuckets.iterator();
        Bucket B, MinBucket = null;
        float Dist, MinDist = 1;
        
        //Encontrar o Bucket Aberto mais próximo do Dado
        while(It.hasNext()){
            B = It.next();
            if(Bucket.isInfinityBucket()){
                
            }
            
            if(B.isAfterTimeFrame(Data)){
                //TimeFrame do Bucket excedeu o limite, fecha-lo
                B.setStatus( Bucket.CLOSED_STATUS );
                lstClosedBuckets.add(B);
                It.remove(); 
            }
            else{
                //Bucket Aberto atual pode ser o melhor
                Dist = B.distance(Data);
                if(Dist < MinDist){
                    MinDist = Dist;
                    MinBucket = B;
                }
            }
        }
        
        //Se a Distancia for menor que o Threshold, colocar no Bucket
        if(MinDist < DistanceThreshold){
            MinBucket.add(Data);
        }
        else{
            //Criar um novo bucket
            MinBucket = BucketFactory.create(Data);
            if(recluster){
                rebucket(MinBucket);
            }
            lstOpenBuckets.add(MinBucket);
        }
    }
    
    private void rebucket(Bucket NewBucket){
        float OldDist, NewDist;
        BucketItem Bi = null;
        for(Bucket B : lstOpenBuckets){
            Iterator<BucketItem> It = B.getData().iterator();
            while(It.hasNext()){
                Bi = It.next();
                OldDist = B.distance(Bi);
                NewDist = NewBucket.distance(Bi);
                if(NewDist < OldDist){
                    //System.out.println("Recluster");
                    RebucketCount++;
                    NewBucket.add(Bi);
                    It.remove();
                }
            }
        }
    }
    
    private void closeRemainingBuckets(){
        Iterator<Bucket> It = lstOpenBuckets.iterator();
        Bucket B = null;
        
        while(It.hasNext()){
            B = It.next();
            B.setStatus(Bucket.CLOSED_STATUS);
            lstClosedBuckets.add(B);
            It.remove();
        }
    }

    public BucketItem createBucketItem(BugPath Data) throws Exception {
        SparseDataVector V = new SparseDataVector(Data.getBugId());
        makeWordTerms(V, Data.getShortDesc(), 1);
        updateNi(V);
        V.logNormalization();
        incrementDocumentCount();

        BucketItem bi = new BucketItem();
        bi.setBugData(Data);
        bi.setSumUnigramData(V);
        return bi;
    } 

    public BucketItem createRepBucketItem(BugPath Data) throws Exception {
//        //SparseDataVector V = makeWordTerms(Data.getBugId(), Data.getShortDesc(),1);
//        SparseDataVector V = new SparseDataVector(Data.getBugId());
//        makeWordTerms(V, Data.getShortDesc(), REP.getWeight(REP.WSUM_UNIGRAM));
//        if(Data.getFullDesc()!=null && Data.getFullDesc().length()>0){
//            makeWordTerms(V, Data.getFullDesc(), REP.getWeight(REP.WDESC_UNIGRAM));
//        }
//        updateNi(V);
//        TotalUnigramLength += V.length();
//        //V.logNormalization();
//
//        //SparseDataVector Big = makeBiGramTerms(Data.getBugId(), Data.getShortDesc(),1);
//        SparseDataVector Bg = new SparseDataVector(Data.getBugId());
//        makeBiGramTerms(Bg, Data.getShortDesc(), REP.getWeight(REP.WSUM_BIGRAM));
//        if(Data.getFullDesc()!=null && Data.getFullDesc().length()>0){
//            makeBiGramTerms(Bg, Data.getFullDesc(), REP.getWeight(REP.WDESC_BIGRAM));
//        }
//        updateNi(Bg);
//        TotalBigramLength += Bg.length();
//        //Bg.logNormalization();

        SparseDataVector V = createRepSparseDataVector(Data, true);
        SparseDataVector Bg = createRepSparseDataVector(Data, false);
        
        incrementDocumentCount();

        BucketItem bi = new BucketItem();
        bi.setBugData(Data);
        bi.setSumUnigramData(V);
        bi.setBigramData(Bg);
        return bi;
    } 
    
    public void recalcNaturalLanguageForRep(BucketItem I) throws Exception{
        SparseDataVector V = createRepSparseDataVector(I.getBugData(), true);
        SparseDataVector Bg = createRepSparseDataVector(I.getBugData(), false);
        
        incrementDocumentCount();

        I.setSumUnigramData(V);
        I.setBigramData(Bg);
    }
    
    private SparseDataVector createRepSparseDataVector(BugPath Data, boolean Unigram) throws Exception{
        SparseDataVector V = new SparseDataVector(Data.getBugId());
        
        if(Unigram){
            makeWordTerms(V, Data.getShortDesc(), REP.getWeight(REP.WSUM_UNIGRAM));
            if(Data.getFullDesc()!=null && Data.getFullDesc().length()>0){
                makeWordTerms(V, Data.getFullDesc(), REP.getWeight(REP.WDESC_UNIGRAM));
            }
            updateNi(V);
            TotalUnigramLength += V.length();
        }
        else{
            makeBiGramTerms(V, Data.getShortDesc(), REP.getWeight(REP.WSUM_BIGRAM));
            if(Data.getFullDesc()!=null && Data.getFullDesc().length()>0){
                makeBiGramTerms(V, Data.getFullDesc(), REP.getWeight(REP.WDESC_BIGRAM));
            }
            updateNi(V);
            TotalBigramLength += V.length();
        }
        return V;
    }
        
    private void makeWordTerms(SparseDataVector Vet, String BugDesc, float Weight) throws Exception{
        Integer ColIndex;
        String Word;
        StringTokenizer stk=new StringTokenizer(BugDesc," ;,!?'\"<>[](){}+*=\\@#$%^&~"); //retirado :.-/_
        //StringTokenizer stk=new StringTokenizer(Description," ;:.,!?'\"<>[](){}-+*=/\\_@#$%^&~");

        //SparseDataVector Vet = new SparseDataVector(BugId);
        BugAppStemmingFilter Filter = new StemmingSnow();
        
        while(stk.hasMoreTokens()) {
            Word = Filter.processWord(stk.nextToken().toLowerCase());
            if (Word != null) {
                ColIndex = hsmWords.get(Word);
                if (ColIndex == null) {
                    //O termo não existe na lista de termos
                    //Inserir o termo na lista de termos
                    ColIndex = WordCount;
                    hsmWords.put(Word, ColIndex);
                    hsmDocFreq.put(ColIndex, 0f);
                    hsmDocNumber.put(ColIndex, 0f);
                    WordCount++;
                }
                //Todo caso, deve-se adicionar frequencia no vetor
                Vet.increment(ColIndex, Weight);
            }
        }
        //DocCount++;
        //updateNi(Vet);
        //Vet.logNormalization();
        //return Vet;
    }
        
    public void incrementDocumentCount(){
        DocCount++;
    }
    
    public void updateNi(SparseDataVector Vet){
        Collection<SparseDataItem> colVetItems = Vet.getItems();
        for(SparseDataItem Item : colVetItems){
            float F = hsmDocFreq.get(Item.getIndex());
            F+=Item.getFrequency();
            hsmDocFreq.put(Item.getIndex(), F);
            
            float Ni = hsmDocNumber.get(Item.getIndex());
            Ni++;
            hsmDocNumber.put(Item.getIndex(), Ni);
        }
    }
    
    public static void setDocumentCount(int d){
        DocCount = d;
    }
    
    private String getNextWord(StringTokenizer stk, BugAppStemmingFilter Filter) throws Exception{
        while(stk.hasMoreTokens()){
            String Word = Filter.processWord(stk.nextToken().toLowerCase());
            if(Word!=null) return Word;
        }
        return null;
    }
    
    private void makeBiGramTerms(SparseDataVector Vet, String BugDesc, float Weight) throws Exception{
        Integer ColIndex;
        String Word1, Word2=null, BiGram;
        StringTokenizer stk=new StringTokenizer(BugDesc," ;,!?'\"<>[](){}+*=\\@#$%^&~"); //retirado :.-/_
        //StringTokenizer stk=new StringTokenizer(Description," ;:.,!?'\"<>[](){}-+*=/\\_@#$%^&~");

        //SparseDataVector Vet = new SparseDataVector(BugId);
        BugAppStemmingFilter Filter = new StemmingSnow();
        
        Word2 = getNextWord(stk, Filter);
        
        while(stk.hasMoreTokens()) {
            Word1 = Word2;
            Word2 = getNextWord(stk, Filter);
            
            if (Word2 != null) {
                BiGram = Word1+":"+Word2;
                
                ColIndex = hsmWords.get(BiGram);
                if (ColIndex == null) {
                    //O termo não existe na lista de termos
                    //Inserir o termo na lista de termos
                    ColIndex = WordCount;
                    hsmWords.put(BiGram, ColIndex);
                    hsmDocFreq.put(ColIndex, 0f);
                    hsmDocNumber.put(ColIndex, 0f);
                    WordCount++;
                }
                //Todo caso, deve-se adicionar frequencia no vetor
                Vet.increment(ColIndex, Weight);
            }
        }
        //DocCount++;
        //updateNiBigram(Vet);
        //Vet.logNormalization();
        //return Vet;
    }
    
//    private void updateNiBigram(SparseDataVector Vet){
//        Collection<SparseDataItem> colVetItems = Vet.getItems();
//        for(SparseDataItem Item : colVetItems){
//            float F = hsmBiDocFreq.get(Item.getIndex());
//            F+=Item.getFrequency();
//            hsmBiDocFreq.put(Item.getIndex(), F);
//            
//            float Ni = hsmBiDocNumber.get(Item.getIndex());
//            Ni++;
//            hsmBiDocNumber.put(Item.getIndex(), Ni);
//        }
//    }
    
    private void calcIdf(SparseDataVector Vet){
        Collection<SparseDataItem> colItems = Vet.getItems();
        for (SparseDataItem Item : colItems) {
            float Idf = (float) DocCount; //Número total de documentos
            Idf /= hsmDocNumber.get(Item.getIndex()); //ni
            Idf = (float) (Math.log(Idf) / Math.log(2)); //log2 (N / ni)
            Item.multFrequency(Idf); //Tf * Idf
        }
    }
    
    protected static float idf(SparseDataItem V){
        float Idf = (float) DocCount;
        Idf /= hsmDocNumber.get(V.getIndex());
        return (float)(Math.log(Idf) / Math.log(2));
    }
    
    public static float getAverageUnigramLength(){
        return TotalUnigramLength / DocCount;
    }

    public static float getAverageBigramLength(){
        return TotalUnigramLength / DocCount;
    }
    

//    protected static float idfBiGram(SparseDataItem V){
//        float Idf = (float) DocCount;
//        Idf /= hsmBiDocNumber.get(V.getIndex());
//        return (float)(Math.log(Idf) / Math.log(2));
//    }
    
    public void writeWordsToFileWihCount(String FileName) throws Exception{
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, ';', CSVWriter.NO_QUOTE_CHARACTER);
        
        String[] Line = new String[3];
        for(String Word : hsmWords.keySet()){
            Integer Index = hsmWords.get(Word);
            Line[0]=Index.toString();
            Line[1]=Word;
            Line[2]=hsmDocFreq.get(Index).toString();
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }
    
    public void writeCloseBucketsToCsv(String FileName) throws Exception {
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, ';', CSVWriter.NO_QUOTE_CHARACTER);
        
        String[] Line = null;
        for(Bucket B : lstClosedBuckets){
            if(B.getItemPathCount()>1){
                Line = B.toCvsLine();
                cwrOutput.writeNext(Line);
            }
        }
        cwrOutput.close();
        fwrWriter.close();
    }

    public void writeClosedBucketsToFile(String FileName) throws Exception {
        if(!Bucket.isInfinityBucket()){
            writeClosedBucketsToFileNormal(FileName);
        }
        else{
            writeClosedBucketsToFileInfinityBucket(FileName);
        }
    }
    
    
    private void writeClosedBucketsToFileNormal(String FileName) throws Exception {
        FileWriter fwr = new FileWriter(FileName);
        int UnitaryCount = 0;
        int NoUnitaryCount = 0;
        float MappedRatio = 0;
        int MappedBuckets = 0;
        int MappedNoUnitary = 0;
        float MappedNoUnitaryRario = 0;
        float AvgTamNoUnitary = 0;
        int BugCount = 0;
        int NoUnitaryWithMoreThanOneMappedBug = 0;
        int TotLost = 0;
        int TotGain = 0;
        float EarlyPrediction = 0;
        float AvgLostBucket;
        float AvgLostBug;
        float AvgGainBucket;
        float AvgGainBug;
        float[] metrics = new float[7];
        Arrays.fill(metrics, 0);
        
        for(Bucket B : lstClosedBuckets){
            BugCount += B.getData().size();
            TotLost += B.getHoursLost();
            TotGain += B.getHoursGain();
            EarlyPrediction+=B.getEarlyCount();
            if(!B.isUnitary()){
                AvgTamNoUnitary+= B.getData().size();
                NoUnitaryCount++;
                fwr.write( B.toString() );
                fwr.write("\n");
                if(B.getItemPathCount()>1){
                    NoUnitaryWithMoreThanOneMappedBug++;
                    //metric1+=B.jaccardPaths(); //Jaccard
                    metrics[0]+=B.calcOverlap();
                    metrics[1]+=B.jaccardPaths();
                    metrics[2]+=B.calcSorenson();
                    metrics[3]+=B.calcOchiai();
                    metrics[4]+=B.calcPSC();
                    metrics[5]+=B.calcKulczynski();
                    metrics[6]+=B.calcBinaryIntersect();
                }
            }
            else{
                UnitaryCount++;
            }
            
            float mapRatio = B.mappedRatio();
            if(mapRatio>0){
                MappedRatio+=mapRatio;
                MappedBuckets++;
                if(!B.isUnitary()){
                    MappedNoUnitary++;
                    MappedNoUnitaryRario+=mapRatio;
                }
            }
        }
        MappedRatio/=(float)MappedBuckets;
        MappedNoUnitaryRario/=(float)MappedNoUnitary;
        AvgTamNoUnitary/=(float)NoUnitaryCount;
        AvgLostBucket=TotLost/(float)lstClosedBuckets.size();
        AvgLostBug=TotLost/(float)BugCount;
        AvgGainBucket=TotGain/(float)lstClosedBuckets.size();
        AvgGainBug=TotGain/(float)BugCount;
        EarlyPrediction/=(float)BugCount;
        for(int i=0; i<metrics.length; i++){
            metrics[i]/=(float)NoUnitaryWithMoreThanOneMappedBug;
        }

        fwr.write("\n\n *** Total Bugs = "+Integer.toString(BugCount));

        fwr.write("\n\n *** Unitary Buckets = "+Integer.toString(UnitaryCount));
        fwr.write("\n *** Total Buckets = "+Integer.toString(lstClosedBuckets.size()));

        fwr.write("\n\n *** Mapped Buckets (All) = "+Integer.toString(MappedBuckets));
        fwr.write("\n *** Average Mapped Ratio (All) = "+Float.toString(MappedRatio));
        fwr.write("\n *** Mapped Buckets (Without Unitary) = "+Integer.toString(MappedNoUnitary));
        fwr.write("\n *** Average Mapped Ratio (Without Unitary) = "+Float.toString(MappedNoUnitaryRario));
        
        fwr.write("\n\n *** Average Bucket Size (Without Unitary) = "+Float.toString(AvgTamNoUnitary));
        fwr.write("\n *** Buckets with more than 1 Mapped Bug (Without Unitary) = "+Integer.toString(NoUnitaryWithMoreThanOneMappedBug));

        fwr.write("\n\n *** Average Hours Lost to PASM (Bug) = "+Float.toString(AvgLostBug));
        fwr.write("\n *** Average Hours Lost to PASM (Bucket) = "+Float.toString(AvgLostBucket));
        fwr.write("\n *** Percentage BugTimeHit success with PASM = "+Float.toString(EarlyPrediction));
        fwr.write("\n *** Average Hours Gain to PASM (Bug) = "+Float.toString(AvgGainBug));
        fwr.write("\n *** Average Hours Gain to PASM (Bucket) = "+Float.toString(AvgGainBucket));
        fwr.write("\n *** Total Hours Gained to PASM (Without Unitary Buckets) = "+Integer.toString(TotGain));

        fwr.write("\n\n *** Metrics ");
        for(int i=0; i<metrics.length; i++){
            fwr.write("\n ******* Average Metric"+Integer.toString(i+1)+" (above Buckets) = "+Float.toString(metrics[i]));
        }
        fwr.close();
    }
    
    private void writeClosedBucketsToFileInfinityBucket(String FileName) throws Exception {
        FileWriter fwr = new FileWriter(FileName);

        int TotalBugs = 0;
        int UnitaryCount = 0;
        int NoUnitaryCount = 0;
        int FileMetricDivisor = 0;
        int TopFileMetricDivisor = 0;

        AverageBucketMetrics Metrics[]=new AverageBucketMetrics[2];
        Metrics[0]=new AverageBucketMetrics();
        Metrics[1]=new AverageBucketMetrics();
        
        AverageBucketMetrics locMet[];
        float allOver, topOver, allSim, topSim;
        
        for (Bucket B : lstOpenBuckets) {
            TotalBugs += B.getData().size();
            if (!B.isUnitary()) {
                fwr.write( B.toString() );
                fwr.write("\n");

                NoUnitaryCount++;
                locMet = B.calcClosedBugMetricsTop();
                allSim = locMet[0].getSimilarity();
                allOver = locMet[0].getOverlap();
                
                if (allSim >= 0) {
                    Metrics[0].Similarity += allSim;
                    Metrics[0].IssueCount += locMet[0].getIssueCount();
                    Metrics[0].Components += locMet[0].getComponents();
                    Metrics[0].Developers += locMet[0].getDevelopers();
                    //locOver = Metrics.getOverlap();
                    if (allOver >= 0) {
                        FileMetricDivisor++;
                        Metrics[0].Overlap += allOver;
                        Metrics[0].Jaccard += locMet[0].getJaccard();;
                    }
                }
                else{
                    Metrics[0].IssueCount++; //Existia pelo menos 1 issue no bucket
                }
                
                topSim = locMet[1].getSimilarity();
                topOver = locMet[1].getOverlap();
                if (topSim >= 0) {
                    Metrics[1].Similarity += topSim;
                    Metrics[1].IssueCount += locMet[1].getIssueCount();
                    Metrics[1].Components += locMet[1].getComponents();
                    Metrics[1].Developers += locMet[1].getDevelopers();
                    //locOver = Metrics.getOverlap();
                    if (topOver >= 0) {
                        TopFileMetricDivisor++;
                        Metrics[1].Overlap += topOver;
                        Metrics[1].Jaccard += locMet[1].getJaccard();;
                    }
                }
                
            }            
            else{
                UnitaryCount++;
            }
            
        }
        
        Metrics[0].IssueCount /= NoUnitaryCount;
        Metrics[0].Components /= NoUnitaryCount;
        Metrics[0].Developers /= NoUnitaryCount;
        Metrics[0].Similarity /= NoUnitaryCount;
        Metrics[0].Overlap /= FileMetricDivisor;
        Metrics[0].Jaccard /= FileMetricDivisor;

        Metrics[1].IssueCount /= NoUnitaryCount;
        Metrics[1].Components /= NoUnitaryCount;
        Metrics[1].Developers /= NoUnitaryCount;
        Metrics[1].Similarity /= NoUnitaryCount;
        Metrics[1].Overlap /= TopFileMetricDivisor;
        Metrics[1].Jaccard /= TopFileMetricDivisor;

        fwr.write("\n\n ** Statistics: Infinity Bucket");
        fwr.write("\n\n *** Total Bugs = "+Integer.toString(TotalBugs));

        fwr.write("\n\n *** Unitary Buckets = "+Integer.toString(UnitaryCount));
        fwr.write("\n *** Non-Unitary Buckets = "+Integer.toString(NoUnitaryCount));
        fwr.write("\n *** Total Buckets = "+Integer.toString(lstOpenBuckets.size()));
        
        fwr.write("\n\n *** Average All Issues in Buckets ");
        fwr.write("\n *** Bucket Size = "+Float.toString(Metrics[0].IssueCount));
        fwr.write("\n *** Similarity = "+Float.toString(Metrics[0].Similarity));
        fwr.write("\n *** Components = "+Float.toString(Metrics[0].Components));
        fwr.write("\n *** Developers = "+Float.toString(Metrics[0].Developers));
        fwr.write("\n *** Overlap = "+Float.toString(Metrics[0].Overlap));
        fwr.write("\n *** Jaccard = "+Float.toString(Metrics[0].Jaccard));
        
        fwr.write("\n\n *** Top 1 Issues in Buckets ");
        fwr.write("\n *** Similarity = "+Float.toString(Metrics[1].Similarity));
        fwr.write("\n *** Components = "+Float.toString(Metrics[1].Components));
        fwr.write("\n *** Developers = "+Float.toString(Metrics[1].Developers));
        fwr.write("\n *** Overlap = "+Float.toString(Metrics[1].Overlap));
        fwr.write("\n *** Jaccard = "+Float.toString(Metrics[1].Jaccard));

        fwr.close();
    }

    
    public void setPasmParameters(float DistanceTreshold, int TimeWindowInDays){
        this.DistanceThreshold = DistanceTreshold;
        BucketFactory.setTimeFrame(TimeWindowInDays);
    }
    
    public static String pasmStatisticsCvsHeader(){
        if(Bucket.isInfinityBucket()){
            return (";;;;Bucket's Average;;Quality Metrics;;;;\nDist Threshold;Calc;Total Buckets;Unitary Buckets;Size in Issues(W/O Unitary);Similarity;Components;Developers;Overlap;Jaccard\n");
        }
        else{
            return ("Dist Threshold;Time Window;Total Buckets;Unitary Buckets;Mapped Buckets (Without Unitary);Average Bucket Size (Without Unitary);Avg Lost Bug; Avg Lost Bucket; Early%; Metrics\n");
        }
    }

    public String pasmStatisticsToCsvLine(){
        if(Bucket.isInfinityBucket()){
            return pasmStatisticsToCsvLineInfinityBucket();
        }
        else{
            return pasmStatisticsToCsvLineTimeBucket();
        }
    }

    public String pasmStatisticsToCsvLineTimeBucket(){
        int UnitaryCount = 0;
        int NoUnitaryCount = 0;
        float MappedRatio = 0;
        int MappedBuckets = 0;
        int MappedNoUnitary = 0;
        float MappedNoUnitaryRario = 0;
        float AvgTamNoUnitary = 0;
        int BugCount = 0;
        int NoUnitaryWithMoreThanOneMappedBug = 0;
        int TotLost = 0;
        int TotGain = 0;
        float EarlyPrediction = 0;
        float AvgLostBucket;
        float AvgLostBug;
        float AvgGainBucket;
        float AvgGainBug;
        float[] metrics = new float[7];
        Arrays.fill(metrics, 0);
        
        for(Bucket B : lstClosedBuckets){
            BugCount+=B.getData().size();
            TotLost+=B.getHoursLost();
            TotGain+=B.getHoursGain();
            EarlyPrediction+=B.getEarlyCount();
            if(!B.isUnitary()){
                AvgTamNoUnitary+= B.getData().size();
                NoUnitaryCount++;
                if(B.getItemPathCount()>1){
                    NoUnitaryWithMoreThanOneMappedBug++;
                    //metric1+=B.jaccardPaths(); //jaccard
                    metrics[0]+=B.calcOverlap();
                    metrics[1]+=B.jaccardPaths();
                    metrics[2]+=B.calcSorenson();
                    metrics[3]+=B.calcOchiai();
                    metrics[4]+=B.calcPSC();
                    metrics[5]+=B.calcKulczynski();
                    metrics[6]+=B.calcBinaryIntersect();
                }
            }
            else{
                UnitaryCount++;
            }
            
            float mapRatio = B.mappedRatio();
            if(mapRatio>0){
                MappedRatio+=mapRatio;
                MappedBuckets++;
                if(!B.isUnitary()){
                    MappedNoUnitary++;
                    MappedNoUnitaryRario+=mapRatio;
                }
            }
        }
        MappedRatio/=(float)MappedBuckets;
        MappedNoUnitaryRario/=(float)MappedNoUnitary;
        AvgTamNoUnitary/=(float)NoUnitaryCount;
        AvgLostBucket=TotLost/(float)lstClosedBuckets.size();
        AvgLostBug=TotLost/(float)BugCount;
        AvgGainBucket=TotGain/(float)lstClosedBuckets.size();
        AvgGainBug=TotGain/(float)BugCount;
        EarlyPrediction/=(float)BugCount;
        for(int i=0; i<metrics.length; i++){
            metrics[i]/=(float)NoUnitaryWithMoreThanOneMappedBug;
        }
        
        StringBuilder stb = new StringBuilder();
        stb.append(DistanceThreshold);
        stb.append(";");
        stb.append(Bucket.WINDOW_OF_DAYS_TO_CLOSE_BUCKET);
        stb.append(";");
        stb.append(lstClosedBuckets.size()); // Total Buckets
        stb.append(";");
        stb.append(UnitaryCount); //Unitary Buckets
        stb.append(";");
        stb.append(MappedNoUnitary); // Mapped Buckets (Without Unitary)
        stb.append(";");
        stb.append(AvgTamNoUnitary); //Average Bucket Size (Without Unitary)
        stb.append(";");
        stb.append(AvgLostBug); //Average Lost (per Bug) to Pasm
        stb.append(";");
        stb.append(AvgLostBucket); //Average Lost (per Bucket) to Pasm
        stb.append(";");
        stb.append(EarlyPrediction); //Percentage of Bugs without overhead in bucket (no lost).
        for(int i=0; i<metrics.length; i++){
            stb.append(";");
            stb.append(metrics[i]); //Coeficient Metrics
        }
        stb.append("\n");
        return stb.toString().replaceAll("NaN", "---").replace('.', ',');
    }
    
    public String pasmStatisticsToCsvLineInfinityBucket(){
        int UnitaryCount = 0;
        int NoUnitaryCount = 0;
        int FileMetricDivisor = 0;
        int TopFileMetricDivisor = 0;

        AverageBucketMetrics Metrics[]=new AverageBucketMetrics[2];
        Metrics[0]=new AverageBucketMetrics();
        Metrics[1]=new AverageBucketMetrics();
        
        AverageBucketMetrics locMet[];
        float allOver, topOver, allSim, topSim;
        
        for (Bucket B : lstOpenBuckets) {
            if (!B.isUnitary()) {
                NoUnitaryCount++;
                //Metrics = B.calcClosedBugMetrics();
                locMet = B.calcClosedBugMetricsTop();
                allSim = locMet[0].getSimilarity();
                allOver = locMet[0].getOverlap();
                
                if (allSim >= 0) {
                    Metrics[0].Similarity += allSim;
                    Metrics[0].IssueCount += locMet[0].getIssueCount();
                    Metrics[0].Components += locMet[0].getComponents();
                    Metrics[0].Developers += locMet[0].getDevelopers();

                    //locOver = Metrics.getOverlap();
                    if (allOver >= 0) {
                        FileMetricDivisor++;
                        Metrics[0].Overlap += allOver;
                        Metrics[0].Jaccard += locMet[0].getJaccard();;
                    }
                }
                else{
                    Metrics[0].IssueCount++; //Existia pelo menos 1 issue no bucket
                }
                
                topSim = locMet[1].getSimilarity();
                topOver = locMet[1].getOverlap();
                if (topSim >= 0) {
                    Metrics[1].Similarity += topSim;
                    Metrics[1].IssueCount += locMet[1].getIssueCount();
                    Metrics[1].Components += locMet[1].getComponents();
                    Metrics[1].Developers += locMet[1].getDevelopers();

                    //locOver = Metrics.getOverlap();
                    if (topOver >= 0) {
                        TopFileMetricDivisor++;
                        Metrics[1].Overlap += topOver;
                        Metrics[1].Jaccard += locMet[1].getJaccard();;
                    }
                }
                
            }            
            else{
                UnitaryCount++;
            }
            
        }
        
        Metrics[0].IssueCount /= NoUnitaryCount;
        Metrics[0].Components /= NoUnitaryCount;
        Metrics[0].Developers /= NoUnitaryCount;
        Metrics[0].Similarity /= NoUnitaryCount;
        Metrics[0].Overlap /= FileMetricDivisor;
        Metrics[0].Jaccard /= FileMetricDivisor;

        Metrics[1].IssueCount /= NoUnitaryCount;
        Metrics[1].Components /= NoUnitaryCount;
        Metrics[1].Developers /= NoUnitaryCount;
        Metrics[1].Similarity /= NoUnitaryCount;
        Metrics[1].Overlap /= TopFileMetricDivisor;
        Metrics[1].Jaccard /= TopFileMetricDivisor;
        
        StringBuilder stb = new StringBuilder();
        stb.append(DistanceThreshold);
        stb.append(";");
        stb.append("All");
        stb.append(";");
        stb.append(lstOpenBuckets.size()); // Total Buckets
        stb.append(";");
        stb.append(UnitaryCount); //Unitary Buckets
        stb.append(";");
        stb.append(Metrics[0].IssueCount); //Average Bucket Size (Without Unitary)
        stb.append(";");
        stb.append(Metrics[0].Similarity); //Average Similarity
        stb.append(";");
        stb.append(Metrics[0].Components); //
        stb.append(";");
        stb.append(Metrics[0].Developers); //
        stb.append(";");
        stb.append(Metrics[0].Overlap); //
        stb.append(";");
        stb.append(Metrics[0].Jaccard); //
        stb.append("\n");

        stb.append(DistanceThreshold);
        stb.append(";");
        stb.append("Top1");
        stb.append(";");
        stb.append(lstOpenBuckets.size()); // Total Buckets
        stb.append(";");
        stb.append(UnitaryCount); //Unitary Buckets
        stb.append(";");
        stb.append(""); //Average Bucket Size (Without Unitary)
        stb.append(";");
        stb.append(Metrics[1].Similarity); //Average Similarity
        stb.append(";");
        stb.append(Metrics[1].Components); //
        stb.append(";");
        stb.append(Metrics[1].Developers); //
        stb.append(";");
        stb.append(Metrics[1].Overlap); //
        stb.append(";");
        stb.append(Metrics[1].Jaccard); //
        stb.append("\n");
        return stb.toString().replaceAll("NaN", "---").replace('.', ',');
    }
    
    
    
//    public static Bucket create(){
//    }
    
    public static void main(String[] args){
        PasmFactory Fac = new PasmFactory();
        Fac.test();
    }
    
    public void test(){
/*[Id=592436, Allow badging items in the awesomebar, Path=[a/chrome/content/bindings.xml, a/chrome/content/browser-ui.js, a/chrome/content/browser.xul, a/themes/core/browser.css]]
  (C)[Id=594816, Add a built-in system for badge handlers, Path=[a/chrome/content/browser-ui.js]]
  [Id=595193, Remove unnecessary zero new notification indicator on badges, Path=[a/chrome/content/browser-ui.js]]
  [Id=595218, Badges on the same domain doesnt always reflect the right update, Path=[a/chrome/content/bindings.xml, a/chrome/content/browser-ui.js]]
  [Id=595639, Prevent badges handlers to access the awesome row directly, Path=[a/chrome/content/bindings.xml, a/chrome/content/browser-ui.js]]

  (C)[Id=495939, New Folder dialog don't use a verb for the confirm button, Path=[a/mail/locales/en-US/chrome/messenger/newFolderDialog.dtd, a/mailnews/base/content/newFolderDialog.xul, a/suite/locales/en-US/chrome/mailnews/newFolderDialog.dtd]]
     [Id=495937, New Smart Folder dialog doesn't use a verb for the confirm button, Path=[a/mail/locales/en-US/chrome/messenger/virtualFolderProperties.dtd, a/mailnews/base/content/virtualFolderProperties.js, a/mailnews/base/content/virtualFolderProperties.xul, a/suite/locales/en-US/chrome/mailnews/virtualFolderProperties.dtd]]
     [Id=495940, Rename Folder dialog don't use a verb for the confirm button, Path=[a/mail/locales/en-US/chrome/messenger/renameFolderDialog.dtd, a/mailnews/base/content/renameFolderDialog.xul, a/suite/locales/en-US/chrome/mailnews/renameFolderDialog.dtd]]

*/
        try {
//            SparseDataVector C = makeWordTerms(0, "Can't disable cache when open new cache backend",1);
//            BucketItem Bc = new BucketItem();
//            Bc.setSumUnigramData(C);
//            
//            SparseDataVector V[]=new SparseDataVector[4];
//            V[0]=makeWordTerms(0, "HTTP cache v2: make nsHttpChannel properly react to FILE_NOT_FOUND from the cache",1);
//            V[1]=makeWordTerms(0, "cache preferences need to be updated",1);
//            V[2]=makeWordTerms(0, "Need a way to check expiration/modification status of a cache entry",1);
//            V[3]=makeWordTerms(0, "<object> contents confused in cache",1);
//            
//            for(int i=0; i<V.length; i++){
//                BucketItem Bi = new BucketItem();
//                Bi.setSumUnigramData(V[i]);
//                
//                System.out.printf("%f \n",1-Bc.distance(Bi));
//            }
            
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
    
}
