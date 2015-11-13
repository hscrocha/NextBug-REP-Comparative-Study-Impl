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
import java.util.HashMap;

/**
 *
 * @author Henrique
 */
public class BugLifeCycleTemplate {
   
    private String TempleFileName = "./buglifecycle_template.dia";
    private HashMap<String, String> hmpTemplate;
    
    public BugLifeCycleTemplate(){
        hmpTemplate = new HashMap<>();
    }
    
    public void setTemplateFileName(String FileName){
        this.TempleFileName = FileName;
    }

    private void populateHashPercentQuartis(AsergBugLifeCycle Stats){
        hmpTemplate.put("0A%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Unconfirmed")));
        hmpTemplate.put("0B%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Confirmed")));
        
        hmpTemplate.put("AA%", Stats.getTemplateStringPQuartisLineBreak('A', 'A'));
        hmpTemplate.put("AB%", Stats.getTemplateStringPQuartis('A', 'B'));
        hmpTemplate.put("AC%", Stats.getTemplateStringPQuartis('A', 'C'));
        hmpTemplate.put("AD%", Stats.getTemplateStringPQuartis('A', 'D'));
        
        hmpTemplate.put("BA%", Stats.getTemplateStringPQuartis('B', 'A'));
        hmpTemplate.put("BB%", Stats.getTemplateStringPQuartisLineBreak('B', 'B'));
        hmpTemplate.put("BC%", Stats.getTemplateStringPQuartis('B', 'C'));
        hmpTemplate.put("BD%", Stats.getTemplateStringPQuartis('B', 'D'));

        hmpTemplate.put("CC%", Stats.getTemplateStringPQuartisLineBreak('C', 'C'));
        hmpTemplate.put("CB%", Stats.getTemplateStringPQuartis('C', 'B'));
        hmpTemplate.put("CD%", Stats.getTemplateStringPQuartis('C', 'D'));

	hmpTemplate.put("DD%", Stats.getTemplateStringPQuartisLineBreak('D', 'D'));
        hmpTemplate.put("DA%", Stats.getTemplateStringPQuartis('D', 'A'));
        hmpTemplate.put("DB%", Stats.getTemplateStringPQuartis('D', 'B'));
        hmpTemplate.put("DE%", Stats.getTemplateStringPQuartis('D', 'E'));
        
        //float EB = Stats.getTemplateStringPAD('E', 'B')+Stats.getTemplateStringPAD('E', 'C'); //EC anomalia, que não deveria ocorrer
        hmpTemplate.put("EE%", Stats.getTemplateStringPQuartisLineBreak('E', 'E'));
        hmpTemplate.put("EA%", Stats.getTemplateStringPQuartis('E', 'A'));
        hmpTemplate.put("EB%", Stats.getTemplateStringPQuartis("EB","EC"));
        hmpTemplate.put("ED%", Stats.getTemplateStringPQuartis('E', 'D'));
    }
    
    @Deprecated
    private void populateHashPercentAvgDev(AsergBugLifeCycle Stats){
        hmpTemplate.put("0A%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Unconfirmed")));
        hmpTemplate.put("0B%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Confirmed")));
        
        hmpTemplate.put("AA%", Stats.getTemplateStringPADLineBreak('A', 'A'));
        hmpTemplate.put("AB%", Stats.getTemplateStringPAD('A', 'B'));
        hmpTemplate.put("AC%", Stats.getTemplateStringPAD('A', 'C'));
        hmpTemplate.put("AD%", Stats.getTemplateStringPAD('A', 'D'));
        
        hmpTemplate.put("BA%", Stats.getTemplateStringPAD('B', 'A'));
        hmpTemplate.put("BB%", Stats.getTemplateStringPADLineBreak('B', 'B'));
        hmpTemplate.put("BC%", Stats.getTemplateStringPAD('B', 'C'));
        hmpTemplate.put("BD%", Stats.getTemplateStringPAD('B', 'D'));

        hmpTemplate.put("CC%", Stats.getTemplateStringPADLineBreak('C', 'C'));
        hmpTemplate.put("CB%", Stats.getTemplateStringPAD('C', 'B'));
        hmpTemplate.put("CD%", Stats.getTemplateStringPAD('C', 'D'));

	hmpTemplate.put("DD%", Stats.getTemplateStringPADLineBreak('D', 'D'));
        hmpTemplate.put("DA%", Stats.getTemplateStringPAD('D', 'A'));
        hmpTemplate.put("DB%", Stats.getTemplateStringPAD('D', 'B'));
        hmpTemplate.put("DE%", Stats.getTemplateStringPAD('D', 'E'));
        
        //float EB = Stats.getTemplateStringPAD('E', 'B')+Stats.getTemplateStringPAD('E', 'C'); //EC anomalia, que não deveria ocorrer
        hmpTemplate.put("EE%", Stats.getTemplateStringPADLineBreak('E', 'E'));
        hmpTemplate.put("EA%", Stats.getTemplateStringPAD('E', 'A'));
        hmpTemplate.put("EB%", Stats.getTemplateStringPAD("EB","EC"));
        hmpTemplate.put("ED%", Stats.getTemplateStringPAD('E', 'D'));
    }
    
    @Deprecated
    private void populateHashPerc(AsergBugLifeCycle Stats){
        hmpTemplate.put("0A%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Unconfirmed")));
        hmpTemplate.put("0B%", bugapp.util.NumberUtil.floatToDiaPercentString(Stats.getArrieved("Confirmed")));
        
        hmpTemplate.put("AA%", Stats.getTemplateStringPerc('A', 'A'));
        hmpTemplate.put("AB%", Stats.getTemplateStringPerc('A', 'B'));
        hmpTemplate.put("AC%", Stats.getTemplateStringPerc('A', 'C'));
        hmpTemplate.put("AD%", Stats.getTemplateStringPerc('A', 'D'));
        
        hmpTemplate.put("BA%", Stats.getTemplateStringPerc('B', 'A'));
        hmpTemplate.put("BB%", Stats.getTemplateStringPerc('B', 'B'));
        hmpTemplate.put("BC%", Stats.getTemplateStringPerc('B', 'C'));
        hmpTemplate.put("BD%", Stats.getTemplateStringPerc('B', 'D'));

        hmpTemplate.put("CC%", Stats.getTemplateStringPerc('C', 'C'));
        hmpTemplate.put("CB%", Stats.getTemplateStringPerc('C', 'B'));
        hmpTemplate.put("CD%", Stats.getTemplateStringPerc('C', 'D'));

	hmpTemplate.put("DD%", Stats.getTemplateStringPerc('D', 'D'));
        hmpTemplate.put("DA%", Stats.getTemplateStringPerc('D', 'A'));
        hmpTemplate.put("DB%", Stats.getTemplateStringPerc('D', 'B'));
        hmpTemplate.put("DE%", Stats.getTemplateStringPerc('D', 'E'));
        
        //float EB = Stats.getTemplateStringPerc('E', 'B')+Stats.getTemplateStringPerc('E', 'C'); //EC anomalia, que não deveria ocorrer
        hmpTemplate.put("EE%", Stats.getTemplateStringPerc('E', 'E'));
        hmpTemplate.put("EA%", Stats.getTemplateStringPerc('E', 'A'));
        hmpTemplate.put("EB%", Stats.getTemplateStringPerc("EB","EC"));//EC anomalia, que não deveria ocorrer
        hmpTemplate.put("ED%", Stats.getTemplateStringPerc('E', 'D'));
    }
    
    public void create(String OutputFileName, AsergBugLifeCycle Stats) throws FileNotFoundException, IOException{
        populateHashPercentQuartis(Stats);
        
        FileWriter fwrOutput = new FileWriter(OutputFileName);
        FileReader frdTemplate = new FileReader(TempleFileName);
        BufferedReader brdTemplate = new BufferedReader(frdTemplate);
        String Line = brdTemplate.readLine();
        while(Line!=null){
            if(Line.contains("<dia:string>")){
                Line = searchAndReplace(Line);
            }
            fwrOutput.write(Line+"\n");
            Line = brdTemplate.readLine();
        }        
        brdTemplate.close();
        frdTemplate.close();
        fwrOutput.close();
    }
    
    private String searchAndReplace(String Line){
        int Index;
        for(String Key : hmpTemplate.keySet()){
            Index = Line.indexOf(Key);
            if(Index>=0){
                Line = Line.replaceAll(Key, hmpTemplate.get(Key));
                hmpTemplate.remove(Key);
                break;
            }
        }
        return Line;
    }
        
}
