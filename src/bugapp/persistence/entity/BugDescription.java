/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class BugDescription {
    
    private int Id;
    private int ComponentId;
    private int AssignedTo;
    private java.sql.Date LastResolved;
    private String ShortDesc;
    
    public BugDescription(){
        
    }

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

    /**
     * @return the AssignedTo
     */
    public int getAssignedTo() {
        return AssignedTo;
    }

    /**
     * @param AssignedTo the AssignedTo to set
     */
    public void setAssignedTo(int AssignedTo) {
        this.AssignedTo = AssignedTo;
    }

    /**
     * @return the ShortDesc
     */
    public String getShortDesc() {
        return ShortDesc;
    }

    /**
     * @param ShortDesc the ShortDesc to set
     */
    public void setShortDesc(String ShortDesc) {
        this.ShortDesc = ShortDesc;
    }

    /**
     * @return the LastResolved
     */
    public java.sql.Date getLastResolved() {
        return LastResolved;
    }

    /**
     * @param LastResolved the LastResolved to set
     */
    public void setLastResolved(java.sql.Date LastResolved) {
        this.LastResolved = LastResolved;
    }

    
}
