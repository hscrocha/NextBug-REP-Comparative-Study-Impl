/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 * 
 * @tabela Profiles
 */
public class Profile {
   private int Id;
   private String Login;
   private String RealName;
   /**
    * @coluna derivada, count(*)
    */
   private int QuantidadeBugs; 

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
     * @return the Login
     */
    public String getLogin() {
        return Login;
    }

    /**
     * @param Login the Login to set
     */
    public void setLogin(String Login) {
        this.Login = Login;
    }

    /**
     * @return the RealName
     */
    public String getRealName() {
        return RealName;
    }

    /**
     * @param RealName the RealName to set
     */
    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    /**
     * @return the QuantidadeBugs
     */
    public int getQuantidadeBugs() {
        return QuantidadeBugs;
    }

    /**
     * @param QuantidadeBugs the QuantidadeBugs to set
     */
    public void setQuantidadeBugs(int QuantidadeBugs) {
        this.QuantidadeBugs = QuantidadeBugs;
    }

}
