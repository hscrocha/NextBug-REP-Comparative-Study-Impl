/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

import java.sql.Date;

/**
 *
 * @author Henrique
 * @tabela Bugs
 */
public class BugComponent {
    
    private int Id;
    private int AssignedTo;
    private Date Creation;
    private Date LastResolved;
    private int ComponentId;
    private String ComponentName;
    private int ProductId;
    private String ProductName;

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
     * @return the ComponentName
     */
    public String getComponentName() {
        return ComponentName;
    }

    /**
     * @param ComponentName the ComponentName to set
     */
    public void setComponentName(String ComponentName) {
        this.ComponentName = ComponentName;
    }

    /**
     * @return the ProductId
     */
    public int getProductId() {
        return ProductId;
    }

    /**
     * @param ProductId the ProductId to set
     */
    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    /**
     * @return the ProductName
     */
    public String getProductName() {
        return ProductName;
    }

    /**
     * @param ProductName the ProductName to set
     */
    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }
    
    
}
