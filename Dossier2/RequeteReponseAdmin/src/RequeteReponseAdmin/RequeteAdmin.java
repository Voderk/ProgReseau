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
public class RequeteAdmin implements Serializable {
    public static int LOGINA = 1;
    public static int LCLIENTS = 2;
    public static int STOP = 3;
    public static int LOGOUTA = 4;

    private int type;
    private String chargeUtile;
    

    public RequeteAdmin(int type, String chargeUtile) {
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
