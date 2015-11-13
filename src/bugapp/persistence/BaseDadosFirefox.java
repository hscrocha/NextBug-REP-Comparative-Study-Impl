/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.persistence;

/**
 *
 * @author Henrique
 */
public class BaseDadosFirefox extends BaseDados{
    
    private static BaseDados Instancia = null;    
    
    private BaseDadosFirefox(){
        SchemaName = "firefox";
        Conexao=connectDatabase();
    }
    
    public static BaseDados getInstancia() {
        if (Instancia == null) {
            Instancia = new BaseDadosFirefox();
        }
        return Instancia;
    }
}
