/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.AsergBugLifeCycle;
import bugapp.persistence.entity.BugActivity;
import bugapp.persistence.entity.BugResolution;
import bugapp.persistence.entity.StatisticEntity;
import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author Henrique
 */
public class BugActivityDAO {
    
    public static ArrayList<BugActivity> getBugs(String strDtBegin) throws SQLException{
        ArrayList<BugActivity> lst = new ArrayList<>();
        String Query="SELECT bug_id,bug_status,creation_ts,cf_last_resolved FROM bugs WHERE creation_ts >= '"+strDtBegin+"' ";
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            BugActivity B = new BugActivity();
            B.setBugId( Rs.getInt("bug_id") );
            B.setStatus( Rs.getString("bug_status") );
            B.setDtCreation( Rs.getTimestamp("creation_ts") );
            B.setDtLastResolved( Rs.getTimestamp("cf_last_resolved") );
            getActivity(B);
            lst.add(B);
        }                
        return lst;
    }
    
    private static void getActivity(BugActivity B) throws SQLException{
        String Query=" SELECT * FROM bugs_activity WHERE bug_id = ? AND fieldid = 29 ORDER BY bug_when ";
        PreparedStatement Pst=BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, B.getBugId());
        ResultSet Rs = Pst.executeQuery(); 
        while(Rs.next()){
            String Removed = Rs.getString("removed");
            String Added = Rs.getString("added");
            Timestamp Date = Rs.getTimestamp("bug_when");
            
            B.setEndDate(Date, Removed);
            B.setBeginDate(Date, Added);
            
            B.addFluxo(Removed);
            B.addFluxo(Added);
            
            if(Removed.equals("UNCONFIRMED") && B.getBegUnconfirmed()==null){
                B.setBegUnconfirmed(B.getDtCreation());
            }
            if(Removed.equals("NEW") && B.getBegConfirmed()==null){
                B.setBegUnconfirmed(B.getDtCreation());
            }
        }
        
    }
    
    public static void saveNewActivity(ArrayList<BugActivity> lst) throws SQLException{
        String Query="INSERT INTO aserg_bug_activity(bug_id,bug_status,index_status,creation_ts,cf_last_resolved,fluxo,begUnconfirmed, endUnconfirmed, begConfirmed, endConfirmed, begAssigned, endAssigned, begResolved, endResolved, begVerified, endVerified) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        BaseDadosFirefox.getInstancia().getConnection().setAutoCommit(false);
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        for(BugActivity Ba : lst){
            Pst.setInt(1, Ba.getBugId());
            Pst.setString(2, Ba.getStatus());
            Pst.setInt(3, Ba.getStatusIndex() );
            Pst.setTimestamp(4, Ba.getDtCreation());
            Pst.setTimestamp(5, Ba.getDtLastResolved());
            Pst.setString(6, Ba.getFluxo());
            
            Pst.setTimestamp(7, Ba.getBegUnconfirmed());
            Pst.setTimestamp(8, Ba.getEndUnconfirmed());
            Pst.setTimestamp(9, Ba.getBegConfirmed());
            Pst.setTimestamp(10, Ba.getEndConfirmed());
            Pst.setTimestamp(11, Ba.getBegAssigned());
            Pst.setTimestamp(12, Ba.getEndAssigned());
            Pst.setTimestamp(13, Ba.getBegResolved());
            Pst.setTimestamp(14, Ba.getEndResolved());
            Pst.setTimestamp(15, Ba.getBegVerified());
            Pst.setTimestamp(16, Ba.getEndVerified());
            
            Pst.executeUpdate();
        }
        BaseDadosFirefox.getInstancia().getConnection().commit();
        BaseDadosFirefox.getInstancia().getConnection().setAutoCommit(true);
    }
    
    public static StatisticEntity getAsergBugActivityCount(String BeginStatus, String EndStatus, 
            String DtBegin, String DtEnd, String Where, BugResolution... Resolution) throws SQLException{
        StatisticEntity Stats = new StatisticEntity();
        if(Resolution==null) Resolution=new BugResolution[]{BugResolution.NULL};
        
        String Query1, Query2, Query3;
        String DateDiff = dateDiffStatus(BeginStatus);

        Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? "+getBugResolutionWhereClause(Resolution);
        Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND "+DateDiff+" >= 0 "+getBugResolutionWhereClause(Resolution);
        Query3=" SELECT abs("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? "+getBugResolutionWhereClause(Resolution);
        
//        if(Resolution[0]==BugResolution.NULL){
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND (resolution IS NULL OR resolution = '') ";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND (resolution IS NULL OR resolution = '')  AND "+DateDiff+" >= 0 ";
//        }else if(Resolution[0]==BugResolution.ALL){
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? ";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b WHERE fluxo LIKE ? AND "+DateDiff+" >= 0 ";
//        }else{
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND resolution = ? ";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND resolution = ?  AND "+DateDiff+" >= 0 ";
//        }
        
        if(DtBegin!=null && DtBegin.length()>0){
            Query1+=" AND a.creation_ts >= '"+DtBegin+"' ";
            Query2+=" AND a.creation_ts >= '"+DtBegin+"' ";
            Query3+=" AND a.creation_ts >= '"+DtBegin+"' ";
        }
        
        if(DtEnd!=null && DtEnd.length()>0){
            Query1+=" AND a.creation_ts <= '"+DtEnd+"' ";
            Query2+=" AND a.creation_ts <= '"+DtEnd+"' ";
            Query3+=" AND a.creation_ts <= '"+DtEnd+"' ";
        }
        
        if(Where!=null && Where.length()>0){
            Query1+=Where;
            Query2+=Where;
            Query3+=Where;
        }
        
        Query3+=" ORDER BY 1 ";

        String Fluxo="%"+ Integer.toString(AsergBugLifeCycle.statusIndex(BeginStatus))
                +Integer.toString(AsergBugLifeCycle.statusIndex(EndStatus))+"%";
        //System.out.println(Query1);
        PreparedStatement Pst1 = BaseDadosFirefox.getInstancia().prepareStatement(Query1);
        PreparedStatement Pst2 = BaseDadosFirefox.getInstancia().prepareStatement(Query2);
        PreparedStatement Pst3 = BaseDadosFirefox.getInstancia().prepareStatement(Query3);
        Pst1.setString(1, Fluxo);
        Pst2.setString(1, Fluxo);
        Pst3.setString(1, Fluxo);
//        if(Resolution.length()>0 && !Resolution.equalsIgnoreCase("All")){
//            Pst1.setString(2, Resolution);
//            Pst2.setString(2, Resolution);
//        }
        ResultSet Rs1 = Pst1.executeQuery(); //COUNT query
        if(Rs1.next()){
            Stats.setCount( Rs1.getInt(1) );
        }
        
        ResultSet Rs2 = Pst2.executeQuery(); //Avg,Dev,Min,Max Query
        if(Rs2.next()){
            Stats.setAverage( Rs2.getFloat(1) );
            Stats.setDeviation( Rs2.getFloat(2) );
            Stats.setMin( Rs2.getFloat(3) );
            Stats.setMax( Rs2.getFloat(4) );
        }
        
        //System.out.println(Query3);
        int Count = Stats.getCount();
        if (Count > 0) {
            ResultSet Rs3 = Pst3.executeQuery();
            

            Rs3.absolute((Count / 4) + 1);
            Stats.setQuartil1(Rs3.getFloat(1));

            Rs3.absolute((Count / 2) + 1);
            Stats.setQuartil2(Rs3.getFloat(1));

            Rs3.absolute((3 * Count / 4) + 1);
            Stats.setQuartil3(Rs3.getFloat(1));
        }
        return Stats;
    }
    
    public static StatisticEntity getAsergBugActivityCount(String Status, String DtBegin, String DtEnd, String Where, BugResolution... Resolution) throws SQLException{
        //int Count=0;
        StatisticEntity Stats = new StatisticEntity();

        if(Resolution==null) Resolution=new BugResolution[]{BugResolution.NULL};
        
        String Query1, Query2, Query3;
        String DateDiff = finalDatesetDiffStatus(Status, DtEnd);
        Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ? "+getBugResolutionWhereClause(Resolution);
        Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ?  AND "+DateDiff+" >= 0 "+getBugResolutionWhereClause(Resolution);
        Query3=" SELECT abs("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ? "+getBugResolutionWhereClause(Resolution);

//        if(Resolution.length()==0){
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ? AND (resolution IS NULL OR resolution = '')";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND (resolution IS NULL OR resolution = '') AND "+DateDiff+" >= 0 ";
//        }else if(Resolution.equalsIgnoreCase("All")){
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b WHERE index_status = ? ";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b WHERE index_status = ?  AND "+DateDiff+" >= 0 ";
//        }else{
//            Query1=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ? AND resolution = ? ";
//            Query2=" SELECT avg("+DateDiff+"),stddev("+DateDiff+"),min("+DateDiff+"),max("+DateDiff+") FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE index_status = ? AND resolution = ?  AND "+DateDiff+" >= 0 ";
//        }

        if(DtBegin!=null && DtBegin.length()>0){
            Query1+=" AND a.creation_ts >= '"+DtBegin+"' ";
            Query2+=" AND a.creation_ts >= '"+DtBegin+"' ";
            Query3+=" AND a.creation_ts >= '"+DtBegin+"' ";
        }
        if(DtEnd!=null && DtEnd.length()>0){
            Query1+=" AND a.creation_ts <= '"+DtEnd+"' ";
            Query2+=" AND a.creation_ts <= '"+DtEnd+"' ";
            Query3+=" AND a.creation_ts <= '"+DtEnd+"' ";
        }
        
        if(Where!=null && Where.length()>0){
            Query1+=Where;
            Query2+=Where;
            Query3+=Where;
        }
        
        Query3+=" ORDER BY 1 ";

        PreparedStatement Pst1 = BaseDadosFirefox.getInstancia().prepareStatement(Query1);
        PreparedStatement Pst2 = BaseDadosFirefox.getInstancia().prepareStatement(Query2);
        PreparedStatement Pst3 = BaseDadosFirefox.getInstancia().prepareStatement(Query3);
        Pst1.setInt(1, BugActivity.statusIndex(Status));
        Pst2.setInt(1, BugActivity.statusIndex(Status));
        Pst3.setInt(1, BugActivity.statusIndex(Status));
//        if(Resolution.length()>0 && !Resolution.equalsIgnoreCase("All")){
//            Pst1.setString(2, Resolution);
//            Pst2.setString(2, Resolution);
//        }
        ResultSet Rs1 = Pst1.executeQuery(); //COUNT query
        if(Rs1.next()){
            Stats.setCount( Rs1.getInt(1) );
        }
        
        ResultSet Rs2 = Pst2.executeQuery(); //Avg,Dev,Min,Max Query
        if(Rs2.next()){
            Stats.setAverage( Rs2.getFloat(1) );
            Stats.setDeviation( Rs2.getFloat(2) );
            Stats.setMin( Rs2.getFloat(3) );
            Stats.setMax( Rs2.getFloat(4) );
        }
        
        //System.out.println(Query3);
        int Count = Stats.getCount();
        if (Count > 0) {
            ResultSet Rs3 = Pst3.executeQuery();

            Rs3.absolute((Count / 4) + 1);
            Stats.setQuartil1(Rs3.getFloat(1));

            Rs3.absolute((Count / 2) + 1);
            Stats.setQuartil2(Rs3.getFloat(1));

            Rs3.absolute((3 * Count / 4) + 1);
            Stats.setQuartil3(Rs3.getFloat(1));
        }
        
        return Stats;
    }
    
    public static float getAsergBugActivityTotal(String Status, String Resolution) throws SQLException{
        float Count=0;
        String Query=null;
        
        if(Resolution==null) Resolution="";
        
        if(Resolution.length()==0){
            Query=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE (fluxo LIKE ? OR index_status = ?) AND (resolution IS NULL OR resolution = '') ";
        }else if(Resolution.equalsIgnoreCase("ALL")){
            Query=" SELECT count(*) FROM aserg_bug_activity WHERE fluxo LIKE ? OR index_status = ? ";
        }else{
            Query=" SELECT count(*) FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE (fluxo LIKE ? OR index_status = ?) AND resolution = ? ";
        }
        String Fluxo="%"+Integer.toString(AsergBugLifeCycle.statusIndex(Status))+"%";
        
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, Fluxo);
        Pst.setInt(2, AsergBugLifeCycle.statusIndex(Status));
        if(Resolution.length()>0 && !Resolution.equalsIgnoreCase("All")){
            Pst.setString(3, Resolution);
        }
        ResultSet Rs = Pst.executeQuery();
        if(Rs.next()){
            Count = Rs.getFloat(1);
        }
        return Count;
    }
    
    private static String dateDiffStatus(String Status){
        return dateDiffStatus( BugActivity.statusIndex(Status) );
    }
    
    private static String dateDiffStatus(int StatusIndex){
        switch(StatusIndex){
            case 0: return "DATEDIFF(endUnconfirmed,begUnconfirmed)";
            case 1: return "DATEDIFF(endConfirmed,begConfirmed)";
            case 2: return "DATEDIFF(endAssigned,begAssigned)";
            case 3: return "DATEDIFF(endResolved,begResolved)";
            default: return "DATEDIFF(endVerified,begVerified)";
        }
    }

    private static String finalDatesetDiffStatus(String Status, String DtEnd){
        return finalDatesetDiffStatus( BugActivity.statusIndex(Status), DtEnd );
    }
    
    private static String finalDatesetDiffStatus(int StatusIndex, String DtEnd){
        String Dt = DtEnd!=null ? DtEnd : "2012-10-31";
        
        switch(StatusIndex){
            case 0: return "DATEDIFF('"+Dt+"',begUnconfirmed)";
            case 1: return "DATEDIFF('"+Dt+"',begConfirmed)";
            case 2: return "DATEDIFF('"+Dt+"',begAssigned)";
            case 3: return "DATEDIFF('"+Dt+"',begResolved)";
            default: return "DATEDIFF('"+Dt+"',begVerified)";
        }
    }
    
    private static String getBugResolutionWhereClause(BugResolution... Resolution){
        if(Resolution.length==0 || Resolution[0]==BugResolution.NULL){
            return " AND (resolution IS NULL OR resolution = '') ";
        }
        else if(Resolution[0]==BugResolution.ALL){
            return "";
        }
        
        StringBuilder stb=new StringBuilder();
        stb.append(" AND (");
        for(int i=0; i<Resolution.length; i++){
            if(i!=0) stb.append(" OR ");
            stb.append("resolution = '");
            stb.append(Resolution[i].toString());
            stb.append("'");
        }
        stb.append(") ");
        return stb.toString();
    }
    
    
    public static ArrayList<BugActivity> getAsergBugActivityWeka(String BeginStatus, String EndStatus, String Resolution) throws SQLException{
        ArrayList<BugActivity> lst = new ArrayList<>();
        if(Resolution==null) Resolution="";
        
        String Query;
        String DateDiff = dateDiffStatus(BeginStatus);

        if(Resolution.length()==0){
            Query=" SELECT bug_id,"+DateDiff+" FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND (resolution IS NULL OR resolution = '')";
        }else if(Resolution.equalsIgnoreCase("All")){
            Query=" SELECT bug_id,"+DateDiff+" FROM aserg_bug_activity WHERE fluxo LIKE ? ";
        }else{
            Query=" SELECT bug_id,"+DateDiff+" FROM aserg_bug_activity a join bugs b USING(bug_id) WHERE fluxo LIKE ? AND resolution = ? ";
        }

        String Fluxo="%"+ Integer.toString(AsergBugLifeCycle.statusIndex(BeginStatus))
                +Integer.toString(AsergBugLifeCycle.statusIndex(EndStatus))+"%";
        
        PreparedStatement Pst1 = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst1.setString(1, Fluxo);
        if(Resolution.length()>0 && !Resolution.equalsIgnoreCase("All")){
            Pst1.setString(2, Resolution);
        }
        
        
        ResultSet Rs1 = Pst1.executeQuery(); 
        while(Rs1.next()){
            BugActivity B = new BugActivity();
            B.setBugId( Rs1.getInt("bug_id") );
            B.setDateDiffStatus(Rs1.getInt(2));
            lst.add(B);
        }
                
        return lst;
    }
    
    
   
}
