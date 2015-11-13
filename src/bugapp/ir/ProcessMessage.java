/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Henrique
 */
public class ProcessMessage {
    
    public static ArrayList<Integer> extractBugId(String Message){
        ArrayList<Integer> lst = null;
        
        Message = Message.toLowerCase();
        lst = extractBugId(Message,"bug",lst);
        lst = extractBugId(Message,"bugs",lst);
        lst = extractBugId(Message,"bug id:",lst);
        lst = extractBugId(Message,"id=",lst);
        lst = extractBugId(Message,"issue #",lst);
        lst = extractBugId(Message,"bugfix",lst);
        lst = extractBugId(Message,"fix",lst);
        
        return lst;
    }
    
    public static ArrayList<Integer> extractBugId(String Message, String BugStr, ArrayList<Integer> lst){
        if(lst==null){
            lst=new ArrayList<Integer>();
        }
        int StartIndex = Message.indexOf(BugStr);
        int EndIndex;
        //String strBugId;
        Integer BugId;
        
        while(StartIndex>=0){
            StartIndex += BugStr.length(); //Inicio do indice depois de 'bug'
            //Eliminar espaços em branco entre 'bug' e o id
            while(StartIndex < Message.length() && (Message.charAt(StartIndex)==' ' || Message.charAt(StartIndex)=='#' || Message.charAt(StartIndex)==':')){
                StartIndex++;
            }
            
            //Posicionar o indíce final no ultimo digito
            EndIndex = StartIndex;
            while(EndIndex < Message.length() && Character.isDigit( Message.charAt(EndIndex) )){
                EndIndex++;
            }
            
            if(StartIndex!=EndIndex){ 
                //Recupera o bug_id
                //strBugId = Message.substring(StartIndex, EndIndex);
                BugId = new Integer(Message.substring(StartIndex, EndIndex));
                if(!lst.contains(BugId)){
                    lst.add(BugId);
                }
            }
            
            StartIndex = Message.indexOf(BugStr,EndIndex);
        }   
        return lst;
    }
    
//    public static void main(String[] args){
//        String Message;
//        
//        //Message="Fix some more issues noted in bug 356872. Remove all references to www.mozilla.com I could find. Move some files around.";
//        //Message="fix wwww links to www from pages integrated from bugzilla tracking bug attachements";
//        Message="Update Contact Us page  with content from Bug #367041";
//        ArrayList<Integer> lst = extractBugId(Message);
//        System.out.println(lst);
//    }
    
}
