/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

import java.sql.Date;
/**
 *
 * @author Henrique
 */
public class BugSimple {
    private int Id;
    private Date Creation;
    private Date LastResolved;
    private int ComponentId;

    /**
     * @return the Id
     */
    public int getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(int Id) {
        this.Id = Id;
    }

    /**
     * @return the Creation
     */
    public Date getCreation() {
        return Creation;
    }

    /**
     * @param Creation the Creation to set
     */
    public void setCreation(Date Creation) {
        this.Creation = Creation;
    }

    /**
     * @return the LastResolved
     */
    public Date getLastResolved() {
        return LastResolved;
    }

    /**
     * @param LastResolved the LastResolved to set
     */
    public void setLastResolved(Date LastResolved) {
        this.LastResolved = LastResolved;
    }

    /**
     * @return the ComponentId
     */
    public int getComponentId() {
        return ComponentId;
    }

    /**
     * @param ComponentId the ComponentId to set
     */
    public void setComponentId(int ComponentId) {
        this.ComponentId = ComponentId;
    }
    
    
    
}
