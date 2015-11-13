/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class AsergCommitPath {
    private int Id;
    private int Revision;
    private char Action;
    private String Type;
    private String Path;
    
    public AsergCommitPath(){
        
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
     * @return the Revision
     */
    public int getRevision() {
        return Revision;
    }

    /**
     * @param Revision the Revision to set
     */
    public void setRevision(int Revision) {
        this.Revision = Revision;
    }

    /**
     * @return the Action
     */
    public char getAction() {
        return Action;
    }

    /**
     * @param Action the Action to set
     */
    public void setAction(char Action) {
        this.Action = Action;
    }

    /**
     * @return the Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type the Type to set
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @return the Path
     */
    public String getPath() {
        return Path;
    }

    /**
     * @param Path the Path to set
     */
    public void setPath(String Path) {
        this.Path = Path.trim();
    }
   
    public boolean equals(AsergCommitPath A){
        return Path.equalsIgnoreCase(A.Path);
    }
    
    
}
