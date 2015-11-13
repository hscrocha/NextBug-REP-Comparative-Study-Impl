/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import java.util.Comparator;

/**
 *
 * @author Henrique
 */
public class OverlapComparator implements Comparator<RecEntry> {

    @Override
    public int compare(RecEntry o1, RecEntry o2) {
        if(o1 == null && o2 == null){
            return 0;
        }
        else if(o1 == null && o2 !=null) {
            return -1;
        }
        else if(o1 != null && o2 ==null){
            return 1;
        }
        
        int d1 = (int) (o1.getOverlap()*1000);
        int d2 = (int) (o2.getOverlap()*1000);
        
        if(d1>=0 && d2>=0){
            return d2-d1;
        }
        else if(d1<0){
            return -1;
        }
        else {//if(d2<0){
            return 1;
        }
    }
    
}
