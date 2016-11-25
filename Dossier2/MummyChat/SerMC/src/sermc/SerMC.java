/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sermc;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import MumService.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
/**
 *
 * @author Gauvain Klug
 */
public class SerMC {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties param = new Properties();
        try {
            param.load(SerMC.class.getResourceAsStream("Config.properties"));
        } catch (IOException ex) {
            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
        }
        int port =  Integer.parseInt(param.getProperty("Port"));
        ServerSocket SSocket;
        Socket CSocket;
        
        Properties Users = new Properties();
        try {
            Users.load(SerMC.class.getResourceAsStream("Users.properties"));
        } catch (IOException ex) {
            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        SSocket = null;
        CSocket = null;
        ThreadUDP UDPlogger = null;
        
        int ClientID = 1;
        try {
            SSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Serveur en attente");
        try {
            MulticastSocket ds = new MulticastSocket(Integer.parseInt(param.getProperty("UDPPort")));
            ds.joinGroup(InetAddress.getByName(param.getProperty("UDPGroup")));
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            UDPlogger = new ThreadUDP(ds, "log_" + sdf.format(cal.getTime())+".txt");
            UDPlogger.start();
        } catch (SocketException ex) {
            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
        }
                
               
        
        while(true)
        {
            try {
                CSocket = SSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Connexion client effectuée");
            
            String req = TCPAccess.readLikeC(CSocket);            
            if(Protocol.ExtractProtocol(req).equals(Protocol.protocols.LOGIN_GROUP.toString()))
            {
                String nom = Protocol.ExtractParam(req, 1);
                String pwd = Protocol.ExtractParam(req, 2);
                int digest = Integer.parseInt(Protocol.ExtractParam(req, 3));
                if(digest == Crypto.h(pwd))
                {
                    if(nom.equalsIgnoreCase("kill"))
                    {
                        if(UDPlogger != null)
                            UDPlogger.setSt(Boolean.TRUE);
                        System.out.println("Message d'arret recu");
                        TCPAccess.writeLikeC(CSocket, "Arret du serveur imminent");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SerMC.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }

                    if(pwd.equals(Users.getProperty(nom)))
                    {
                        System.out.println("Login OK");
                        TCPAccess.writeLikeC(CSocket, "YES " + param.getProperty("UDPGroup") + " " + ClientID++);
                    }
                    else
                    {
                        TCPAccess.writeLikeC(CSocket, "Password invalide pour " + nom);
                        System.out.println("Login NOK");
                    }
                }
                else
                {
                    TCPAccess.writeLikeC(CSocket, "Erreur de reception du mot de passe");
                }
            }
            
            
            
        }
       
       
            
        
    }
    
}
