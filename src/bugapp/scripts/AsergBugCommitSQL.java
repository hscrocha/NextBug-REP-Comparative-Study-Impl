/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.scripts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Henrique
 */
public class AsergBugCommitSQL {
    
    protected StringBuilder stbScript;
    
    public AsergBugCommitSQL(){
        this(null);
    }
    
    public AsergBugCommitSQL(String TableSulfix){
        stbScript=new StringBuilder();
        stbScript.append("INSERT INTO aserg_bug_commit");
        if(TableSulfix!=null){
            stbScript.append(TableSulfix);
        }
        stbScript.append("(revision,bug_id) values \n");
        //stbScript.append("INSERT INTO aserg_bug_commit(revision,bug_id) values \n");
    }
    
    public void append(int Revision, ArrayList<Integer> lstBugId){
        for(Integer BugId : lstBugId){       
            stbScript.append("(");
            stbScript.append(Revision);
            stbScript.append(",");
            stbScript.append(BugId);
            stbScript.append("),\n");
        }
    }
    
    public void append(int Revision, HashSet<Integer> hshBugId){
        for(Integer BugId : hshBugId){       
            stbScript.append("(");
            stbScript.append(Revision);
            stbScript.append(",");
            stbScript.append(BugId);
            stbScript.append("),\n");
        }
    }
    
    
    public void append2(int BugId, ArrayList<Integer> lstRevision){
        for(Integer Revision : lstRevision){       
            stbScript.append("(");
            stbScript.append(Revision);
            stbScript.append(",");
            stbScript.append(BugId);
            stbScript.append("),\n");
        }
    }
    
    private void fixLastComma(){
        int len = stbScript.length();
        stbScript.delete(len-2, len);
        stbScript.append(";\n");
    }
    
    public void saveToFile(String FileName) throws IOException {
        fixLastComma();
        
        FileWriter fwr=new FileWriter(FileName);
        fwr.write(stbScript.toString());
        fwr.close();
    }
}
