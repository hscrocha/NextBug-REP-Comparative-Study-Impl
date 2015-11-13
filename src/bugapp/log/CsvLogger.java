/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.log;

import au.com.bytecode.opencsv.CSVWriter;
import bugapp.oldir.BooleanFeatureVector;
import bugapp.persistence.entity.BugDescription;
import bugapp.persistence.entity.BugSimple;
import java.io.FileWriter;

/**
 *
 * @author Henrique
 */
public class CsvLogger extends Logger{

    private FileWriter fwrWriter;
    private CSVWriter cwrOutput;

    public CsvLogger(){
        
    }
    
    @Override
    public void open(String FileName) throws Exception{
        fwrWriter = new FileWriter(FileName);
        cwrOutput = new CSVWriter(fwrWriter, ';');
    }
    
    @Override
    public void close() throws Exception {
        cwrOutput.close();
        fwrWriter.close();
    }
    
    @Override
    public void logSimilarity(BugDescription Query, BugDescription Doc, BooleanFeatureVector Vq, double Similarity){
        String[] Line = new String[10];
        
        Line[0]=Double.toString(Similarity).replace('.', ',');
        Line[1]=Integer.toString( Query.getId() );
        Line[2]=Integer.toString( Doc.getId() );

        cwrOutput.writeNext(Line);
    }
    
    @Override
    public void logContextChange(BugSimple Initial, BugSimple Change, BugSimple End, int Count){
        String[] Line = new String[10];
        
        Line[0]=Integer.toString( Initial.getId() );
        Line[1]=Integer.toString( Initial.getComponentId() );
        Line[2]=" -- ";
        Line[3]=Integer.toString( Change.getId() );
        Line[4]=Integer.toString( Change.getComponentId() );
        Line[5]=" -- ";
        Line[6]=Integer.toString( End.getId() );
        Line[7]=Integer.toString( End.getComponentId() );
        Line[8]=" -- ";
        Line[9]=Integer.toString( Count );
        
        cwrOutput.writeNext(Line);
    }

    @Override
    public void logContextChange(BugSimple Initial, BugSimple Change){
        String[] Line = new String[5];
        
        Line[0]=Integer.toString( Initial.getId() );
        Line[1]=Integer.toString( Initial.getComponentId() );
        Line[2]=" -- ";
        Line[3]=Integer.toString( Change.getId() );
        Line[4]=Integer.toString( Change.getComponentId() );
        
        cwrOutput.writeNext(Line);
    }
    
}
