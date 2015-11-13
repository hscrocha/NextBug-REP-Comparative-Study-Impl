/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.pasm;

import java.util.Comparator;

/**
 *
 * @author Henrique
 */
public class BugIdComparator implements Comparator<RecEntry>{

    @Override
    public int compare(RecEntry o1, RecEntry o2) {
        if(o1 == null && o2 == null){
            return 0;
        }
        else if(o1 == null && o2 !=null) {
            return 1;
        }
        else if(o1 != null && o2 ==null){
            return -1;
        }
        
        return o2.getIssueData().getBugId() - o1.getIssueData().getBugId();
    }
    
}
