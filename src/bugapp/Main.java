/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp;

/**
 *
 * @author Henrique
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(args.length<1){
            bugapp.gui.MainFrame.main(args);
        }
        else{
            //System.out.println(args[0]);
            CommandLineExec Cle=new CommandLineExec(args);
            Cle.exec();
        }
    }
}
