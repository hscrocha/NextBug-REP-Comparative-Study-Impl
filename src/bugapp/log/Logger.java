/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.log;

import bugapp.oldir.BooleanFeatureVector;
import bugapp.persistence.entity.BugDescription;
import bugapp.persistence.entity.BugSimple;

/**
 *
 * @author Henrique
 */
public abstract class Logger {
    
    public abstract void open(String FileName) throws Exception;
    public abstract void close() throws Exception;
    public abstract void logContextChange(BugSimple Initial, BugSimple Change, BugSimple End, int Count);
    public abstract void logContextChange(BugSimple Initial, BugSimple Change);
    public abstract void logSimilarity(BugDescription Query, BugDescription Doc, BooleanFeatureVector Vq, double Similarity);
}
