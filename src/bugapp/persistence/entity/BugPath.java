/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Henrique
 */
public class BugPath {
    private int BugId;
    private java.sql.Date DtCreation;
    private java.sql.Date DtClose;
    private String ShortDesc;
    private String FullDesc = "";
    private int ComponentId = 0;
    private String ComponentName = null;
    private int DeveloperId = 0;
    private String DeveloperName = null;
    private ArrayList<String> lstPaths;

    //The Following fields are for REP
    protected int ProductId = 0;
    protected String Type = null;
    protected int Priority = 0;
    protected int Version = 0;
    
    public BugPath(){
        lstPaths=new ArrayList<String>();
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
     * @return the DtCreation
     */
    public java.sql.Date getDtCreation() {
        return DtCreation;
    }

    /**
     * @param DtCreation the DtCreation to set
     */
    public void setDtCreation(java.sql.Date DtCreation) {
        this.DtCreation = DtCreation;
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
     * @return the lstPaths
     */
    public ArrayList<String> getPaths() {
        return lstPaths;
    }

    /**
     * @param lstPaths the lstPaths to set
     */
    public void setPaths(ArrayList<String> lstPaths) {
        this.lstPaths = lstPaths;
    }
    
    
    /**
     * 
     */
    public void addPath(String Path){
        lstPaths.add(Path);
    }
    
    /**
     * 
     */
    public void clearPathData(){
        lstPaths.clear();
    }    

    /**
     * @return the DtClose
     */
    public java.sql.Date getDtClose() {
        return DtClose;
    }

    /**
     * @param DtClose the DtClose to set
     */
    public void setDtClose(java.sql.Date DtClose) {
        this.DtClose = DtClose;
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
    public void setComponent(int ComponentId) {
        this.ComponentId = ComponentId;
    }

    /**
     * @return the DeveloperId
     */
    public int getDeveloperId() {
        return DeveloperId;
    }

    /**
     * @param DeveloperId the DeveloperId to set
     */
    public void setDeveloper(int DeveloperId) {
        this.DeveloperId = DeveloperId;
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
    public void setComponent(String ComponentName) {
        this.ComponentName = ComponentName;
    }

    /**
     * @return the DeveloperName
     */
    public String getDeveloperName() {
        return DeveloperName;
    }

    /**
     * @param DeveloperName the DeveloperName to set
     */
    public void setDeveloper(String DeveloperName) {
        this.DeveloperName = DeveloperName;
    }
    
    public boolean isSameDeveloper(BugPath B){
        if(DeveloperName == null){
            return DeveloperId == B.DeveloperId;
        }                
        else{
            return DeveloperName.equals(B.DeveloperName);
        }
    }
    
    public boolean isSameComponent(BugPath B){
        if(ComponentName == null){
            return ComponentId == B.ComponentId;
        }                
        else{
            return ComponentName.equals(B.ComponentName);
        }
    }
    
    public boolean isSameType(BugPath B){
        if(Type!=null && B.Type!=null){
            return Type.equals(B.Type);
        }
        return false;
    }
    
    public boolean isSameProduct(BugPath B){
        return ProductId == B.ProductId;
    } 
    
    public int priorityDif(BugPath B){
        return Math.abs(Priority - B.Priority);
    }
    
    public int versionDif(BugPath B){
        return Math.abs(Version - B.Version);
    }
    
    public float overlap(BugPath Bi){
        float over = 0, intersect,  min;

        if(this.getPaths().isEmpty() || Bi.getPaths().isEmpty()){
            over = -1;
        }
        else if(this != Bi) {
            
            intersect = bugapp.util.SetUtil.intersection(this.getPaths(), Bi.getPaths());
            min = bugapp.util.SetUtil.min(this.getPaths(), Bi.getPaths());

            over = intersect / min;
        }
        return over;
    }

    /**
     * @return the ProductIt
     */
    public int getProductId() {
        return ProductId;
    }

    /**
     * @param ProductId the ProductIt to set
     */
    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
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
     * @return the Priority
     */
    public int getPriority() {
        return Priority;
    }

    /**
     * @param strPriority the Priority to set
     */
    public void setPriority(String strPriority) {
        switch(strPriority){
            case "P1": 
                this.Priority = 1;
                break;

            case "P2": 
                this.Priority = 2;
                break;

            case "P3": 
                this.Priority = 3;
                break;

            case "P4": 
                this.Priority = 4;
                break;

            case "P5": 
                this.Priority = 5;
                break;
                
            default:
                this.Priority = 0;
                break;
        }
    }
    
    public void setVersion(String strVersion){
        strVersion = strVersion.replaceAll("[^\\d.]", "");
        StringTokenizer stk = new StringTokenizer(strVersion, ".", false);
        
        if(stk.hasMoreTokens()){
            this.Version = Integer.parseInt(stk.nextToken())*10;
            while(stk.hasMoreTokens()){
                String ver = stk.nextToken();
                if(ver.length()>0)
                    Version += Integer.parseInt( ver );
            }
        }
        else{
            this.Version = 0;
        }
    }
    
    public int getVersion(){
        return Version;
    }

    /**
     * @return the FullDesc
     */
    public String getFullDesc() {
        return FullDesc;
    }

    /**
     * @param FullDesc the FullDesc to set
     */
    public void setFullDesc(String FullDesc) {
        this.FullDesc = FullDesc;
    }

}
