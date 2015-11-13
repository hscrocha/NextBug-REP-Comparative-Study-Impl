/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import bugapp.persistence.entity.BugPath;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Henrique
 */
public class PasmAdhoc extends PasmFactory {
    
    private LinkedList<BucketItem> lstOpenIssues;
    private AverageBucketMetrics[] Metrics;
    public static float OverlapThreshold = 0.5f;
    private RecBugs.CalcOptions MetricCalculation = RecBugs.CalcOptions.AVERAGE;
    
    private FileWriter fwrLogOutput;
    private boolean LogOnFile = false;
    private boolean LogOnlyIssuesWithRecommendations = false;
    private String LogFileName = "AdhocPasm.txt";
    
    private FileWriter fwrDisperseCondensed;
    private FileWriter fwrDisperseRecommendations;
    private int MaxTopDisperse = 0;
    private String DisperseCharFileName = "AdhocPasm-DisperseChartInfo";
    
    private FileWriter fwrMaxOverlapOpenBug;
    private String strMaxOverlapOpenBug = "max-overlap-openbugs";
    private boolean logMaxOverlapOpenBug = false;
    
    private FileWriter fwrPerformanceTime;
    private String strPerformanceFileName = "performance-time.csv";
    private boolean logPerformanceTime = false;

    public PasmAdhoc(){
        super();
        this.lstOpenIssues = new LinkedList<BucketItem>();
        this.Metrics=new AverageBucketMetrics[RecBugs.MAX_RECOMEND];
        for(int i=0; i<Metrics.length; i++){
            Metrics[i]=new AverageBucketMetrics();
        }
    }
    
    public void setLogPerformanceTime(String FileName){
        this.logPerformanceTime = true;
        this.strPerformanceFileName = FileName;        
    }
    
    public void setLogMaxOverlapOpenBug(String FileName){
        this.logMaxOverlapOpenBug = true;
        this.strMaxOverlapOpenBug = FileName;
    }
    
    public void setLogDisperseCharInformation(String FileName, int MaxTop){
        this.MaxTopDisperse = MaxTop;
        this.DisperseCharFileName = FileName;
    }
    
    public void setLogToFileOn(String FileName) throws Exception{
        this.setLogToFileOn(FileName, false);
    }
    
    public void setLogToFileOn(String FileName, boolean OnlyIssuesWithRecommendations) throws Exception{
        this.LogOnFile = true;
        this.LogFileName = FileName;
        this.LogOnlyIssuesWithRecommendations = OnlyIssuesWithRecommendations;
    }
    
    @Override
    public void setPasmParameters(float DistanceTreshold, int TimeWindowInDays){
        this.DistanceThreshold = DistanceTreshold;
        //BucketFactory.setTimeFrame(TimeWindowInDays);
    }
    
    private void startLogFiles() throws Exception{
        if(LogOnFile){
            fwrLogOutput=new FileWriter(LogFileName);
        }
        
        if(MaxTopDisperse>0){
            fwrDisperseCondensed = new FileWriter(DisperseCharFileName+"-condensed.csv");
            //CSV header; query Bug Id; # recs; 
            fwrDisperseCondensed.write("q-Id;#rec;avg-sim;avg-ovl;max-sim;max-ovl\n");

            fwrDisperseRecommendations = new FileWriter(DisperseCharFileName+"-recommendations.csv");
            fwrDisperseRecommendations.write("q-Id;#rec;rec-Id;sim;ovl\n");
        }

        if(logMaxOverlapOpenBug){
            fwrMaxOverlapOpenBug = new FileWriter(strMaxOverlapOpenBug);
            //CSV header;  
            fwrMaxOverlapOpenBug.write("q-Id;q-creation;max-Id;max-ovl;open-count;Aq(1)-ovl\n");
        }
        
        if(logPerformanceTime){
            fwrPerformanceTime = new FileWriter(strPerformanceFileName);
            //CSV header
            fwrPerformanceTime.write("Bug-id;Date;Likelihood;Precision;#of Recs;Time(ns)\n");
        }
    }
    
