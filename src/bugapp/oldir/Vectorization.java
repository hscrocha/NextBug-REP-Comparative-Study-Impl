/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.oldir;

import brcluster.ClusterDataInterface;
import bugapp.persistence.entity.BugDescription;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Henrique
 */
public class Vectorization {
    
    public static ArrayList<ClusterDataInterface> transformFeatures(ArrayList<BugDescription> lstBugs){
        ArrayList<ClusterDataInterface> lstFeatures=new ArrayList<ClusterDataInterface>();
        for(BugDescription B : lstBugs){
            lstFeatures.add( makeFeatureVector(B) );
        }        
        return lstFeatures;
    }
    
    public static FeatureVector makeFeatureVector(BugDescription B){
        FeatureVector V = new FeatureVector();
        StringTokenizer stk = new StringTokenizer(B.getShortDesc());
        String Word;
        while(stk.hasMoreTokens()){
            Word=stk.nextToken();
            Word=steamWord(Word);
            //Eliminar Stop-words
            if(!isStopWord(Word)){
                V.addFeatureOrWeight(Word);
            }
        }
        return V;
    }
    
    public static BooleanFeatureVector makeBooleanVector(BugDescription B){
        BooleanFeatureVector V=new BooleanFeatureVector();
        
        StringTokenizer stk = new StringTokenizer(B.getShortDesc());
        String Word;
        while(stk.hasMoreTokens()){
            Word=stk.nextToken();
            Word=steamWord(Word);
            //Eliminar Stop-words
            if(!isStopWord(Word) && !V.hasFeature(Word)){
                V.addFeature(Word.toLowerCase());
            }
        }
        return V;
    }
    
    /**
     * Verifica se é Stop-word, gambiarra mudar para um método de verdade
     * @param Word
     * @return 
     */
    public static boolean isStopWord(String Word){
        return Word.length()<=2;
    }
    
    /**
     * Contrai a palavra para seu radical
     * @param Word
     * @return 
     */
    public static String steamWord(String Word){
        return Word.toLowerCase();
    }
    
    public static double similarityBoolean(BooleanFeatureVector Q, BugDescription D){
        double d=0;

        StringTokenizer stk = new StringTokenizer(D.getShortDesc());
        BooleanFeatureVector Vd = new BooleanFeatureVector();
        String Word;
        while(stk.hasMoreTokens()){
            Word=stk.nextToken();
            Word=steamWord(Word);
            //Eliminar Stop-words
            if(!isStopWord(Word) && !Vd.hasFeature(Word)){
                Vd.addFeature(Word);
                if(Q.hasFeature(Word))
                    d+=1;
            }
        }
        
        return d/(double)Q.getNumberOfFeatures();
    }
}
