/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class BugCluster {
    
    private int Id;
    private float Dist;
    private int AssignedId;
    private String ShortDesc;
    private java.sql.Date LastResolved;
    private java.sql.Date Creation;
    private String Resolution;
    private int ComponentId;
    
    public BugCluster(){
        
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
     * @return the AssignedId
     */
    public int getAssignedId() {
        return AssignedId;
    }

    /**
     * @param AssignedId the AssignedId to set
     */
    public void setAssignedId(int AssignedId) {
        this.AssignedId = AssignedId;
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

    /**
     * @return the Creation
     */
    public java.sql.Date getCreation() {
        return Creation;
    }

    /**
     * @param Creation the Creation to set
     */
    public void setCreation(java.sql.Date Creation) {
        this.Creation = Creation;
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
     * @return the Dist
     */
    public float getDist() {
        return Dist;
    }

    /**
     * @param Dist the Dist to set
     */
    public void setDist(float Dist) {
        this.Dist = Dist;
    }

    /**
     * @return the Resolution
     */
    public String getResolution() {
        return Resolution;
    }

    /**
     * @param Resolution the Resolution to set
     */
    public void setResolution(String Resolution) {
        this.Resolution = Resolution;
    }
    
}
