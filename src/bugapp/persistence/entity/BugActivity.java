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
public class BugActivity {
    
    protected int BugId;
    protected String Status;
    protected java.sql.Timestamp DtCreation;
    protected java.sql.Timestamp DtLastResolved;
    
    protected String Fluxo; //U - C - I - R - V
    
    protected java.sql.Timestamp BeginDate[] = new Timestamp[5];
    protected java.sql.Timestamp EndDate[] = new Timestamp[5];
    protected int[] DaysDiff = new int[5];
    protected int DateDiffStatus;
    
    public BugActivity(){
        for(int i=0; i<BeginDate.length; i++){
            BeginDate[i]=null;
            EndDate[i]=null;
        }
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
     * @return the Status
     */
    public String getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(String Status) {
        this.Status = Status;
    }
    
    public int getStatusIndex(){
        return statusIndex(Status);
    }

    /**
     * @return the DtCreation
     */
    public java.sql.Timestamp getDtCreation() {
        return DtCreation;
    }

    /**
     * @param DtCreation the DtCreation to set
     */
    public void setDtCreation(java.sql.Timestamp DtCreation) {
        this.DtCreation = DtCreation;
    }

    /**
     * @return the DtLastResolved
     */
    public java.sql.Timestamp getDtLastResolved() {
        return DtLastResolved;
    }

    /**
     * @param DtLastResolved the DtLastResolved to set
     */
    public void setDtLastResolved(java.sql.Timestamp DtLastResolved) {
        this.DtLastResolved = DtLastResolved;
    }

    /**
     * @return the Fluxo
     */
    public String getFluxo() {
        return Fluxo;
    }

    /**
     * @param Fluxo the Fluxo to set
     */
    public void setFluxo(String Fluxo) {
        this.Fluxo = Fluxo;
    }

    /**
     * @return the BegUnconfirmed
     */
    public java.sql.Timestamp getBegUnconfirmed() {
        return BeginDate[0];
    }

    /**
     * @param BegUnconfirmed the BegUnconfirmed to set
     */
    public void setBegUnconfirmed(java.sql.Timestamp BegUnconfirmed) {
        this.BeginDate[0] = BegUnconfirmed;
    }

    /**
     * @return the EndUnconfirmed
     */
    public java.sql.Timestamp getEndUnconfirmed() {
        return EndDate[0];
    }

    /**
     * @param EndUnconfirmed the EndUnconfirmed to set
     */
    public void setEndUnconfirmed(java.sql.Timestamp EndUnconfirmed) {
        this.EndDate[0] = EndUnconfirmed;
    }

    /**
     * @return the BegConfirmed
     */
    public java.sql.Timestamp getBegConfirmed() {
        return BeginDate[1];
    }

    /**
     * @param BegConfirmed the BegConfirmed to set
     */
    public void setBegConfirmed(java.sql.Timestamp BegConfirmed) {
        this.BeginDate[1] = BegConfirmed;
    }

    /**
     * @return the EndConfirmed
     */
    public java.sql.Timestamp getEndConfirmed() {
        return EndDate[1];
    }

    /**
     * @param EndConfirmed the EndConfirmed to set
     */
    public void setEndConfirmed(java.sql.Timestamp EndConfirmed) {
        this.EndDate[1] = EndConfirmed;
    }

    /**
     * @return the BegAssigned
     */
    public java.sql.Timestamp getBegAssigned() {
        return BeginDate[2];
    }

    /**
     * @param BegAssigned the BegAssigned to set
     */
    public void setBegAssigned(java.sql.Timestamp BegAssigned) {
        this.BeginDate[2] = BegAssigned;
    }

    /**
     * @return the EndAssigned
     */
    public java.sql.Timestamp getEndAssigned() {
        return EndDate[2];
    }

    /**
     * @param EndAssigned the EndAssigned to set
     */
    public void setEndAssigned(java.sql.Timestamp EndAssigned) {
        this.EndDate[2] = EndAssigned;
    }

    /**
     * @return the BegResolved
     */
    public java.sql.Timestamp getBegResolved() {
        return BeginDate[3];
    }

    /**
     * @param BegResolved the BegResolved to set
     */
    public void setBegResolved(java.sql.Timestamp BegResolved) {
        this.BeginDate[3] = BegResolved;
    }

    /**
     * @return the EndResolved
     */
    public java.sql.Timestamp getEndResolved() {
        return EndDate[3];
    }

    /**
     * @param EndResolved the EndResolved to set
     */
    public void setEndResolved(java.sql.Timestamp EndResolved) {
        this.EndDate[3] = EndResolved;
    }

    /**
     * @return the BegVerified
     */
    public java.sql.Timestamp getBegVerified() {
        return BeginDate[4];
    }

    /**
     * @param BegVerified the BegVerified to set
     */
    public void setBegVerified(java.sql.Timestamp BegVerified) {
        this.BeginDate[4] = BegVerified;
    }

    /**
     * @return the EndVerified
     */
    public java.sql.Timestamp getEndVerified() {
        return EndDate[4];
    }

    /**
     * @param EndVerified the EndVerified to set
     */
    public void setEndVerified(java.sql.Timestamp EndVerified) {
        this.EndDate[4] = EndVerified;
    }
    
    public void setBeginDate(java.sql.Timestamp BeginDate, String Status){
        this.BeginDate[ statusIndex(Status) ] = BeginDate;
    }
    
    public java.sql.Timestamp getBeginDate(String Status){
        return this.BeginDate[ statusIndex(Status) ];
    }

    public void setEndDate(java.sql.Timestamp EndDate, String Status){
        this.EndDate[ statusIndex(Status) ] = EndDate;
    }
    
    public java.sql.Timestamp getEndnDate(String Status){
        return this.EndDate[ statusIndex(Status) ];
    }
    
    public void setDateDiff(int Diff, String Status){
        this.DaysDiff[ statusIndex(Status) ] = Diff;
    }
    
    public int getDateDiff(String Status){
        return this.DaysDiff[ statusIndex(Status) ];
    }
    
    public static int statusIndex(String Status){
        if(Status.equalsIgnoreCase("UNCONFIRMED")){
            return 0;
        }else if(Status.equalsIgnoreCase("NEW") || Status.equalsIgnoreCase("CONFIRMED") || Status.equalsIgnoreCase("REOPEN")){
            return 1;
        }else if(Status.equalsIgnoreCase("ASSIGNED") || Status.equalsIgnoreCase("IN_PROGRESS")){
            return 2;
        }else if(Status.equalsIgnoreCase("RESOLVED")){
            return 3;
        }else{ //if(Status.equalsIgnoreCase("VERIFIED") || Status.equalsIgnoreCase("CLOSED")){
            return 4;
        }
    }
    
    public static int statusIndex(char Status){
        switch(Status){
            case 'a':
            case 'A': return 0;
            case 'b':
            case 'B': return 1;
            case 'c':
            case 'C': return 2;
            case 'd':
            case 'D': return 3;
            default: return 4;
        }
    }
    
    public static String indexToStatus(int i){
        switch(i){
            case 0: return "Unconfirmed";
            case 1: return "Confirmed";
            case 2: return "Assigned";
            case 3: return "Resolved";
            default: return "Verified";
        }
    }
    
    
    
    public void addFluxo(String Status){
        String S = Integer.toString(statusIndex(Status));
        if(Fluxo==null){
            Fluxo = S;
        } else if(Fluxo.length()<29){ //if(!Fluxo.contains(S)){
            Fluxo=Fluxo+S;
        }
    }

    /**
     * @return the DateDiffStatus
     */
    public int getDateDiffStatus() {
        return DateDiffStatus;
    }

    /**
     * @param DateDiffStatus the DateDiffStatus to set
     */
    public void setDateDiffStatus(int DateDiffStatus) {
        this.DateDiffStatus = DateDiffStatus;
    }
        
}

/**
create table aserg_bug_activity (
   bug_id mediumint not null,
   bug_status varchar(20),
   index_status int,
   creation_ts datetime,
   cf_last_resolved datetime,
   fluxo varchar(20),

   begUnconfirmed datetime,
   endUnconfirmed datetime,
   begConfirmed datetime,
   endConfirmed datetime,
   begAssigned datetime,
   endAssigned datetime,
   begResolved datetime,
   endResolved datetime,
   begVerified datetime,
   endVerified datetime,
   constraint aserg_bug_activity_pk primary key(bug_id),
   constraint aserg_bug_activity_fk1 foreign key(bug_id) references bugs(bug_id)
);
 */