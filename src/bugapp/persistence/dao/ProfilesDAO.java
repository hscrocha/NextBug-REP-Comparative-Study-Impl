/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.Profile;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class ProfilesDAO {
    
    public static ArrayList<Profile> getProfilesAssignedToBugs(java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<Profile> lst=new ArrayList<Profile>();
        String Query="select distinct userid, login_name from profiles p join bugs b on p.userid = b.assigned_to where p.userid <> 1 and b.resolution = 'FIXED' and b.creation_ts >= '2009-09-01' and b.cf_last_resolved <= '2012-09-31'";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            Profile P = new Profile();
            P.setId( Rs.getInt("userid") );
            P.setLogin( Rs.getString("login_name") );
            lst.add(P);
        }        
        return lst;
    }
    
    public static ArrayList<Profile> getProfilesAssignedToBugsWithCount(java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<Profile> lst=new ArrayList<Profile>();
        //String Query="select distinct userid, login_name from profiles p join bugs b on p.userid = b.assigned_to where p.userid <> 1 and b.resolution = 'FIXED' and b.creation_ts >= '2009-09-01' and b.cf_last_resolved <= '2012-09-31'";
        String Query="select userid, login_name, count(*) as qt_bugs from profiles p join bugs b on p.userid = b.assigned_to where userid <> 1 and b.resolution = 'FIXED' and b.creation_ts >= '2009-09-01' and b.cf_last_resolved <= '2012-09-31' group by userid, login_name having count(*) > 1 order by qt_bugs desc";

        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            Profile P = new Profile();
            P.setId( Rs.getInt("userid") );
            P.setLogin( Rs.getString("login_name") );
            P.setQuantidadeBugs( Rs.getInt("qt_bugs") );
            lst.add(P);
        }        
        return lst;
    }

    public static ArrayList<Profile> getAssignedToBugsWithCount() throws SQLException{
        ArrayList<Profile> lst=new ArrayList<Profile>();
        //String Query="select distinct userid, login_name from profiles p join bugs b on p.userid = b.assigned_to where p.userid <> 1 and b.resolution = 'FIXED' and b.creation_ts >= '2009-09-01' and b.cf_last_resolved <= '2012-09-31'";
        String Query="SELECT assigned_to, count(bug_id) as qt_bugs FROM bugs WHERE creation_ts > '2009-01-01' AND resolution = 'FIXED' AND assigned_to <> 1 GROUP BY assigned_to HAVING count(bug_id) > 19 ORDER BY 2 DESC";

        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            Profile P = new Profile();
            P.setId( Rs.getInt("assigned_to") );
            P.setQuantidadeBugs( Rs.getInt("qt_bugs") );
            lst.add(P);
        }        
        return lst;
    }
    
    
}
