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
public class BaseDadosMylyn extends BaseDados{
    
    private static BaseDados Instancia = null;    
    
    private BaseDadosMylyn(){
        SchemaName = "mylyn";
        Conexao=connectDatabase();
    }
    
    public static BaseDados getInstancia() {
        if (Instancia == null) {
            Instancia = new BaseDadosMylyn();
        }
        return Instancia;
    }
}
