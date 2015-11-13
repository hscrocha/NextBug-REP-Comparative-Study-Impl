/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Henrique
 */
public class BugAttach {
    
    private int BugId;
    private LinkedList<Integer> lstAttachId;
    
    public BugAttach(){
        this.BugId = -1;
        this.lstAttachId=new LinkedList<Integer>();
    }

    /**
     * @return the BugId
     */
    public int getBugId() {
        return BugId;
    }

    /**
     * @param BugId the BugId to set
     */
    public void setBugId(int BugId) {
        this.BugId = BugId;
    }

    /**
     * @return the lstAttachId
     */
    public LinkedList<Integer> getAttachIds() {
        return lstAttachId;
    }
    
    public void addAttachId(int AttachId){
        lstAttachId.add(AttachId);
    }
    
    
}
