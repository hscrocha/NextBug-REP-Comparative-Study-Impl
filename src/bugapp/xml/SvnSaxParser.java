/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.xml;

import bugapp.persistence.entity.AsergCommit;
import bugapp.persistence.entity.AsergCommitPath;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Henrique
 */
public class SvnSaxParser extends DefaultHandler{

    private AsergCommit CurrentCommit;
    private AsergCommitPath CurrentPath;
    private StringBuilder stbData;
    private FileWriter fwrCommit=null;
    private FileWriter fwrPath=null;
    
    public SvnSaxParser(){
        super();
        
        CurrentCommit = new AsergCommit();
        stbData=new StringBuilder();
    }
    
    public void setOutputFileName(String FileName) throws IOException{
        fwrCommit = new FileWriter(FileName+"_commit.sql");
        fwrPath = new FileWriter(FileName+"_paths.sql");
        
        fwrCommit.write("INSERT INTO aserg_commit(revision,author,dt_commit,message) VALUES \n");
        fwrPath.write("INSERT INTO aserg_commit_path(revision,kind,action,path) VALUES \n");
    }
    
    public void parse(String FileName) throws Exception{
        SAXParserFactory saxFac=SAXParserFactory.newInstance();
        
        SAXParser saxParser=saxFac.newSAXParser();
        saxParser.parse(FileName, this);
    }
    
    @Override
    public void startElement(String uri, String localName, String tagName, Attributes attList) throws SAXException {
        if(tagName.equals("path")){
            CurrentPath = new AsergCommitPath();
            CurrentPath.setRevision( CurrentCommit.getRevision() );
            CurrentPath.setType( attList.getValue("kind") );
            CurrentPath.setAction( attList.getValue("action").charAt(0) );
        }
        else if(tagName.equals("logentry")){
            CurrentCommit.setRevision( Integer.parseInt(attList.getValue("revision")));
        }
//        else if(tagName.equals("author")){
//
//        }
//        else if(tagName.equals("date")){
//
//        }
//        else if(tagName.equals("msg")){
//
//        }

    }
    
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        stbData.append(ch, start, length);
    }
 
    @Override
    public void endElement(String uri, String localName, String tagName) throws SAXException {    
        if(tagName.equals("path")){
            CurrentPath.setPath( stbData.toString() );
            CurrentCommit.addPath(CurrentPath);
        }
        else if(tagName.equals("logentry")){
            //Gravar log no SQL
            writeSQLtoFile();
            CurrentCommit.clearPathData();
        }
        else if(tagName.equals("author")){
            CurrentCommit.setAuthor( stbData.toString() );
        }
        else if(tagName.equals("date")){
            CurrentCommit.setDtCommit( toSqlStringData(stbData.toString()) );
        }
        else if(tagName.equals("msg")){
            CurrentCommit.setMessage( stbData.toString() );
        }
        //stbData=new StringBuilder();
        stbData.setLength(0);
    }
    
    @Override
    public void endDocument() throws SAXException{
        if(fwrCommit!=null){
            try {
                fwrCommit.close();
                fwrPath.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
    
    private String toSqlStringData(String strLogDate){
        strLogDate = strLogDate.substring(0, 20);
        strLogDate = strLogDate.replace("T", " ");
        //2013-04-25T08:34:02.403049Z
        return strLogDate;
    }
    
    private void writeSQLtoFile(){
        try {
            fwrCommit.write( CurrentCommit.commitToScriptSQL() );
            fwrPath.write( CurrentCommit.pathToScriptSQL() );
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
