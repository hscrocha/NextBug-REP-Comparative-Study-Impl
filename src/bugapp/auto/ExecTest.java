/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.auto;

import bugapp.pasm.BucketItem;
import bugapp.pasm.PasmAdhoc;
import bugapp.pasm.PasmFactory;
import bugapp.persistence.entity.BugPath;

/**
 *
 * @author Henrique
 */
public class ExecTest {
    
    public static void main(String[] args){
        try{
            cosineSimilarityTest();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public static void cosineSimilarityTest() throws Exception{
        
        PasmFactory Pm = new PasmAdhoc();
        
        BugPath BpMain = new BugPath();
        BpMain.setShortDesc("Can't disable cache when open new cache backend");
        
        BucketItem Main = Pm.createBucketItem(BpMain);
        
        BugPath[] V = new BugPath[4];
        for(int i=0; i<V.length; i++){
            V[i] = new BugPath();
        }
        V[0].setShortDesc("HTTP cache v2: make nsHttpChannel properly react to FILE_NOT_FOUND from the cache");
        V[1].setShortDesc("cache preferences need to be updated");
        V[2].setShortDesc("Need a way to check expiration/modification status of a cache entry");
        V[3].setShortDesc("<object> contents confused in cache");
        
        for (BugPath Bpi : V) {
            BucketItem Bi = Pm.createBucketItem(Bpi);
            System.out.printf(" %f %s \n",1- Main.getSumUnigramData().cosineDistance( Bi.getSumUnigramData()), Bpi.getShortDesc() );
            //System.out.printf(" %f %s \n", 1-Main.distance(Bi), Bpi.getShortDesc());
        }
        
    }
    
}
