/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.connection;

import bugapp.persistence.entity.AsergSurvey;
import bugapp.persistence.entity.BugAttach;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class MozillaConnection {
    
    public static void bugzillaDevInfo(AsergSurvey S) throws MalformedURLException, IOException{
        String strBugzillaContent = fetchFile( makeDevUrl(S.getDevLogin()) );
        int BugsAssigned = extractBugsAssignedFromProfile(strBugzillaContent);
        String DevName = extractDevNamesFromProfile(strBugzillaContent);
        S.setBugsResolved(BugsAssigned);
        S.setDevName(DevName);
    }
    
    public ArrayList<String> process(BugAttach B) throws MalformedURLException, IOException, InterruptedException {
        ArrayList<String> lstFiles = new ArrayList<String>();
        
        for(Integer AttachId : B.getAttachIds()){
            String strFileContent = fetchFile(B.getBugId(),AttachId);
            extractFileNamesFromAttachment(strFileContent, lstFiles);
        }
        Thread.sleep(100);
        
        return lstFiles;
    }
    
    public String fetchFile(int BugId, int AttachId) throws MalformedURLException, IOException{
        return fetchFile( makeBugUrl(BugId, AttachId) );
    }
    
    public static String fetchFile(URL WebFile) throws MalformedURLException, IOException{
        URLConnection Con = WebFile.openConnection();
        Con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
        java.io.InputStream urlStream = Con.getInputStream();

        // first, read in the entire URL
        byte buff[] = new byte[1000];
        int bcount = urlStream.read(buff);
        StringBuilder Content = new StringBuilder();
        String strNewContent;

        while (bcount != -1) {
            strNewContent = new String(buff, 0, bcount);
            Content.append(strNewContent);
            bcount = urlStream.read(buff);
        }
        urlStream.close();
        buff = null;

        return Content.toString();
    }
    
    public void extractFileNamesFromAttachment(String strFileContent, ArrayList<String> lstFiles){
        int Pos = 0;
        int PosFinal = 0;
        int Index = strFileContent.indexOf("+++ ", Pos);
        char CurrentChar;
        String strFileName;
        
        while(Index>0){
            Pos = Index + 4;
            PosFinal = Pos+1;
            CurrentChar = strFileContent.charAt(PosFinal);
            while( CurrentChar!=' ' && CurrentChar!='\n' && CurrentChar!='\t'){
                PosFinal++;
                CurrentChar = strFileContent.charAt(PosFinal);
            }
            //PosFinal = strFileContent.indexOf(' ',Pos+1);
            
            strFileName = strFileContent.substring(Pos, PosFinal);
//            if(strFileName.endsWith("@@")){
//                strFileName = strFileName.substring(0, strFileName.length()-2);
//            }
            
            if(!lstFiles.contains(strFileName)){
                lstFiles.add(strFileName);
            }
            
            Pos = PosFinal+1;
            Index = strFileContent.indexOf("+++ ", Pos);
        }
    }
    
    public void extractFileAndMethodNamesFromAttachment(String strFileContent, ArrayList<String> lstFiles){
        int Pos = 0;
        int PosFinal = 0;
        int Index = strFileContent.indexOf("+++ ", Pos);
        char CurrentChar;
        String strFileName;
        
        while(Index>0){
            Pos = Index + 4;
            PosFinal = Pos+1;
            CurrentChar = strFileContent.charAt(PosFinal);
            while( CurrentChar!=' ' && CurrentChar!='\n' && CurrentChar!='\t'){
                PosFinal++;
                CurrentChar = strFileContent.charAt(PosFinal);
            }
            //PosFinal = strFileContent.indexOf(' ',Pos+1);
            
            strFileName = strFileContent.substring(Pos, PosFinal);
//            if(strFileName.endsWith("@@")){
//                strFileName = strFileName.substring(0, strFileName.length()-2);
//            }
            
            if(!lstFiles.contains(strFileName)){
                if(strFileName.endsWith(".js") || strFileName.endsWith(".xml") || strFileName.endsWith(".html")){
                    //Procurar por FUNCTION
                }
                else if(strFileName.endsWith(".cpp") || strFileName.endsWith(".c")){
                    //Procurar pelo @@ e ::
                }
                
                lstFiles.add(strFileName);
            }
            
            Pos = PosFinal+1;
            Index = strFileContent.indexOf("+++ ", Pos);
        }
    }

    
    
    public static URL makeBugUrl(int BugId, int AttachId) throws MalformedURLException{
        return new URL(makeBugStringUrl(BugId, AttachId));
    }

    public static String makeBugStringUrl(int BugId, int AttachId){
        return ("https://bug"+Integer.toString(BugId)+".bugzilla.mozilla.org/attachment.cgi?id="+Integer.toString(AttachId)+"&action=diff&collapsed=&context=patch&format=raw&headers=1");
    }
    
    public static String makeDevStringUrl(String DevLogin){
        //return ("https://bugzilla.mozilla.org/user_profile?login="+DevLogin);
        return ("https://bugzilla.mozilla.org/user_profile?login="+DevLogin+"&Bugzilla_login=hscrocha@gmail.com&Bugzilla_password=Toscomor1*&Bugzilla_remember=on");
    }
    
    public static URL makeDevUrl(String DevLogin) throws MalformedURLException {
        return new URL(makeDevStringUrl(DevLogin));
    }
    
    private static String extractDevNamesFromProfile(String ProfileWebPageContent){
//  <th>Name</th>
//  <td colspan="2">Rick Bryce [:rbryce]
//  </td>        
        int Index1 = ProfileWebPageContent.indexOf("<th>Name</th>");
        if(Index1<0) return null;
        
        int Index2 = ProfileWebPageContent.indexOf(">",Index1+14) + 1;
        int Index3 = ProfileWebPageContent.indexOf("<",Index2);
        
        //System.out.printf("%d %d %d %n", Index1, Index2, Index3);
        String DevName = ProfileWebPageContent.substring(Index2, Index3);
        int Index4 = DevName.indexOf("[");
        if(Index4 > 0) DevName = DevName.substring(0,Index4);

        int Index5 = DevName.indexOf("(");
        if(Index5 > 0) DevName = DevName.substring(0,Index5);
        
        return DevName.trim();
    }
    
    private static int extractBugsAssignedFromProfile(String ProfileWebPageContent){
    // <th>Assigned to</th>
    // <td class="numeric">
    //  <a href="buglist.cgi?query_format=advanced&amp;emailtype1=exact&amp;emailassigned_to1=1&amp;email1=rbryce%40mozilla.com"
    //    target="_blank">950</a>
        
        int Index1 = ProfileWebPageContent.indexOf("<th>Assigned to</th>") + 21;
        int Index2 = ProfileWebPageContent.indexOf(">",Index1)+1;
        int Index3 = ProfileWebPageContent.indexOf(">",Index2)+1; // Assigned to
        int Index4 = ProfileWebPageContent.indexOf("<",Index3);
        //System.out.println( (FileContent.substring(Index1)) );
        
        String strNum = ProfileWebPageContent.substring(Index3,Index4);
        //System.out.printf("%d %d %d %d %s", Index1, Index2, Index3, Index4, strNum);
//        System.out.println(strNum);
        int Num = -1;
        try{
            Num = Integer.parseInt(strNum);
        }catch(NumberFormatException ex){
            Num = -1;
        }
        
        return Num;
    }
    
    /*
    protected void search(){
        try{
            URLConnection con = SearchURL.openConnection();
            con.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            java.io.InputStream urlStream = con.getInputStream();
            
            // first, read in the entire URL
            byte buff[] = new byte[1000];
            int bcount = urlStream.read(buff);
            StringBuffer Content = new StringBuffer();
            String strNewContent;
            
            while(bcount != -1){
                strNewContent=new String(buff,0,bcount);
                Content.append(strNewContent);
                bcount = urlStream.read(buff);
            }
            urlStream.close();
            buff=null;
            
            String lowerCaseContent = Content.toString().toLowerCase();
            
            int index = 0;
            while( (index = lowerCaseContent.indexOf("<a", index))!=-1 ) {
                if ((index = lowerCaseContent.indexOf("href", index)) == -1)
                    break;
                if ((index = lowerCaseContent.indexOf("=", index)) == -1)
                    break;
                
                index++;
                String remaining = Content.substring(index);
                
                StringTokenizer st=new StringTokenizer(remaining,"\t\n\r\">#");
                String strLink = st.nextToken();
                
                Owner.addLink(strLink.trim());
            }
            
        }catch(Exception e){
            System.out.println(e);
        }finally{
            Owner.finishedSearch();
        }
        
    }
    
     */
    
    /*public static void main(String[] args){
        
        try {
            String strBugzillaContent;
            //strBugzillaContent = fetchFile( makeDevUrl("fbukevin@gmail.com") );            
            strBugzillaContent = fetchFile( makeDevUrl("rbryce@mozilla.com") );            
            //strBugzillaContent = fetchFile( makeDevUrl("bjacob@mozilla.com") );            
            int BugsAssigned = extractBugsAssignedFromProfile(strBugzillaContent);
            String DevName = extractDevNamesFromProfile(strBugzillaContent);
            System.out.println(BugsAssigned);
            System.out.println(DevName);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    } //*/
}
