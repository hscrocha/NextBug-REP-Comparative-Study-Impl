/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.cvs;

import au.com.bytecode.opencsv.CSVReader;
import bugapp.persistence.entity.AsergSurvey;
import bugapp.persistence.entity.BugPath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Henrique
 */
public class BugsCSV {
//            ArrayList<BugPath> lstBugs = BugsDAO.getBugPathFromGeneral();
    
    public static ArrayList<BugPath> getBugPaths(String FileName) throws FileNotFoundException, IOException, ParseException{
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        FileReader frd = new FileReader(FileName);
        CSVReader crd = new CSVReader(frd,';');
        
        StringTokenizer tok;
        BugPath DataItem;
        String[] Line = crd.readNext(); // 0 - BugId, 1 - Dt Inicio, 2 - Dt Final, 3 - Summary, 4 - Files/Classes
        while(Line!=null){
            DataItem = new BugPath();
            DataItem.setBugId( Integer.parseInt(Line[0]) );
            DataItem.setDtCreation( bugapp.util.DateUtil.stringToSqlDate(Line[1]) );
            DataItem.setDtClose( bugapp.util.DateUtil.stringToSqlDate(Line[2]) );
            DataItem.setShortDesc( Line[3] );
            
            tok=new StringTokenizer(Line[4], "[]|;", false);
            while(tok.hasMoreTokens()){
                DataItem.addPath( tok.nextToken() );
            }
            
            lst.add(DataItem);
            Line = crd.readNext();
        }
        crd.close();
        frd.close();
        return lst;
    }
    
    public static ArrayList<BugPath> getBugPathsFromMozillaExp(String FileName) throws FileNotFoundException, IOException, ParseException{
        return getBugPathsFromMozillaExp(new File(FileName));
    }

    public static ArrayList<BugPath> getBugPathsFromMozillaExp(File BugsCsv) throws FileNotFoundException, IOException, ParseException{
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        FileReader frd = new FileReader(BugsCsv);
        CSVReader crd = new CSVReader(frd,',');
        
        StringTokenizer tok;
        BugPath DataItem;
        crd.readNext(); //skips the header line
        String[] Line = crd.readNext(); //0 ID, 1 Comp, 2 Assignee, 3 Resolution, 4 Summary, 5 Last Resolved, 6 Opened
        while(Line!=null){
            DataItem = new BugPath();
            DataItem.setBugId( Integer.parseInt(Line[0]) );
            DataItem.setComponent( Line[1] );
            DataItem.setDeveloper( Line[2] );
            DataItem.setShortDesc( Line[4] );
            if(Line[5]==null || Line[5].trim().length()<=0){
                DataItem.setDtClose( bugapp.util.DateUtil.todayAsSQLDate());
            }
            else{
                DataItem.setDtClose( bugapp.util.DateUtil.stringToSqlDate(Line[5],"yyyy-MM-dd HH:mm:ss") );
            }

            DataItem.setDtCreation( bugapp.util.DateUtil.stringToSqlDate(Line[6],"yyyy-MM-dd HH:mm:ss") );
            //DataItem.s
            
//            tok=new StringTokenizer(Line[4], "[]|;", false);
//            while(tok.hasMoreTokens()){
//                DataItem.addPath( tok.nextToken() );
//            }
//            
            lst.add(DataItem);
            Line = crd.readNext();
        }
        crd.close();
        frd.close();
        return lst;
    }
    
    public static ArrayList<AsergSurvey> getSurveyFromMozillaCsv(File BugsCsv) throws FileNotFoundException, IOException, ParseException{
        ArrayList<AsergSurvey> lst = new ArrayList<AsergSurvey>();
        FileReader frd = new FileReader(BugsCsv);
        CSVReader crd = new CSVReader(frd,',');
        
        StringTokenizer tok;
        AsergSurvey DataItem;
        crd.readNext(); //skips the header line
        String[] Line = crd.readNext(); //0 ID, 1 Comp, 2 Assignee, 3 Resolution, 4 Summary, 5 Last Resolved, 6 Opened
        while(Line!=null){
            DataItem = new AsergSurvey();
            DataItem.setBugId( Integer.parseInt(Line[0]) );
            //DataItem.setComponent( Line[1] );
            DataItem.setDevLogin(Line[2] );
            DataItem.setShortDesc( Line[4] );
            
//            tok=new StringTokenizer(Line[4], "[]|;", false);
//            while(tok.hasMoreTokens()){
//                DataItem.addPath( tok.nextToken() );
//            }
//            
            lst.add(DataItem);
            Line = crd.readNext();
        }
        crd.close();
        frd.close();
        return lst;
    }
    
    
}
