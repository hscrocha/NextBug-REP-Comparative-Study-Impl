/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

import au.com.bytecode.opencsv.CSVWriter;
import bugapp.persistence.dao.AsergTestsDAO;
import bugapp.persistence.entity.BugCluster;
import bugapp.persistence.entity.BugDescription;
import bugapp.stem.BugAppStemmingFilter;
import bugapp.stem.StemmingSnow;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Henrique
 */
public class ProcessedData {
    private static final char CSV_SEPARATOR = ';';
    private static final char QUOTE_CHAR = CSVWriter.NO_QUOTE_CHARACTER;
    
    public static final byte TF_RAW_FREQUENCY = 1; //fij
    public static final byte TF_LOG_NORMALIZATION = 2; //1 + log2 fij
    public static final byte TF_DOUBLE_NORMALIZATION_05 = 3; //Not implemented
    
    public static final byte IDF_UNARY = 1; // 1
    public static final byte IDF_INVERSE_FREQUENCY = 2; //log2 N/ni
    public static final byte IDF_INV_FREQUENCY_SMOOTH = 3; //Not implemented
    public static final byte IDF_INV_FREQUENCY_MAX = 4; //Not implemented
    public static final byte IDF_PROBABILISTIC_INV = 5; //Not implemented
    
    private static byte DefaultTFWeightMethod = TF_LOG_NORMALIZATION;
    private static byte DefaultIDFWeightMethod = IDF_INVERSE_FREQUENCY;
    
    public static byte DistanceMeasure = SparseClusterCenter.COSINE_DISTANTE;
    
    //private ArrayList<String> lstWords;
    //private ArrayList<Integer> lstBugId;
    private HashMap<String, Integer> hsmWords;
    private HashMap<Integer, Integer> hsmDocFreq; //F //Não preciso do F para os cálculos
    private HashMap<Integer, Float> hsmDocNumber; //ni(F)
    
    private ArrayList<SparseDataVector> lstDataVector = null;
    private ArrayList<SparseClusterCenter> lstClusterCenter = null; //Usado no K-means,medoids
    
    private float DevIntra = 0;
    private float DevInter = 0;
    private float Cintra = 0;
    private float Cinter = 0;
    private float BetaCv = 0;
    private float BetaVar = 0;
    private float CRR = 0;
    private int OutlierCount = 0;

    //private int RowCount;
    private int ColCount;
    
    public ProcessedData(){
        //lstWords=new ArrayList<String>(); //Cols
        //lstBugId=new ArrayList<Integer>(); //Rows
        hsmWords=new HashMap<String, Integer>();
        hsmDocFreq=new HashMap<Integer, Integer>();
        hsmDocNumber=new HashMap<Integer, Float>();
        lstDataVector=new ArrayList<SparseDataVector>();
        
        //RowCount=0;
        ColCount=1;
    }
    
    public void add(int BugId, String BugDesc) throws Exception{
        //lstBugId.add(B.getId());
        makeWordTerms(BugId,BugDesc);
        //RowCount++;
    }

    public void add(BugDescription B) throws Exception{
        //lstBugId.add(B.getId());
        makeWordTerms(B.getId(),B.getShortDesc());
        //RowCount++;
    }

    private void makeWordTerms(int BugId, String BugDesc) throws Exception{
        Integer ColIndex;
        String Word;
        StringTokenizer stk=new StringTokenizer(BugDesc," ;,!?'\"<>[](){}+*=\\@#$%^&~"); //retirado :.-/_
        //StringTokenizer stk=new StringTokenizer(Description," ;:.,!?'\"<>[](){}-+*=/\\_@#$%^&~");

        SparseDataVector Vet = new SparseDataVector(BugId);
        BugAppStemmingFilter Filter = new StemmingSnow();
        
        while (stk.hasMoreTokens()) {
            Word = Filter.processWord(stk.nextToken().toLowerCase());
            if (Word != null) {
                ColIndex = hsmWords.get(Word);
                if (ColIndex == null) {
                    //O termo não existe na lista de termos
                    //Inserir o termo na lista de termos
                    ColIndex = ColCount;
                    hsmWords.put(Word, ColIndex);
                    hsmDocFreq.put(ColIndex, 0);
                    hsmDocNumber.put(ColIndex, 0f);
                    ColCount++;
                }
                //Todo caso, deve-se adicionar frequencia no vetor
                Vet.increment(ColIndex);
            }
        }
        lstDataVector.add(Vet);
        updateNi(Vet);
        //M[RowCount] = Row;
    }
    