    public void closeLogFiles() throws Exception{
        if(LogOnFile){
            //writeMetricsToLogFile(lstBugs.size());
            fwrLogOutput.close();
        }
        if(MaxTopDisperse>0){
            fwrDisperseCondensed.close();
            fwrDisperseRecommendations.close();
        }
        if(logMaxOverlapOpenBug){
            fwrMaxOverlapOpenBug.close();
        }
        if(logPerformanceTime){
            fwrPerformanceTime.close();
        }
    }
    
    public void nextBug(ArrayList<BugPath> Queries, ArrayList<BugPath> Documents) throws Exception{
        startLogFiles();
        
        //setDocumentCount( Queries.size() + Documents.size() );
        //System.out.println("Processing Open Issues list ... ");
        for(BugPath D : Documents){
            lstOpenIssues.add( createBucketItem(D) );
        }
        
        //System.out.println("NextBug ... ");
        //AverageBucketMetrics[] M;
        BucketItem Iq; //BucketItem = Processed Issue
        for(BugPath Q : Queries){
            Iq = createBucketItem(Q);
            //lstOpenIssues.add(I);
            nextBug(Iq);
            //updateMetrics(M);
        }
        
        closeLogFiles();
    }
    
    private void nextBug(BucketItem Q) throws Exception{
        //BucketItem Q = createBucketItem(MainIssue);
        //RecBugs Rec = new RecBugs();
        //Rec.setMain(I);
        RecBugs Rec = new RecBugs();
        Rec.setMain(Q);

        BucketItem Dj;
        Iterator<BucketItem> It = lstOpenIssues.iterator();
        while(It.hasNext()){
            Dj = It.next(); 
            //Rec.addIfPassThreshold(Dj, DistanceThreshold);
            Rec.addSameCompThreshold(Dj, DistanceThreshold);
        }

        if(LogOnFile && (!LogOnlyIssuesWithRecommendations || Rec.getRecommendationsCount() > 0)) {
            fwrLogOutput.write(Rec.toString());
            //fwrLogOutput.write(Rec.toMozillaDailyLogString());
            fwrLogOutput.write("\n");
        }
    }

    public void maxoverlapOpenBugs(ArrayList<BugPath> lstBugs) throws Exception {
        startLogFiles();
                
        BucketItem I; //BucketItem = Processed Issue
        //setDocumentCount( lstBugs.size() );
        for(BugPath Bp : lstBugs){
            I = createBucketItem(Bp);
            maxoverlapOpenBugs(I);
        }
        
        closeLogFiles();
    }

    protected void maxoverlapOpenBugs(BucketItem Current) throws Exception{
        Iterator<BucketItem> It = lstOpenIssues.iterator();
        BucketItem Item;
        java.sql.Date DtCreation = Current.getBugData().getDtCreation();
        RecBugs Rec = new RecBugs();
        Rec.setMain(Current);
        boolean Aux = false;
        
        //Passar por todas Issues e encontrar as 10 mais similares
        while(It.hasNext()){
            Item = It.next(); 
            if(DtCreation.after(Item.getBugData().getDtCreation())){
                
                if(DtCreation.after(Item.getBugData().getDtClose())){
                    //Item da base já foi fechada antes da atual chegar
                    //Remover o item da base usando o Iterator
                    It.remove();
                }
                else{
                    //calcular as estatisticas entre a issue atual
                    //e o item iterado da base
                    Rec.addForMaxOverlap(Item);
                   //Aux |= Rec.addThresholdOverlap(Item, DistanceThreshold, OverlapThreshold);
                }
            }
        }
        //Nova issue será adicionada no final da base
        lstOpenIssues.add(Current); 
        if(logMaxOverlapOpenBug){
            writeToMaxOpenBugs(Rec);
        }
    }
    
