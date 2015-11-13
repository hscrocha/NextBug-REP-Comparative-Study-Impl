/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.auto;

import brcluster.ClusterDataInterface;
import brcluster.ClusterFactory;
import bugapp.connection.MozillaConnection;
import bugapp.ir.ProcessedData;
import bugapp.ir.Matrix;
import bugapp.ir.ProcessMessage;
import bugapp.oldir.Vectorization;
import bugapp.log.CsvLogger;
import bugapp.persistence.dao.BugsDAO;
import bugapp.persistence.dao.ProfilesDAO;
import bugapp.persistence.entity.BugSimple;
import bugapp.persistence.entity.Profile;
import java.util.ArrayList;
import bugapp.log.Logger;
import bugapp.oldir.BooleanFeatureVector;
import bugapp.pasm.PasmAdhoc;
import bugapp.pasm.PasmFactory;
import bugapp.persistence.cvs.BugsCSV;
import bugapp.persistence.dao.AsergClusterDAO;
import bugapp.persistence.dao.AsergCommitDAO;
import bugapp.persistence.dao.BugActivityDAO;
import bugapp.persistence.dao.BugAttachDAO;
import bugapp.persistence.dao.MiscelaneousDAO;
import bugapp.persistence.entity.AsergBugLifeCycle;
import bugapp.persistence.entity.AsergCommit;
import bugapp.persistence.entity.BugActivity;
import bugapp.persistence.entity.BugAttach;
import bugapp.persistence.entity.BugCluster;
import bugapp.persistence.entity.BugDescription;
import bugapp.persistence.entity.BugPath;
import bugapp.persistence.entity.BugResolution;
import bugapp.persistence.entity.StatisticEntity;
import bugapp.persistence.template.BugLifeCycleTemplate;
import bugapp.scripts.AsergBugCommitSQL;
import bugapp.xml.SvnSaxParser;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Henrique
 */
public class Core {
    
    public static final int FIND_CONTEXT_CHANGE_INTERVAL = 21;

