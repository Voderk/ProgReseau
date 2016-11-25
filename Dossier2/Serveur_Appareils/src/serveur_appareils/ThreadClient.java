/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class ThreadClient extends Thread
{
    private SourceTache tachesAExecuter;
    private String nom;
    private Runnable tacheEnCours;
    private List<Socket> Client;
    private Socket So;
    
    public ThreadClient(SourceTache st,String n,List<Socket> c)
    {
        tachesAExecuter = st;
        nom = n;
        Client =c;
    }
    
    public void run()
    {
         while(!isInterrupted())
         {
             try {
                 System.out.println("Thread Client avant un get");
                 tacheEnCours = tachesAExecuter.getTache();
             } 
             catch (InterruptedException ex) 
             {
                 System.out.println("Interruption : " + ex.getMessage());
             }
             System.out.println("Run de tache en cours");
             tacheEnCours.run();
             
             
         }
    }
}
