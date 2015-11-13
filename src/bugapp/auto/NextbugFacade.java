/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.auto;

import bugapp.pasm.PasmAdhoc;
import bugapp.pasm.REP;
import bugapp.pasm.RecBugs;
import bugapp.persistence.cvs.BugsCSV;
import bugapp.persistence.dao.AsergSurveyDAO;
import bugapp.persistence.dao.BugsDAO;
import bugapp.persistence.dao.MylynDAO;
import bugapp.persistence.dao.ProfilesDAO;
import bugapp.persistence.entity.AsergSurvey;
import bugapp.persistence.entity.BugPath;
import bugapp.persistence.entity.Profile;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class NextbugFacade {

    public static final int MOZILLA_PROCESS = 1;
    public static final int MYLYN_PROCESS = 2;
    
    public static void repPasm(int Process, boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            String Filename = "REP-";
            ArrayList<BugPath> lstBugs;
            if(Process == MOZILLA_PROCESS){
                Filename += "Mozilla";
                lstBugs = BugsDAO.getAllBugFilesForRep("2012-10-01",true); //Mozilla
                //lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01"); //Mozilla
            }
            else{
                if(ContextFiles) {
                    Filename += "Mylyn-Context";
                    lstBugs = MylynDAO.getOnlyContextBugFiles("2009-01-01"); //Mylyn
                }
                else{
                    Filename += "Mylyn-Commits";
                    lstBugs = MylynDAO.getOnlyCommitBugFiles("2009-01-01"); //Mylyn
                }
            }

            System.out.println("REP NextBug ...");
            Filename = Filename + "-Metrics.csv";
            FileWriter fwr=new FileWriter(Filename);
            fwr.write(PasmAdhoc.getCvsHeader());
            fwr.close();
            
            System.out.println("Training 1...");
            REP.train(lstBugs, 30, 24, 0.001f);
            REP.fixWeight(REP.K1_BIGRAM_INDEX, REP.K1_UNIGRAM_INDEX, REP.K3_BIGRAM_INDEX, REP.K3_UNIGRAM_INDEX);
            
            System.out.println("Training 2...");
            REP.train(lstBugs, 30, 24, 0.001f);
            REP.unfixWeight(REP.K3_UNIGRAM_INDEX, REP.K3_BIGRAM_INDEX);
            REP.fixWeight(REP.BF_BIGRAM_INDEX, REP.BF_UNIGRAM_INDEX, REP.WSUM_UNIGRAM, REP.WSUM_BIGRAM, REP.WDESC_UNIGRAM, REP.WDESC_BIGRAM);
            
            float thresholds[]={0.9f};
            
            for (int i=0; i<thresholds.length; i++) {
                System.out.printf("   Distance Threshold = %f %n", thresholds[i]);
                PasmAdhoc Pa = new PasmAdhoc();
                Pa.setPasmParameters(thresholds[i], 0);
                Pa.repPasm(lstBugs);

                System.out.println("   Appending results to file");
                String CvsLine = Pa.getMetricsInCvsFileString();
                fwr = new FileWriter(Filename, true);
                fwr.write(CvsLine);
                fwr.close();
            }
            
            REP.printWeights();
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void repTrainPasm(int Process, boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            String Filename = "REP-Train-";
            ArrayList<BugPath> lstBugs;
            if(Process == MOZILLA_PROCESS){
                Filename += "Mozilla";
                //lstBugs = BugsDAO.getAllBugFilesForRep("2009-01-01",true); //Mozilla
                lstBugs = BugsDAO.getOnlyMappedBugFilesForREP("2009-01-01", true); //Mozilla
                //lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01"); //Mozilla
            }
            else{
                if(ContextFiles) {
                    Filename += "Mylyn-Context";
                    lstBugs = MylynDAO.getOnlyContextBugFiles("2009-01-01"); //Mylyn
                }
                else{
                    Filename += "Mylyn-Commits";
                    lstBugs = MylynDAO.getOnlyCommitBugFiles("2009-01-01"); //Mylyn
                }
            }

            System.out.println("REP NextBug ...");
            Filename = Filename + "-Metrics.csv";
            FileWriter fwr=new FileWriter(Filename);
            fwr.write(PasmAdhoc.getCvsHeader());
            fwr.close();
            
            System.out.println("Training 1...");
            REP.train(lstBugs, 30, 24, 0.001f);
            REP.fixWeight(REP.K1_BIGRAM_INDEX, REP.K1_UNIGRAM_INDEX, REP.K3_BIGRAM_INDEX, REP.K3_UNIGRAM_INDEX);
            
            System.out.println("Training 2...");
            REP.train(lstBugs, 30, 24, 0.001f);
            REP.unfixWeight(REP.K3_UNIGRAM_INDEX, REP.K3_BIGRAM_INDEX);
            REP.fixWeight(REP.BF_BIGRAM_INDEX, REP.BF_UNIGRAM_INDEX, REP.WSUM_UNIGRAM, REP.WSUM_BIGRAM, REP.WDESC_UNIGRAM, REP.WDESC_BIGRAM);
            
            //float thresholds[]={0.225f, 0.25f, 0.275f, 0.3f, 0.325f, 0.35f};
            float thresholds[]={0.5f, 0.525f, 0.55f, 0.575f,
                    0.6f, 0.625f, 0.65f, 0.675f,
                    0.7f, 0.725f, 0.75f, 0.775f,
                    0.8f, 0.825f, 0.85f, 0.875f,
                    0.9f, 0.925f, 0.95f, 0.975f,
                    1.01f};
            //float thresholds[]={0.9f};
            
            for (int i=0; i<thresholds.length; i++) {
                RecBugs.GambiRecs = RecBugs.GambiOracle = 0;
                System.out.printf("   Distance Threshold = %f %n", thresholds[i]);
                PasmAdhoc Pa = new PasmAdhoc();
                Pa.setPasmParameters(thresholds[i], 0);
                //Pa.setLogPerformanceTime("rep-performance-Lk-t"+Float.toString(thresholds[i])+".csv");
                //Pa.setLogToFileOn("rep-recomends-log.txt", true);
                Pa.repPasm(lstBugs);

                System.out.println("   Appending results to file");
                String CvsLine = Pa.getMetricsInCvsFileString();
                fwr = new FileWriter(Filename, true);
                fwr.write(CvsLine);
                fwr.close();
    
                System.out.printf(" Empty Recs: %d, Empty Oracle %d \n", RecBugs.GambiRecs, RecBugs.GambiOracle);
            }
            
            REP.printWeights();
//            PasmAdhoc Pa=new PasmAdhoc();
//            Pa.setPasmParameters(0.9f, 0);
//            //Pa.setLogToFileOn(Filename+".txt");
//            Pa.repPasm(lstBugs);
//            System.out.println("Writing CSV file ...");
//            Pa.writeMetricsToCsvFile(Filename+"-Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void baselinePasm(int Process, boolean SameComponent, boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            String Filename = "Baseline-"+(SameComponent?"Component-":"All-");
            ArrayList<BugPath> lstBugs;
            if(Process == MOZILLA_PROCESS){
                Filename += "Mozilla";
                lstBugs = BugsDAO.getAllBugFiles("2009-01-01"); //Mozilla
                //lstBugs = BugsDAO.getOnlyMappedBugFiles("2009-01-01"); //Mozilla
            }
            else{
                if(ContextFiles) {
                    Filename += "Mylyn-Context";
                    lstBugs = MylynDAO.getOnlyContextBugFiles("2009-01-01"); //Mylyn
                }
                else{
                    Filename += "Mylyn-Commits";
                    lstBugs = MylynDAO.getOnlyCommitBugFiles("2009-01-01"); //Mylyn
                }
            }

            System.out.println("Baseline NextBug ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(2f, 0);
            //Pa.setLogToFileOn(Filename+".txt");
            Pa.baselinePasm(lstBugs,SameComponent);
            System.out.println("Writing CSV file ...");
            Pa.writeMetricsToCsvFile(Filename+"-Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    

    public static void nextbugMylynMaxOverlapOpenBugs(boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            String Filename = null;
            ArrayList<BugPath> lstBugs = null;
 
            if(ContextFiles) {
                Filename = "Mylyn-Context-max-overlap-open-bug.csv";
                lstBugs = MylynDAO.getOnlyContextBugFiles("2009-01-01"); //Mylyn
            }
            else{
                Filename = "Mylyn-Commit-max-overlap-open-bug.csv";
                lstBugs = MylynDAO.getOnlyCommitBugFiles("2009-01-01"); //Mylyn
            }
            System.out.printf("Total bugs recovered = %d", lstBugs.size());

            System.out.println("\nAdhoc PASM ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(0.7f, 0);
            Pa.setLogMaxOverlapOpenBug(Filename);
            Pa.pasm(lstBugs);
            //Pa.maxoverlapOpenBugs(lstBugs);
            
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile("Adhoc_PASM_Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void nextbugMozillaMaxOverlapOpenBugs(){
        try{
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla
            System.out.printf("Total bugs recovered = %d", lstBugs.size());
            
            System.out.println("\nAdhoc PASM ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(0.7f, 0);
            Pa.setLogMaxOverlapOpenBug("Mozilla-max-overlap-open-bug.csv");
            //Pa.maxoverlapOpenBugs(lstBugs);
            Pa.pasm(lstBugs);
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile("Adhoc_PASM_Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void nextbugMozillaDisperseChart(){
        try{
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugPath> lstBugs = BugsDAO.getAllBugFiles("2009-01-01","blocker","critical","major","enhancement"); //Mozilla

            System.out.println("Adhoc PASM ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(0.9f, 0);
            Pa.setLogDisperseCharInformation("Mozilla-Disperse",3);
            Pa.pasm(lstBugs);
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile("Adhoc_PASM_Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
/*
        public static void nextbugMylynPararameterTest(boolean ConsideredFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            
            String Filename = null;
            ArrayList<BugPath> lstBugs = null;
 
            if(ConsideredFiles) {
                Filename = "Mylyn_Considered_Parameter_Statistics.csv";
                lstBugs = MylynDAO.getAllBugConsideredFiles("2009-01-01"); //Mylyn
            }
            else{
                Filename = "Mylyn_Parameter_Statistics.csv";
                lstBugs = MylynDAO.getAllBugFiles("2009-01-01"); //Mylyn
            }

    */    
    public static void nextbugMylynDisperseChart(boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            String Filename = null;
            ArrayList<BugPath> lstBugs = null;
 
            if(ContextFiles) {
                Filename = "Mylyn-Context-Disperse";
                lstBugs = MylynDAO.getAllContextBugFiles("2009-01-01"); //Mylyn
            }
            else{
                Filename = "Mylyn-Commit-Disperse";
                lstBugs = MylynDAO.getAllCommitBugFiles("2009-01-01"); //Mylyn
            }

            System.out.println("Adhoc PASM ...");
            PasmAdhoc Pa=new PasmAdhoc();
            Pa.setPasmParameters(0.9f, 0);
            Pa.setLogDisperseCharInformation(Filename,3);
            Pa.pasm(lstBugs);
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile("Adhoc_PASM_Metrics.csv");
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    public static void nextbugContextChange(int Type){
        try{
            System.out.println("Recuperando os Devs ...");
            
            ArrayList<Profile> lstDevs = null;
            String strFileName = "context-cahnge-new.csv";
            if(Type == MOZILLA_PROCESS){
                lstDevs = ProfilesDAO.getAssignedToBugsWithCount();
                strFileName = "Mozilla-context-cahnge-new.csv";
            }
            else if(Type == MYLYN_PROCESS){
                lstDevs = MylynDAO.getAssignedToBugsWithCount();
                strFileName = "Mylyn-context-cahnge-new.csv";
            }

            System.out.println("Abrindo arquivo de log ...");
            FileWriter fwr=new FileWriter(strFileName);
            fwr.write("Dev;Total Bugs;Mapped Bugs;Context Change;Maybe;Same Context\n");
            
            System.out.println("Analisando cada Dev ...");
            for(Profile Dev : lstDevs){
                ArrayList<BugPath> lstBugs = null;
                if(Type == MOZILLA_PROCESS){
                    lstBugs = BugsDAO.getBugFilesFromDev( Dev.getId() );
                }else if(Type == MYLYN_PROCESS){
                    lstBugs = MylynDAO.getBugFilesFromDev( Dev.getId() );
                }
                
                int Maybe = 0;
                int ContextChange = 0;
                int SameContext = 0;
                int MappedBugs = 0;
                
                BugPath A, B;
                if (lstBugs.size() >= 2) {
                    A = lstBugs.get(0);
                    for (int i = 1; i < lstBugs.size(); i++) {
                        B = lstBugs.get(i);
                        if (!A.getPaths().isEmpty()) {
                            //Main Bug does have mapped files
                            if (!B.getPaths().isEmpty()) {
                                //Both Bugs have mapped files, measure context change
                                MappedBugs++;

                                if (A.overlap(B) < 0.1) {
                                    //Possible Context change if there is a C similar to A
                                    if (contextChangeMining(A,B)) {
                                        ContextChange++;
                                    } else {
                                    //No option for context change
                                        //SameContext++;
                                    }
                                } else {
                                    SameContext++;
                                }
                                A = B;
                            } else {
                                //Bug 2 do not have mapped files, so its a maybe
                                Maybe++;
                            }
                        } else { //A.getPaths().isEmpty()
                            //Bug A não tem paths então tem como medir
                            Maybe++;
                            if (!B.getPaths().isEmpty()) {
                                MappedBugs++;
                                A = B; //B passa a ser o principal por ter arquivos
                            }
                        }

                    } //fim for de bugs

                    //fwr.write("Dev;Total Bugs;Mapped Bugs;Context Change;Maybe;Same Context\n");
                    StringBuilder stb = new StringBuilder();
                    stb.append(Dev.getId());
                    stb.append(";");
                    stb.append(Dev.getQuantidadeBugs());
                    stb.append(";");
                    stb.append(MappedBugs);
                    stb.append(";");
                    stb.append(ContextChange);
                    stb.append(";");
                    stb.append(Maybe);
                    stb.append(";");
                    stb.append(SameContext);
                    stb.append("\n");

                    fwr.write(stb.toString());
                }
            }

            System.out.println("Fechando o arquivo ...");
            fwr.close();
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        
    }
    
    private static boolean contextChangeMining(BugPath Base, BugPath Par){
        try {
            ArrayList<BugPath> lstBugs = BugsDAO.getBugFilesCreatedInBase(Base, Par.getDtClose());
            for (BugPath Bp : lstBugs) {
                if (Base.overlap(Bp) > 0.6) {
                    return true;
                }
            }
            return false;
        } catch (java.sql.SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    
    public static void nextbugMylynPararameterTest(boolean ContextFiles){
        try{
            System.out.println("Recuperando os Dados ...");
            
            String Filename = null;
            ArrayList<BugPath> lstBugs = null;
 
            if(ContextFiles) {
                Filename = "Mylyn-Context-Parameter-Statistics.csv";
                lstBugs = MylynDAO.getAllContextBugFiles("2009-01-01"); //Mylyn
            }
            else{
                Filename = "Mylyn-Commit-Parameter-Statistics.csv";
                lstBugs = MylynDAO.getAllCommitBugFiles("2009-01-01"); //Mylyn
            }

            System.out.println("Adhoc PASM ...");
            FileWriter fwr=new FileWriter(Filename);
            fwr.write(PasmAdhoc.getCvsHeader());
            fwr.close();
            
            //for (int threshold = 6; threshold <= 9; threshold++) { //0.6 - 0.9
            //for (int threshold = 10; threshold <= 10; threshold++) { //0.8 - 0.9
            for(float threshold = 0.7f; threshold <= 0.91f; threshold+=0.1f){
                    
                    System.out.printf("   Distance Threshold = %f %n",threshold);
                    PasmAdhoc Pa=new PasmAdhoc();
                    Pa.setPasmParameters(threshold, 0);
                    Pa.setLogPerformanceTime("Mylyn-likelihood-t"+Float.toString(threshold)+".csv");
                    Pa.pasm(lstBugs);
                    
                    System.out.println("   Appending results to file");
                    String CvsLine = Pa.getMetricsInCvsFileString();
                    fwr=new FileWriter(Filename,true);
                    fwr.write(CvsLine);
                    fwr.close();
            }
            
            
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    
    
    public static void nextbugMylyn(boolean ContextFiles) {
        try {
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugPath> lstBugs = null;
            String LogFileName = null;
            String MetricFileName = null;

            if(ContextFiles) {
                LogFileName = "NextBugMylynConsidered.txt";
                MetricFileName = "Nextbug_Mylyn_MetricsConsidered.csv";
                lstBugs = MylynDAO.getAllContextBugFiles("2009-01-01"); //Mylyn
            }
            else{
                LogFileName = "NextBugMylyn.txt";
                MetricFileName = "Nextbug_Mylyn_Metrics.csv";
                lstBugs = MylynDAO.getAllCommitBugFiles("2009-01-01"); //Mylyn
            }

            System.out.println("Next Bug ...");
            PasmAdhoc Pa = new PasmAdhoc();
            Pa.setPasmParameters(1f, 0);
            Pa.setLogToFileOn(LogFileName);
            Pa.setLogPerformanceTime("mylyn-performance.csv");
            Pa.pasm(lstBugs);
            System.out.println("Writing CSV file ...");
            Pa.writeMetricsToCsvFile(MetricFileName);

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void nextbugMozillaDailyExperiment(String strDate) {
        try {
            System.out.println("Recuperando os Dados ...");
            ArrayList<BugPath> lstOpenBugs = null;
            ArrayList<BugPath> lstFixedBugs = null;

            //lstBugs = MylynDAO.getAllBugConsideredFiles("2009-01-01"); //Mylyn
            lstFixedBugs = BugsCSV.getBugPathsFromMozillaExp("./mozilla_daily/bugs-"+strDate+"_fixed.csv");
            lstOpenBugs = BugsCSV.getBugPathsFromMozillaExp("./mozilla_daily/bugs-"+strDate+"_open.csv");

            System.out.println("Next Bug ...");
            PasmAdhoc Pa = new PasmAdhoc();
            Pa.setPasmParameters(0.7f, 0);
            Pa.setLogToFileOn("./mozilla_daily/MozillaDaily-"+strDate+".txt",true);
            Pa.nextBug(lstFixedBugs, lstOpenBugs);
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile(MetricFileName);

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void nextbugCvsFileToMozillaSurveyDB(java.io.File FixedBugsCsv) {
        try {
            System.out.println("Recuperando os Dados ...");
            //ArrayList<BugPath> lstOpenBugs = null;
            ArrayList<AsergSurvey> lstFixedBugs = null;

            //lstBugs = MylynDAO.getAllBugConsideredFiles("2009-01-01"); //Mylyn
            lstFixedBugs = BugsCSV.getSurveyFromMozillaCsv(FixedBugsCsv);
            //lstOpenBugs = BugsCSV.getBugPathsFromMozillaExp("./mozilla_daily/bugs-"+strDate+"_open.csv");

            System.out.println("Inserting into DB ...");
            for(AsergSurvey SubjectSurvey : lstFixedBugs){
               if (!AsergSurveyDAO.existsDeveloper(SubjectSurvey)) { //Subject was not already part of the survey
                   bugapp.connection.MozillaConnection.bugzillaDevInfo(SubjectSurvey);
                   AsergSurveyDAO.insert(SubjectSurvey);
               }
            }
            
            //System.out.println("Writing CSV file ...");
            //Pa.writeMetricsToCsvFile(MetricFileName);

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    
}
