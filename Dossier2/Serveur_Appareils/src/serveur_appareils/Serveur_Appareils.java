/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Serveur_Appareils {
    
    private static SourceTache tachesAExecuter = new ListeTaches();
    private static List<Socket> Clients = new ArrayList<Socket>();
    
    public static void main(String[] args) throws IOException {
            
        int port = Integer.parseInt(ReadProperties("Port"));
        int portc = Integer.parseInt(ReadProperties("PortServeurC"));
        int porta = Integer.parseInt(ReadProperties("PortAdmin"));
        ThreadServeurVente ts = new ThreadServeurVente(port,new ListeTaches(),Clients);
        ThreadSerMvt tsm = new ThreadSerMvt(portc,Clients);
        ThreadAdmin ta = new ThreadAdmin(porta,Clients);
        ts.start();     
        tsm.start();
        ta.start();
    }
    
    static public String ReadProperties(String key) 
    {
        try {
            Properties config = new Properties();
           
                config.load(Serveur_Appareils.class.getResourceAsStream("Config.properties"));
                        
            return config.getProperty(key);
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        } 
    }
}
