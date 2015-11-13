/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.AsergCommitPath;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergCommitPathDAO {
    
    public static ArrayList<String> getPaths(int BugId) throws SQLException {
        ArrayList<String> lst = new ArrayList<String>();
        String Query="SELECT DISTINCT path FROM aserg_commit_path join aserg_bug_commit using(revision) WHERE bug_id = ? ORDER BY path";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, BugId);
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            lst.add( Rs.getString("path") );
        }        
        return lst;
    }
}
