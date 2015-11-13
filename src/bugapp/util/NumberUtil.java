/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.util;

/**
 *
 * @author Henrique
 */
public class NumberUtil {
    
    public static String floatToBr(float f){
       String str = String.format("%.4f", f);
       return str.replace('.', ',');
    }
    
    public static int floatToPercentRound(float f){
        return Math.round(f*100);
    }
    
    public static String floatToDiaPercentString(float f){
        if(f>=0.006){
            return Integer.toString(floatToPercentRound(f))+"%";
        }
        else{
            if(f==0) return "0%";
            else return "&lt;1%";
        }
    }
    
    public static String floatToStrRound(float f){
       return String.format("%d", Math.round(f));
    }
}
