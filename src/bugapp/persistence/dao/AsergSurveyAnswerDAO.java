/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.AsergSurveyAnswer;
import java.sql.*;

/**
 *
 * @author Henrique
 */
public class AsergSurveyAnswerDAO {
    
    public static boolean exists(int CodSurvey) throws SQLException {
        String Query=" SELECT cod_survey FROM aserg_survey_answer WHERE cod_survey = ?";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, CodSurvey);
        ResultSet Rs = Pst.executeQuery();
        return Rs.next();
    }
    
    public static void save(AsergSurveyAnswer Answer) throws SQLException{
        if(exists(Answer.getCodSurvey())){
            update(Answer);
        }
        else{
            insert(Answer);
        }
    }
    
    public static void insert(AsergSurveyAnswer Answer) throws SQLException{
        String Query=" INSERT INTO aserg_survey_answer(cod_survey,q1,q2,q3,q4,q1detail,q2detail) values (?,?,?,?,?,?,?);";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, Answer.getCodSurvey());
        Pst.setString(2, Answer.getQ1());
        Pst.setString(3, Answer.getQ2());
        Pst.setString(4, Answer.getQ3());
        Pst.setString(5, Answer.getQ4());
        Pst.setString(6, Answer.getQ1Detail());
        Pst.setString(7, Answer.getQ2Detail());
        Pst.executeUpdate();
    }

    public static void update(AsergSurveyAnswer Answer) throws SQLException{
        String Query=" UPDATE aserg_survey_answer SET q1 = ?, q2 = ?, q3 = ?, q4 = ?, q1detail = ?, q2detail = ? WHERE cod_survey = ?;";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, Answer.getQ1());
        Pst.setString(2, Answer.getQ2());
        Pst.setString(3, Answer.getQ3());
        Pst.setString(4, Answer.getQ4());
        Pst.setString(5, Answer.getQ1Detail());
        Pst.setString(6, Answer.getQ2Detail());
        Pst.setInt(7, Answer.getCodSurvey());
        Pst.executeUpdate();
    }
    
    public static AsergSurveyAnswer get(int CodSurvey) throws SQLException{
        AsergSurveyAnswer Answer = null;
        
        String Query=" SELECT * FROM aserg_survey_answer WHERE cod_survey = ?; ";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, CodSurvey);
        ResultSet Rs = Pst.executeQuery();
        if(Rs.next()){
            Answer = new AsergSurveyAnswer();
            Answer.setCodSurvey(CodSurvey);
            Answer.setQ1( Rs.getString("q1") );
            Answer.setQ2( Rs.getString("q2") );
            Answer.setQ3( Rs.getString("q3") );
            Answer.setQ4( Rs.getString("q4") );
            Answer.setQ1Detail( Rs.getString("q1detail") );
            Answer.setQ2Detail( Rs.getString("q2detail") );
        }
        return Answer;
    }
    
}
