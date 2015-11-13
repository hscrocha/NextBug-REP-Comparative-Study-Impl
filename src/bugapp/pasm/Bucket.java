/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import bugapp.util.DateUtil;
import bugapp.util.SetUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Henrique
 */
public abstract class Bucket {
    protected static int WINDOW_OF_DAYS_TO_CLOSE_BUCKET = 0;
    
    public static final byte OPEN_STATUS = 0;
    public static final byte CLOSED_STATUS = 1;
    public static int BucketCount = 0;
    
    private int Id;
    private java.sql.Date DtBegin;
    private java.sql.Date DtEnd;
    private byte Status;
    
    protected LinkedList<BucketItem> lstData;
    protected HashMap<String, Float> hsmPaths;
    protected int ItemPathCount = 0;
    
    private int HoursLost = -1;
    private int HoursGain = -1;
    private int EarlyCount = -1;
    //private BucketItem Center = null; //Medoid Bucket

    public Bucket(){
        Id = ++BucketCount;
        Status = OPEN_STATUS;
        lstData = new LinkedList<BucketItem>();
        hsmPaths = new HashMap<String, Float>();
    }
    
    //public abstract Object getCentroid();
    //public abstract float distance(SparseDataVector V);

    /**
     * @return the Id
     */
    public int getId() {
        return Id;
    }

    /**
     * @return the DtStart
     */
    public java.util.Date getDtBegin() {
        return DtBegin;
    }

    /**
     * @param DtBegin the DtBegin to set
     */
    public void setDtBegin(java.sql.Date DtBegin) {
        this.DtBegin = DtBegin;
        
//        Calendar C=Calendar.getInstance();
//        C.setTimeInMillis( DtBegin.getTime() );
//        C.add(Calendar.DAY_OF_MONTH, WINDOW_OF_DAYS_TO_CLOSE_BUCKET);
//        this.DtEnd = new java.sql.Date( C.getTimeInMillis() );
        
        if(WINDOW_OF_DAYS_TO_CLOSE_BUCKET > 0){
            this.DtEnd = DateUtil.addDate(DtBegin, WINDOW_OF_DAYS_TO_CLOSE_BUCKET);
        }
    }

    /**
     * @return the Status
     */
    public byte getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(byte Status) {
        this.Status = Status;
    }

    /**
     * @return the lstData
     */
    public LinkedList<BucketItem> getData() {
        return lstData;
    }

    public void clearAssignedData(){
        lstData.clear();
    }
    
    public void add(BucketItem B){
        lstData.add(B);
        addPaths(B);
    }
    
    protected void addPaths(BucketItem B){
        ArrayList<String> lst = B.getBugData().getPaths();
        if(lst.size() > 0) {
            ItemPathCount++;
            for (String S : lst) {
                if (hsmPaths.containsKey(S)) {
                    float Count = hsmPaths.get(S);
                    Count++;
                    hsmPaths.put(S, Count);
                } 
                else {
                    hsmPaths.put(S, 1f);
                }
            }
        }
    }
    
    /**
     * @return the Center
     */
    public BucketItem getCenter() {
        return null;
    }

    /**
     * @param Center the Center to set
     */
    public void setCenter(BucketItem Center) {
        
    }
    
    public abstract float distance(BucketItem I);

//    public float distance(BucketItem I){
//        return medoidDistance(I);
//    }
//    
//    private float medoidDistance(BucketItem I){
//        return Center.distance(I);
//    }
//    
//    /**
//     * Not yet implemented
//     */
//    @Deprecated
//    private float hierarchicalDistance(BucketItem I){
//        return 0;
//    }

    /**
     * @return the DtEnd
     */
    public java.sql.Date getDtEnd() {
        return DtEnd;
    }
    
    public boolean isAfterTimeFrame(BucketItem I){
        if(WINDOW_OF_DAYS_TO_CLOSE_BUCKET > 0){
            return I.getBugData().getDtCreation().after(DtEnd);
        }
        else{
            //Bucket nunca fecha
            return false;
        }
    }
    
