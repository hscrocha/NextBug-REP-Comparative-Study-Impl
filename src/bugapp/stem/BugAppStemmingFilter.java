/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.stem;

/**
 *
 * @author Henrique
 */
public abstract class BugAppStemmingFilter {
    
    public abstract String processWord(String Word) throws Exception;
}
