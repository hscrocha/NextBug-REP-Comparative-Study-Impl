/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import au.com.bytecode.opencsv.CSVWriter;
import bugapp.persistence.BaseDadosFirefox;
import bugapp.persistence.entity.BugCluster;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergClusterDAO {
    
    public static ArrayList<BugCluster> getAsergClusterData(int Cluster) throws SQLException{
        ArrayList<BugCluster> lst = new ArrayList<BugCluster>();
        String Query="select dist,bug_id,assigned_to,short_desc,component_id,creation_ts,cf_last_resolved,resolution from bugs join aserg_cluster_medoid using(bug_id) where cluster = "+Cluster;
        ResultSet Rs = BaseDadosFirefox.getInstancia().executeQuery(Query);
        while(Rs.next()){
            BugCluster B=new BugCluster();
            B.setId( Rs.getInt("bug_id") );
            B.setDist( Rs.getFloat("dist") );
            B.setAssignedId( Rs.getInt("assigned_to") );
            B.setShortDesc( Rs.getString("short_desc") );
            B.setComponentId( Rs.getInt("component_id") );
            B.setCreation( Rs.getDate("creation_ts") );
            B.setLastResolved( Rs.getDate("cf_last_resolved") );
            B.setResolution( Rs.getString("resolution") );
            lst.add(B);
        }        
        return lst;
    }
    
    public static void saveSampleAsergClusterDataToFile(String FileName,  ArrayList<BugCluster> lst) throws IOException{
        int seed[] = bugapp.util.Gerador.randomIndex(lst.size(), 0.1f);
        int Max = lst.size()/10; //10% sample
        
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, ';',CSVWriter.NO_QUOTE_CHARACTER);
        String[] Line = new String[8];
        
        Line[0]="Dist";
        Line[1]="Short Description";
        Line[2]="Last Resolved";
        Line[3]="Creation";
        Line[4]="Resolution";
        Line[5]="Assigned To";
        Line[6]="ComponentId";
        Line[7]="BugId";
        cwrOutput.writeNext(Line);
        
        for(int i=0; i<Max; i++){
            BugCluster B = lst.get(seed[i]);
            Line[0]= Float.toString(B.getDist()).replace('.', ',');
            Line[1]=B.getShortDesc();
            Line[2]=B.getLastResolved().toString();
            Line[3]=B.getCreation().toString();
            Line[4]=B.getResolution();
            Line[5]=Integer.toString( B.getAssignedId() );
            Line[6]=Integer.toString( B.getComponentId() );
            Line[7]=Integer.toString( B.getId() );
            
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }
    
}
