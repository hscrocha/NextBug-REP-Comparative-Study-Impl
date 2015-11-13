/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence.template;

import bugapp.persistence.entity.AsergBugLifeCycle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Henrique
 */
@Deprecated
public class BugLifeCycleTemplateOld {
   
    private String TempleFileName = "./buglifecycle_template.dia";
    private String OutputFileName;
    
    public BugLifeCycleTemplateOld(){
        
    }
    
    public void setOutputFileName(String FileName){
        this.OutputFileName = FileName;
    }
    
    public void setTemplateFileName(String FileName){
        this.TempleFileName = FileName;
    }
    
    private String readTemplate() throws FileNotFoundException, IOException{
        StringBuilder stb = new StringBuilder();
        
        FileReader flr = new FileReader(TempleFileName);
        BufferedReader brd = new BufferedReader(flr);
        String Line = brd.readLine();
        while(Line!=null){
            stb.append(Line);
            stb.append("\n");
            Line = brd.readLine();
        }        
        brd.close();
        flr.close();
        
        return stb.toString();
    }
    
    private void writeOutput(String Output) throws IOException{
        FileWriter flw = new FileWriter(OutputFileName);
        flw.write(Output);
        flw.close();
    }
    
    private String replace(String KeyTemplate, int Stat, String Template){
        String Value = Integer.toString(Stat)+"%";
        return Template.replaceFirst(KeyTemplate, Template);
    }

    private String replace(String KeyTemplate, float Stat, String Template){
        String Value = bugapp.util.NumberUtil.floatToDiaPercentString(Stat)+"%";
        return Template.replaceFirst(KeyTemplate, Template);
    }
    
    public void createOutput(AsergBugLifeCycle Stats) throws IOException{
        String Output = readTemplate();
        
        Output = replace("0A%", Stats.getArrieved("Unconfirmed"), Output);
        Output = replace("0B%", Stats.getArrieved("Confirmed"), Output);
        System.gc();
        
        Output = replace("AA%", Stats.getPercentage('A', 'A'), Output);
        Output = replace("AB%", Stats.getPercentage('A', 'B'), Output);
        Output = replace("AC%", Stats.getPercentage('A', 'C'), Output);
        Output = replace("AD%", Stats.getPercentage('A', 'D'), Output);
        System.gc();
        
        Output = replace("BB%", Stats.getPercentage('B', 'B'), Output);
        Output = replace("BC%", Stats.getPercentage('B', 'C'), Output);
        Output = replace("BD%", Stats.getPercentage('B', 'D'), Output);
        System.gc();

        Output = replace("CC%", Stats.getPercentage('C', 'C'), Output);
        Output = replace("CB%", Stats.getPercentage('C', 'B'), Output);
        Output = replace("CD%", Stats.getPercentage('C', 'D'), Output);
        System.gc();
        
        Output = replace("DD%", Stats.getPercentage('D', 'D'), Output);
        Output = replace("DA%", Stats.getPercentage('D', 'A'), Output);
        Output = replace("DB%", Stats.getPercentage('D', 'B'), Output);
        Output = replace("DE%", Stats.getPercentage('D', 'E'), Output);
        System.gc();
        
        Output = replace("EE%", Stats.getPercentage('E', 'E'), Output);
        Output = replace("EA%", Stats.getPercentage('E', 'A'), Output);
        Output = replace("EB%", Stats.getPercentage('E', 'B'), Output);
        System.gc();

        writeOutput(Output);
    }
}