    public void repPasm(ArrayList<BugPath> lstBugs) throws Exception {
        startLogFiles();
        if(LogOnFile){
            //Write Weights in Log File
            fwrLogOutput.write("REP Weights: "+REP.weightsToString()+"\n\n");
        }
                
        AverageBucketMetrics[] M;
        BucketItem I; //BucketItem = Processed Issue
        //System.out.println( lstBugs.size() );
        //setDocumentCount( lstBugs.size() );
        for(BugPath Bp : lstBugs){
            I = createRepBucketItem(Bp);
            //lstOpenIssues.add(I);
            M=repPasmBucketless(I);
            //baselineUpdateMetrics(M);
            updateMetrics(M);
        }
        for(int i=0; i<Metrics.length; i++){
            //System.out.printf("M[%d] = %f \n", i, Metrics[i].IssueCount);
            Metrics[i].Components/= Metrics[i].IssueCount;
            Metrics[i].Developers/= Metrics[i].IssueCount;
            Metrics[i].DevLikelihood/= Metrics[i].IssueCount;
            Metrics[i].Similarity/= Metrics[i].IssueCount;
            Metrics[i].Overlap/= Metrics[i].OverlapCount;
            Metrics[i].TaskCoeficient/= Metrics[i].OverlapCount;
            Metrics[i].ExternalOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Jaccard/= Metrics[i].OverlapCount;
            Metrics[i].MaxOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Precision/= Metrics[i].PrecCount;
            Metrics[i].Likelihood/= Metrics[i].PrecCount;
            Metrics[i].Recall/= Metrics[i].RecCount;
            Metrics[i].MaxRecall /= Metrics[i].RecCount;
            Metrics[i].Feedback /= ((float)lstBugs.size());
        }
        
        closeLogFiles();
    }
    
    public void baselinePasm(ArrayList<BugPath> lstBugs, boolean Component) throws Exception {
        startLogFiles();
                
        AverageBucketMetrics[] M;
        BucketItem I; //BucketItem = Processed Issue
        //setDocumentCount(lstBugs.size());
        for(BugPath Bp : lstBugs){
            I = createBucketItem(Bp);
            //lstOpenIssues.add(I);
            M=baselinePasmBucketless(I, Component);
            baselineUpdateMetrics(M);
        }
        for(int i=0; i<Metrics.length; i++){
            Metrics[i].Components/= Metrics[i].IssueCount;
            Metrics[i].Developers/= Metrics[i].IssueCount;
            Metrics[i].DevLikelihood/= Metrics[i].IssueCount;
            Metrics[i].Similarity/= Metrics[i].IssueCount;
            Metrics[i].Overlap/= Metrics[i].OverlapCount;
            Metrics[i].TaskCoeficient/= Metrics[i].OverlapCount;
            Metrics[i].ExternalOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Jaccard/= Metrics[i].OverlapCount;
            Metrics[i].MaxOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Precision/= Metrics[i].PrecCount;
            Metrics[i].Likelihood/= Metrics[i].PrecCount;
            Metrics[i].Recall/= Metrics[i].RecCount;
            Metrics[i].MaxRecall /= Metrics[i].RecCount;
            Metrics[i].Feedback /= ((float)lstBugs.size());
        }
        closeLogFiles();
    }

    protected AverageBucketMetrics[] baselinePasmBucketless(BucketItem Current, boolean Component) throws Exception{
        Iterator<BucketItem> It = lstOpenIssues.iterator();
        BucketItem Item;
        java.sql.Date DtCreation = Current.getBugData().getDtCreation();
        RecBugs Rec = new RecBugs();
        Rec.setMain(Current);
        boolean Aux = false;
        
        //Passar por todas Issues e encontrar as 10 mais similares
        while(It.hasNext()){
            Item = It.next(); 
            if(DtCreation.after(Item.getBugData().getDtCreation())){
                
                if(DtCreation.after(Item.getBugData().getDtClose())){
                    //Item da base já foi fechada antes da atual chegar
                    //Remover o item da base usando o Iterator
                    It.remove();
                }
                else{
                    //calcular as estatisticas entre a issue atual
                    //e o item iterado da base
                    //Aux |= Rec.addSameCompThresholdOverlap(Item, DistanceThreshold, OverlapThreshold);
                    if(Component){
                        Aux |= Rec.addSameCompThresholdOverlap(Item, 2, OverlapThreshold);
                    }
                    else{
                        Aux |= Rec.addThresholdOverlap(Item, 2, OverlapThreshold);
                    }
                }
            }
        }
        //Nova issue será adicionada no final da base
        lstOpenIssues.add(Current); 
        
        AverageBucketMetrics[] M = null;
        if(Aux){
            M = Rec.baselineMetrics(MetricCalculation);
        }

        if(LogOnFile){
            writeToLogFile(Rec, M);
        }
        if(MaxTopDisperse>0){
            writeToDisperse(Rec);
        }
        if(logMaxOverlapOpenBug){
            writeToMaxOpenBugs(Rec);
        }
        return M; //retorna as metricas dessa issue
    }

