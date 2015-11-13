/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.entity;

import java.sql.Timestamp;

/**
 *
 * @author Henrique
 */
public class AsergSurvey {
    
    private int CodSurvey;
    private String DevLogin;
    private String DevName;
    private Timestamp DtSurvey;
    private int BugsResolved;
    private int BugId;
    private String ShortDesc;
    private char Status;
    
    public AsergSurvey(){
        CodSurvey = -1;
        DevLogin = null;
        DtSurvey = null;
        DevName = null;
        BugsResolved = 0;
        BugId = 0;
        ShortDesc = null;
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
     * @return the DevLogin
     */
    public String getDevLogin() {
        return DevLogin;
    }

    /**
     * @param DevLogin the DevLogin to set
     */
    public void setDevLogin(String DevLogin) {
        this.DevLogin = DevLogin;
    }

    /**
     * @return the DtSurvey
     */
    public Timestamp getDtSurvey() {
        return DtSurvey;
    }

    /**
     * @param DtSurvey the DtSurvey to set
     */
    public void setDtSurvey(Timestamp DtSurvey) {
        this.DtSurvey = DtSurvey;
    }

    /**
     * @return the BugsResolved
     */
    public int getBugsResolved() {
        return BugsResolved;
    }

    /**
     * @param BugsResolved the BugsResolved to set
     */
    public void setBugsResolved(int BugsResolved) {
        this.BugsResolved = BugsResolved;
    }

    /**
     * @return the BugId
     */
    public int getBugId() {
        return BugId;
    }

    /**
     * @param BugId the BugId to set
     */
    public void setBugId(int BugId) {
        this.BugId = BugId;
    }

    /**
     * @return the ShortDesc
     */
    public String getShortDesc() {
        return ShortDesc;
    }

    /**
     * @param ShortDesc the ShortDesc to set
     */
    public void setShortDesc(String ShortDesc) {
        this.ShortDesc = ShortDesc;
    }

    /**
     * @return the Status
     */
    public char getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(char Status) {
        this.Status = Status;
    }

    /**
     * @return the DevName
     */
    public String getDevName() {
        return DevName;
    }

    /**
     * @param DevName the DevName to set
     */
    public void setDevName(String DevName) {
        this.DevName = DevName;
    }
    
    
            
}

/**
  
   create table aserg_survey (
   cod_survey int not null auto_increment,
   dev_login varchar(255) not null,
   dev_name varchar(255),
   survey_date datetime not null,
   bugs_resolved int,
   bugid mediumint,
   short_desc varchar(1024),
   status char,

   constraint aserg_survey_pk primary key(cod_survey)
);

alter table aserg_survey convert to character set utf8 collate utf8_general_ci ;
 
 */