    private void updateNi(SparseDataVector Vet){
        Collection<SparseDataItem> colVetItems = Vet.getItems();
        for(SparseDataItem Item : colVetItems){
            int F = hsmDocFreq.get(Item.getIndex());
            F+=(int)Item.getFrequency();
            hsmDocFreq.put(Item.getIndex(), F);
            
            float Ni = hsmDocNumber.get(Item.getIndex());
            Ni++;
            hsmDocNumber.put(Item.getIndex(), Ni);
        }
    }
    
    public void writeToArffFile(String FileName) throws Exception {
        writeWordsToFile(FileName+"_word_hash.csv");
        writeDataToFile(FileName+".arff");
    }
    
    private void writeWordsToFile(String FileName) throws Exception{
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[2];
        for(String Word : hsmWords.keySet()){
            Line[0]=hsmWords.get(Word).toString();
            Line[1]=Word;
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }
    
    public void writeWordsToFileWihCount(String FileName) throws Exception{
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
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
    
    
    private void writeDataToFile(String FileName) throws Exception {
        FileWriter fwrWriter = new FileWriter(FileName);
        fwrWriter.write("% Title: Firefox Sparse Bugs/Terms Vectors \n");
        fwrWriter.write("% Word Hash can found in <filename_without_extension>_word_hash.csv \n");
        fwrWriter.write("% Creator: Henrique S. C. Rocha \n");
        fwrWriter.write("% \n");
        fwrWriter.write("@relation 'bugapp'\n");
        fwrWriter.write("@attribute 'bug_id' numeric \n");
        for(String Word : hsmWords.keySet()){
            fwrWriter.write("@attribute '");
            fwrWriter.write(Word);
            fwrWriter.write(" ");
            fwrWriter.write(hsmWords.get(Word).toString());
            fwrWriter.write("' numeric \n");
        }
        fwrWriter.write("@data \n");
        for(SparseDataVector Vet : lstDataVector){
            fwrWriter.write(Vet.toArffDataLine());
        }
        fwrWriter.close();
    }
    
    
    public void applyWeightMethod(){
        for(SparseDataVector Vet : lstDataVector){
            calcWeightMethod(Vet);
        }
    }
    
    public int[] generateClusterCentroidSeed(){
        return bugapp.util.Gerador.randomIndex(lstDataVector.size(), 0.5f);
//        int[] seed = new int[lstDataVector.size()];
//        java.util.Random R=new java.util.Random(System.currentTimeMillis());
//        int aux, swap;
//        int Max = seed.length/2;
//        
//        for(int i=0; i<seed.length; i++){
//            seed[i]=i;
//        }
//        
//        for(int i=Max; i>0; i--){
//            swap = R.nextInt(i);
//            
//            aux = seed[i-1];
//            seed[i-1] = seed[swap];
//            seed[swap] = aux;
//        }
//        
//        return seed;
    }
    
    public int[] generateClusterCentroidSeed(int K){
        int seed[]=new int[K];
        int i, inc;
        java.util.Random R=new java.util.Random(System.currentTimeMillis());
        
        for(i=0, inc=(lstDataVector.size()-1)/K; i<K; i++){
            seed[i]=i*inc+R.nextInt(inc);
        }
        return seed;
    }
    
    public void hierarchicalClustering(int Kinit, int Kfinal){
        int ClusterCount = lstDataVector.size();
        lstClusterCenter=new ArrayList<SparseClusterCenter>(ClusterCount);
        
        SparseClusterCenter Ci, Cj;
        byte hdistmeasure = 2;
        int i, j;
        int Index1=0, Index2=0;
        int Outlier = 0;
        float Dist, MinDist;
        
        //Começa criando 1 cluster para cada dado
        for(i=0; i<ClusterCount; i++){
            SparseClusterCenter Centers = new SparseClusterCenter();
            Centers.setCenter( lstDataVector.get( i ) );            
            lstClusterCenter.add( Centers );
        }
        
        while(ClusterCount > Kfinal){
            
            //Procura pelos 2 clusters com as menores distancias
            MinDist = 2;
            for(i=0; i<ClusterCount-1; i++){
                Ci = lstClusterCenter.get(i);
                for(j=i+1; j<ClusterCount; j++){
                    Cj = lstClusterCenter.get(j);
                    Dist=Ci.hierarchicalDistance(Cj, hdistmeasure);
                    if(Dist < MinDist){
                        MinDist = Dist;
                        Index1 = i;
                        Index2 = j;
                    }
                }
            }
            
            //Detecção de Outliers
            if(MinDist == 1){
                Outlier++;
            }
            
            //Unir Ci com Cj no mesmo cluster
            Ci = lstClusterCenter.get(Index1);
            Cj = lstClusterCenter.get(Index2);
            Ci.join(Cj);
            lstClusterCenter.remove(Index2);
            ClusterCount--;
            
            //Verifica se deve se começar a gravars os cluster no log
            if(ClusterCount<=Kinit){
                
            }
        }
    }
    
    
    /**
     * Perform a K-means or a K-medoids clustering algorithm.
     * The used algorithm depends on the distance measurement 
     * stored in the class attribute 'DistanceMeasure'. 
     * Euclidian Distance uses K-means, and Cosine Distance
     * uses K-medoids.
     * 
     * @param K The number of clusters
     * @param MaxLoops The maximum number of loops the algorithm will run (may end earlier).
     * @return The number of Loops performed by the algorithm.
     */
    public int clustering(int K, int MaxLoops){
        int[] Seed = generateClusterCentroidSeed(K);
        return clustering(K, MaxLoops, Seed);
    }
    
    public int clustering(int K, int MaxLoops, int[] Seed){
        lstClusterCenter=new ArrayList<SparseClusterCenter>(K);
        //Inicializar os Centroides dos clusters com vetores de dados distribuídos 
        float MinDist, Dist;
        int i, j, l, MinClusterIndex;
        SparseDataVector Data;
        boolean Changed;
//        Random R = new Random(System.currentTimeMillis());
        
//        for(i=0, inc=(lstDataVector.size()-1)/K; i<K; i++){
        for(i=0; i<K; i++){
            SparseClusterCenter Centers = new SparseClusterCenter();
//            Centers.setCenter( lstDataVector.get(i*inc+R.nextInt(inc) ) );            
            Centers.setCenter( lstDataVector.get( Seed[i] ) );            
            lstClusterCenter.add( Centers );
        }
        
        for(l=0; l<MaxLoops; l++){
            OutlierCount = 0;
            
            //Limpar os dados designados a cada cluster
            for(i=0; i<K; i++){
                lstClusterCenter.get(i).clearAssignedData();
            }
            
            //Designar o dado ao cluster mais próximo
            for(j=0; j<lstDataVector.size(); j++){
                Data = lstDataVector.get(j);
                MinDist = lstClusterCenter.get(0).distance(Data,DistanceMeasure);
                MinClusterIndex = 0;
                for(i=1; i<K; i++){
                    Dist = lstClusterCenter.get(i).distance(Data,DistanceMeasure);
                    if(Dist < MinDist){
                        MinDist = Dist;
                        MinClusterIndex = i;
                    }
                }
                lstClusterCenter.get(MinClusterIndex).add(Data);
                if(MinClusterIndex==0 && MinDist==1){
                    //Outlier
                    OutlierCount++;
                }
            }
            
            //recalcular os centroides dos clusters
            Changed = false;
            for(i=0; i<K; i++){
                Changed|=lstClusterCenter.get(i).calcCenter(DistanceMeasure);
            }
            
            if(!Changed){
                break;
            }
        }
        
        return l;
    }
    
    public void saveClusteringToFile(String FileName, float Threshold) throws Exception{
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return;
        }
        writeWordsToFileWihCount(FileName+"_whash.csv");
        
        FileWriter fwr=new FileWriter(FileName);
        fwr.write("#Cluster = "+lstClusterCenter.size()+"\n");
        for(SparseClusterCenter C : lstClusterCenter){
            SparseDataVector Center = C.getCenter();
            fwr.write("\n *** Cluster: ");
            fwr.write(Center.toFileDataLine());
            fwr.write("* Data ");
            fwr.write(Integer.toString(C.getData().size()));
            fwr.write("\n");
            for(SparseDataVector V : C.getData()){
                float d = C.distance(V, DistanceMeasure);
                if (!Float.isNaN(d) && d <= Threshold) {
                    fwr.write(Integer.toString(V.getBugId()));
                    fwr.write(" ");
                    fwr.write(Float.toString(d));
                    fwr.write("\n");
                }
            }
        }
        fwr.close();
    }
    
    /**
     * MAIS RAPIDO que tentar enviar inserts através da conexão JDBC.
     * 
     * @param FileName
     * @throws Exception 
     */
    public void saveClusteringToScriptSQL(String FileName) throws Exception{
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return;
        }
        FileWriter fwr=new FileWriter(FileName);
        fwr.write( clusteringToScriptSQL() );
//        fwr.write("delete from aserg_cluster_medoid;\n\n");
//        fwr.write("insert into aserg_cluster_medoid(bug_id,cluster,dist) values \n");
//        int ClusterNumber=0;
//        float Dist;
//        for(SparseClusterCenter C : lstClusterCenter){
//            ClusterNumber++;
//            for(SparseDataVector Data : C.getData()){
//                fwr.write("(");
//                fwr.write(Integer.toString(Data.getBugId()));
//                fwr.write(",");
//                fwr.write(Integer.toString(ClusterNumber));
//                fwr.write(",");
//                Dist = C.distance(Data, SparseClusterCenter.COSINE_DISTANTE);
//                if(!Float.isNaN(Dist)){
//                    fwr.write( Float.toString(Dist) );
//                }
//                else{
//                    fwr.write("NULL");
//                }
//                fwr.write("),\n");
//            }
//        }
//        fwr.write("(35,0,NULL); -- Gambiarra \n\n");     
//        fwr.write("delete from aserg_cluster_medoid where bug_id = 35 and cluster = 0; -- Conserta Gambiarra \n");
        fwr.close();
    }

