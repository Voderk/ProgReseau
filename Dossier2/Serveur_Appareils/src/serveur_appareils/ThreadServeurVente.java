/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import RequeteClient.ClientSocket;
import RequeteClient.ClientAppareil;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static serveur_appareils.Serveur_Appareils.ReadProperties;

/**
 *
 * @author Alex
 */
public class ThreadServeurVente extends Thread{
    private int port;
    private SourceTache tachesAExecuter;
    private List<ClientSocket> Clients;
    private ServerSocket SSocket;
    int nbrClient = 0;
    
    public ThreadServeurVente(int p,SourceTache st,List<ClientSocket> c)
    {
        port = p;
        tachesAExecuter = st;
        Clients = c;
        
    }
    
    public void run()
    {
        try
        {
            SSocket = new ServerSocket(port);
        } 
        catch (IOException ex) 
        {
            System.err.println("Erreur de port d'écoute ! [" + ex + "]");
            System.exit(1);
        }
        
        int nbrThread = Integer.parseInt(ReadProperties("NbrThread"));
        //Création d'un pool de thread
        for (int i=0; i<nbrThread; i++) // 3 devrait être constante ou une propriété du fichier de config
        {
            ThreadClient thr = new ThreadClient (tachesAExecuter,"Thread du pool n°"+String.valueOf(i));
            thr.start();
        }
        
        Socket CSocket  = null;
        while(!isInterrupted())
        {
            try
            {
                System.out.println("***Serveur en attente***");
                CSocket = SSocket.accept();
                ClientSocket temp = new ClientSocket();
                temp.setCSocket(CSocket);
                int portUrgence = Integer.parseInt(ReadProperties("PortUrgence")) + nbrClient;
                temp.setPortUrgence(portUrgence);
                Clients.add(temp);
                nbrClient++;
                
                ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                oos.writeInt(portUrgence);
                oos.flush();
                
            } 
            catch (IOException ex) 
            {
                System.err.println("Erreur d'accept ! [" + ex + "]");
                System.exit(1);
            }
            
            ClientAppareil req = new ClientAppareil(CSocket,Clients);
            Runnable travail = req.createRunnable(CSocket);
            if(travail != null)
            {
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail mis dans la file");
            }
            else
            {
                System.out.println("Pas de travail mis dans la file");
            }
            
        }
        
    }
    
}

