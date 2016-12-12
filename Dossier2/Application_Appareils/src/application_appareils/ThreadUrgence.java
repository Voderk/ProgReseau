/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application_appareils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Alex
 */
public class ThreadUrgence extends Thread {
    
    private int port;
    private Applic_Appareil app;

    public ThreadUrgence(int port) {
        this.port = port;
    }

    public ThreadUrgence(int i, Applic_Appareil appli) {
       port = i;
       app = appli;
    }
    
    public void run()
    {
        while(!isInterrupted())
        {
            try 
            { 
                System.out.println("ThreadUrgence ! Port: "+port);
                ServerSocket SocketUrgence = new ServerSocket(port);

                Socket s = SocketUrgence.accept();
                
                System.out.println("Urgence !!");
                
                JOptionPane.showMessageDialog(app,"Le serveur va s'arrêter dans 60 secondes!","Stop !",JOptionPane.WARNING_MESSAGE,null);
                Thread.sleep(5000);
                app.StopApplic();
                
            } 
            catch (IOException ex) 
            {
                System.out.println("Erreur de réseau:" + ex.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadUrgence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
