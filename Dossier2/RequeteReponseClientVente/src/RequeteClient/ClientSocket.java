/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteClient;

import java.net.Socket;

/**
 *
 * @author Alex
 */
public class ClientSocket {
    private Socket CSocket;
    private int portUrgence;

    public ClientSocket() {
    }

    public Socket getCSocket() {
        return CSocket;
    }

    public void setCSocket(Socket CSocket) {
        this.CSocket = CSocket;
    }

    public int getPortUrgence() {
        return portUrgence;
    }

    public void setPortUrgence(int portUrgence) {
        this.portUrgence = portUrgence;
    }
    
    
}
