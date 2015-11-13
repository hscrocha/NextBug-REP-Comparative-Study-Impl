/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.AsergCommit;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergCommitDAO {

    public static ArrayList<AsergCommit> getCommits(String DtBegin, String DtEnd) throws SQLException{
        ArrayList<AsergCommit> lst=new ArrayList<AsergCommit>();
        String Query="SELECT revision,dt_commit FROM aserg_commit WHERE dt_commit >= '"+DtBegin+"' AND dt_commit <= '"+DtEnd+"' ORDER BY dt_commit";
        ResultSet Rs=BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            AsergCommit C=new AsergCommit();
            C.setRevision( Rs.getInt("revision") );
            //C.setMessage( Rs.getString("message") );
            C.setDtCommit( Rs.getTimestamp("dt_commit"));
            lst.add(C);
        }        
        return lst;
    }
    
    public static ArrayList<AsergCommit> getCommits() throws SQLException{
        ArrayList<AsergCommit> lst=new ArrayList<AsergCommit>();
        String Query="SELECT revision,message,dt_commit FROM aserg_commit ORDER BY dt_commit";
        ResultSet Rs=BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            AsergCommit C=new AsergCommit();
            C.setRevision( Rs.getInt("revision") );
            C.setMessage( Rs.getString("message") );
            C.setDtCommit( Rs.getDate("dt_commit"));
            lst.add(C);
        }        
        return lst;
    }
    
    public static ArrayList<AsergCommit> getCommitsShort() throws SQLException{
        ArrayList<AsergCommit> lst=new ArrayList<AsergCommit>();
        String Query="SELECT revision,message FROM aserg_commit ORDER BY dt_commit";
        ResultSet Rs=BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            AsergCommit C=new AsergCommit();
            C.setRevision( Rs.getInt("revision") );
            C.setMessage( Rs.getString("message") );
            lst.add(C);
        }        
        return lst;
    }
    
    public static ArrayList<Integer> getRevisionCommitsWithBug(int BugId) throws SQLException {
        ArrayList<Integer> lst = new ArrayList<Integer>();
        String Query="SELECT revision FROM aserg_commit WHERE message LIKE '%"+BugId+"%' ORDER BY dt_commit";
        ResultSet Rs=BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            int Rev = Rs.getInt("revision");
            lst.add(Rev);            
        }        
        return lst;
    }
}