    protected AverageBucketMetrics[] repPasmBucketless(BucketItem Current) throws Exception{
        Iterator<BucketItem> It = lstOpenIssues.iterator();
        BucketItem Item;
        java.sql.Date DtCreation = Current.getBugData().getDtCreation();
        RecBugs Rec = new RecBugs();
        Rec.setMain(Current);
        boolean Aux = false;
        
        long time1 = System.nanoTime();
        //Passar por todas Issues e encontrar as 10 mais similares
        while(It.hasNext()){
            Item = It.next(); 
            if(DtCreation.after(Item.getBugData().getDtCreation())){
                
                if(DtCreation.after(Item.getBugData().getDtClose())){
                    //Item da base já foi fechada antes da atual chegar
                    //Remover o item da base usando o Iterator
                    It.remove();
                }
                else{
                    //calcular as estatisticas entre a issue atual
                    //e o item iterado da base
//                    if(Component){
//                        Aux |= Rec.addSameCompThresholdOverlap(Item, 2, OverlapThreshold);
//                    }
//                    else{
//                        Aux |= Rec.addThresholdOverlap(Item, 2, OverlapThreshold);
//                    }
                    Aux |= Rec.addForRep(Item, DistanceThreshold, OverlapThreshold);
                }
            }
        }
        //Nova issue será adicionada no final da base
        lstOpenIssues.add(Current); 
        
        long time2 = System.nanoTime();

        AverageBucketMetrics[] M = null;
        if(Aux){
            M = Rec.calc(MetricCalculation);
        }

        if(LogOnFile){
            writeToLogFile(Rec, M);
        }
        if(MaxTopDisperse>0){
            writeToDisperse(Rec);
        }
        if(logMaxOverlapOpenBug){
            writeToMaxOpenBugs(Rec);
        }
        if(logPerformanceTime){
            logPerformance(Rec, M, time2-time1);
//            String Lik = M!=null && M.length>2 && M[2].getLikelihood()>=0 ? Float.toString(M[2].getLikelihood()):"";
//            String Bas = ""; //M!=null && M.length>2 && M[2].getBaselineLikelihood()>=0 ? Float.toString(M[2].getBaselineLikelihood()):"";
//            fwrPerformanceTime.write(Rec.getMainCreationDate()+";"+Lik+";"+Bas+";"+Long.toString(time2-time1)+";\n");
        }
        return M; //retorna as metricas dessa issue
    }
    
