/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteClient;

import java.io.Serializable;
import java.net.Socket;
import requtepoolthreads.Requete;

/**
 *
 * @author Alex
 */
public class RequeteClient implements Serializable {
    public static int REQUEST_LOGIN = 1;
    public static int SEARCH_GOOD = 2;
    public static int TAKE_GOOD = 3;
    public static int BUY_GOOD = 4;
    public static int DELIVERY_GOOD = 5;
    public static int LIST_SALES = 6;
    public static int ALL_CLIENT = 7;
    public static int NEW_CLIENT = 8;
    public static int SEARCH_CLIENT = 9;
    public static int REQUEST_LOGOUT = 10;
    public static int VIDE_PANIER = 11;
    
    
    
    private int type;
    private String chargeUtile;
    

    public RequeteClient(int type, String chargeUtile) {
        this.type = type;
        this.chargeUtile = chargeUtile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChargeUtile() {
        return chargeUtile;
    }

    public void setChargeUtile(String chargeUtile) {
        this.chargeUtile = chargeUtile;
    }
}
