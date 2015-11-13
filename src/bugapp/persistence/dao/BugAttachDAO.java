/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.BugAttach;
import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author Henrique
 */
public class BugAttachDAO {
    
    private PreparedStatement SavedPreparedStatment = null;
    private PreparedStatement Saved2 = null;
    
    public void prepareSaveBugPath() throws SQLException {
        BaseDadosFirefox.getInstancia().getConnection().setAutoCommit(false);
        String Query="INSERT INTO aserg_bug_file(bug_id,filename) values (?,?);";
        SavedPreparedStatment = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        
        String Query2="INSERT INTO aserg_bug_file_count(bug_id,file_count) values (?,?);";
        Saved2 = BaseDadosFirefox.getInstancia().prepareStatement(Query2);
    }
    
    public void saveBugPath(int BugId, ArrayList<String> lstFileNames) throws SQLException{
        Saved2.setInt(1, BugId);
        Saved2.setInt(2, lstFileNames.size());
        Saved2.executeUpdate();
        
        SavedPreparedStatment.setInt(1, BugId);
        for(String FileName : lstFileNames){
            SavedPreparedStatment.setString(2, FileName);
            SavedPreparedStatment.executeUpdate();
        }
        BaseDadosFirefox.getInstancia().getConnection().commit();
    }
    
    public void closeSaveBugPath() throws SQLException{
        SavedPreparedStatment = null;
        Saved2 = null;
        BaseDadosFirefox.getInstancia().getConnection().setAutoCommit(true);
    }

    public static ArrayList<BugAttach> getBugAttachs() throws SQLException{
        ArrayList<BugAttach> lst = new ArrayList<BugAttach>();
        
        //int LastBug = getMaxBugIdFromBugFiles(); //671092;
        //String Query="SELECT bug_id,attach_id from attachments a join bugs b using(bug_id) left join aserg_bug_file ab using(bug_id) where ab.filename is null and b.creation_ts >= '2009-01-01' and resolution = 'FIXED' and bug_id >= "+LastBug+" order by bug_id;";
        String Query="SELECT bug_id,attach_id from attachments a join bugs b using(bug_id) left join aserg_bug_file_count ab using(bug_id) where ab.file_count is null and b.creation_ts >= '2009-01-01' and resolution = 'FIXED' order by bug_id;";
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        
        BugAttach B = null;
        int BugId = 0;
        while(Rs.next()){
            BugId = Rs.getInt("bug_id");
            
            if(B == null){
                B=new BugAttach();
                B.setBugId( BugId );
            }
            else if(B.getBugId() != BugId){ //mudou de bug                
                lst.add(B); //adiciona antigo na lista               
                
                B=new BugAttach(); //cria novo objeto para novo bug
                B.setBugId( BugId ); 
            }
            
            B.addAttachId( Rs.getInt("attach_id") );
        }
        
        if(B!=null){ //adiciona o ultimo bug na 
            lst.add(B);
        }
        
        return lst;
    }
    
    protected static int getMaxBugIdFromBugFiles() throws SQLException{
        String Query="SELECT max(bug_id) FROM aserg_bug_file; ";
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        Rs.next();
        return Rs.getInt(1);
    }
    
}
