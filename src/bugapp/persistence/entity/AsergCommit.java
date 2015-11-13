/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergCommit {
    private int Revision;
    private String Author;
    private String strDtCommit;
    private java.util.Date udtDtCommit;
    private String Message;
    private ArrayList<AsergCommitPath> lstPaths = null;
    
    //public static int MaxAuthorLen = 0;
    //public static int MaxMessageLen = 0;
    //public static int MaxPathLen = 0;
    //public static int MaxKindLen = 0;
    
    public AsergCommit(){
        lstPaths=new ArrayList<AsergCommitPath>();
    }
    
    /*
        revision int not null,
   author varchar(100), -- profiles.login_name
   dt_commit datetime,
   message varchar(200),
   bug_id mediumint(9),

     */

    /**
     * @return the Revision
     */
    public int getRevision() {
        return Revision;
    }

    /**
     * @param Revision the Revision to set
     */
    public void setRevision(int Revision) {
        this.Revision = Revision;
    }

    /**
     * @return the Author
     */
    public String getAuthor() {
        return Author;
    }

    /**
     * @param Author the Author to set
     */
    public void setAuthor(String Author) {
        this.Author = Author.trim();
    }

    /**
     * @return the DtCommit
     */
    public String getDtCommitAsString() {
        return strDtCommit;
    }
    
    public java.util.Date getDtCommitAsUtilDate(){
        return udtDtCommit;
    }
    
    public java.sql.Date getDtCommit(){
        return bugapp.util.DateUtil.toSqlDate(udtDtCommit);
    }

    /**
     * @param DtCommit the DtCommit to set
     */
    public void setDtCommit(String DtCommit) {
        this.strDtCommit = DtCommit.trim();
    }
    
    public void setDtCommit(java.util.Date DtCommit){
        this.udtDtCommit = DtCommit;
    }
    
    public void setDtCommit(java.sql.Date DtCommit){
        this.udtDtCommit = bugapp.util.DateUtil.toUtilDate(DtCommit);
    }
    
    /**
     * @return the Message
     */
    public String getMessage() {
        return Message;
    }

    /**
     * @param Message the Message to set
     */
    public void setMessage(String Message) {
        this.Message = Message.replaceAll("'", " ").replaceAll("\n", " ");
        this.Message = this.Message.trim();
    }

    /**
     * @return the Paths
     */
    public ArrayList<AsergCommitPath> getPaths() {
        return lstPaths;
    }
    
    /**
     * 
     */
    public void addPath(AsergCommitPath Path){
        lstPaths.add(Path);
    }
    
    /**
     * 
     */
    public void clearPathData(){
        lstPaths.clear();
    }
    
    public String commitToScriptSQL(){
        StringBuilder stb=new StringBuilder();
        //stb.append("INSERT INTO aserg_commit(revision,author,dt_commit,message) VALUES \n");
        stb.append("(");
        stb.append(Revision);
        stb.append(",'");
        stb.append(Author);
        stb.append("','");
        stb.append(strDtCommit);
        stb.append("','");
        stb.append(Message);
        stb.append("'), \n");
        return stb.toString();
    }
    
    public String pathToScriptSQL(){
        StringBuilder stb=new StringBuilder();
        
        for(AsergCommitPath Path : lstPaths){
            //stb.append("INSERT INTO aserg_commit_path(revision,kind,action,path) VALUES \n");
            stb.append("(");
            stb.append(Revision);
            stb.append(",'");
            stb.append(Path.getType());
            stb.append("','");
            stb.append(Path.getAction());
            stb.append("','");
            stb.append(Path.getPath());
            stb.append("'),\n");
            
            //if(Path.getPath().length() > MaxPathLen) MaxPathLen = Path.getPath().length();
            //if(Path.getType().length() > MaxKindLen) MaxKindLen = Path.getType().length();
        }
        return stb.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder stb = new StringBuilder();
        stb.append("(");
        stb.append(Revision);
        if(strDtCommit!=null){
            stb.append(",");
            stb.append(strDtCommit);
        }else if(udtDtCommit!=null){
            stb.append(",");
            stb.append(udtDtCommit);
        }
        
        if(Author!=null){
            stb.append(",");
            stb.append(Author);
        }
        
        stb.append(")");
        return stb.toString();
    }
}
