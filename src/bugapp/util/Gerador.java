/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.util;

/**
 *
 * @author Henrique
 */
public class Gerador {

    public static int[] randomIndex(int MaxIndex, float SamplePercent){
        int[] seed = new int[MaxIndex];
        java.util.Random R=new java.util.Random(System.currentTimeMillis());
        int aux, swap;
        int Max = (int)(seed.length*SamplePercent+1);
        
        for(int i=0; i<seed.length; i++){
            seed[i]=i;
        }
        
        for(int i=0; i<Max; i++){
            swap = i + R.nextInt(MaxIndex-i);
            
            aux = seed[i];
            seed[i] = seed[swap];
            seed[swap] = aux;
        }
        
        return seed;
    }
    
}
