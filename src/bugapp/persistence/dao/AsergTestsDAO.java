/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.dao;

import bugapp.ir.SparseClusterCenter;
import bugapp.ir.SparseDataVector;
import bugapp.persistence.BaseDadosFirefox;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class AsergTestsDAO {
    
    public static void removeClusterKmedoidTest() throws SQLException {
        String Query="delete from aserg_cluster_medoid;";
        BaseDadosFirefox.getInstancia().executeUpdate(Query);        
    }
    
    public static void saveClusterKmedoidTest(ArrayList<SparseClusterCenter> lstClusters) throws SQLException{
        removeClusterKmedoidTest();
        
        String Query="insert into aserg_cluster_medoid(bug_id,cluster,dist) values (?,?,?);";
        PreparedStatement Pst = BaseDadosFirefox.getInstancia().prepareStatement(Query);
        
        int ClusterNumber=0;
        float Dist = 0;
        for(SparseClusterCenter C : lstClusters){
            ClusterNumber++;
            for(SparseDataVector Data : C.getData()){
                Pst.setInt(1, Data.getBugId());
                Pst.setInt(2, ClusterNumber);
                Dist = C.distance(Data, SparseClusterCenter.COSINE_DISTANTE);
                if(!Float.isNaN(Dist)){
                    Pst.setFloat(3, Dist);
                }
                else{
                    Pst.setNull(3, java.sql.Types.FLOAT);
                }
                Pst.executeUpdate();
            }
        }
        
    }
    
}