    @Override
    public void pasm(ArrayList<BugPath> lstBugs) throws Exception {
        startLogFiles();
                
        AverageBucketMetrics[] M;
        BucketItem I; //BucketItem = Processed Issue
        //setDocumentCount(lstBugs.size());
        for(BugPath Bp : lstBugs){
            I = createBucketItem(Bp);
            //lstOpenIssues.add(I);
            M=pasmBucketless(I);
            updateMetrics(M);
        }
//        System.out.println();
//        for(int i=0; i<Metrics.length; i++){
//            System.out.printf("ext%d = %f ",i+1, Metrics[i].ExternalOverlap);
//        }
        
        //float BugCount = (float) lstBugs.size();
        for(int i=0; i<Metrics.length; i++){
            Metrics[i].Components/= Metrics[i].IssueCount;
            Metrics[i].Developers/= Metrics[i].IssueCount;
            Metrics[i].DevLikelihood/= Metrics[i].IssueCount;
            Metrics[i].Similarity/= Metrics[i].IssueCount;
            Metrics[i].Overlap/= Metrics[i].OverlapCount;
            Metrics[i].TaskCoeficient/= Metrics[i].OverlapCount;
            Metrics[i].ExternalOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Jaccard/= Metrics[i].OverlapCount;
            Metrics[i].MaxOverlap/= Metrics[i].ExternalOvCount;
            Metrics[i].Precision/= Metrics[i].PrecCount;
            Metrics[i].Likelihood/= Metrics[i].PrecCount;
            Metrics[i].BaselinePrecision/= Metrics[i].PrecCount;
            Metrics[i].BaselineLikelihood/= Metrics[i].PrecCount;
            Metrics[i].Recall/= Metrics[i].RecCount;
            Metrics[i].MaxRecall /= Metrics[i].RecCount;
            Metrics[i].Feedback /= ((float)lstBugs.size());

//            System.out.printf("\n[%d] Ic = %f, Oc = %f, Pc = %f, Rc = %f, Ec = %f", i+1,
//                    Metrics[i].IssueCount,Metrics[i].OverlapCount,
//                Metrics[i].PrecCount,Metrics[i].RecCount,Metrics[i].ExternalOvCount);
        }
//        System.out.println();
        
        closeLogFiles();
    }
    
    protected AverageBucketMetrics[] pasmBucketless(BucketItem Current) throws Exception{
        Iterator<BucketItem> It = lstOpenIssues.iterator();
        BucketItem Item;
        java.sql.Date DtCreation = Current.getBugData().getDtCreation();
        RecBugs Rec = new RecBugs();
        Rec.setMain(Current);
        boolean Aux = false;
        
        long time1 = System.nanoTime();
        //Passar por todas Issues e encontrar as 10 mais similares
        while(It.hasNext()){
            Item = It.next(); 
            if(DtCreation.after(Item.getBugData().getDtCreation())){
                
                if(DtCreation.after(Item.getBugData().getDtClose())){
                    //Item da base já foi fechada antes da atual chegar
                    //Remover o item da base usando o Iterator
                    It.remove();
                }
                else{
                    //calcular as estatisticas entre a issue atual
                    //e o item iterado da base
                    Aux |= Rec.addSameCompThresholdOverlap(Item, DistanceThreshold, OverlapThreshold);
                   //Aux |= Rec.addThresholdOverlap(Item, DistanceThreshold, OverlapThreshold);
                }
            }
        }
        //Nova issue será adicionada no final da base
        lstOpenIssues.add(Current); 

        long time2 = System.nanoTime();
        //System.out.println( time2-time1 );
        
        AverageBucketMetrics[] M = null;
        if(Aux){
            M = Rec.calc(MetricCalculation);
        }

        if(LogOnFile){
            writeToLogFile(Rec, M);
        }
        if(MaxTopDisperse>0){
            writeToDisperse(Rec);
        }
        if(logMaxOverlapOpenBug){
            writeToMaxOpenBugs(Rec);
        }
        if(logPerformanceTime){
            logPerformance(Rec, M, time2-time1);
        }
        return M; //retorna as metricas dessa issue
    }
    
