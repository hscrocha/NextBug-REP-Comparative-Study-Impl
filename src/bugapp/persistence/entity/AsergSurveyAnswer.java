/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class AsergSurveyAnswer {
    private int CodSurvey;
    private String Q1;
    private String Q1Detail;
    private String Q2;
    private String Q2Detail;
    private String Q3;
    private String Q4;
    
    public AsergSurveyAnswer(){
        
    }            

    /**
     * @return the CodSurvey
     */
    public int getCodSurvey() {
        return CodSurvey;
    }

    /**
     * @param CodSurvey the CodSurvey to set
     */
    public void setCodSurvey(int CodSurvey) {
        this.CodSurvey = CodSurvey;
    }

    /**
     * @return the Q1
     */
    public String getQ1() {
        return Q1;
    }

    /**
     * @param Q1 the Q1 to set
     */
    public void setQ1(String Q1) {
        this.Q1 = Q1;
    }

    /**
     * @return the Q2
     */
    public String getQ2() {
        return Q2;
    }

    /**
     * @param Q2 the Q2 to set
     */
    public void setQ2(String Q2) {
        this.Q2 = Q2;
    }

    /**
     * @return the Q3
     */
    public String getQ3() {
        return Q3;
    }

    /**
     * @param Q3 the Q3 to set
     */
    public void setQ3(String Q3) {
        this.Q3 = Q3;
    }

    /**
     * @return the Q4
     */
    public String getQ4() {
        return Q4;
    }

    /**
     * @param Q4 the Q4 to set
     */
    public void setQ4(String Q4) {
        this.Q4 = Q4;
    }

    /**
     * @return the Q1Detail
     */
    public String getQ1Detail() {
        return Q1Detail;
    }

    /**
     * @param Q1Detail the Q1Detail to set
     */
    public void setQ1Detail(String Q1Detail) {
        this.Q1Detail = Q1Detail;
    }

    /**
     * @return the Q2Detail
     */
    public String getQ2Detail() {
        return Q2Detail;
    }

    /**
     * @param Q2Detail the Q2Detail to set
     */
    public void setQ2Detail(String Q2Detail) {
        this.Q2Detail = Q2Detail;
    }
    
    
    
    
}

/*

create table aserg_survey_answer(
   cod_survey int not null,
   q1 char(5),
   q2 char,
   q3 varchar(512),
   q4 varchar(1024),
   q1detail varchar(512),
   q2detail varchar(512),

   constraint aserg_survey_answer_pk primary key(cod_survey),
   constraint aserg_survey_answer_fk1 foreign key(cod_survey) references aserg_survey(cod_survey)
);

*/

