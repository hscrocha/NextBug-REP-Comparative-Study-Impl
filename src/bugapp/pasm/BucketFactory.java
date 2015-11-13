/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

/**
 *
 * @author Henrique
 */
public class BucketFactory {
    
    public static void setTimeFrame(int TimeFrame){
        Bucket.WINDOW_OF_DAYS_TO_CLOSE_BUCKET = TimeFrame;
    }
    
    public static Bucket create(BucketItem I){
        return createMedoid(I);
        //return createHierarchical(I);
    }
    
    private static Bucket createMedoid(BucketItem I){
        Bucket B = new BucketMedoid();
        B.add(I);
        B.setCenter(I);
        B.setDtBegin(I.getBugData().getDtCreation());
        return B;
    }
    
    private static Bucket createHierarchical(BucketItem I){
        Bucket B = new BucketHierarchical();
        B.add(I);
        B.setDtBegin(I.getBugData().getDtCreation());
        return B;
    }
    
}
