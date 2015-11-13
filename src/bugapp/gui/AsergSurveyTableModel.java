/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.gui;

import bugapp.persistence.entity.AsergSurvey;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Henrique
 */
public class AsergSurveyTableModel extends DefaultTableModel{
    
    public AsergSurveyTableModel(){
        super();
        this.setColumnIdentifiers(new String[]{"St","S-id","Dev Login","Dev Name","#Assig","Date","Bug","Description"});
    }
    
    public void clear(){
        this.setRowCount(0);
    }
    
    public void add(AsergSurvey S){
        Object[] Line = new Object[8];
        Line[0]=S.getStatus();
        Line[1]=S.getCodSurvey();
        Line[2]=S.getDevLogin();
        Line[3]=S.getDevName();
        Line[4]=S.getBugsResolved();
        Line[5]=S.getDtSurvey();
        Line[6]=S.getBugId();
        Line[7]=S.getShortDesc();
        
        this.addRow(Line);
    }
    
    public void add(ArrayList<AsergSurvey> lst){
        for(AsergSurvey S : lst){
            add(S);
        }
    }
    
    
}
