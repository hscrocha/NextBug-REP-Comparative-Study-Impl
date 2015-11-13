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
public class StatisticEntity {
    
    protected int Count;
    protected float Average;
    protected float Deviation;
    protected float Min;
    protected float Max;
    protected float Median;
    
    protected float Quartil1;
    protected float Quartil3;
    
    public StatisticEntity(){
        Count=0;
        Average=0;
        Deviation=0;
        Min=0;
        Max=0;
        Median=0;
        Quartil1=0;
        Quartil3=0;
    }

    /**
     * @return the Count
     */
    public int getCount() {
        return Count;
    }

    /**
     * @param Count the Count to set
     */
    public void setCount(int Count) {
        this.Count = Count;
    }

    /**
     * @return the Average
     */
    public float getAverage() {
        return Average;
    }

    /**
     * @param Average the Average to set
     */
    public void setAverage(float Average) {
        this.Average = Average;
    }

    /**
     * @return the Deviation
     */
    public float getDeviation() {
        return Deviation;
    }

    /**
     * @param Deviation the Deviation to set
     */
    public void setDeviation(float Deviation) {
        this.Deviation = Deviation;
    }

    /**
     * @return the Min
     */
    public float getMin() {
        return Min;
    }

    /**
     * @param Min the Min to set
     */
    public void setMin(float Min) {
        this.Min = Min;
    }

    /**
     * @return the Max
     */
    public float getMax() {
        return Max;
    }

    /**
     * @param Max the Max to set
     */
    public void setMax(float Max) {
        this.Max = Max;
    }

    /**
     * @return the Median
     */
    public float getMedian() {
        return Median;
    }

    /**
     * @param Median the Median to set
     */
    public void setMedian(float Median) {
        this.Median = Median;
    }

    /**
     * @return the Quartil1
     */
    public float getQuartil1() {
        return Quartil1;
    }

    /**
     * @param Quartil1 the Quartil1 to set
     */
    public void setQuartil1(float Quartil1) {
        this.Quartil1 = Quartil1;
    }

    /**
     * @return the Quartil2
     */
    public float getQuartil2() {
        return Median;
    }

    /**
     * @param Quartil2 the Quartil2 to set
     */
    public void setQuartil2(float Quartil2) {
        setMedian(Quartil2);
    }

    /**
     * @return the Quartil3
     */
    public float getQuartil3() {
        return Quartil3;
    }

    /**
     * @param Quartil3 the Quartil3 to set
     */
    public void setQuartil3(float Quartil3) {
        this.Quartil3 = Quartil3;
    }
    
}
