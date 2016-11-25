/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import RequeteClient.Article;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class ThreadSerMvt extends Thread
{
    Socket CSocket = null;
    private int port;
    private ServerSocket SSocket;
    String Fournisseur;
    String Zone;
    int Capacité;
    int NbrArticle = 0;
    boolean connected = false;
    List<Socket> SocketClient;
    public ThreadSerMvt(int p,List<Socket> c)
    {
        port = p;
        SocketClient = c;
    }
    
    public void run()
    {
        int command;
        
        List<String> parameters = new ArrayList<String>();
        try
        {
            SSocket = new ServerSocket(port);
        } 
        catch (IOException ex) 
        {
            System.err.println("Erreur de port d'écoute ! [" + ex + "]");
            System.exit(1);
        }
        while(!isInterrupted())
        {
            System.out.println("***Serveur en attente***");
            try 
            {
                CSocket = SSocket.accept();
                SocketClient.add(CSocket);
                connected = true;
            } 
            catch (IOException ex) 
            {
                System.err.println("Erreur de port d'écoute ! [" + ex + "]");
                System.exit(1);
            }

            System.out.println("Connexion établie !");

            while(connected)
            {
                String requete = null;
                try 
                {
                    requete = ReceiveMessage(CSocket);
                    System.out.println(requete);
                } 
                catch (IOException ex) 
                {
                    System.err.println("Erreur lors du receive ! [" + ex + "]");
                    System.exit(1);
                }

                command = getCommand(requete);
                parameters = getParameter(requete);

                switch(command)
                {
                    case 1:
                        traiteLogin(parameters);
                        break;
                    case 2:
                        traiteInputDevice1(parameters);
                        break;
                    case 3:
                        traiteInputDevice2(parameters);
                        break;
                    case 4:
                        traiteGetDelivery1(parameters);
                        break;
                    case 5:
                        traiteGetDelivery2(parameters);
                        break;
                    case 6:
                        traiteGetDeliveryEnd(parameters);
                        break;
                    case 7:
                        traiteDeconnect(parameters);
                        break;
                    case 8: 
                        traiteEOC(parameters);
                        connected = false;
                        break;
                }
  
            }
        }
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc="Command">
    private int getCommand(String requete)
    {
        StringTokenizer st = new StringTokenizer(requete,":");
        String command = st.nextToken();
        
        switch(command)
        {
            case "CONNECT":
                return 1;
            case "INPUT_DEVICES1":
                return 2;
            case "INPUT_DEVICES2":
                return 3;
            case "GET_DELIVERY1":
                return 4;
            case "GET_DELIVERY2":
                return 5;
            case "GET_DELIVERY_END":
                return 6;
            case "DECONNECT":
                return 7;
            case "EOC":
                return 8;
        }
        return 0;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Parameter">
    private List<String> getParameter(String requete)
    {
        String temp;
        List<String> parameter = new ArrayList<String>();
        
        StringTokenizer st = new StringTokenizer(requete,":");
        String command = st.nextToken();
        String param = st.nextToken();
        st = new StringTokenizer(param,"#");
        param = st.nextToken();
        st = new StringTokenizer(param,"/");
        try
        {
            do
            {
                temp = st.nextToken();
                parameter.add(temp);
            }while(temp != null);
        }
        catch(NoSuchElementException ex)
        {
            
        }

        return parameter;
    }
        // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Login">
    private void traiteLogin(List<String> parameters) {
        BeanSQL MySQLBean = null;
        try {
            String Login,Password;
            ResultSet Result;
            Login = parameters.get(0);
            Password = parameters.get(1);
            MySQLBean = ConnexionBD();
            String requete = "SELECT Password FROM loginvendeur where login like '" + Login +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                String TruePassword = Result.getString(1);
                
                if(Password.equals(TruePassword))
                {
                    SendMessage(CSocket,"OK\r\n");
                }
                else
                {
                    SendMessage(CSocket,"NOKPASSWORD\r\n");
                }
            }
            else
                SendMessage(CSocket,"NOKLOGIN\r\n");
            
            MySQLBean.SQLClose();
        } 
        catch (ClassNotFoundException ex) 
        {
            System.err.println("Erreur class not found! [" + ex + "]");
            
        } 
        catch (SQLException ex) 
        {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            try {
                SendMessage(CSocket,"ERRORBD\r\n");
            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
            
        } catch (IOException ex) 
        {
            System.err.println("Erreur Reseau ! [" + ex + "]");
        } 
        
            
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Input_device1">
    private void traiteInputDevice1(List<String> parameters) {
        BeanSQL MySQLBean = null;
        try {
            System.out.println("ICI");
            ResultSet Result;
            Fournisseur = parameters.get(0);
            MySQLBean = ConnexionBD();
            String requete = "SELECT * FROM fournisseur where nom like '" + Fournisseur +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                SendMessage(CSocket,"OK\r\n");
            }
            else
                SendMessage(CSocket,"NOK\r\n");
            
            MySQLBean.SQLClose();
        } 
        catch (ClassNotFoundException ex) 
        {
            System.err.println("Erreur class not found! [" + ex + "]");
            System.exit(1);
        } 
        catch (SQLException ex) 
        {
            System.err.println("Erreur de la base de donnée! [" + ex + "]");
            try {
                SendMessage(CSocket,"ERRORBD\r\n");
            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
        } catch (IOException ex) 
        {
            System.err.println("Erreur lors du receive ! [" + ex + "]");
            System.exit(1);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Input_device2">
    private void traiteInputDevice2(List<String> parameters) {
        
        List<Article> NewArticles = new ArrayList<Article>();
        
        //Initialisation des articles
        for(int i = 0; i< parameters.size();i++)
        {
            Article temp =  new Article();
            StringTokenizer st = new StringTokenizer(parameters.get(i),";");
            temp.setNom(st.nextToken());
            temp.setMarque(st.nextToken());
            temp.setType(st.nextToken());
            temp.setPrix(Float.parseFloat(st.nextToken()));
            temp.setQuantite(Integer.parseInt(st.nextToken()));
            NewArticles.add(temp);
        }

        BeanSQL MySQLBean = null;
        String Emplacement = "INPUT_DEVICES2:";
        try {
            ResultSet Result;
            MySQLBean = ConnexionBD();
            
            for(int j=0;j<NewArticles.size();j++)
            {
                boolean insert  =true;
                Article temp = NewArticles.get(j);
                int x,y;
                
                MySQLBean.SQLStartTransaction();
                int TypePrecis = 0;
                String requete = "SELECT * FROM typeappareils where Libellé like '"+ temp.getNom()+"'";
                Result = MySQLBean.SQLExecuteQuery(requete);
                boolean res = Result.next();
                if(res)
                {
                    TypePrecis = Integer.parseInt(Result.getString("TypePrécis"));
                    requete = "SELECT * FROM emplacements where TypeArticle like '"+TypePrecis +"'";
                    Result = MySQLBean.SQLExecuteQuery(requete);
                    Result.next();
                    x = Result.getInt("X");
                    y = Result.getInt("Y");
                    if(j == 0)
                    {
                        Emplacement = Emplacement + x +"-" +y;
                    }
                    else
                    {
                        Emplacement = Emplacement+"/" + x +"-" +y ;
                    }
                                       
                }
                else
                {
                    requete = "SELECT * FROM emplacements where TypeArticle is null";
                    Result = MySQLBean.SQLExecuteQuery(requete);
                    res = Result.next();
                    if(res)
                    {
                        x = Result.getInt("X");
                        y = Result.getInt("Y");
                        if(j == 0)
                        {
                            Emplacement = Emplacement + x +"-" +y;
                        }
                        else
                        {
                            Emplacement = Emplacement+"/" + x +"-" +y ;
                        }
                        requete = "SELECT Max(CAST(TypePrécis AS UNSIGNED)) FROM typeappareils";
                        Result = MySQLBean.SQLExecuteQuery(requete);
                        Result.next();

                        TypePrecis = Result.getInt(1) + 1;

                        requete = "SELECT * FROM fournisseur where nom ='"+ Fournisseur +"'";
                        Result = MySQLBean.SQLExecuteQuery(requete);
                        Result.next();

                        int IDFournisseur = Result.getInt(1);


                        requete = "INSERT INTO typeappareils (TypePrécis,Marque,Libellé,PrixAchat,PrixVenteBase,Fournisseur) VALUES ('"+ TypePrecis  +"', '"+ temp.getMarque()+"', '"+ temp.getNom() +"', '"+temp.getPrix()+"', '"+temp.getPrix() +50+"', '"+IDFournisseur+"')";
                        MySQLBean.SQLExecute(requete);

                        requete = "UPDATE emplacements SET TypeArticle='"+ TypePrecis+"' WHERE X ='"+ x+"' and Y ='"+y+"'";
                        MySQLBean.SQLExecute(requete);
                    }
                    else
                    {
                        insert = false;
                        Emplacement = Emplacement + "Pas de place disponible pour cette article";
                        if(j == 0)
                        {
                            Emplacement = Emplacement + "Pas de place disponible pour cette article";
                        }
                        else
                        {
                            Emplacement = Emplacement+"/" + "Pas de place disponible pour cette article";
                        }
                    }

                }
                
                if(insert)
                {
                    requete = "SELECT Max(NumSerie) FROM appareils";
                    Result = MySQLBean.SQLExecuteQuery(requete);
                    Result.next();
                    int NumArticle = Result.getInt(1) +1 ;

                    for(int k = 0;k < temp.getQuantite();k++)
                    {
                        requete = "INSERT INTO appareils(NumSerie,TypeGénéral,TypePrécis,PrixVente) VALUES ('"+NumArticle+"', '"+temp.getType() +"', '"+ TypePrecis +"', '"+ temp.getPrix()+50 +"')";
                        MySQLBean.SQLExecute(requete);
                        NumArticle++;
                    }
                }
            }
            MySQLBean.SQLCommit();
            Emplacement = Emplacement + "#\r\n";
            
            
            
            SendMessage(CSocket,Emplacement);
            
            MySQLBean.SQLClose();
        } 
        catch (ClassNotFoundException ex) 
        {
            System.err.println("Erreur class not found! [" + ex + "]");
            System.exit(1);
        } 
        catch (SQLException ex) 
        {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            try {
                SendMessage(CSocket,"INPUT_DEVICES2:ERRORBD#\r\n");
            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
        } 
        catch (IOException ex) 
        {
            System.err.println("Erreur lors du receive ! [" + ex + "]");
            System.exit(1);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get_Delivery1">
    private void traiteGetDelivery1(List<String> parameters) {
        ResultSet Result;
        BeanSQL MySQLBean = new BeanSQL(); 
        String NumVehicule = parameters.get(0);
        Zone = parameters.get(1);
        String Reponse;
        try
        {
            MySQLBean = ConnexionBD();
            MySQLBean.SQLStartTransaction();
            String requete = "Select * from vehicules where PlaqueImmatriculation = '"+ NumVehicule+"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            Boolean res = Result.next();
            
            if(res)
            {
                Capacité = Result.getInt("Capacité");
                requete = "Select * from Appareils inner join typeappareils on Appareils.typePrécis = typeappareils.TypePrécis where destination = '"+ Zone +"' and EtatPaiement like 'Libre'";
                Result = MySQLBean.SQLExecuteQuery(requete);
                res = Result.next();

                if(res)
                {
                    String NumSerie = Result.getString("NumSerie");
                    Reponse = "GET_DELIVERY1:YES/" + Result.getString("NumSerie") + "/" + Result.getString("Libellé") + "#\r\n";
                    requete = "UPDATE appareils SET EtatPaiement= 'Livré' WHERE NumSerie='"+ NumSerie +"'";
                    MySQLBean.SQLExecute(requete);
                    MySQLBean.SQLCommit(); 
                    NbrArticle++;
                }
                else
                {
                    Reponse = "GET_DELIVERY1:NO/NoArticle#\r\n";
                }
            }
            else
            {
                Reponse = "GET_DELIVERY1:NO/NoCamion#\r\n";
            }
            SendMessage(CSocket,Reponse);
        } 
        catch (SQLException ex) {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            try {
                SendMessage(CSocket,"GET_DELIVERY1:ERRORBD#\r\n");

            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
        } 
        catch (IOException ex) {
            System.err.println("Erreur lors du receive ! [" + ex + "]");
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            System.exit(1);
        }
   
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get_Delivery2">
    private void traiteGetDelivery2(List<String> parameters) {
        ResultSet Result;
        BeanSQL MySQLBean; 
        String Reponse;
        
        try
        {
            if(NbrArticle < Capacité)
            {
                MySQLBean = ConnexionBD();
                MySQLBean.SQLStartTransaction();
                String requete = "Select * from Appareils inner join typeappareils on Appareils.typePrécis = typeappareils.TypePrécis where destination = '"+ Zone +"' and EtatPaiement like 'Libre'";
                Result = MySQLBean.SQLExecuteQuery(requete);
                boolean res = Result.next();

                if(res)
                {
                    String NumSerie = Result.getString("NumSerie");
                    Reponse = "GET_DELIVERY2:YES/" + Result.getString("NumSerie") + "/" + Result.getString("Libellé") + "#\r\n";
                    requete = "UPDATE appareils SET EtatPaiement= 'Livré' WHERE NumSerie='"+ NumSerie +"'";
                    MySQLBean.SQLExecute(requete);
                    MySQLBean.SQLCommit(); 
                    NbrArticle++;
                }
                else
                {
                    Reponse = "GET_DELIVERY2:NO/NoArticle#\r\n";
                }
            }
            else
            {
                Reponse = "GET_DELIVERY2:NO/NoPlace!\r\n";
            }
            SendMessage(CSocket,Reponse);
        }
        catch (SQLException ex) {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            try {
                SendMessage(CSocket,"GET_DELIVERY2:ERRORBD#\r\n");
            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
        } 
        catch (IOException ex) {
            System.err.println("Erreur lors du receive ! [" + ex + "]");
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            System.err.println("Erreur base de donnée ! [" + ex + "]");
            System.exit(1);
        }  
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get_Delivery_End">
    private void traiteGetDeliveryEnd(List<String> parameters) {
        String Reponse = "OK\r\n";
        NbrArticle = 0;
        
        try {
            SendMessage(CSocket,Reponse);
        } catch (IOException ex) {
            Logger.getLogger(ThreadSerMvt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Deconnect">
    private void traiteDeconnect(List<String> parameters) {
        BeanSQL MySQLBean = null;
        try {
            String Login,Password;
            ResultSet Result;
            Login = parameters.get(0);
            Password = parameters.get(1);
            MySQLBean = ConnexionBD();
            String requete = "SELECT Password FROM loginvendeur where login like '" + Login +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                String TruePassword = Result.getString(1);
                
                if(Password.equals(TruePassword))
                {
                    SendMessage(CSocket,"OK\r\n");
                }
                else
                {
                    SendMessage(CSocket,"NOKPASSWORD\r\n");
                }
            }
            else
                SendMessage(CSocket,"NOKLOGIN\r\n");
            
            MySQLBean.SQLClose();
        } 
        catch (ClassNotFoundException ex) 
        {
            System.err.println("Erreur class not found! [" + ex + "]");
            System.exit(1);
        } 
        catch (SQLException ex) 
        {
             System.err.println("Erreur base de donnée ! [" + ex + "]");
            try {
                SendMessage(CSocket,"ERRORBD\r\n");
            } catch (IOException ex1) {
                 System.err.println("Erreur Reseau ! [" + ex + "]");
            }
        } catch (IOException ex) 
        {
            System.err.println("Erreur lors du receive ! [" + ex + "]");
            System.exit(1);
        } 
        try {
            CSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ThreadSerMvt.class.getName()).log(Level.SEVERE, null, ex);
        }
        SocketClient.remove(CSocket);
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="EOC">
    private void traiteEOC(List<String> parameters) {
        try {
            CSocket.close();
            SocketClient.remove(CSocket);
            connected = false;
        } catch (IOException ex) {
            Logger.getLogger(ThreadSerMvt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold>
    
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
    
    // <editor-fold defaultstate="collapsed" desc="Send/Receive">
    private String ReceiveMessage(Socket s) throws IOException
    {
        StringBuffer message = new StringBuffer();
        byte b = 0,c = 0;
        boolean end = false, nearend = false; 
        DataInputStream dis = new DataInputStream( new BufferedInputStream(s.getInputStream()));
        while(!end)
        {
            b = dis.readByte();
            if(b == '\r' && nearend == false)
                nearend= true;
            else
            {
                if(b== '\n' && nearend == true)
                    end  = true;
                else
                {
                    nearend = false;
                    message.append((char) b);
                }
            }
        }
        return new String(message); 
    }
    
    private void SendMessage(Socket s, String Message) throws IOException
    {
        /*Message = Message + "\r\n";
        byte[] array = Message.getBytes();*/
        
        
        
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        dos.write(Message.getBytes());
        dos.flush();
    }
    // </editor-fold>

    
 
}
