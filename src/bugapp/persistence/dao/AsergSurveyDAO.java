/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.AsergSurvey;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergSurveyDAO {
    
    public enum SurveyStatus{
        PENDING('p'), SENT('s'), ANSWERED('A'), EXCLUDED('e'), OTHER('o');
        private final char c;
        
        SurveyStatus(char c){
            this.c=c;
        }
        
        public String toSQL(){
            return Character.toString(c);
        }
    }
    
    public static ResultSet getResultSetSurveys(char Status, String Date, String BugClause) throws SQLException{
        String Query="SELECT status,cod_survey,dev_login,dev_name,bugs_resolved,survey_date,bug_id,short_desc FROM aserg_survey ";
        String Where="";
        if(Status!=0){
            Where+=" status = '"+Status+"' ";
        }
        if(Date!=null){
            if(Where.length()>0) Where+=" AND ";
            Where+=" survey_date > '"+Date+"' ";
        }
        if(BugClause!=null){
            if(Where.length()>0) Where+=" AND ";
            Where+=" bugs_resolved "+BugClause+" ";
        }
        
        if(Where.length()>0) Query+=" WHERE "+Where;
        
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        return Rs;
    }
    
    public static ArrayList<AsergSurvey> getSurveys(char Status, String Date, String BugClause) throws SQLException{
        ArrayList<AsergSurvey> lst = new ArrayList<>();
        
        String Query="SELECT * FROM aserg_survey ";
        String Where="";
        if(Status!=0){
            Where+=" status = '"+Status+"' ";
        }
        if(Date!=null){
            if(Where.length()>0) Where+=" AND ";
            Where+=" survey_date > '"+Date+"' ";
        }
        if(BugClause!=null){
            if(Where.length()>0) Where+=" AND ";
            Where+=" bugs_resolved "+BugClause+" ";
        }
        
        if(Where.length()>0) Query+=" WHERE "+Where;
        
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            AsergSurvey S = new AsergSurvey();
            S.setCodSurvey( Rs.getInt("cod_survey") );
            S.setDevLogin( Rs.getString("dev_login") );
            S.setDevName( Rs.getString("dev_name") );
            S.setBugId(Rs.getInt("bug_id") );
            S.setShortDesc(Rs.getString("short_desc") );
            S.setDtSurvey(Rs.getTimestamp("survey_date") );
            S.setBugsResolved( Rs.getInt("bugs_resolved") );
            S.setStatus(Rs.getString("status").charAt(0) );
            
            lst.add(S);
        }
        return lst;
    }
    
    public static boolean existsDeveloper(AsergSurvey S) throws SQLException{
        String Query="SELECT cod_survey FROM aserg_survey WHERE dev_login = ? LIMIT 0,1;";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, S.getDevLogin());
        ResultSet Rs = Pst.executeQuery();
        return Rs.next();
    }
    
    public static void updateDevInfo(AsergSurvey S) throws SQLException{
        if(S.getBugsResolved() < 0 && S.getDevName()==null){
            return;
        }
        
        String Query = "UPDATE aserg_survey SET bugs_resolved = ?, dev_name = ? WHERE cod_survey = ?; ";
        
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, S.getBugsResolved());
        Pst.setString(2, S.getDevName());
        Pst.setInt(3, S.getCodSurvey());
        Pst.executeUpdate();
    }
    
    public static void updateStatus(SurveyStatus St, AsergSurvey... lstSubs) throws SQLException{
        String Query = "UPDATE aserg_survey SET status = ? WHERE cod_survey IN (";
        Query+= Integer.toString(lstSubs[0].getCodSurvey());
        for(int i=1; i<lstSubs.length; i++){
            Query+= "," + Integer.toString(lstSubs[i].getCodSurvey());
        }
        Query+=");";
        
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, St.toSQL() );
        Pst.executeUpdate();
    }
    
    public static void updateStatus(int CodSurvey, SurveyStatus St) throws SQLException{
        String Query = "UPDATE aserg_survey SET status = ? WHERE cod_survey = ?; ";
        
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, St.toSQL() );
        Pst.setInt(2, CodSurvey);
        Pst.executeUpdate();
    }
    
    public static void updateStatus(AsergSurvey Sub, SurveyStatus St) throws SQLException{
        updateStatus(Sub.getCodSurvey(), St);
    }
    
    public static void insert(AsergSurvey S) throws SQLException{
        //if (!AsergSurveyDAO.existsDeveloper(S)) { //Subject was not already part of the survey
            String Query = "INSERT INTO aserg_survey(dev_login,bug_id,short_desc,bugs_resolved,dev_name,survey_date,status) VALUES (?,?,?,?,?,now(),'p');";
            PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
            Pst.setString(1, S.getDevLogin());
            Pst.setInt(2, S.getBugId());
            Pst.setString(3, S.getShortDesc());
            Pst.setInt(4, S.getBugsResolved());
            if(S.getDevName()!=null){
                Pst.setString(5, S.getDevName());
            }
            else{
                Pst.setNull(5, Types.VARCHAR);
            }
            Pst.executeUpdate();
        //}
    }
}
