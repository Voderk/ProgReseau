/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteClient;

import java.io.Serializable;
import java.util.List;
import requtepoolthreads.Reponse;

/**
 *
 * @author Alex
 */
public class ReponseClient implements Reponse,Serializable{
    private int code;
    private String chargeUtile;
    private List<Article> listArticle;
    private Article AchatArticle;
    private List<Client> listClient;

    ReponseClient() {
        
    }

    public List<Article> getListArticle() {
        return listArticle;
    }

    public void setListArticle(List<Article> listArticle) {
        this.listArticle = listArticle;
    }
    
    
    public ReponseClient(int code, String chargeUtile) {
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

    public Article getAchatArticle() {
        return AchatArticle;
    }

    public void setAchatArticle(Article AchatArticle) {
        this.AchatArticle = AchatArticle;
    }

    public List<Client> getListClient() {
        return listClient;
    }

    public void setListClient(List<Client> listClient) {
        this.listClient = listClient;
    }
    
    
}
