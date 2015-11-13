/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.BugResolution;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class MiscelaneousDAO {
    
    public static ArrayList<Integer> getResolutionTimeFrimBugs(String DtBegin, String DtEnd, BugResolution Resolution) throws SQLException{
        ArrayList<Integer> lst = new ArrayList<>();
        String Query="select ABS(DATEDIFF(b.cf_last_resolved,b.creation_ts)) from bugs b where b.creation_ts >= '"+DtBegin+"' and b.creation_ts <= '"+DtEnd+"' and resolution = ? ORDER BY 1 ";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setString(1, Resolution.toString());
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            lst.add( Rs.getInt(1) );
        }                
        return lst;
    }
    
}