    public static void bugLifeCycleParWekaHumberto(String Resolution){
        try{
            String FileName = "./wekalifecycle"+Resolution+".csv";
            FileWriter fwr = new FileWriter(FileName);
            ArrayList<BugActivity> lst;

            String[] Status={"Unconfirmed","Confirmed","Assigned","Resolved","Verified"};
            for(int i=0; i<Status.length; i++){
                for(int j=0; j<Status.length; j++){
                    if(i!=j){
                        System.out.printf("\n %s -> %s ",Status[i],Status[j]);
                        lst = BugActivityDAO.getAsergBugActivityWeka(Status[i], Status[j], Resolution);
                        for(BugActivity B : lst){
                            fwr.write( Integer.toString(B.getBugId())+","+Status[i]+","+Status[j]+","+Integer.toString(B.getDateDiffStatus())+"\n");
                        }
                    }
                }
            }
            fwr.close();
            System.out.println("Encerrado.");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void bugResolutionTime(String DtBegin, String DtEnd, String strResolution){
        try{
            BugResolution Resolution = BugResolution.valueOf(strResolution);
            ArrayList<Integer> lst = MiscelaneousDAO.getResolutionTimeFrimBugs(DtBegin, DtEnd, Resolution);
            
            String FileName = "./ResolutionTimes"+Resolution.toString()+DtBegin+"_"+DtEnd+".csv";
            FileWriter fwr = new FileWriter(FileName);
            for(int i : lst){
                fwr.write(Integer.toString(i)+"\n");
            }
            fwr.close();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void bugLifeCycle(String strResolution){
        try{
            String[] Status={"Unconfirmed","Confirmed","Assigned","Resolved","Verified"};
            AsergBugLifeCycle blc = new AsergBugLifeCycle();
            BugResolution[] Resolution;
            if(strResolution!=null){
                Resolution = new BugResolution[1];
                Resolution[0] = BugResolution.valueOf(strResolution);
            }
            else{
                Resolution = new BugResolution[]{BugResolution.NULL};
                //Resolution = new BugResolution[]{BugResolution.DUPLICATE, BugResolution.INVALID};
            }
//            Resolution = new BugResolution[]{BugResolution.WONTFIX, BugResolution.WORKSFORME, BugResolution.INCOMPLETE};
//            System.out.println("Recuperando os Totais...");
//            for(int i=0; i<Status.length; i++){
//                float N = BugActivityDAO.getAsergBugActivityTotal(Status[i], Resolution);
//                blc.setTotal(N, Status[i]);
//            }
            
            System.out.println("Recuperando os Caminhos...");
            String DtBegin = "2011-01-01";
            String DtEnd = "2011-12-31";
            String Where = " AND product_id = 21 "; //" AND b.bug_severity <> 'enhancement' AND b.bug_severity <> 'blocker' ";
            
            StatisticEntity Stats;
            for(int i=0; i<Status.length; i++){
                for(int j=0; j<Status.length; j++){
                    if(i!=j){
                        Stats = BugActivityDAO.getAsergBugActivityCount(Status[i], Status[j], DtBegin, DtEnd, Where, Resolution);
                        blc.setStat(Stats, Status[i], Status[j]);
                    }
                    else{
                        Stats = BugActivityDAO.getAsergBugActivityCount(Status[i], DtBegin, DtEnd, Where, Resolution);
                        blc.setStat(Stats, Status[i], Status[j]);
                    }
                }
            }
            
            System.out.println("Calculando Porcentagens...");
            blc.calcPercentages();
                        
            System.out.println("Gravando Arquivo CSV...");
            String FileName = "./buglifecycle/buglifecycle"+Arrays.toString(Resolution)+"-"+DtBegin.substring(0,4)+"-WHERE";//+"-"+DtEnd.substring(0,7);
            FileWriter fwr = new FileWriter(FileName+".csv");
            fwr.write( blc.toCsvFile() );
            fwr.close();
            
            System.out.println("Gravando Arquivo de Imagem...");
            BugLifeCycleTemplate Template = new BugLifeCycleTemplate();
            Template.create(FileName+".dia",blc);
            
            System.out.println("Encerrado.");
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void createNewBugActivity(){
        try{
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugActivity> lst = BugActivityDAO.getBugs("2009-01-01");
            
            System.out.println("Salvando os Dados ...");
            BugActivityDAO.saveNewActivity(lst);

            System.out.println("Encerrado.");
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void generateCSVforMEScourse(){
        try{
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugPath> lstBugs = BugsDAO.getBugsFromCoreFirefox("1996-01-01");
            String Filename = "Mozilla-MES-dataset.csv";
            
            System.out.println("Gravando no arquivo CSV ...");
            FileWriter fwr=new FileWriter(Filename);
            
            for (BugPath B : lstBugs) {
                String CvsLine = B.getComponentId() + "," + B.getShortDesc() + "," + B.getBugId() + "," + B.getDtCreation() + "\n";
                fwr.write(CvsLine);
            }
            fwr.close();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void adhocIssuesParameterProduct(int... lstProdIds){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsCSV.getBugPaths("bugs_lifetime-lucene-svn-2001-2013.csv"); //Lucene
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla

            //ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            ArrayList<BugPath> lstBugs;
            String Filename = "Mozilla-Prod-Parameter-Statistics.csv";
            
            System.out.println("NextBug ...");
            FileWriter fwr=new FileWriter(Filename);
            fwr.write("P-id;"+PasmAdhoc.getCvsHeader());
            fwr.close();
            
            for (int ProdId : lstProdIds) {
                System.out.printf(" - Reading bugs from prod %d %n", ProdId);
                lstBugs = BugsDAO.getAllBugFilesFromProduct(ProdId,"2009-01-01"); //Mozilla
                System.out.println(lstBugs.size());

            //for (int threshold = 5; threshold <= 9; threshold++) { //0.6 - 0.9
                //for (int threshold = 10; threshold <= 10; threshold++) { //0.8 - 0.9
                for (float threshold = 0.7f; threshold <= 0.7f; threshold += 0.7f) {

                    System.out.printf("   Distance Threshold = %f %n", threshold);
                    PasmAdhoc Pa = new PasmAdhoc();
                    Pa.setPasmParameters(threshold, 0);
                    Pa.pasm(lstBugs);

                    System.out.println("   Appending results to file");
                    String CvsLine = Integer.toString(ProdId)+";"+Pa.getMetricsInCvsFileString().replaceAll("\n", "\n ;");
                    fwr = new FileWriter(Filename, true);
                    fwr.write(CvsLine);
                    fwr.close();
                }
            }
            
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void adhocIssuesParameterTest(){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsCSV.getBugPaths("bugs_lifetime-lucene-svn-2001-2013.csv"); //Lucene
            ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01"); //Mozilla

            //ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            //ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01"); //Mozilla
            String FileName = "NextBug-Moz-Parameter-Statistics.csv";

            System.out.println("Adhoc PASM ...");
            FileWriter fwr=new FileWriter(FileName);
            fwr.write(PasmAdhoc.getCvsHeader());
            fwr.close();
            float thresholds[]={0.7f, 0.725f, 0.75f, 0.775f, 0.8f, 0.825f, 0.85f, 0.875f, 0.9f, 0.925f, 0.95f, 0.975f, 1.01f};            
            //float thresholds[]={0.9f};            
            for(int i=0; i<thresholds.length; i++){
                    
                    System.out.printf("   Distance Threshold = %f %n",thresholds[i]);
                    PasmAdhoc Pa=new PasmAdhoc();
                    Pa.setPasmParameters(thresholds[i], 0);
                    //Pa.setLogPerformanceTime("NextBug-moz-performance-t"+Float.toString(thresholds[i])+".csv");
                    //Pa.setLogToFileOn("NextBug-recs-log.txt", true);
                    Pa.pasm(lstBugs);
                    
                    System.out.println("   Appending results to file");
                    String CvsLine = Pa.getMetricsInCvsFileString();
                    fwr=new FileWriter(FileName,true);
                    fwr.write(CvsLine);
                    fwr.close();
            }
            
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    

    public static void adhocPasmIssues(){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsCSV.getBugPaths("bugs_lifetime-lucene-svn-2001-2013.csv"); //Lucene
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla

            System.out.println("Adhoc PASM ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(0.9f, 0);
            Pa.setLogToFileOn("AdhocPasm.txt");
            Pa.setLogPerformanceTime("mozilla-performance.csv");
            Pa.pasm(lstBugs);
            System.out.println("Writing CSV file ...");
            Pa.writeMetricsToCsvFile("Adhoc_PASM_Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void pasmParametersTest(){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsCSV.getBugPaths("bugs_lifetime-lucene-svn-2001-2013.csv"); //Lucene
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            
            System.out.println("PASM ...");
            FileWriter fwr=new FileWriter("PASM_Parameter_Statistics.csv");
            fwr.write(PasmFactory.pasmStatisticsCvsHeader());
            fwr.close();
            
            int window = 0;
//            for (int threshold = 7; threshold <= 18; threshold++) { //0.35 - 0.9
            for (int threshold = 8; threshold >= 8; threshold--) {
                //for (int window = 15; window <= 30; window += 15) {
                    System.out.printf("   Distance Threshold = %f, Time Window = %d %n",threshold*0.05f,window);
                    PasmFactory Pf = new PasmFactory();
                    Pf.setPasmParameters(threshold*0.05f, window);
                    Pf.pasm(lstBugs);
                    
                    System.out.println("   Appending results to file");
                    String CvsLine = Pf.pasmStatisticsToCsvLine();
                    fwr=new FileWriter("PASM_Parameter_Statistics.csv",true);
                    fwr.write(CvsLine);
                    fwr.close();
                //}
            }
            
            //fwr.close();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
        
    public static void pasm(){
        //pasmLucene();
        pasmMozilla();
    }
    
    
    public static void pasmLucene(){
        try{
            System.out.println("Lendo CSV ...");
            ArrayList<BugPath> lstBugs = BugsCSV.getBugPaths("bugs_lifetime-lucene-svn-2001-2013.csv");
            
            System.out.println("PASM ...");
            PasmFactory Pf = new PasmFactory();
            Pf.setPasmParameters(0.85f, 30);
            Pf.pasm(lstBugs);
            
            System.out.println("Gravando resultados em arquivo ...");
            Pf.writeWordsToFileWihCount("Lucene_PasmBuckets_wordhash.csv");
            Pf.writeClosedBucketsToFile("Lucene_PasmBuckets.txt");
            Pf.writeCloseBucketsToCsv("Lucene_PasmBuckets.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void pasmMozilla(){
        try{
            System.out.println("Consultando o BD ...");
            //ArrayList<BugPath> lstBugs = BugsDAO.getBugPathFromGeneral();
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            
            System.out.println("PASM ...");
            PasmFactory Pf = new PasmFactory();
            Pf.setPasmParameters(0.75f, 0);
            Pf.pasm(lstBugs);
            
            //Pf.test();
            
            System.out.println("Gravando resultados em arquivo ...");
            Pf.writeWordsToFileWihCount("Mozilla_PasmBuckets_wordhash.csv");
            Pf.writeClosedBucketsToFile("Mozilla_PasmBuckets.txt");
            Pf.writeCloseBucketsToCsv("Mozilla_PasmBuckets.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void mapBugsAttachsToFiles(){
        try{
            System.out.println("Consultando o BD ...");
            ArrayList<BugAttach> lstBugs = BugAttachDAO.getBugAttachs();
            
            MozillaConnection Con = new MozillaConnection();
            ArrayList<String> lstFiles = null;
//            StringBuilder stb = new StringBuilder();
            
            BugAttachDAO DAO = new BugAttachDAO();
            DAO.prepareSaveBugPath();
            
            System.out.println("Processando URLs ...");
            for(BugAttach B : lstBugs){
                lstFiles = Con.process(B);
                //if(lstFiles.size()>0){
                    DAO.saveBugPath(B.getBugId(), lstFiles);
                    System.out.println("Saved Bug "+B.getBugId()+" and his "+lstFiles.size()+" filenames on BD");
                //}
//                for(String File : lstFiles){
//                    stb.append("(");
//                    stb.append(B.getBugId());
//                    stb.append(",'");
//                    stb.append(File);
//                    stb.append("'),\n");
//                }
            }
            DAO.closeSaveBugPath();
            
//            System.out.println("Gravando Arquivo SQL ...");
//            FileWriter fwr = new FileWriter("./BugsFilesMozilla.sql");
//            fwr.write("insert into aserg_bug_file(bug_id,filename) values \n");
//            fwr.write(stb.toString());
//            fwr.close();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void mapGeneralBugsToCommits(){
        try{
            AsergBugCommitSQL SqlOut = new AsergBugCommitSQL();

            ArrayList<Integer> lstRevision = null;
            ArrayList<Integer> lstBugId = null;
            
            lstBugId = BugsDAO.getBugIdFromGeneral();
            for(int BugId : lstBugId){
                lstRevision = AsergCommitDAO.getRevisionCommitsWithBug(BugId);
                if(lstRevision.size() > 0){
                    SqlOut.append2(BugId, lstRevision);
                }
            }
            SqlOut.saveToFile("./CommitGeneralBugs.sql");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void testMapCommitsBugIdToScriptSQL(String Year){
        try{
            AsergBugCommitSQL SqlOut = new AsergBugCommitSQL("_new");
            ArrayList<AsergCommit> lstCommits = AsergCommitDAO.getCommits(Year+"-01-01 00:00:00",Year+"-12-31 23:59:59");
            //ArrayList<AsergCommit> lstCommits = AsergCommitDAO.getCommits("2011-12-31 00:00:00","2011-12-31 23:59:59");
            ArrayList<Integer> setBugId = null;

            for(AsergCommit C : lstCommits){
                
                //setBugId.addAll( ProcessMessage.extractBugId(C.getMessage()) );
                setBugId = BugsDAO.getBugIdsResolvedAround(C, 2);
                System.out.printf("%s - %s\n",C,setBugId);
                if(setBugId.size() > 0){
                    SqlOut.append(C.getRevision(), setBugId);
                }
            }
            SqlOut.saveToFile("./NewCommitBugs"+Year+".sql");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void newMapCommitsBugIdToScriptSQL(){
        try{
            AsergBugCommitSQL SqlOut = new AsergBugCommitSQL("_new");
            ArrayList<AsergCommit> lstCommits = AsergCommitDAO.getCommits();
            HashSet<Integer> setBugId = null;

            for(AsergCommit C : lstCommits){
                setBugId = new HashSet<Integer>();
                setBugId.addAll( ProcessMessage.extractBugId(C.getMessage()) );
                setBugId.addAll( BugsDAO.getBugIdsResolvedAround(C.getDtCommitAsUtilDate(), 2) );
                
                if(setBugId.size() > 0){
                    SqlOut.append(C.getRevision(), setBugId);
                }
            }
            SqlOut.saveToFile("./NewCommitBugs.sql");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void mapCommitsBugIdToScriptSQL(){
        try{
            AsergBugCommitSQL SqlOut = new AsergBugCommitSQL();
            ArrayList<AsergCommit> lstCommits = AsergCommitDAO.getCommitsShort();
            ArrayList<Integer> lstBugId = null;
            for(AsergCommit C : lstCommits){
                lstBugId = ProcessMessage.extractBugId(C.getMessage());
                if(lstBugId.size() > 0){
                    SqlOut.append(C.getRevision(), lstBugId);
                }
            }
            SqlOut.saveToFile("./CommitBugs.sql");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void xmlCommitsToSQL(){
        try{
            SvnSaxParser XmlParser = new SvnSaxParser();
            XmlParser.setOutputFileName("./logMozilla");
            XmlParser.parse("./logMozillaCom.xml");
            //logMozillaCom.xml
            //System.out.printf("\nAuthor = %d, Message = %d, Path = %d, Kind = %d\n", AsergCommit.MaxAuthorLen, AsergCommit.MaxMessageLen, AsergCommit.MaxPathLen, AsergCommit.MaxKindLen );
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public static void asergCluster(){
        try{
            ArrayList<BugCluster> lstBugs;
            
            for(int i=1; i<=7; i++){
                System.out.printf("Consultando o BD dados do cluster %d%n",i);
                lstBugs = AsergClusterDAO.getAsergClusterData(i);
                
//                System.out.println("Criando o objeto ProcessedData Out...");
//                ProcessedData Out = new ProcessedData();
//
//                System.out.println("Criando os vetores com os dados do banco...");
//                for (BugCluster B : lstBugs) {
//                    Out.add(B.getId(),B.getShortDesc());
//                }
//                
//                System.out.println("Criando arquivo de frequencia de palavras...");
//                Out.writeWordsToFileWihCount("ClusterWords"+i+".csv");
                
                AsergClusterDAO.saveSampleAsergClusterDataToFile("Cluster"+i+"Sample.csv", lstBugs);               
            }
            System.out.println("Terminou...");
            System.gc();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void testsKmedoids(java.util.Date DtInicio, java.util.Date DtFinal, int Ci, int Cf){
        try{
            System.out.println("Consultando o BD...");
            ArrayList<BugDescription> lstBugs;
            lstBugs = BugsDAO.getBugDescFromFirefox(DtInicio, DtFinal);
            //lstBugs = BugsDAO.getBugDesc(DtInicio, DtFinal);

            System.out.println("Criando o objeto Arff Out...");
            ProcessedData Out = new ProcessedData();
            
            System.out.println("Criando os vetores com os dados do banco...");
            for(BugDescription B : lstBugs){
                Out.add(B);
            }
            System.out.println("Calculando os pesos TF-IDF...");
            Out.applyWeightMethod();
            
            FileOutputStream fzout = new FileOutputStream("medoid_scripts.zip");
            ZipOutputStream zout = new ZipOutputStream(fzout);
            
            FileWriter fwr = new FileWriter("medoid_analysis_test"+Ci+"-"+Cf+".csv");
            fwr.write("clusters;dev-intra;c-intra;dev-inter;c-inter;beta-var;beta-cv;CRR;#Outlier\n");

            //FileWriter fno = new FileWriter("medoid_analysis_test"+Ci+"-"+Cf+"_no_outliers.csv");
            //fno.write("clusters;dev-intra;c-intra;dev-inter;c-inter;beta-var;beta-cv\n");
            
            int[] Seed = Out.generateClusterCentroidSeed();
            for(int i=Ci; i<=Cf; i++){
                System.out.printf("Executando K-medoids com %d clusters\n",i);
                int L=Out.clustering(i, 10, Seed);
                System.out.printf("K-medoids %d terminou com %d Loops\n",i,L);

                //System.out.println("Gravando resultado no arquivo");
                //Out.saveClusteringToFile("./medoid.txt",0.7f);
            
                //System.out.println("Gravando resultado no banco de dados");
                //Out.saveClusteringToDatabase();
                
                System.out.println("Calculando métricas de cluster");
                Out.calcClusterMetrics(false);
                fwr.write(Out.metricsToCsvLine());
                
                //System.out.println("Calculando métricas de cluster without outliers");
                //Out.calcClusterMetrics(true);
                //fno.write(Out.metricsToCsvLine());
                

                System.out.println("Gerando script para banco de dados");
                //Out.saveClusteringToScriptSQL("./medoid_script"+i+".sql");
                Out.saveClusteringToZippedScriptSQL(zout, "./medoid_script"+i+".sql");
                
                //System.out.println("Colocando o script no zip");
                //zout.putNextEntry(new ZipEntry("./medoid_script"+i+".sql"));
                //zout.closeEntry();
            }
            fwr.close();
            //fno.close();
            zout.close();
            fzout.close();
            
            System.out.println("Terminou...");
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void kmedoids(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            System.out.println("Consultando o BD...");
            ArrayList<BugDescription> lstBugs;
            lstBugs = BugsDAO.getBugDescFromFirefox(DtInicio, DtFinal);
            //lstBugs = BugsDAO.getBugDesc(DtInicio, DtFinal);

            System.out.println("Criando o objeto Arff Out...");
            ProcessedData Out = new ProcessedData();
            
            System.out.println("Criando os vetores com os dados do banco...");
            for(BugDescription B : lstBugs){
                Out.add(B);
            }
            System.out.println("Calculando os pesos TF-IDF...");
            Out.applyWeightMethod();

            System.out.println("Executando o kmedoids");
            int L=Out.clustering(80, 100);
            System.out.println("K-medoids terminou com "+Integer.toString(L)+" Loops");

            System.out.println("Gravando resultado no arquivo");
            Out.saveClusteringToFile("./medoid.txt",0.7f);
            
            //System.out.println("Gravando resultado no banco de dados");
            //Out.saveClusteringToDatabase();

            System.out.println("Gerando script para banco de dados");
            Out.saveClusteringToScriptSQL("./medoid_script.sql");
            
            System.out.println("Terminou...");
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void toWekaArff(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            System.out.println("Consultando o BD...");
            ArrayList<BugDescription> lstBugs = BugsDAO.getBugDesc(DtInicio, DtFinal);

            System.out.println("Criando o objeto Arff Out...");
            ProcessedData Out = new ProcessedData();
            
            System.out.println("Criando os vetores com os dados do banco...");
            for(BugDescription B : lstBugs){
                Out.add(B);                
            }
            
            System.out.println("Calculando os pesos TF-IDF...");
            Out.applyWeightMethod();
            
            System.out.println("Gravando os vetores no arquivo...");
            Out.writeToArffFile("./SparseData");

            System.out.println("Terminou...");
        
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void matrixDocTerm(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            System.out.println("Consultando o BD...");
            ArrayList<BugDescription> lstBugs = BugsDAO.getBugDesc(DtInicio, DtFinal);

            System.out.println("Criando a Matrix...");
            Matrix Mat = new Matrix(lstBugs.size());
            
            System.out.println("Populando a Matrix com os dados do banco...");
            for(BugDescription B : lstBugs){
                Mat.add(B);                
            }
            
            System.out.println("Gravando matriz nos arquivos...");
            Mat.writeToFile("./Matriz");

            System.out.println("Terminou...");
        
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
   
    
    public static void kmeans(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            System.out.println("Consultando o BD...");
            ArrayList<BugDescription> lstBugs = BugsDAO.getBugDesc(DtInicio, DtFinal);
            System.out.println("Transformando Bugs em Features...");
            ArrayList<ClusterDataInterface> lstFeatures = Vectorization.transformFeatures(lstBugs);
            
            lstBugs = null;
            
            System.out.println("Clustering...");
            ClusterFactory Fac=new ClusterFactory(lstFeatures);
            long l=Fac.kmeans(50, 100);
            System.out.printf("Fim %d\n",l);
            
            System.out.println(Fac.getClusterCollection());
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void findSimilarBugs(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            //Abre o arquivo de Log para os resultados
            Logger BugLog = new CsvLogger();
            BugLog.open("./SimilarBugs.csv");

            //Recupera todos os desenvolvedores que corrigiram algum bug entre as datas especificadas
            ArrayList<Profile> lstDev = ProfilesDAO.getProfilesAssignedToBugs(DtInicio, DtFinal);
            for(Profile P : lstDev){
                //Pega todos os bugs deste devel
                ArrayList<BugDescription> lstBugsDesc = BugsDAO.getDevelBugDesc(P.getId(), DtInicio, DtFinal);
                for(BugDescription Q : lstBugsDesc){
                    java.sql.Date DtMarkInit = Q.getLastResolved();
                    //Cria um vetor com a desc que representa Q
                    BooleanFeatureVector Vq = Vectorization.makeBooleanVector(Q);
                    
                    ArrayList<BugDescription> lst = BugsDAO.getDevelComponentBugs(Q, DtMarkInit, DtFinal);
                    for(BugDescription D : lst){
                        //Não é necessário criar um vetor com a desc que representa D
                        //porque a função de similaridade já faz isso
                        double sim=Vectorization.similarityBoolean(Vq, D);
                        
                        //Se similaridade for maior que um Limite, logar
                        if(sim>0.6){
                            BugLog.logSimilarity(Q, D, Vq, sim);
                        }
                    }
                }
                System.gc();
            }
            
            BugLog.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    
    public static void findContextChange(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            //Abre o arquivo de Log para os resultados
            Logger BugLog = new CsvLogger();
            BugLog.open("./ContextChange.csv");
            
            //Recupera todos os desenvolvedores que corrigiram algum bug entre as datas especificadas
            ArrayList<Profile> lstDev = ProfilesDAO.getProfilesAssignedToBugs(DtInicio, DtFinal);
            for(Profile P : lstDev){
                //Recupera todos os bugs corrigidos pelo desenvolvedor atual
                ArrayList<BugSimple> lstBugs = BugsDAO.getDevelBugs(P.getId(), DtInicio, DtFinal);
                for(int i=0; i<lstBugs.size(); i++){
                    BugSimple BugInitialMark = lstBugs.get(i);
                    ArrayList<BugSimple> lst = BugsDAO.getDevelBugsUntilDate(P.getId(), BugInitialMark.getCreation(), FIND_CONTEXT_CHANGE_INTERVAL);
                    boolean Changed = false;
                    BugSimple BugFirstChange = null;
                    int Count = 1;
                    for(BugSimple C : lst){
                        Count++;
                        if(BugInitialMark.getComponentId()!=C.getComponentId()){
                            Changed = true;
                            BugFirstChange = C;
                        }
                        else if(Changed){
                            //Mudança de Contexto Detectada.
                            //Gravar no log.
                            BugLog.logContextChange(BugInitialMark, BugFirstChange, C, Count);
                            
                            //Deslocar o ArrayList de B até que depois de C
                            while(i<lstBugs.size() && BugInitialMark.getId() != C.getId()){
                                BugInitialMark = lstBugs.get(i);
                                i++;
                            }
                            break;
                        }
                    }
                }
            }
            
            BugLog.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void findSimpleContextChange(java.util.Date DtInicio, java.util.Date DtFinal){
        try{
            //Abre o arquivo de Log para os resultados
            Logger BugLog = new CsvLogger();
            BugLog.open("./SimpleContextChange.csv");
            
            //Recupera todos os desenvolvedores que corrigiram algum bug entre as datas especificadas
            ArrayList<Profile> lstDev = ProfilesDAO.getProfilesAssignedToBugs(DtInicio, DtFinal);
            for(Profile P : lstDev){
                //Recupera todos os bugs corrigidos pelo desenvolvedor atual
                ArrayList<BugSimple> lstBugs = BugsDAO.getDevelBugs(P.getId(), DtInicio, DtFinal);
                for(int i=0; i<lstBugs.size(); i++){
                    BugSimple B = lstBugs.get(i);
                    ArrayList<BugSimple> lst = BugsDAO.getDevelBugsUntilDate(P.getId(), B.getCreation(), FIND_CONTEXT_CHANGE_INTERVAL);
                    for(BugSimple C : lst){
                        if(B.getComponentId()!=C.getComponentId()){
                            //Trocou 
                            //Gravar no log.
                            BugLog.logContextChange(B, C);
                            
                            //Deslocar o ArrayList de B até que depois de C
                            while(i<lstBugs.size() && B.getId() != C.getId()){
                                B = lstBugs.get(i);
                                i++;
                            }
                            break;
                        }
                    }
                }
            }
            
            BugLog.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