    private void updateMetrics(AverageBucketMetrics[] M){
        if (M != null) {
            for (int i = 0; i < M.length; i++) {
                Metrics[i].Feedback += M[i].Feedback;
                if (M[i].Similarity >= 0) {
                    Metrics[i].IssueCount++;
                    Metrics[i].Components += M[i].Components;
                    Metrics[i].Developers += M[i].Developers;
                    Metrics[i].DevLikelihood += M[i].DevLikelihood;
                    Metrics[i].Similarity += M[i].Similarity;
                    
                    if(M[i].ExternalOverlap > 0){
                        Metrics[i].ExternalOverlap += M[i].ExternalOverlap;
                        Metrics[i].MaxOverlap += M[i].MaxOverlap;
                        Metrics[i].ExternalOvCount++;
                    }
                    
                    if (M[i].Overlap >= 0) {
                        Metrics[i].OverlapCount++;
                        Metrics[i].Overlap += M[i].Overlap;
                        Metrics[i].Jaccard += M[i].Jaccard;
                        Metrics[i].TaskCoeficient += M[i].TaskCoeficient;
                    
                        if(M[i].Precision >= 0){
                            Metrics[i].PrecCount++;
                            Metrics[i].Precision += M[i].Precision;
                            Metrics[i].Likelihood += M[i].Likelihood;
                            //Metrics[i].BaselinePrecision+= M[i].BaselinePrecision;
                            //Metrics[i].BaselineLikelihood+= M[i].BaselineLikelihood;
                        }
                    }
//                    else if(AcumulatedPastMetrics && i!=0 && M[i-1].Precision>=0){
//                        Metrics[i].PrecCount++;
//                        Metrics[i].Precision += M[i-1].Precision;
//                        Metrics[i].Likelihood += M[i-1].Likelihood;
//                    }
                    
                    if(M[i].Recall >= 0){
                        Metrics[i].RecCount++;
                        Metrics[i].Recall += M[i].Recall;
                        Metrics[i].setMaxRecall(Metrics[i].getMaxRecall() + M[i].getMaxRecall());
                    }
                    
                }
//                else if(AcumulatedPastMetrics && i!=0 && M[i-1].Similarity>0){
//                    Metrics[i].IssueCount++;
//                    Metrics[i].Components += M[i-1].Components;
//                    Metrics[i].Developers += M[i-1].Developers;
//                    Metrics[i].DevLikelihood += M[i-1].DevLikelihood;
//                    Metrics[i].Similarity += M[i-1].Similarity;
//                    if(M[i-1].Precision>=0){
//                        Metrics[i].PrecCount++;
//                        Metrics[i].Precision += M[i-1].Precision;
//                        Metrics[i].Likelihood += M[i-1].Likelihood;
//                    }
//                    
//                }
            }
        }
    }
    
    private void baselineUpdateMetrics(AverageBucketMetrics[] M){
        if (M != null) {
            for (int i = 0; i < M.length; i++) {
                Metrics[i].Feedback += M[i].Feedback;
                if (M[i].Similarity >= 0) {
                    Metrics[i].IssueCount++;
                    Metrics[i].Components += M[i].Components;
                    Metrics[i].Developers += M[i].Developers;
                    Metrics[i].DevLikelihood += M[i].DevLikelihood;
                    Metrics[i].Similarity += M[i].Similarity;
                    
                    if(M[i].ExternalOverlap >= 0 ){
                        Metrics[i].ExternalOverlap += M[i].ExternalOverlap;
                        Metrics[i].MaxOverlap += M[i].MaxOverlap;
                        Metrics[i].ExternalOvCount++;
                    }
                    
                    if (M[i].Overlap >= 0) {
                        Metrics[i].OverlapCount++;
                        Metrics[i].Overlap += M[i].Overlap;
                        Metrics[i].Jaccard += M[i].Jaccard;
                        Metrics[i].TaskCoeficient += M[i].TaskCoeficient;
                    }
                    if(M[i].Precision >= 0){
                        Metrics[i].PrecCount++;
                        Metrics[i].Precision += M[i].Precision;
                        Metrics[i].Likelihood += M[i].Likelihood;
                    }
                    if(M[i].Recall >= 0){
                        Metrics[i].RecCount++;
                        Metrics[i].Recall += M[i].Recall;
                        Metrics[i].setMaxRecall(Metrics[i].getMaxRecall() + M[i].getMaxRecall());
                    }
                }
            }
        }
    }
    
