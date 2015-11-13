/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.BugComponent;
import bugapp.persistence.entity.BugDescription;
import bugapp.persistence.entity.BugPath;
import bugapp.persistence.entity.BugSimple;
import bugapp.util.DateUtil;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class BugsDAO {
    
    public static ArrayList<Integer> getBugIdsResolvedAround(bugapp.persistence.entity.AsergCommit C, int Min) throws SQLException{
        ArrayList<Integer> lst = new ArrayList<Integer>();
        java.sql.Date DtBegin = bugapp.util.DateUtil.addMinutes(C.getDtCommitAsUtilDate(), -Min);
        java.sql.Date DtEnd = bugapp.util.DateUtil.addMinutes(C.getDtCommitAsUtilDate(), +Min);
        
        String Query = " SELECT DISTINCT bug_id FROM bugs LEFT JOIN aserg_bug_commit USING(bug_id) WHERE cf_last_resolved >= ? AND cf_last_resolved <= ? AND revision IS NULL;";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setTimestamp(1, bugapp.util.DateUtil.toTimestamp(DtBegin));
        Pst.setTimestamp(2, bugapp.util.DateUtil.toTimestamp(DtEnd));
        ResultSet Rs = Pst.executeQuery();
        //System.out.println(Pst.toString());
        while(Rs.next()){
            lst.add( Rs.getInt("bug_id") );
        }
        return lst;
    }
    
    public static ArrayList<Integer> getBugIdsResolvedAround(java.util.Date DtResolved, int Min) throws SQLException{
        ArrayList<Integer> lst = new ArrayList<Integer>();
        java.sql.Date DtBegin = bugapp.util.DateUtil.addMinutes(DtResolved, -Min);
        java.sql.Date DtEnd = bugapp.util.DateUtil.addMinutes(DtResolved, +Min);
        
        String Query = " SELECT DISTINCT bug_id FROM bugs WHERE cf_last_resolved >= ? AND cf_last_resolved <= ? ";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setDate(1, DtBegin);
        Pst.setDate(2, DtEnd);
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            lst.add( Rs.getInt("bug_id") );
        }
        return lst;
    }

    @Deprecated
    public static ArrayList<Integer> getBugIdsResolvedAt(java.sql.Date DtResolved) throws SQLException{
        ArrayList<Integer> lst = new ArrayList<Integer>();
        String Query = " SELECT DISTINCT bug_id FROM bugs WHERE cf_last_resolved = ? ORDER BY bug_id ";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setDate(1, DtResolved);
        ResultSet Rs = Pst.executeQuery();
        while(Rs.next()){
            lst.add( Rs.getInt("bug_id") );
        }
        return lst;
    }
    
    public static ArrayList<BugPath> getBugFilesCreatedInBase(BugPath Base, java.sql.Date ResolvedDt) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug=" SELECT DISTINCT bug_id FROM bugs JOIN aserg_bug_file USING (bug_id) WHERE cf_last_resolved >= ? AND creation_ts <= ? AND ( ";
       
        String strWhere="";
        for(String Path : Base.getPaths()){
            if(strWhere.length()!=0)
                strWhere+=" OR ";
            strWhere+=(" filename = '"+Path.replaceAll("'", "''")+"' ");
        }
        QueryBug += strWhere +" )";
        
        System.out.println(QueryBug);
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        
        PreparedStatement PstBugs = BaseDadosFirefox.getInstancia().prepareStatement(QueryBug);
        PstBugs.setDate(1, Base.getDtCreation());
        PstBugs.setDate(2, ResolvedDt);
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);

        ResultSet RsBug = PstBugs.executeQuery();
        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }

    
    public static ArrayList<BugPath> getBugsFromCoreFirefox(String DtBegin) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug=" SELECT product_id,bug_severity,bug_id,creation_ts FROM bugs WHERE creation_ts >= '"+DtBegin+"' AND (product_id = 1 or product_id = 21) ORDER BY bug_id; ";
        //System.out.println(QueryBug);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            //B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("product_id") );
            B.setShortDesc(RsBug.getString("bug_severity") );

            lst.add(B);
        }
        
        return lst;
    }

    public static ArrayList<BugPath> getBugFilesFromDev(int DevId) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug=" SELECT DISTINCT bug_id,creation_ts,cf_last_resolved,component_id FROM bugs WHERE resolution = 'FIXED' AND assigned_to = "+DevId+" AND creation_ts >= '2009-01-01' ORDER BY cf_last_resolved ";
        //System.out.println(QueryBug);
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( DevId );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }

    public static ArrayList<BugPath> getAllBugFilesFromProduct(int ProdId, String strDateBegin, String... Severities) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to FROM bugs WHERE resolution = 'FIXED' AND product_id = "+ProdId;
        QueryBug+=makeQueryWhere(false, strDateBegin, Severities);
        QueryBug+=" ORDER BY creation_ts; ";
        System.out.println(QueryBug);
        
        String QueryFiles=" SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename; ";
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getBugFilesOpennedWhenForRep(BugPath Main) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to,product_id,priority,bug_severity,version FROM bugs WHERE resolution = 'FIXED' AND creation_ts <= ? AND cf_last_resolved >= ? ORDER BY creation_ts; ";
        PreparedStatement PstBugs = BaseDadosFirefox.getInstancia().prepareStatement(QueryBug);
        PstBugs.setDate(1, Main.getDtClose());
        PstBugs.setDate(2, Main.getDtCreation());
        ResultSet RsBug = PstBugs.executeQuery();
        //System.out.println(RsBug.getStatement().toString());
        
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );
            
            B.setProductId( RsBug.getInt("product_id") );
            B.setPriority( RsBug.getString("priority") );
            B.setVersion( RsBug.getString("version") );
            B.setType( RsBug.getString("bug_severity").equals("enhancement")?"E":"B" );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getAllBugFilesForRep(String strDateBegin, boolean GetFullDesc, String... Severities) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to,product_id,priority,bug_severity,version FROM bugs WHERE resolution = 'FIXED' ";
        QueryBug+=makeQueryWhere(false, strDateBegin, Severities);
        QueryBug+=" ORDER BY creation_ts; ";
        System.out.println(QueryBug);
        
        String QueryFullDesc = "SELECT bug_id,thetext FROM longdescs WHERE bug_id = ? LIMIT 0,1";
        PreparedStatement PstFullDesc = BaseDadosFirefox.getInstancia().prepareStatement(QueryFullDesc);
        
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );
            
            B.setProductId( RsBug.getInt("product_id") );
            B.setPriority( RsBug.getString("priority") );
            B.setVersion( RsBug.getString("version") );
            B.setType( RsBug.getString("bug_severity").equals("enhancement")?"E":"B" );

            if(GetFullDesc){
                StringBuilder stbFullDesc = new StringBuilder();
                PstFullDesc.setInt(1, B.getBugId());
                ResultSet RsFullDesc = PstFullDesc.executeQuery();
                while(RsFullDesc.next()){
                    stbFullDesc.append( RsFullDesc.getString("thetext") );
                }
                B.setFullDesc(stbFullDesc.toString());
            }
            
            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }

            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getAllBugFiles(String strDateBegin, String... Severities) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to FROM bugs WHERE resolution = 'FIXED' ";
        QueryBug+=makeQueryWhere(false, strDateBegin, Severities);
        QueryBug+=" ORDER BY creation_ts; ";
        System.out.println(QueryBug);
        
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getOnlyMappedBugFiles(String strDateBegin, String... Severities) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to FROM bugs JOIN aserg_bug_file_count USING(bug_id) WHERE file_count > 0";
        QueryBug+= makeQueryWhere(false, strDateBegin, Severities);
        QueryBug+=" ORDER BY creation_ts; ";
        
        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
        
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getOnlyMappedBugFilesForREP(String strDateBegin, boolean GetFullDescription, String... Severities) throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        
        String QueryBug="SELECT DISTINCT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to,product_id,priority,bug_severity,version FROM bugs JOIN aserg_bug_file_count USING(bug_id) WHERE file_count > 0 AND resolution = 'FIXED' ";
        QueryBug+= makeQueryWhere(false, strDateBegin, Severities);
        QueryBug+=" ORDER BY creation_ts; ";
        
        String QueryFullDesc = "SELECT bug_id,thetext FROM longdescs WHERE bug_id = ? LIMIT 0,1";
        PreparedStatement PstFullDesc = BaseDadosFirefox.getInstancia().prepareStatement(QueryFullDesc);

        String QueryFiles="SELECT DISTINCT filename FROM aserg_bug_file WHERE bug_id = ? ORDER BY filename;";
       
        PreparedStatement PstFiles = BaseDadosFirefox.getInstancia().prepareStatement(QueryFiles);
        ResultSet RsBug = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);

        while(RsBug.next()){
            BugPath B = new BugPath();
            B.setBugId( RsBug.getInt("bug_id") );
            B.setShortDesc( RsBug.getString("short_desc") );
            B.setDtCreation( RsBug.getDate("creation_ts") );
            B.setDtClose( RsBug.getDate("cf_last_resolved") );
            B.setComponent( RsBug.getInt("component_id") );
            B.setDeveloper( RsBug.getInt("assigned_to") );

            B.setProductId( RsBug.getInt("product_id") );
            B.setPriority( RsBug.getString("priority") );
            B.setVersion( RsBug.getString("version") );
            B.setType( RsBug.getString("bug_severity").equals("enhancement")?"E":"B" );
            if(GetFullDescription){
                StringBuilder stbFullDesc = new StringBuilder();
                PstFullDesc.setInt(1, B.getBugId());
                ResultSet RsFullDesc = PstFullDesc.executeQuery();
                while(RsFullDesc.next()){
                    stbFullDesc.append( RsFullDesc.getString("thetext") );
                }
                B.setFullDesc(stbFullDesc.toString());
            }

            PstFiles.setInt(1, B.getBugId() );
            ResultSet RsFiles = PstFiles.executeQuery();
            while(RsFiles.next()){
                B.addPath( RsFiles.getString("filename") );
            }
            lst.add(B);
        }
        
        return lst;
    }
    
    public static ArrayList<BugPath> getBugPathFromGeneral() throws SQLException {
        ArrayList<BugPath> lst = new ArrayList<BugPath>();
        String QueryBug="SELECT bug_id,short_desc,creation_ts,cf_last_resolved,component_id,assigned_to FROM bugs WHERE component_id = 1526 AND resolution = 'FIXED' AND bug_severity = 'normal' ORDER BY creation_ts";
        String QueryPath="SELECT DISTINCT path FROM aserg_commit_path join aserg_bug_commit using(revision) WHERE bug_id = ? ORDER BY path";

        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(QueryPath);
        
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(QueryBug);
        while(Rs.next()){
            BugPath B = new BugPath();
            B.setBugId( Rs.getInt("bug_id") );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setDtCreation( Rs.getDate("creation_ts") );
            B.setDtClose( Rs.getDate("cf_last_resolved") );
            B.setComponent( Rs.getInt("component_id") );
            B.setDeveloper( Rs.getInt("assigned_to") );
            
            Pst.setInt(1, B.getBugId() );
            ResultSet RsPath = Pst.executeQuery();
            while(RsPath.next()){
                B.addPath( RsPath.getString("path") );
            }
            lst.add(B);
        }
        return lst;
    }
    
    public static ArrayList<Integer> getBugIdFromGeneral() throws SQLException {
        ArrayList<Integer> lst = new ArrayList<Integer>();
        String Query="SELECT bug_id FROM bugs WHERE component_id = 1526";
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            int BugId = Rs.getInt("bug_id");
            lst.add(BugId);
        }        
        return lst;
    }
    
    public static ArrayList<BugDescription> getBugDesc(java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugDescription> lst = new ArrayList<BugDescription>();
        String Query="select bug_id,component_id,assigned_to,short_desc,cf_last_resolved from bugs where cf_last_resolved >= ? and cf_last_resolved <= ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setDate(1, bugapp.util.DateUtil.toSqlDate(DtInit));
        Pst.setDate(2, bugapp.util.DateUtil.toSqlDate(DtEnd));

        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugDescription B = new BugDescription();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setAssignedTo( Rs.getInt("assigned_to") );
            
            lst.add(B);            
        }       
        
        return lst;
    }

    public static ArrayList<BugDescription> getBugDescFromFirefox(java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugDescription> lst = new ArrayList<BugDescription>();
        String Query="select bug_id,component_id,assigned_to,short_desc,cf_last_resolved from bugs where cf_last_resolved >= ? and cf_last_resolved <= ? and product_id = ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setDate(1, bugapp.util.DateUtil.toSqlDate(DtInit));
        Pst.setDate(2, bugapp.util.DateUtil.toSqlDate(DtEnd));
        Pst.setInt(3, 21); //Firefox product_id = 21

        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugDescription B = new BugDescription();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setAssignedTo( Rs.getInt("assigned_to") );
            
            lst.add(B);            
        }       
        
        return lst;
    }
    
    public static ArrayList<BugDescription> getDevelBugDesc(int UserId, java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugDescription> lst = new ArrayList<BugDescription>();
        String Query="select bug_id,component_id,short_desc,cf_last_resolved from bugs where assigned_to = ? and creation_ts >= ? and cf_last_resolved <= ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, UserId);
        Pst.setDate(2, bugapp.util.DateUtil.toSqlDate(DtInit));
        Pst.setDate(3, bugapp.util.DateUtil.toSqlDate(DtEnd));
        
        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugDescription B = new BugDescription();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setAssignedTo(UserId);
            
            lst.add(B);            
        }       
        
        return lst;
    }    
    
    public static ArrayList<BugDescription> getDevelComponentBugs(BugDescription D, java.sql.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugDescription> lst = new ArrayList<BugDescription>();
        String Query="select bug_id,short_desc,cf_last_resolved from bugs where bug_id <> ? and assigned_to = ? and component_id = ? and cf_last_resolved >= ? and cf_last_resolved <= ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, D.getId());
        Pst.setInt(2, D.getAssignedTo());
        Pst.setInt(3, D.getComponentId());
        Pst.setDate(4, DtInit);
        Pst.setDate(5, bugapp.util.DateUtil.toSqlDate(DtEnd));

        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugDescription B = new BugDescription();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( D.getComponentId() );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setAssignedTo( D.getAssignedTo() );
            
            lst.add(B);            
        }       
        
        return lst;
    }
    
    public static ArrayList<BugComponent> getDevelBugComponents(int UserId, java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugComponent> lst = new ArrayList<BugComponent>();
        //String Query="select bug_id,creation_ts,cf_last_resolved,component_id from bugs where assigned_to = ? and creation_ts >= '2009-09-01' and cf_last_resolved <= '2012-09-31' order by bug_id";
        String Query="select b.bug_id,b.creation_ts,b.cf_last_resolved,b.component_id,c.name as component_name,b.product_id,p.name as product_name from bugs b join components c on b.component_id = c.id join products p on b.product_id = p.id where b.assigned_to = ? and b.creation_ts >= ? and b.cf_last_resolved <= ? order by b.cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, UserId);
        Pst.setDate(2, bugapp.util.DateUtil.toSqlDate(DtInit));
        Pst.setDate(3, bugapp.util.DateUtil.toSqlDate(DtEnd));
        
        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugComponent B=new BugComponent();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setCreation( Rs.getDate("creation_ts") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setComponentName( Rs.getString("component_name"));
            B.setProductId( Rs.getInt("product_id"));
            B.setProductName( Rs.getString("product_name") );            
            lst.add(B);
        }
        return lst;
    }
    
    public static ArrayList<BugSimple> getDevelBugs(int UserId, java.util.Date DtInit, java.util.Date DtEnd) throws SQLException{
        ArrayList<BugSimple> lst = new ArrayList<BugSimple>();
        String Query="select bug_id,creation_ts,cf_last_resolved,component_id from bugs where assigned_to = ? and creation_ts >= ? and cf_last_resolved <= ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, UserId);
        Pst.setDate(2, bugapp.util.DateUtil.toSqlDate(DtInit));
        Pst.setDate(3, bugapp.util.DateUtil.toSqlDate(DtEnd));
        
        ResultSet Rs=Pst.executeQuery();
        while(Rs.next()){
            BugSimple B=new BugSimple();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setCreation( Rs.getDate("creation_ts") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            lst.add(B);
        }
        return lst;
    }

    public static ArrayList<BugSimple> getDevelBugsUntilDate(int UserId, Date DtInicio, int Days) throws SQLException{
        ArrayList<BugSimple> lst = new ArrayList<BugSimple>();

        // DtFim = DtInicio + Days;
        Date DtFim = DateUtil.addDate(DtInicio, Days); 
//        Calendar C=Calendar.getInstance();
//        C.setTime(DtInicio);
//        C.add(Calendar.DAY_OF_MONTH, Days);
//        DtFim = new java.sql.Date(C.getTimeInMillis());
        
        
        String Query="select bug_id,creation_ts,cf_last_resolved,component_id from bugs where assigned_to = ? and creation_ts >= ? and cf_last_resolved <= ? order by cf_last_resolved";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        Pst.setInt(1, UserId);
        Pst.setDate(2, DtInicio);
        Pst.setDate(3, DtFim);
        ResultSet Rs=Pst.executeQuery();

        while(Rs.next()){
            BugSimple B=new BugSimple();
            B.setId( Rs.getInt("bug_id") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setCreation( Rs.getDate("creation_ts") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            lst.add(B);
        }
        return lst;
    }
    
    protected static String makeQueryWhere(boolean PutWhere, String strBeginDate, String... Severities){
        StringBuilder stb = new StringBuilder();

        if(strBeginDate!=null){
            stb.append(" creation_ts >= '");
            stb.append(strBeginDate);
            stb.append("' ");
        }

        for(String S : Severities){
            if(stb.length()>0){
                stb.append(" AND ");
            }
            stb.append(" bug_severity <> '");
            stb.append(S);
            stb.append("' ");
        }
        
        if(stb.length()>0){
            if(PutWhere){
                stb.insert(0, " WHERE ");
            }
            else{
                stb.insert(0, " AND ");
            }
        }
        
        return stb.toString();
    }
    
}
