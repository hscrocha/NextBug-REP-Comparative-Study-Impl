///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package bugapp.stem;
//
//import java.io.*;
//import org.apache.lucene.analysis.*;
//import org.apache.lucene.analysis.tokenattributes.*;
//import org.apache.lucene.analysis.snowball.*;
//import org.apache.lucene.util.*;
//
///**
// *
// * @author Henrique
// */
//public class StemmingNewLucene {
//    
//
//    public static String Stem(String text, String language){
//        
//        StringBuffer result = new StringBuffer();
//        if (text!=null && text.trim().length()>0){
//            StringReader tReader = new StringReader(text);
//            Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_41,language);
//            TokenStream tStream = analyzer.tokenStream("contents", tReader);
//            TermAttribute term = tStream.addAttribute(TermAttribute.class);
//
//            try {
//                while (tStream.incrementToken()){
//                    result.append(term.term());
//                    result.append(" ");
//                }
//            } catch (IOException ioe){
//                System.out.println("Error: "+ioe.getMessage());
//            }
//        }
//
//        // If, for some reason, the stemming did not happen, return the original text
//        if (result.length()==0)
//            result.append(text);
//        return result.toString().trim();
//    }
//
//    public static void main (String[] args){
//        Stemmer.Stem("Michele Bachmann amenities pressed her allegations that the former head of her Iowa presidential bid was bribed by the campaign of rival Ron Paul to endorse him, even as one of her own aides denied the charge.", "English");
//    }
//    
//}
