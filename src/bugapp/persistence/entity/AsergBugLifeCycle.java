/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class AsergBugLifeCycle {
    private static final int MAX_STATUS = 5;
    
    //private int[][] Count; //Bug Count: [i][j] = From [i] -> [j] transition
    private StatisticEntity[][] Stats; //[i][j] = From [i] -> [j] transition

    private float[] Arrieved; //Bugs arrieved at the Bugzilla
    private float[] Total; //Total - to calculate percentages
    private float[][] Perc;
    private float[] PercArrieved;
    
    public AsergBugLifeCycle(){
        //Count=new int[MAX_STATUS][MAX_STATUS];
        Stats=new StatisticEntity[MAX_STATUS][MAX_STATUS];

        Arrieved=new float[2];
        Total=new float[MAX_STATUS];
        Perc=new float[MAX_STATUS][MAX_STATUS];
        PercArrieved=new float[2];
        
        for(int i=0; i<Stats.length; i++){
            for(int j=0; j<Stats.length; j++){
                Stats[i][j]=new StatisticEntity();
                Perc[i][j]=0;
            }
        }
    }
    
    public static int statusIndex(String Status){
        return BugActivity.statusIndex(Status);
    }
    
    public static int statusIndex(char Status){
        return BugActivity.statusIndex(Status);
    }
    
    public static String indexToStatus(int i){
        return BugActivity.indexToStatus(i);
    }
    
    public void setCount(int Number, String BeginStatus, String EndStatus){
        int i = statusIndex(BeginStatus);
        int j = statusIndex(EndStatus);
        Stats[i][j].setCount( Number );
    }
    
    public void setStat(StatisticEntity Stat, String BeginStatus, String EndStatus){
        int i = statusIndex(BeginStatus);
        int j = statusIndex(EndStatus);
        Stats[i][j] = Stat;
    }
    
    private void calcArrieved(){
        float CreatedConf = Total[statusIndex("Confirmed")];
        float CreatedUnc =  Total[statusIndex("Unconfirmed")];
        for(int i=0; i<Stats.length; i++){
            if(i!=statusIndex("Confirmed")){
                CreatedConf-=Stats[i][statusIndex("Confirmed")].getCount();
            }
        }
        for(int i=0; i<Stats.length; i++){
            if(i!=statusIndex("Unconfirmed")){
                CreatedUnc-=  Stats[i][statusIndex("Unconfirmed")].getCount();
            }
        }
        
        Arrieved[ statusIndex("Unconfirmed") ] = CreatedUnc;
        Arrieved[ statusIndex("Confirmed")] = CreatedConf;
        
        for(int i=0; i<Arrieved.length; i++){
            PercArrieved[i]=Arrieved[i]/(CreatedConf+CreatedUnc);
        }
    }
    
    public void setTotal(float Number, String Status){
        Total[statusIndex(Status)] = Number;
    }
    
    public void calcPercentages(){
        for(int i=0; i<Stats.length; i++){
            Total[i] = 0;
            for(int j=0; j<Stats[0].length; j++){
                Total[i] += Stats[i][j].getCount();
            }
            
            for(int j=0; j<Stats[0].length; j++){
                Perc[i][j] = Stats[i][j].getCount() / Total[i];
            }
        }
        
        calcArrieved();
    }
   
    @Deprecated
    public void calcPercentagesOld(){
        for(int i=0; i<Stats.length; i++){
            float SubTot = 0;
            for(int j=0; j<Stats[0].length; j++){
                if(i!=j){
                    SubTot += Stats[i][j].getCount();
                    Perc[i][j] = Stats[i][j].getCount() / Total[i];
                }
            }
            Stats[i][i].setCount( (int) (Total[i]-SubTot) );
            Perc[i][i]=Stats[i][i].getCount()/Total[i];
        }
        
        calcArrieved();
    }
    
    public float getPercentage(String begStatus, String endStatus){
        return Perc[ statusIndex(begStatus) ][ statusIndex(endStatus) ];
    }

    public float getPercentage(char begStatus, char endStatus){
        return Perc[ statusIndex(begStatus) ][ statusIndex(endStatus) ];
    }
    
    public float getPercentage(int begStatus, int endStatus){
        return Perc[begStatus][endStatus];
    }
    
    public float getAverage(char begStatus, char endStatus){
        return Stats[ statusIndex(begStatus) ][ statusIndex(endStatus) ].getAverage();
    }

    public float getDeviation(char begStatus, char endStatus){
        return Stats[ statusIndex(begStatus) ][ statusIndex(endStatus) ].getDeviation();
    }

    public String getTemplateStringPerc(char begStatus, char endStatus){
        return bugapp.util.NumberUtil.floatToDiaPercentString( getPercentage(begStatus, endStatus) );
    }
    
    public StatisticEntity getStat(char begStatus, char endStatus){
        return Stats[ statusIndex(begStatus) ][ statusIndex(endStatus) ];
    }

    public String getTemplateStringPerc(String... StatusPair){
        float LocalPerc = 0;
        for(String Pair : StatusPair){
            LocalPerc += getPercentage(Pair.charAt(0), Pair.charAt(1));
        }
        return bugapp.util.NumberUtil.floatToDiaPercentString( LocalPerc );
    }

    public String getTemplateStringPAD(char begStatus, char endStatus){
        return (bugapp.util.NumberUtil.floatToDiaPercentString(getPercentage(begStatus, endStatus))
               +" ("+bugapp.util.NumberUtil.floatToStrRound(getAverage(begStatus, endStatus))
               +"+"+bugapp.util.NumberUtil.floatToStrRound(getDeviation(begStatus, endStatus))
               +")");
    }

    public String getTemplateStringPADLineBreak(char begStatus, char endStatus){
        return (bugapp.util.NumberUtil.floatToDiaPercentString(getPercentage(begStatus, endStatus))
               +"\n("+bugapp.util.NumberUtil.floatToStrRound(getAverage(begStatus, endStatus))
               +"+"+bugapp.util.NumberUtil.floatToStrRound(getDeviation(begStatus, endStatus))
               +")");
    }
    
    public String getTemplateStringPAD(String... StatusPair){
        float LocalPerc = 0, Avg = 0, Dev = 0;
        for(String Pair : StatusPair){
            float CurPerc = getPercentage(Pair.charAt(0), Pair.charAt(1));
            LocalPerc += CurPerc;
            Avg += getAverage(Pair.charAt(0), Pair.charAt(1)) * CurPerc;
            Dev += getDeviation(Pair.charAt(0), Pair.charAt(1)) * CurPerc;
        }
        Avg/=LocalPerc;
        Dev/=LocalPerc;
        
        return (bugapp.util.NumberUtil.floatToDiaPercentString(LocalPerc)
               +" ("+bugapp.util.NumberUtil.floatToStrRound(Avg)
               +"+"+bugapp.util.NumberUtil.floatToStrRound(Dev)
               +")");
    }
    
    public String getTemplateStringPQuartis(char begStatus, char endStatus){
        return (bugapp.util.NumberUtil.floatToDiaPercentString(getPercentage(begStatus, endStatus))
               +"("+bugapp.util.NumberUtil.floatToStrRound( getStat(begStatus, endStatus).getMedian())
               +")");
    }

    public String getTemplateStringPQuartisLineBreak(char begStatus, char endStatus){
        return (bugapp.util.NumberUtil.floatToDiaPercentString(getPercentage(begStatus, endStatus))
               +"\n("+bugapp.util.NumberUtil.floatToStrRound( getStat(begStatus, endStatus).getMedian())
               +")");
    }
    
    public String getTemplateStringPQuartis(String... StatusPair){
        float TotalPerc = 0;
        String FirstPair = StatusPair[0];
        for(String Pair : StatusPair){
            float CurPerc = getPercentage(Pair.charAt(0), Pair.charAt(1));
            TotalPerc += CurPerc;
        }
        
        float Med = getStat(FirstPair.charAt(0), FirstPair.charAt(1)).getMedian();
        return (bugapp.util.NumberUtil.floatToDiaPercentString(TotalPerc)
               +" ("+bugapp.util.NumberUtil.floatToStrRound(Med)
               +")");
    }
    
    public float getArrieved(String Status){
        return PercArrieved[ statusIndex(Status) ];
    }
    
    public float getArrieved(int Status){
        return PercArrieved[Status];
    }
    
    public String toCsvFile(){
        StringBuilder stb = new StringBuilder();
        stb.append("Begin;End;Count;%;Min;Max;Avg;Dev\n");
        
        for(int i=0; i<Arrieved.length; i++){
            stb.append(" -------- ;");
            stb.append( indexToStatus(i) );
            stb.append(";");
            stb.append( bugapp.util.NumberUtil.floatToBr(Arrieved[i]) );
            stb.append(";");
            stb.append( bugapp.util.NumberUtil.floatToBr(PercArrieved[i]));
            stb.append("\n");
        }
        
        for(int i=0; i<Stats.length; i++){
            for(int j=0; j<Stats[0].length; j++){
                stb.append( indexToStatus(i) );
                stb.append(";");
                stb.append( indexToStatus(j) );
                stb.append(";");
                stb.append(Stats[i][j].getCount());
                stb.append(";");
                stb.append( bugapp.util.NumberUtil.floatToBr(Perc[i][j]) );
                stb.append(";");
                stb.append( bugapp.util.NumberUtil.floatToBr(Stats[i][j].getMin()) );
                stb.append(";");
                stb.append( bugapp.util.NumberUtil.floatToBr(Stats[i][j].getMax() ));
                stb.append(";");
                stb.append( bugapp.util.NumberUtil.floatToBr(Stats[i][j].getAverage()));
                stb.append(";");
                stb.append( bugapp.util.NumberUtil.floatToBr(Stats[i][j].getDeviation()) );
                stb.append("\n");
            }
            
        }
        
        stb.append("\n");
        stb.append("\n");
        stb.append("Status;Total\n");
        for(int i=0; i<Total.length; i++){
            stb.append( indexToStatus(i) );
            stb.append(";");
            stb.append( bugapp.util.NumberUtil.floatToBr(Total[i]));
            stb.append("\n");
        }
        
        return stb.toString();
    }
}
