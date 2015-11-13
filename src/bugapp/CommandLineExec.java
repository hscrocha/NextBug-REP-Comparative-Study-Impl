/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp;

import bugapp.auto.Core;
import bugapp.auto.NextbugFacade;

/**
 *
 * @author Henrique
 */
public class CommandLineExec {
    
    private String Command = null;
    private String[] Pars = null;
    
    public CommandLineExec(String[] args){
        if(args.length>=1){
            Command = args[0];
            Pars = args;
        }
    }
    
    public void exec(){
        try{
            long Milis=System.currentTimeMillis();
            System.out.println("Begin");

            parseAndExecuteCommand();
            
            long End=System.currentTimeMillis();
            System.out.printf("End %d%n",(End-Milis)/1000);
            System.out.printf("Local Datetime: %s \n", new java.util.Date());
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    private void parseAndExecuteCommand(){
        String P1=Pars.length>=2?Pars[1]:null;
        
        switch(Command){
            case "mapTest":
                Core.testMapCommitsBugIdToScriptSQL(Pars[1]);
                break;
                
            case "bugActivity":
                Core.createNewBugActivity();
                break;
                
            case "lifeCycle":
                Core.bugLifeCycle(P1);
                break;

            case "resolutionTime":
                Core.bugResolutionTime(Pars[1], Pars[2], Pars[3]);
                break;
                
            case "wekaLifeCycle":
                Core.bugLifeCycleParWekaHumberto(P1);
                break;

            case "nextbug":
                Core.adhocIssuesParameterTest();
                break;
                
            case "rep":
                NextbugFacade.repTrainPasm(NextbugFacade.MOZILLA_PROCESS, true);
                break;
                
            default:
                System.out.println("Command line not reconized.");
        }
    }
}