    public static String getCvsHeader(){
        return "Dist;Calc;BugTotal;BugsWithOverl;Sim;Comp;Dev;DevLik;Overl;MaxRecall;Precision;Recall;Likelihood;Feedback\n";
    }
    
    public String getMetricsInCvsFileString(){
        StringBuilder stb=new StringBuilder();
        for(int i=0; i<Metrics.length; i++){
            stb.append(DistanceThreshold);
            stb.append(";");
            stb.append("Top-");
            stb.append(i+1);
            stb.append(";");
            stb.append(Metrics[i].getIssueCount());
            stb.append(";");
            stb.append(Metrics[i].getOverlapCount());
            stb.append(";");
            stb.append(Metrics[i].getSimilarity());
            stb.append(";");
            stb.append(Metrics[i].getComponents());
            stb.append(";");
            stb.append(Metrics[i].getDevelopers());
            stb.append(";");
            stb.append(Metrics[i].getDevLikelihood());
            stb.append(";");
            stb.append(Metrics[i].getOverlap());
            stb.append(";");
            stb.append(Metrics[i].getMaxRecall());
            stb.append(";");
            stb.append(Metrics[i].getPrecision());
            stb.append(";");
            stb.append(Metrics[i].getRecall());
            stb.append(";");
            stb.append(Metrics[i].getLikelihood());
            stb.append(";");
            stb.append(Metrics[i].getFeedback());
            stb.append("\n");
        }
        return stb.toString().replace('.', ',');
    }
    
    
    public void writeMetricsToCsvFile(String FileName) throws Exception{
        StringBuilder stb=new StringBuilder();
        
        stb.append( getCvsHeader() );
        stb.append( getMetricsInCvsFileString());

        FileWriter fwr = new FileWriter(FileName);
        fwr.write(stb.toString());
        fwr.close();
    }
    
    private void writeToMaxOpenBugs(RecBugs R) throws Exception{
        String S = R.toCsvStringMaxOverlap();
        if(S!=null && S.length()>0){
            fwrMaxOverlapOpenBug.write(S);
        }
        
        //if(R.getMain().getPaths().size() > 0){ //Only if the Main has files for overlap
            
//            RecEntry MaxOv = R.getOpenBugWithMaxOverlap();
//            int CountOpen = R.getOpenBugWithOverlapCount();
//        if (MaxOv != null) {
//
//            StringBuilder stb = new StringBuilder();
//            stb.append(R.getMainBugId());
//            stb.append(";");
//            stb.append(R.getMain().getDtCreation());
//            stb.append(";");
//
//            stb.append(MaxOv.getIssueData().getBugId());
//            stb.append(";");
//            stb.append(MaxOv.getOverlap());
//            stb.append(";");
//            stb.append(CountOpen);
//            stb.append("\n");
//            fwrMaxOverlapOpenBug.write(stb.toString());
//        }
//            else{
//                stb.append("0;0;0\n");
//            }
            
        //}
    }
    
    private void writeToDisperse(RecBugs R) throws Exception{
        if (R.getRecommendationsCountWithOverlap() > 0) {
            //fwrDisperse.write("qId;#rec;avg-sim;max-sim;avg-ovl;max-ovl\n");
           
            DisperseMetricEntity Met = R.calcDisperseMetrics(MaxTopDisperse);
            
            StringBuilder stb = new StringBuilder();
            stb.append(R.getMainBugId());
            stb.append(";");
            stb.append(R.getRecommendationsCountWithOverlap()<MaxTopDisperse?R.getRecommendationsCountWithOverlap():MaxTopDisperse);
            stb.append(";");
            stb.append(Met.getAverageSimilarity());
            stb.append(";");
            stb.append(Met.getAverageOverlap());
            stb.append(";");
            stb.append(Met.getMaxSimilarity());
            stb.append(";");
            stb.append(Met.getMaxOverlap());
            stb.append("\n");
            fwrDisperseCondensed.write(stb.toString());
            
            fwrDisperseRecommendations.write( Met.getDetail() );
            //fwrDisperseRecommendations.write("q-Id;#rec;rec-Id;sim;ovl\n");
        }
    }
    