    public static boolean isInfinityBucket(){
        return (WINDOW_OF_DAYS_TO_CLOSE_BUCKET <= 0);
    }
    
    public boolean isUnitary(){
        return lstData.size()<=1;
    }
    
    public float jaccardPaths(){

        if(isUnitary()){
            return 1;
        }
        else if(ItemPathCount<=1){
            return 0;
        }
        else{
            return calcJaccardAvg();
            /*float cj = 0;
            float size = (float)ItemPathCount;
            for(String Path : hsmPaths.keySet()){
                if(hsmPaths.get(Path)>1){
                    cj += hsmPaths.get(Path)/size;
                }
            }
            return cj/hsmPaths.size();*/
        }
    }
    
    /**
     * Métrica de ... 
     */
    public float calcMetricGeneric(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min, dif;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        dif = A.getBugData().getPaths().size() + B.getBugData().getPaths().size() - 2*intersect;
                        
                        parOver = 0;
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    /**
     * Métrica de Binary Intersect = (Formula minha) 
     */
    public float calcBinaryIntersect(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                                                
                        parOver = (intersect>0)?1:0;
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }

    
    
    /**
     * Métrica de Kulczynski = 1/2*[a/(a + b) + a/(a + c)]
     */
    public float calcKulczynski(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min, dif;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        dif = A.getBugData().getPaths().size() + B.getBugData().getPaths().size() - 2*intersect;
                        
                        parOver = intersect / A.getBugData().getPaths().size() + intersect / B.getBugData().getPaths().size();
                        parOver /= 2;
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    /**
     * Métrica de PSC = a^2/[(b + a)(c + a)]
     */
    public float calcPSC(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min, dif;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        dif = A.getBugData().getPaths().size() + B.getBugData().getPaths().size() - 2*intersect;
                        
                        parOver = intersect*intersect/(A.getBugData().getPaths().size()*B.getBugData().getPaths().size());
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    
    /**
     * Métrica de Ochiai = a/[(a + b)(a + c)]^1/2
     */
    public float calcOchiai(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min, dif;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        
                        parOver = (float) Math.sqrt(A.getBugData().getPaths().size()*B.getBugData().getPaths().size());
                        parOver = intersect / parOver;
                        
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    /**
     * Métrica de Sorenson = 2a/(2a + b + c)
     * @return 
     */
    public float calcSorenson(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        
                        parOver = 2*intersect / (intersect + SetUtil.union(intersect, A.getBugData().getPaths(), B.getBugData().getPaths()));
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }

    public float calcMetric1(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min, dif;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        dif = A.getBugData().getPaths().size() + B.getBugData().getPaths().size() - 2*intersect;
                        
                        if(A.getBugData().getPaths().size()<B.getBugData().getPaths().size()){
                            min = (float) A.getBugData().getPaths().size();
                        }
                        else{
                            min = (float) B.getBugData().getPaths().size();
                        }
                        if(dif > 0 )
                            parOver = (2* intersect - dif) / dif;
                        else parOver = 2* intersect;
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    public float calcOverlap2(){
        if(ItemPathCount<=1){
            return 0;
        }
        
        float intersect, min;
        float totOver = 0; //Overlap Total
        float locOver = 0; //Overlap Local
        float parOver = 0; //Overlap entre A e B
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
                        if(A.getBugData().getPaths().size()<B.getBugData().getPaths().size()){
                            min = (float) A.getBugData().getPaths().size();
                        }
                        else{
                            min = (float) B.getBugData().getPaths().size();
                        }
                        parOver = 2* intersect / min;
                        if(parOver>1){
                            parOver = 1;
                        }
                        locOver += parOver;
                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    public float calcOverlap(){
        if(ItemPathCount<=1){ 
            return 0;
        }
        
        //float intersect, min;
        float totOver = 0;
        float locOver = 0;
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locOver=0;
                for (BucketItem B : lstData) {
                    locOver += A.overlap(B);
//                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
//                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
//                        if(A.getBugData().getPaths().size()<B.getBugData().getPaths().size()){
//                            min = (float) A.getBugData().getPaths().size();
//                        }
//                        else{
//                            min = (float) B.getBugData().getPaths().size();
//                        }
//                        locOver += intersect / min;
//                    }
                }
                locOver/=(float)ItemPathCount-1;
                totOver += locOver;
            }
        }
        return totOver/((float)ItemPathCount);
    }
    
    private float calcJaccardAvg(){
        //float intersect, union;
        float totJac = 0;
        float locJac = 0;
        //float count = 0;
        
        for(BucketItem A : lstData){
            if(!A.getBugData().getPaths().isEmpty()) {
                locJac=0;
                for (BucketItem B : lstData) {
                    locJac += A.jaccard(B);
//                    if (A != B && !B.getBugData().getPaths().isEmpty()) {
//                        intersect = SetUtil.intersection(A.getBugData().getPaths(), B.getBugData().getPaths());
//                        union = A.getBugData().getPaths().size() + B.getBugData().getPaths().size() - intersect;
//                        locJac += intersect / union;
//                    }
                }
                locJac/=(float)ItemPathCount-1;
                totJac += locJac;
            }
        }
        return totJac/((float)ItemPathCount);
    }
    
    public float mappedRatio(){
        float ItemCount = lstData.size(); //Total Items in Buckets
        float MappedItems = (float) ItemPathCount; //Number of Items that have mapped Paths
        return MappedItems / ItemCount;
    }
    
    public int getItemPathCount(){
        return ItemPathCount;
    }
    
    public float averageSimilarity(){
        BucketItem C = getCenter();
        float s = 0;
        if(C!=null){
            for(BucketItem I : getData()){
                s+= (1 - C.distance(I));
            }
            s/=((float)getData().size());
        }
        return s;
    }
    
    public String[] toCvsLine(){
        String[] S=new String[0];
        LinkedList<String> lst=new LinkedList<String>();
        lst.add( Integer.toString(Id) );
        lst.add( DtBegin.toString() );
        lst.add( DtEnd.toString() );
        //lst.add( Float.toString(jaccardPaths()).replace('.', ',') );
        lst.add( Float.toString(calcOverlap()).replace('.', ',') );
        lst.add( Float.toString(calcOverlap2()).replace('.', ',') );
        lst.add( Float.toString(mappedRatio()).replace('.', ',') );
        lst.add( Integer.toString(getHoursGain()) );
        lst.add( Integer.toString(getHoursLost()) );
        for(BucketItem I : lstData){
            lst.addAll( I.getBugData().getPaths() );
        }
        return lst.toArray(S);
    }
    
    @Override
    public String toString(){
        StringBuilder stb=new StringBuilder();
        stb.append("=== Bucket #");
        stb.append(Id);
        stb.append(", Sim: ");
        stb.append(averageSimilarity());
        stb.append(", overlap: ");
        stb.append(calcOverlap());
        stb.append(", jaccard: ");
        stb.append(jaccardPaths());
//        stb.append(", Hours(G,L): ");
//        stb.append(getHoursGain());
//        stb.append(",");
//        stb.append(getHoursLost());
        stb.append(", map: ");
        stb.append(mappedRatio());
        if (!Bucket.isInfinityBucket()) {
            stb.append(", Dt[");
            stb.append( DtBegin.toString() );
            stb.append(" - ");
            stb.append( DtEnd.toString() );
            stb.append("]");
        } 
        else {
            AverageBucketMetrics Met[] = calcClosedBugMetricsTop();
            if(Met[0].getSimilarity() > 0){
                stb.append("\n   All");
                stb.append(Met[0].toString());
            }
            if(Met[1].getSimilarity() > 0){
                stb.append("\n   Top1");
                stb.append(Met[1].toString());
            }
        }
        stb.append("\n");
        
        BucketItem C = getCenter();
        for(BucketItem I : lstData){
            stb.append("  ");
            if(C==I){
                stb.append("(C)");
            }
            else{
                stb.append("   ");
            }
            stb.append( I.toString() );
            stb.append("\n");
        }
        return stb.toString();
    }

    
    /**
     * @return the HoursLost
     */
    public int getHoursLost() {
        if(HoursLost<0){
            calcDaysLostGainToBucket();
        }
        return HoursLost;
    }

    /**
     * @return the HoursGain
     */
    public int getHoursGain() {
        if(HoursGain<0){
            calcDaysLostGainToBucket();
        }
        return HoursGain;
    }

    /**
     * @return the DaysLost
     */
    public int getDaysLost() {
        if(HoursLost<0){
            calcDaysLostGainToBucket();
        }
        return HoursLost/60;
    }

    /**
     * @return the DaysGain
     */
    public int getDaysGain() {
        if(HoursGain<0){
            calcDaysLostGainToBucket();
        }
        return HoursGain/60;
    }

    /**
     * @return the EarlyCount
     */
    public int getEarlyCount() {
        if(EarlyCount<0){
            calcDaysLostGainToBucket();
        }
        return EarlyCount;
    }

    
    private void calcDaysLostGainToBucket() {
        int GanhoPerda = 0;
        this.HoursGain = 0;
        this.HoursLost = 0;
        this.EarlyCount = 0;
        for (BucketItem I : lstData) {
            if (I.getBugData().getDtClose() != null) {

                GanhoPerda = DateUtil.hoursDiference(DtEnd, I.getBugData().getDtClose());
                if (GanhoPerda > 0) {
                    HoursGain += GanhoPerda;
                    EarlyCount++;
                } else {
                    HoursLost += (-GanhoPerda);
                }
            }
        }
    }
    
    public AverageBucketMetrics[] calcClosedBugMetricsTop(){
        
        AverageBucketMetrics Met[] = new AverageBucketMetrics[2];
        Met[0]=new AverageBucketMetrics();
        Met[1]=new AverageBucketMetrics();

        float OverlapCount;
        float Count;
        float Comp;
        float Devel;
        float Overlap;
        float Jaccard;
        float Similarity;
        
        float TopCount=0;
        float TopOverlapCount=0;
        float MaxSim = -1;
        BucketItem MaxItem = null;
        
        float AverageDivisor=0;
        float AverageOverlapDivisor = 0;
        
        for(BucketItem Ia : getData()){
            Count=0;
            Devel=0;
            Comp=0;
            Overlap=0;
            Similarity = 0;
            Jaccard = 0;
            OverlapCount = 0;
            for(BucketItem Ib : getData()){
                if(Ia != Ib 
                       && Ia.getBugData().getDtClose().after( Ib.getBugData().getDtCreation() )
                       && Ia.getBugData().getDtClose().before( Ib.getBugData().getDtClose()) ){
                    Count++;
                    Similarity+=(1-Ia.distance(Ib) );
                    if(Similarity>MaxSim){
                        MaxSim = Similarity;
                        MaxItem = Ib;
                    }
                    
                    if(!Ia.getBugData().getPaths().isEmpty() && !Ib.getBugData().getPaths().isEmpty()){                     
                        OverlapCount++;
                        Overlap+= Ia.overlap(Ib);
                        Jaccard+= Ia.jaccard(Ib);
                    }

                    if(Ia.isSameComponent(Ib)){
                        Comp++;
                    }
                    if(Ia.isSameDevel(Ib)){
                        Devel++;
                    }
                }
            }
            
            if (MaxSim >= 0) {
                TopCount++;
                if (Ia.isSameComponent(MaxItem)) {
                    Met[1].Components++;
                }
                if (Ia.isSameDevel(MaxItem)) {
                    Met[1].Developers++;
                }
                Met[1].Similarity += MaxSim;
                if (!Ia.getBugData().getPaths().isEmpty() && !MaxItem.getBugData().getPaths().isEmpty()) {
                    TopOverlapCount++;
                    Met[1].Overlap += Ia.overlap(MaxItem);
                    Met[1].Jaccard += Ia.jaccard(MaxItem);
                }
            }
            
            if(Count>0){
                //Somente se existia mais de 1 issue no bucket quando foi fechado
                AverageDivisor++;
                Met[0].IssueCount+=(Count+1);
                Met[0].Components += (Comp / Count);
                Met[0].Developers += (Devel / Count);
                Met[0].Similarity += (Similarity / Count);
                
                if(OverlapCount > 0){
                    AverageOverlapDivisor++;
                    Met[0].Overlap += (Overlap / OverlapCount);
                    Met[0].Jaccard += (Jaccard / OverlapCount);
                }
            }
        }
        
        if(AverageDivisor > 0) {
            Met[0].IssueCount /= AverageDivisor;
            Met[0].Components /= AverageDivisor;
            Met[0].Developers /= AverageDivisor;
            Met[0].Similarity /= AverageDivisor;
        }
        else{
            Met[0].IssueCount = 1;
            Met[0].Components = -1;
            Met[0].Developers = -1;
            Met[0].Similarity = -1;
        }
        
        if (AverageOverlapDivisor > 0) {
            Met[0].Overlap /= AverageOverlapDivisor;
            Met[0].Jaccard /= AverageOverlapDivisor;
        }
        else{
            Met[0].Overlap = -1;
            Met[0].Jaccard = -1;
        }
        
        if(TopCount>0){
            Met[1].Components/=TopCount;
            Met[1].Developers/=TopCount;
            Met[1].Similarity/=TopCount;
        }
        else{
            Met[1].Components=-1;
            Met[1].Developers=-1;
            Met[1].Similarity=-1;
        }
        
        if(TopOverlapCount>0){
            Met[1].Overlap/=TopOverlapCount;
            Met[1].Jaccard/=TopOverlapCount;
        }
        else{
            Met[1].Overlap=-1;
            Met[1].Jaccard=-1;
        }
        
        return Met;
    }    
    
    
    
    public AverageBucketMetrics calcClosedBugMetrics(){
        
        float OverlapCount;
        float Count;
        float Comp;
        float Devel;
        float Overlap;
        float Jaccard;
        float Similarity;
        
        float AverageDivisor=0;
        float AverageOverlapDivisor = 0;
        float AverageIssues=0;
        float AverageDevel=0;
        float AverageComp=0;
        float AverageOverlap=0;
        float AverageSimilarity = 0;
        float AverageJaccard = 0;
        
        
        for(BucketItem Ia : getData()){
            Count=0;
            Devel=0;
            Comp=0;
            Overlap=0;
            Similarity = 0;
            Jaccard = 0;
            OverlapCount = 0;
            for(BucketItem Ib : getData()){
                if(Ia != Ib 
                       && Ia.getBugData().getDtClose().after( Ib.getBugData().getDtCreation() )
                       && Ia.getBugData().getDtClose().before( Ib.getBugData().getDtClose()) ){
                    Count++;
                    Similarity+=(1-Ia.distance(Ib) );
                    
                    if(!Ia.getBugData().getPaths().isEmpty() && !Ib.getBugData().getPaths().isEmpty()){                     
                        OverlapCount++;
                        Overlap+= Ia.overlap(Ib);
                        Jaccard+= Ia.jaccard(Ib);
                    }

                    if(Ia.isSameComponent(Ib)){
                        Comp++;
                    }
                    if(Ia.isSameDevel(Ib)){
                        Devel++;
                    }
                }
            }
            
            if(Count>0){
                //Somente se existia mais de 1 issue no bucket quando foi fechado
                AverageDivisor++;
                AverageIssues +=(Count+1);
                AverageComp += (Comp / Count);
                AverageDevel += (Devel / Count);
                AverageSimilarity += (Similarity / Count);
                
                if(OverlapCount > 0){
                    AverageOverlapDivisor++;
                    AverageOverlap += (Overlap / OverlapCount);
                    AverageJaccard += (Jaccard / OverlapCount);
                }
            }
        }
        
        if (AverageDivisor > 0) {
            AverageIssues /= AverageDivisor;
            AverageComp /= AverageDivisor;
            AverageDevel /= AverageDivisor;
            AverageSimilarity /= AverageDivisor;
        }
        else{
            AverageIssues = 1;
            AverageComp = -1;
            AverageDevel = -1;
            AverageSimilarity = -1;
        }
        
        if (AverageOverlapDivisor > 0) {
            AverageOverlap /= AverageOverlapDivisor;
            AverageJaccard /= AverageOverlapDivisor;
        }
        else{
            AverageOverlap = -1;
            AverageJaccard = -1;
        }
        
        AverageBucketMetrics Met = new AverageBucketMetrics();
        Met.setIssueCount(AverageIssues);
        Met.setComponents(AverageComp);
        Met.setDevelopers(AverageDevel);
        Met.setOverlap(AverageOverlap);
        Met.setJaccard(AverageJaccard);
        Met.setSimilarity(AverageSimilarity);
        
        return Met;
    }    

    public AverageBucketMetrics calcClosedBugMetricsTop1(){
        
        float Count = 0;
        float Similarity;
        float MaxSim;
        float ItemsWithFilesCount = 0;
        
        BucketItem MaxItem = null;
        
        float AverageDevel=0;
        float AverageComp=0;
        float AverageOverlap=0;
        float AverageSimilarity = 0;
        float AverageJaccard = 0;
        
        for(BucketItem Ia : getData()){
            Similarity = 0;
            MaxSim = -1;
            for(BucketItem Ib : getData()){
                if(Ia != Ib 
                       && Ia.getBugData().getDtClose().after( Ib.getBugData().getDtCreation() )
                       && Ia.getBugData().getDtClose().before( Ib.getBugData().getDtClose()) ){
                    
                    Similarity+=(1-Ia.distance(Ib) );
                    if(Similarity > MaxSim){
                        MaxSim = Similarity;
                        MaxItem = Ib;                        
                    }
                }
            }
            
            if (MaxSim >= 0) {
                Count++;
                if (Ia.isSameComponent(MaxItem)) {
                    AverageComp++;
                }
                if (Ia.isSameDevel(MaxItem)) {
                    AverageDevel++;
                }
                AverageSimilarity += MaxSim;
                if (!Ia.getBugData().getPaths().isEmpty() && !MaxItem.getBugData().getPaths().isEmpty()) {
                    ItemsWithFilesCount++;
                    AverageOverlap += Ia.overlap(MaxItem);
                    AverageJaccard += Ia.jaccard(MaxItem);
                }
            }
            
        }
        
        if(Count>0){
            AverageComp/=Count;
            AverageDevel/=Count;
            AverageSimilarity/=Count;
        }
        else{
            AverageComp=-1;
            AverageDevel=-1;
            AverageSimilarity=-1;
        }
        
        if(ItemsWithFilesCount>0){
            AverageOverlap/=ItemsWithFilesCount;
            AverageJaccard/=ItemsWithFilesCount;
        }
        else{
            AverageOverlap=-1;
            AverageJaccard=-1;
        }
        
        AverageBucketMetrics Met = new AverageBucketMetrics();
        Met.setIssueCount(0);
        Met.setComponents(AverageComp);
        Met.setDevelopers(AverageDevel);
        Met.setOverlap(AverageOverlap);
        Met.setJaccard(AverageJaccard);
        Met.setSimilarity(AverageSimilarity);
        
        return Met;
    }    

    
}
