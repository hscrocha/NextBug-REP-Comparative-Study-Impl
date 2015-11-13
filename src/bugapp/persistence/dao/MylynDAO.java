/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosMylyn;
import bugapp.persistence.entity.BugPath;
import bugapp.persistence.entity.Profile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class MylynDAO {
    
    public static ArrayList<BugPath> getAllCommitBugFiles(String strDateBegin) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bugid,description,creation,lastactivity,assigneduser,c.comp_id "
                +" FROM bugs b JOIN components c ON b.component = c.comp_name "
                +" WHERE creation >= '"+strDateBegin+"' AND resolution = 'FIXED' "
                +" ORDER BY creation; ";
        System.out.println(QueryBug);
        
        //String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        String QueryFiles="SELECT DISTINCT file FROM bugs b LEFT JOIN commits c ON b.id = c.bugid "
                +" JOIN commit_file_map cfm on c.id = cfm.commitid " 
                +" JOIN files f on cfm.fileid = f.id "
                +" WHERE b.id = ? "
                +" ORDER BY file; ";
        
        PreparedStatement PstFiles = BaseDadosMylyn.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosMylyn.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bugid") );
            B.setShortDesc( RsBug.getString("description") );
            B.setDtCreation( RsBug.getDate("creation") );
            B.setDtClose( RsBug.getDate("lastactivity") );
            B.setComponent( RsBug.getInt("comp_id") );
            B.setDeveloper( RsBug.getInt("assigneduser") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("file") );
            }
            lst.add(B);
        }
        
        return lst;
    }

    public static ArrayList<BugPath> getOnlyCommitBugFiles(String strDateBegin) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT b.bugid,b.description,b.creation,b.lastactivity,b.assigneduser,c.comp_id "
                +" FROM bugs b JOIN components c ON b.component = c.comp_name "
                +" JOIN commits f ON b.id = f.bugid "
                +" WHERE b.creation >= '"+strDateBegin+"' AND b.resolution = 'FIXED' "
                +" ORDER BY b.creation; ";
        System.out.println(QueryBug);
        
        //String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        String QueryFiles="SELECT DISTINCT file FROM bugs b JOIN commits c ON b.id = c.bugid "
                +" JOIN commit_file_map cfm on c.id = cfm.commitid " 
                +" JOIN files f on cfm.fileid = f.id "
                +" WHERE b.bugid = ? "
                +" ORDER BY file; ";
        
        PreparedStatement PstFiles = BaseDadosMylyn.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosMylyn.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bugid") );
            B.setShortDesc( RsBug.getString("description") );
            B.setDtCreation( RsBug.getDate("creation") );
            B.setDtClose( RsBug.getDate("lastactivity") );
            B.setComponent( RsBug.getInt("comp_id") );
            B.setDeveloper( RsBug.getInt("assigneduser") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("file") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    
    public static ArrayList<BugPath> getAllContextBugFiles(String strDateBegin) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bugid,description,creation,lastactivity,assigneduser,c.comp_id "
                +" FROM bugs b JOIN components c ON b.component = c.comp_name "
                +" WHERE creation >= '"+strDateBegin+"' AND resolution = 'FIXED' "
                +" ORDER BY creation; ";
        System.out.println(QueryBug);
        
        //String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        String QueryFiles="SELECT DISTINCT file FROM bugs b LEFT JOIN contexts c ON b.id = c.bugid "
                +" JOIN context_file_map cfm on c.id = cfm.contextid " 
                +" JOIN files f on cfm.fileid = f.id "
                +" WHERE b.bugid = ? "
                +" ORDER BY file; ";
        
        PreparedStatement PstFiles = BaseDadosMylyn.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosMylyn.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bugid") );
            B.setShortDesc( RsBug.getString("description") );
            B.setDtCreation( RsBug.getDate("creation") );
            B.setDtClose( RsBug.getDate("lastactivity") );
            B.setComponent( RsBug.getInt("comp_id") );
            B.setDeveloper( RsBug.getInt("assigneduser") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("file") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getOnlyContextBugFiles(String strDateBegin) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT b.bugid,b.description,b.creation,b.lastactivity,b.assigneduser,c.comp_id "
                +" FROM bugs b JOIN components c ON b.component = c.comp_name "
                +" JOIN contexts f ON b.id = f.bugid "
                +" WHERE b.creation >= '"+strDateBegin+"' AND b.resolution = 'FIXED' "
                +" ORDER BY b.creation; ";
        System.out.println(QueryBug);
        
        //String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        String QueryFiles="SELECT DISTINCT file FROM bugs b JOIN contexts c ON b.id = c.bugid "
                +" JOIN context_file_map cfm on c.id = cfm.contextid " 
                +" JOIN files f on cfm.fileid = f.id "
                +" WHERE b.bugid = ? "
                +" ORDER BY file; ";
        
        //sSystem.out.println(QueryFiles);
        PreparedStatement PstFiles = BaseDadosMylyn.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosMylyn.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bugid") );
            B.setShortDesc( RsBug.getString("description") );
            B.setDtCreation( RsBug.getDate("creation") );
            B.setDtClose( RsBug.getDate("lastactivity") );
            B.setComponent( RsBug.getInt("comp_id") );
            B.setDeveloper( RsBug.getInt("assigneduser") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("file") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<Profile> getAssignedToBugsWithCount() throws SQLException{
        ArrayList<Profile> lst=new ArrayList<Profile>();
        //String Query="select distinct userid, login_name from profiles p join bugs b on p.userid = b.assigned_to where p.userid <> 1 and b.resolution = 'FIXED' and b.creation_ts >= '2009-09-01' and b.cf_last_resolved <= '2012-09-31'";
        String Query="SELECT assigneduser, count(bugid) as qt_bugs FROM bugs WHERE creation > '2009-01-01' AND resolution = 'FIXED' GROUP BY assigneduser HAVING count(bugid) > 2 ORDER BY 2 DESC";

        PreparedStatement Pst = BaseDadosMylyn.getInstancia().prepareStatement(Query);
        
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            Profile P = new Profile();
            P.setId( Rs.getInt("assigneduser") );
            P.setQuantidadeBugs( Rs.getInt("qt_bugs") );
            lst.add(P);
        }        
        return lst;
    }
    
    public static ArrayList<BugPath> getBugFilesFromDev(int DevId) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug=" SELECT DISTINCT b.bugid,creation,lastactivity,c.comp_id FROM bugs b join components c on b.component = c.comp_name WHERE resolution = 'FIXED' AND assigneduser = "+DevId+" AND creation >= '2009-01-01' ORDER BY lastactivity;";
        //System.out.println(QueryBug);
        //String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        String QueryFiles="SELECT DISTINCT file FROM bugs b JOIN contexts c ON b.id = c.bugid "
                +" JOIN context_file_map cfm on c.id = cfm.contextid " 
                +" JOIN files f on cfm.fileid = f.id "
                +" WHERE b.bugid = ? "
                +" ORDER BY file; ";
        
        PreparedStatement PstFiles = BaseDadosMylyn.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosMylyn.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bugid") );
            B.setDtCreation( RsBug.getDate("creation") );
            B.setDtClose( RsBug.getDate("lastactivity") );
            B.setComponent( RsBug.getInt("comp_id") );
            B.setDeveloper( DevId );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("file") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    
}
