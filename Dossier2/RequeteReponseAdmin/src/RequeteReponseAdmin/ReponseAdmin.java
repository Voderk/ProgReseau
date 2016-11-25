/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponseAdmin;

import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import requtepoolthreads.Reponse;

/**
 *
 * @author Alex
 */
public class ReponseAdmin implements Reponse,Serializable{
    private int code;
    private String chargeUtile;
    private List<String> listClient;

    
    public ReponseAdmin() {
        
    }
    
    public ReponseAdmin(int code, String chargeUtile) {
        this.code = code;
        this.chargeUtile = chargeUtile;
    }

    


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getChargeUtile() {
        return chargeUtile;
    }

    public void setChargeUtile(String chargeUtile) {
        this.chargeUtile = chargeUtile;
    } 

    public List<String> getListClient() {
        return listClient;
    }

    public void setListClient(List<String> listClient) {
        this.listClient = listClient;
    }

   
    
    
    
}
