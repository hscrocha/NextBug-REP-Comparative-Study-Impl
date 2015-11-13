/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Henrique
 */
public class DateUtil {
    
    public static String getSqlDateAsString(java.sql.Date T, String Mask){
        return getDateAsString(toUtilDate(T), Mask);
    }
    
    
    
    public static java.sql.Date todayAsSQLDate(){
        return toSqlDate(today());
    }
    
    public static java.util.Date today(){
        return new java.util.Date();
    }
    
    public static java.util.Date yesterday(){
        Calendar C=Calendar.getInstance();
        C.add(Calendar.DAY_OF_MONTH, -1);
        return C.getTime();
    }
    
    public static String getDateAsString(java.util.Date Dt, String Format){
        SimpleDateFormat Formatter = new SimpleDateFormat(Format);
        return Formatter.format(Dt);
    }
    
    public static java.sql.Date addDate(java.util.Date DtInicio, int Days){
        java.sql.Date DtFim; 
        Calendar C=Calendar.getInstance();
        C.setTime(DtInicio);
        C.add(Calendar.DAY_OF_MONTH, Days);
        DtFim = new java.sql.Date(C.getTimeInMillis());
        return DtFim;
    }
    
    public static java.sql.Timestamp toTimestamp(java.sql.Date Dt){
        return new java.sql.Timestamp( Dt.getTime() );
    }

    public static java.sql.Timestamp toTimestamp(java.util.Date Dt){
        return new java.sql.Timestamp( Dt.getTime() );
    }

    public static java.sql.Date addMinutes(java.util.Date DtInicio, int Min){
        java.sql.Date DtFim; 
        Calendar C=Calendar.getInstance();
        C.setTime(DtInicio);
        C.add(Calendar.MINUTE, Min);
        DtFim = new java.sql.Date(C.getTimeInMillis());
        return DtFim;
    }
    
    public static java.sql.Date toSqlDate(java.util.Date Dt){
        return new java.sql.Date( Dt.getTime() );
    }
    
    public static java.util.Date toUtilDate(java.sql.Date Dt){
        return new java.util.Date( Dt.getTime() );
    }
    
    public static java.util.Date stringToUtilDate(String Text) throws ParseException{
        SimpleDateFormat Formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return Formatter.parse(Text);
    }

    public static java.util.Date stringToUtilDate(String Text, String Mask) throws ParseException{
        SimpleDateFormat Formatter = new SimpleDateFormat(Mask);
        return Formatter.parse(Text);
    }
    
    public static java.sql.Date stringToSqlDate(String Text) throws ParseException {
        return toSqlDate(stringToUtilDate(Text));
    }

    public static java.sql.Date stringToSqlDate(String Text,String Mask) throws ParseException {
        return toSqlDate(stringToUtilDate(Text,Mask));
    }
    
    public static int daysDiference(java.sql.Date DtBegin, java.sql.Date DtEnd){
        return (int)getDateDiff(DtBegin, DtEnd, TimeUnit.DAYS);
    }

    public static int hoursDiference(java.sql.Date DtBegin, java.sql.Date DtEnd){
        return (int)getDateDiff(DtBegin, DtEnd, TimeUnit.HOURS);
    }

    public static boolean withinRange(java.util.Date DtBase, java.util.Date DtComp, int MinuteRange){
        long MinDiff = Math.abs( getDateDiff(DtBase, DtComp, TimeUnit.MINUTES) );
        return ( MinDiff <= MinuteRange);
    }
    
    /**
     * Get a diff between two dates
     *
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    protected static long getDateDiff(java.sql.Date date1, java.sql.Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get a diff between two dates
     *
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    protected static long getDateDiff(java.util.Date date1, java.util.Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    
//    public static void main(String[] args){
//        java.sql.Date DtBegin = new java.sql.Date(System.currentTimeMillis());
//        java.sql.Date DtEnd = addDate(DtBegin, 20);
//        
//        System.out.println(hoursDiference(DtEnd, DtBegin));
//        
//    }
    
}
