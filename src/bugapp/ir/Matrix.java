/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.ir;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import bugapp.persistence.entity.BugDescription;
import bugapp.stem.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Henrique
 */
public class Matrix {
    private static final char CSV_SEPARATOR = ';';
    private static final char QUOTE_CHAR = CSVWriter.NO_QUOTE_CHARACTER;
    
    private short[][] M; //Rows: Docs, Columns: Words
    private ArrayList<String> lstWords;
    private ArrayList<Integer> lstBugId;
    
    private int RowCount;
    private int ColCount;
    
    public Matrix(int DocumentCount){
        M=new short[DocumentCount][];
        lstWords=new ArrayList<String>(); //Cols
        lstBugId=new ArrayList<Integer>(); //Rows
        
        RowCount=0;
        ColCount=0;
    }
    
    public Matrix(){
        M=null;
        lstWords=new ArrayList<String>(); //Cols
        lstBugId=new ArrayList<Integer>(); //Rows
        
        RowCount=0;
        ColCount=0;
    }
    
    public void add(BugDescription B) throws Exception{
        lstBugId.add(B.getId());
        makeWordTerms(B.getShortDesc());
        RowCount++;
    }
    
    private void makeWordTerms(String Description) throws Exception{
        int ColIndex;
        String Word;
        StringTokenizer stk=new StringTokenizer(Description," ;:.,!?'\"<>[](){}-+*=/\\_@#$%^&~");
        short[] Row = new short[ColCount + stk.countTokens() + 10];
        
        BugAppStemmingFilter Filter = new StemmingSnow();
        
        while (stk.hasMoreTokens()) {
            Word = Filter.processWord(stk.nextToken().toLowerCase());
            if (Word != null) {
                ColIndex = lstWords.indexOf(Word);
                if (ColIndex >= 0) {
                    //O termo já existe na matriz
                    Row[ColIndex]++;
                } else {
                    //inserir o termo na matriz
                    ColIndex = ColCount;
                    lstWords.add(Word);
                    Row[ColIndex]++;
                    ColCount++;
                }
            }
        }

        M[RowCount] = Row;
    }
    
    public void readFromFile(String FileName) throws Exception {
        readWordsFromFile(FileName+"_term.csv");
        readBugIdFromFile(FileName+"_bugs.csv");
        readMatrixDataFromFile(FileName+"_data.csv");
    }
    
    public void writeToFile(String FileName) throws Exception {
        writeWordsToFile(FileName+"_term.csv");
        //writeBugIdToFile(FileName+"_bugs.csv");
        //writeMatrixDataFile(FileName+"_data.csv");
        //writeCompleteMatrixFile(FileName+".csv");
        writeTransposeMatrixFile(FileName+"_trans.csv");
    }

    private void writeTransposeMatrixFile(String FileName) throws Exception{
        int i, j;
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[RowCount];
        
        //Line[0]="";
        for(i=0; i<lstBugId.size(); i++){
            Line[i]="B"+lstBugId.get(i).toString();
        }
        cwrOutput.writeNext(Line);
        
        for(j=0; j<ColCount; j++){
            //Line[0]=lstWords.get(j).toString();
            for(i=0; i<M.length && i<RowCount; i++){
                if(j < M[i].length ){
                    Line[i]=Short.toString( M[i][j] );
                }
                else{
                    Line[i]="0";
                }
            }
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }
    
    
    private void writeCompleteMatrixFile(String FileName) throws Exception{
        int i, j;
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[ColCount+1];
        Line[0]="d/t";
        for(i=1; i<lstWords.size(); i++){
            Line[i]=lstWords.get(i-1);
        }
        cwrOutput.writeNext(Line);
        
        for(i=0; i<RowCount; i++){
            Line[0]=lstBugId.get(i).toString();
            for(j=0; j<M[i].length && j<ColCount; j++){
                Line[j+1] = Short.toString( M[i][j] );
            }
            for(; j<ColCount; j++){
                Line[j+1] = "0";
            }
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }

    private void writeMatrixDataFile(String FileName) throws Exception{
        int i, j;
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[ColCount];
        for(i=0; i<RowCount; i++){
            for(j=0; j<M[i].length && j<ColCount; j++){
                Line[j] = Short.toString( M[i][j] );
            }
            for(; j<ColCount; j++){
                Line[j] = "0";
            }
            cwrOutput.writeNext(Line);
        }
        cwrOutput.close();
        fwrWriter.close();
    }

    private void readMatrixDataFromFile(String FileName) throws Exception {
        int i, j;
        FileReader frdReader = new FileReader(FileName);
        CSVReader crdReader = new CSVReader(frdReader, CSV_SEPARATOR);
        
        M = new short[lstBugId.size()][lstWords.size()];
        i = 0;
        String[] Line = crdReader.readNext();
        while(Line!=null){
            for(j=0; j<lstWords.size(); j++){
                M[i][j]=Short.parseShort(Line[j]);
            }            
            i++;
            Line = crdReader.readNext();
        }
        crdReader.close();
        frdReader.close();
    }
    
    
    private void writeBugIdToFile(String FileName) throws Exception{
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[lstBugId.size()];
        for(int i=0; i<lstBugId.size(); i++){
            Line[i]=lstBugId.get(i).toString();
        }
        cwrOutput.writeNext(Line);
        
        cwrOutput.close();
        fwrWriter.close();
    }
    
    private void readBugIdFromFile(String FileName) throws Exception {
        FileReader frdReader = new FileReader(FileName);
        CSVReader crdReader = new CSVReader(frdReader, CSV_SEPARATOR);
        
        String[] Line = crdReader.readNext();
        for(String BugId : Line){
            lstBugId.add( Integer.parseInt(BugId) );
        }
        Line = null;
        
        crdReader.close();
        frdReader.close();
    }
    
    private void writeWordsToFile(String FileName) throws Exception{
        FileWriter fwrWriter = new FileWriter(FileName);
        CSVWriter cwrOutput = new CSVWriter(fwrWriter, CSV_SEPARATOR, QUOTE_CHAR);
        
        String[] Line = new String[lstWords.size()];
        Line = lstWords.toArray(Line);
        cwrOutput.writeNext(Line);
        
        cwrOutput.close();
        fwrWriter.close();
    }

    private void readWordsFromFile(String FileName) throws Exception {
        FileReader frdReader = new FileReader(FileName);
        CSVReader crdReader = new CSVReader(frdReader, CSV_SEPARATOR);
        
        String[] Line = crdReader.readNext();
        lstWords.addAll(Arrays.asList(Line));
        Line = null;
        
        crdReader.close();
        frdReader.close();
    }
    
}
