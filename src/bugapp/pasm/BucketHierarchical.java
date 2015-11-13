/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

/**
 *
 * @author Henrique
 */
public class BucketHierarchical extends Bucket{

    public static final byte MAX_DIST = 1;
    public static final byte MIN_DIST = 2;
    public static final byte AVG_DIST = 3;
    
    public static byte DistanceMeasure = AVG_DIST;
    
    @Override
    public float distance(BucketItem I) {
        switch(DistanceMeasure){
            case MAX_DIST:
                return maxDistance(I);
                
            case MIN_DIST:
                return minDistance(I);
                
            default:
                return avgDistance(I);
        }
    }
    
    public float maxDistance(BucketItem I){
        float Max = 0;
        float Dist;
        
        for(BucketItem Ai : lstData){
            Dist = I.distance(Ai);
            if(Dist > Max){
                Max = Dist;
            }
        }
        return Max;
    }

    public float minDistance(BucketItem I){
        float Min = 1;
        float Dist;
        
        for(BucketItem Ai : lstData){
            Dist = I.distance(Ai);
            if(Dist < Min){
                Min = Dist;
            }
        }
        return Min;
    }

    public float avgDistance(BucketItem I){
        float Avg = 0;
        
        for(BucketItem Ai : lstData){
            Avg += I.distance(Ai);
        }
        Avg /= ((float)lstData.size());
        return Avg;
    }
}
