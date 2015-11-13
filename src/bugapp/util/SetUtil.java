/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.util;

import java.util.Collection;

/**
 *
 * @author Henrique
 */
public class SetUtil {
    public static float intersection(Collection A, Collection B) {
        float Count = 0;
        for (Object Item : A) {
            if (B.contains(Item)) {
                Count++;
            }
        }
        return Count;
    }
    
    public static float union(Collection A, Collection B){
        return union(intersection(A, B), A, B);
    }
    
    public static float union(float Intersect, Collection A, Collection B){
        return (A.size()+B.size()-Intersect);
    }
        
    public static float min(Collection A, Collection B){
        if(A.size() < B.size()) {
            return A.size();
        }
        else {
            return B.size();
        }
    }

}