    /**
     * MAIS RAPIDO que tentar enviar inserts através da conexão JDBC.
     * 
     * @param FileName
     * @throws Exception 
     */
    public void saveClusteringToZippedScriptSQL(ZipOutputStream zout, String FileName) throws Exception{
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return;
        }

        String str = clusteringToScriptSQL();
        zout.putNextEntry(new ZipEntry(FileName));
        zout.write( str.getBytes() );
        zout.closeEntry();
    }
    
    private String clusteringToScriptSQL() throws Exception{
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return "";
        }
        
        StringBuilder stb=new StringBuilder();
        stb.append("delete from aserg_cluster_medoid;\n\n");
        stb.append("insert into aserg_cluster_medoid(bug_id,cluster,dist) values \n");
        int ClusterNumber=0;
        float Dist;
        for(SparseClusterCenter C : lstClusterCenter){
            ClusterNumber++;
            for(SparseDataVector Data : C.getData()){
                stb.append("(");
                stb.append(Data.getBugId());
                stb.append(",");
                stb.append(ClusterNumber);
                stb.append(",");
                Dist = C.distance(Data, SparseClusterCenter.COSINE_DISTANTE);
                if(!Float.isNaN(Dist)){
                    stb.append( Dist );
                }
                else{
                    stb.append("NULL");
                }
                stb.append("),\n");
            }
        }
        stb.append("(35,0,NULL); -- Gambiarra \n\n");     
        stb.append("delete from aserg_cluster_medoid where bug_id = 35 and cluster = 0; -- Conserta Gambiarra \n");
        return stb.toString();
    }

    
    /**
     * Procure usar a função <code>saveClusteringToScriptSQL(String FileName)</code>
     * que é mais rápido.
     * 
     * @throws Exception 
     */
    @Deprecated
    public void saveClusteringToDatabase() throws Exception{
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return;
        }
        AsergTestsDAO.saveClusterKmedoidTest(lstClusterCenter);
    }
    
    private void calcWeightMethod(SparseDataVector Vet){
        switch(DefaultTFWeightMethod){
            case TF_LOG_NORMALIZATION:
                Vet.logNormalization(); //1 + log2 fij
                break;
                
            default:
                //Faz nada, RAW Freq que já foi feito
                //break;
        }
        
        switch(DefaultIDFWeightMethod){
            case IDF_INVERSE_FREQUENCY:
                Collection<SparseDataItem> colItems = Vet.getItems();
                for(SparseDataItem Item : colItems){
                    float Idf = (float)ColCount; //Número total de documentos
                    Idf /= hsmDocNumber.get(Item.getIndex()); //ni
                    Idf =(float)(Math.log(Idf)/Math.log(2)); //log2 (N / ni)
                    Item.multFrequency(Idf); //Tf * Idf
                }
                break;
                
            default:
                //Faz nada, Unary IDF dá o mesmo de TF
                //break;
        }
    }
    
    public void calcClusterMetrics(boolean ExcludeOutliners){
        if(lstClusterCenter==null || lstClusterCenter.isEmpty()){
            return;
        }
        
        float k = (float) lstClusterCenter.size();
        float AvgDist = 0; // d-
        for(SparseClusterCenter C : lstClusterCenter){
            if(ExcludeOutliners){
                AvgDist+=C.getAvgCenterDistWithoutOutliers();
            }
            else{
                AvgDist+=C.getAvgCenterDist();
            }
        }
        AvgDist/=k;
        
        DevIntra = 0;
        for(SparseClusterCenter C : lstClusterCenter){
            float d;
            if(ExcludeOutliners)
                d = C.getAvgCenterDistWithoutOutliers() - AvgDist;
            else                
                d = C.getAvgCenterDist() - AvgDist;
            DevIntra += d*d;
        }
        DevIntra/=(k-1);
        DevIntra=(float)Math.sqrt(DevIntra);
        
        Cintra = DevIntra/AvgDist;
        
        float D = 0;
        for(int i=0; i<lstClusterCenter.size(); i++){
            for(int j=i+1; j<lstClusterCenter.size(); j++){
                D+=lstClusterCenter.get(i).distance(lstClusterCenter.get(j).getCenter(), DistanceMeasure);
            }
        }
        D/=(k*(k-1)/2);
        
        DevInter = 0;
        for(int i=0; i<lstClusterCenter.size(); i++){
            for(int j=i+1; j<lstClusterCenter.size(); j++){
                float Dij;
                Dij=lstClusterCenter.get(i).distance(lstClusterCenter.get(j).getCenter(), DistanceMeasure);
                Dij-= D;
                DevInter+=(Dij*Dij);
            }
        }
        //System.out.println(DevInter);
        DevInter/=((k*(k-1)/2)-1);
        DevInter=(float)Math.sqrt(DevInter);
        
        Cinter = DevInter / D;
        
        BetaVar = (DevIntra*DevIntra) / (DevInter*DevInter);
        BetaCv = Cintra / Cinter;
        CRR = Cinter / (Cinter + Cintra);
        
    }
    
    public String metricsToCsvLine(){
        StringBuilder stb=new StringBuilder();
        stb.append(lstClusterCenter.size());
        stb.append(";");
        stb.append(Float.toString(DevIntra).replace('.', ','));
        //stb.append(DevIntra);
        stb.append(";");
        stb.append(Float.toString(Cintra).replace('.', ','));
        //stb.append(Cintra);
        stb.append(";");
        stb.append(Float.toString(DevInter).replace('.', ','));
        //stb.append(DevInter);
        stb.append(";");
        stb.append(Float.toString(Cinter).replace('.', ','));
        //stb.append(Cinter);
        stb.append(";");
        stb.append(Float.toString(BetaVar).replace('.', ','));
        //stb.append(BetaVar);
        stb.append(";");
        stb.append(Float.toString(BetaCv).replace('.', ','));
        //stb.append(BetaCv);
        stb.append(";");
        stb.append(Float.toString(CRR).replace('.', ','));
        stb.append(";");
        stb.append(Integer.toString(OutlierCount));
        stb.append("\n");
        return stb.toString();
    }
    
    /**
     * @return the Cintra
     */
    public float getCintra() {
        return Cintra;
    }

    /**
     * @return the Cinter
     */
    public float getCinter() {
        return Cinter;
    }

    /**
     * @return the BetaCv
     */
    public float getBetaCv() {
        return BetaCv;
    }

    /**
     * @return the BetaVar
     */
    public float getBetaVar() {
        return BetaVar;
    }

    /**
     * @return the DevIntra
     */
    public float getDevIntra() {
        return DevIntra;
    }

    /**
     * @return the DevInter
     */
    public float getDevInter() {
        return DevInter;
    }
    
//    public static void main(String[] args){
//        int[] seed = new int[100];
//        java.util.Random R=new java.util.Random(System.currentTimeMillis());
//        int aux, swap;
//        int Max = seed.length/2;
//        
//        for(int i=0; i<seed.length; i++){
//            seed[i]=i;
//        }
//        
//        for(int i=Max; i>0; i--){
//            swap = R.nextInt(i);
//            
//            aux = seed[i-1];
//            seed[i-1] = seed[swap];
//            seed[swap] = aux;
//        }
//
//        System.out.println( java.util.Arrays.toString(seed) );
//    }
}
