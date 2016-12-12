/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import RequeteClient.ClientSocket;
import RequeteClient.RequeteAdmin;
import RequeteReponseAdmin.ReponseAdmin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class ThreadAdmin extends Thread {
    Socket CSocket = null;
    private int port;
    private ServerSocket SSocket;
    boolean connected = false;
    List<ClientSocket> Client;
    
    public ThreadAdmin(int p,List<ClientSocket> c)
    {
        port = p;
        Client = c;
        
    }
    
    public void run()
    {
        RequeteAdmin req = null;
        try
        {
            SSocket = new ServerSocket(port);
        } 
        catch (IOException ex) 
        {
            System.err.println("Erreur de port d'écoute ! [" + ex + "]");
            System.exit(1);
        }
        
        System.out.println("Connexion établie !");
        while(!isInterrupted())
        {
            System.out.println("***Serveur en attente***");
            try 
            {
                
                CSocket = SSocket.accept();
                connected = true;
            } 
            catch (IOException ex) 
            {
                System.err.println("Erreur de port d'écoute ! [" + ex + "]");
                System.exit(1);
            }
            ObjectInputStream ois = null;
            while(connected)
            {
                try
                {
                    ois = new ObjectInputStream(CSocket.getInputStream());
                    req = (RequeteAdmin)ois.readObject();
                }
                catch(ClassNotFoundException e)
                {
                    System.err.println("Erreur de def de classe["+ e.getMessage()+"]");
                }
                catch(IOException e)
                {
                    System.err.println("Erreur ? ["+ e.getMessage()+"]");
                }

                switch(req.getType())
                {
                    case 1:
                        traiteLogin(req.getChargeUtile());
                        break;
                    case 2: 
                        traitListSocket();
                        break;
                    case 3:
                        traiteStop();
                        break;
                    case 4:
                        traiteLogout(req.getChargeUtile());
                        connected = false;
                        break;

                }
            }
            try {
                CSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ThreadAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void traiteLogin(String chargeUtile) {
        ResultSet Result;
        BeanSQL MySQLBean;
        ReponseAdmin rep = null;

        try {
            MySQLBean = ConnexionBD();
            StringTokenizer st = new StringTokenizer(chargeUtile,"#");
            String Login = st.nextToken();
            String Password = st.nextToken();
            
            String requete = "SELECT Password FROM loginvendeur where login like '" + Login +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                
                String TruePassword = Result.getString(1);

                if(Password.equals(TruePassword))
                {
                    rep = new ReponseAdmin(1,"OK");
                }
                else
                {
                    rep = new ReponseAdmin(1,"Mot de passe invalide");
                }
            }
            else
                rep = new ReponseAdmin(1,"Login invalide");
            MySQLBean.SQLClose();
            
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println(ex.getMessage());
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
            rep = new ReponseAdmin(-1,"Erreur de la base de donnée");
        }
        
        finally
        {
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(CSocket.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }
        }
        
    }
    
    private void traitListSocket() {
        ReponseAdmin rep = new ReponseAdmin();
        List<String> Socket = new ArrayList<String>();
        String temp;
        ClientSocket tempcs;
        Socket temps;
        ObjectOutputStream oos = null;
        try
        {
            for(int i =0;i<Client.size();i++)
            {
                tempcs = Client.get(i);
                temps = tempcs.getCSocket();
                temp = temps.getInetAddress().toString() + " " + temps.getPort();
                Socket.add(temp);
            }
            rep.setListClient(Socket);
            
            oos = new ObjectOutputStream(CSocket.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch(IOException e)
        {
            System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
        } 
    }
    
    // <editor-fold defaultstate="collapsed" desc="Connexion_BD">
    private BeanSQL ConnexionBD() throws ClassNotFoundException, SQLException
    {
        BeanSQL MySQLBean = new BeanSQL();
        
        MySQLBean.setDriver("com.mysql.jdbc.Driver");

        MySQLBean.setUser("root");
        MySQLBean.setPwd("oracle");
        MySQLBean.setUrl("jdbc:mysql://localhost/bd_societe");
        
        MySQLBean.SQLConnect();
        
        return MySQLBean;
    }
    // </editor-fold>

    private void traiteStop() {
        try {
            for(int i=0;i<Client.size();i++)
            {
                ClientSocket temp = Client.get(i);
                Socket s = new Socket(temp.getCSocket().getInetAddress(),temp.getPortUrgence());
                s.close();
            }
        } catch (IOException ex) {
            System.err.println("Erreur réseau Admin? ["+ex.getMessage()+"]");
        }
    }

    private void traiteLogout(String chargeUtile) {
        ResultSet Result;
        BeanSQL MySQLBean;
        ReponseAdmin rep = null;

        try {
            MySQLBean = ConnexionBD();
            StringTokenizer st = new StringTokenizer(chargeUtile,"#");
            String Login = st.nextToken();
            String Password = st.nextToken();
            
            String requete = "SELECT Password FROM loginvendeur where login like '" + Login +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                
                String TruePassword = Result.getString(1);

                if(Password.equals(TruePassword))
                {
                    rep = new ReponseAdmin(1,"OK");
                    connected = false;
                }
                else
                {
                    rep = new ReponseAdmin(1,"Mot de passe invalide");
                }
            }
            else
                rep = new ReponseAdmin(1,"Login invalide");
            MySQLBean.SQLClose();
            
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println(ex.getMessage());
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
            rep = new ReponseAdmin(-1,"Erreur de la base de donnée");
        }
        
        finally
        {
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(CSocket.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }
            
        }
    }

    
}