    private void writeToLogFile(RecBugs R, AverageBucketMetrics[] M) throws Exception{
        if (M != null) {
            if (!LogOnlyIssuesWithRecommendations || R.getRecommendationsCount() > 0) {
                fwrLogOutput.write(R.toString());

                StringBuilder stb = new StringBuilder();
                for (int i = 0; i < M.length; i++) {
                    stb.append("Top-");
                    stb.append(i + 1);
                    stb.append(",s=");
                    stb.append(M[i].getSimilarity());
                    stb.append(",c=");
                    stb.append(M[i].getComponents());
                    stb.append(",d=");
                    stb.append(M[i].getDevelopers());
                    stb.append(",dlk=");
                    stb.append(M[i].getDevLikelihood());
                    stb.append(",o=");
                    stb.append(M[i].getOverlap());
                    stb.append(",max_o=");
                    stb.append(M[i].getMaxOverlap());
                    stb.append(",exto=");
                    stb.append(M[i].getExternalOverlap());
                    stb.append(",j=");
                    stb.append(M[i].getJaccard());
                    stb.append(",p=");
                    stb.append(M[i].getPrecision());
                    stb.append(",r=");
                    stb.append(M[i].getRecall());
                    stb.append(",l=");
                    stb.append(M[i].getLikelihood());
                    stb.append("\n");
                }

                fwrLogOutput.write(stb.toString());
            }
        }
        
    }
    
    private void writeMetricsToLogFile(int TotalBugs) throws Exception{
        StringBuilder stb=new StringBuilder();
        
        stb.append("\n\n=== Global Metrics\n");
        for(int i=0; i<Metrics.length; i++){
            stb.append("Top-");
            stb.append(i+1);
            stb.append(", Measured Bugs=");
            stb.append(Metrics[i].getIssueCount());
            stb.append(", Bugs With Ov=");
            stb.append(Metrics[i].getOverlapCount());
            stb.append(", Sim=");
            stb.append(Metrics[i].getSimilarity());
            stb.append(", Comp=");
            stb.append(Metrics[i].getComponents());
            stb.append(", Dev=");
            stb.append(Metrics[i].getDevelopers());
            stb.append(", DevLik=");
            stb.append(Metrics[i].getDevLikelihood());
            stb.append(", Ov=");
            stb.append(Metrics[i].getOverlap());
            stb.append(", MaxOv=");
            stb.append(Metrics[i].getMaxOverlap());
            stb.append(", ExtOv=");
            stb.append(Metrics[i].getExternalOverlap());
            stb.append(", Jac=");
            stb.append(Metrics[i].getJaccard());
            stb.append(", Prec=");
            stb.append(Metrics[i].getPrecision());
            stb.append(", Rec=");
            stb.append(Metrics[i].getRecall());
            stb.append(", Lik=");
            stb.append(Metrics[i].getLikelihood());
            stb.append("\n");
        }
        stb.append("\n\n=== Total Bugs: ");
        stb.append(TotalBugs);

        fwrLogOutput.write(stb.toString());
    }
    
    public static float getOverlapThreshold(){
        return OverlapThreshold;
    }
    
    private void logPerformance(RecBugs Rec, AverageBucketMetrics[] M, long time) throws IOException{
        int index = 0;
        if(M!=null) index = M.length<=3? M.length-1: 3;
        
        String Lik = M != null && M[index].getLikelihood() >= 0 ? Float.toString(M[index].getLikelihood()) : "";
        String Prec = M != null && M[index].getPrecision()>= 0 ? Float.toString(M[index].getPrecision()) : "";
        //String Bas = M != null && M[index].getBaselineLikelihood()>=0 ? Float.toString(M[index].getBaselineLikelihood()):"";
        fwrPerformanceTime.write(Rec.getMainBugId() + ";" + Rec.getMainCreationDate() + ";" + Lik + ";" + Prec + ";" + Rec.getRecommendationsCount() +";"+Long.toString(time) + ";\n");
    }

}
