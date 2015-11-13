/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

/**
 *
 * @author Henrique
 */
public class BucketMedoid extends Bucket {
    private BucketItem Center = null;
    
    public BucketMedoid(){
        super();
    }
    
    @Override
    public void add(BucketItem B){
        super.add(B);
        calcCenter();
    }

    /**
     * @return the Center
     */
    @Override
    public BucketItem getCenter() {
        return Center;
    }

    /**
     * @param Center the Center to set
     */
    @Override
    public void setCenter(BucketItem Center) {
        this.Center = Center;
    }
    
    @Override
    public float distance(BucketItem I){
        return this.Center.distance(I);
    }
    
    public void calcCenter(){
        float MinDist = 1;
        float Dist;
        
        for(BucketItem B : lstData){
            Dist = 0;
            for(BucketItem C : lstData){
                Dist += B.distance(C);
            }
            Dist /= (float) (lstData.size() - 1);
            if(Dist < MinDist){
                MinDist = Dist;
                Center = B;
            }
        }
    }
    
}
