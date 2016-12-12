/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteClient;

import com.sun.jmx.snmp.daemon.CommunicationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import requtepoolthreads.Requete;

/**
 *
 * @author Alex
 */
public class ClientAppareil implements Requete {
    
    Socket CSocket;
    private static int CONNECT = 1;
    private static int DISCONNECT = 2;
    private int etat = 0;
    private static List<Article> BuyArticle;
    private static List<Article> ListSales;
    private List<ClientSocket> SocketClient;

    public ClientAppareil(Socket CSocket,List<ClientSocket> c) {
        CSocket = CSocket;
        BuyArticle = new ArrayList<Article>();
        ListSales = new ArrayList<Article>();
        SocketClient = c;
    }
    
    public Runnable createRunnable(Socket s) {
        return new Runnable()
        {
            public void run()
            {
                traiteClient(s);
            }
        };
    }
    
    // <editor-fold defaultstate="collapsed" desc="Boucle">
    private void traiteClient(Socket s)
    {
        
        ObjectInputStream ois = null;
        RequeteClient req = null;

        do
        {
            try
            {
                ois = new ObjectInputStream(s.getInputStream());
                req = (RequeteClient)ois.readObject();
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
                case 1: //REQUEST_LOGIN
                    traiteLogin(s,req.getChargeUtile());                
                    break;
                case 2: //SEARCH GOOD
                    traiteSearchGood(s,req.getChargeUtile());
                    break;
                case 3:
                    traiteTakeGood(s,req.getChargeUtile());
                    break;
                case 4:
                    traiteBuyGood(s,req.getChargeUtile());
                    break;
                case 5:
                    traiteDeliveryGood(s,req.getChargeUtile());
                    break;
                case 6:
                    traiteListSales(s);
                    break;
                case 7:
                    traiteSearchAllClient(s);
                    break;
                case 8:
                    traiteNewClient(s,req.getChargeUtile());
                    break;
                case 9:
                    traiteSearchClient(s,req.getChargeUtile());
                    break;
                case 10:
                    traiteLogout(s,req.getChargeUtile());
                    break;
                case 11:
                    traiteVidePanier(s);
                    break;
            }
        }while(etat != DISCONNECT);
        
        
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Login">
    public  void traiteLogin(Socket s,String Chu) {
        
        ResultSet Result;
        BeanSQL MySQLBean;
        ReponseClient rep = null;

        try {
            MySQLBean = ConnexionBD();
            StringTokenizer st = new StringTokenizer(Chu,"#");
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
                    rep = new ReponseClient(1,"OK");
                    etat = CONNECT;
                }
                else
                {
                    rep = new ReponseClient(1,"Mot de passe invalide");
                }
            }
            else
                rep = new ReponseClient(1,"Login invalide");
            MySQLBean.SQLClose();
            
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println(ex.getMessage());
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
            rep = new ReponseClient(-1,"Erreur de la base de donnée");
        }
        
        finally
        {
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Search_Good">
    private void traiteSearchGood(Socket s, String chargeUtile) {
        
        List<Article> listArticle = new ArrayList<Article>();
        Article temp;
        ResultSet Result;
        BeanSQL MySQLBean ;
        ReponseClient rep = null;

        try {
            MySQLBean = ConnexionBD();
            //MySQLBean.SQLConnect();
            
            String requete = "Select *, count(*) as NbrAppareil from Appareils inner join typeappareils on (appareils.TypePrécis = typeappareils.TypePrécis) where TypeGénéral = '" + chargeUtile + "' and EtatPaiement = 'Libre' group by Appareils.TypePrécis";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                do
                {
                   temp = new Article();
                   temp.setQuantite(Result.getInt("NbrAppareil"));
                   temp.setNom(Result.getString("Libellé"));
                   temp.setMarque(Result.getString("Marque"));
                   temp.setPrix(Result.getFloat("PrixVente"));
                   temp.setEtatPaiement(Result.getString("EtatPaiement"));
                   listArticle.add(temp); 
                }while(Result.next());
                
                rep = new ReponseClient();
                rep.setCode(1);
                rep.setListArticle(listArticle);

                MySQLBean.SQLClose();
            }
            else
            {
                rep = new ReponseClient();
                rep.setCode(2);
                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            rep = new ReponseClient(-1,"Erreur de la base de donnée");
        } catch (ClassNotFoundException ex) {
            System.err.println("Erreur de la base de donnée: "+ ex.getMessage());
        }
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch(IOException e)
        {
            System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Take_Good">
    private static synchronized void traiteTakeGood(Socket s, String chargeUtile) {
        
        Article temp;
        ResultSet Result;
        BeanSQL MySQLBean = new BeanSQL();
        ReponseClient rep;
        MySQLBean.setDriver("com.mysql.jdbc.Driver");

        MySQLBean.setUser("root");
        MySQLBean.setPwd("oracle");
        MySQLBean.setUrl("jdbc:mysql://localhost/bd_societe");
        try { 
            
            MySQLBean.SQLConnect();
            MySQLBean.SQLStartTransaction();
            String requete = "Select * from Appareils inner join typeappareils on (appareils.TypePrécis = typeappareils.TypePrécis) where Libellé = '" + chargeUtile + "' and EtatPaiement = 'Libre'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            boolean res = Result.next();
            if(res)
            {
                temp = new Article();
                temp.setNumSerie(Result.getInt("NumSerie"));
                temp.setNom(Result.getString("Libellé"));
                temp.setMarque(Result.getString("Marque"));
                temp.setPrix(Result.getFloat("PrixVente"));
                temp.setEtatPaiement(Result.getString("EtatPaiement"));
                BuyArticle.add(temp);
                
                requete = "UPDATE appareils SET EtatPaiement='Reservé' WHERE NumSerie='"+temp.getNumSerie() +"'";
                MySQLBean.SQLExecute(requete);
                MySQLBean.SQLCommit();
                
                rep = new ReponseClient();
                rep.setCode(1);
                rep.setAchatArticle(temp);
                ObjectOutputStream oos = null;
                try
                {
                    oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(rep);
                    oos.flush();
                }
                catch(IOException e)
                {
                    System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
                }

                MySQLBean.SQLClose();
            }
            else
            {
                rep = new ReponseClient();
                rep.setCode(2);
                rep.setChargeUtile("Article plus disponible");
                ObjectOutputStream oos = null;
                try
                {
                    oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(rep);
                    oos.flush();
                }
                catch(IOException e)
                {
                    System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
                }
            }
        
        } catch (SQLException ex) {
            System.err.println("Erreur de la base de donnée: "+ ex.getMessage());
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Buy_Good">
    private void traiteBuyGood(Socket s, String chargeUtile) {
        Article temp;
        ResultSet Result;
        BeanSQL MySQLBean;
        ReponseClient rep;
        
        //try { 
            
            /*MySQLBean = ConnexionBD();
            for(int i = 0;i < BuyArticle.size();i++)
            {
                temp = BuyArticle.get(i);
                String requete = "UPDATE appareils SET EtatPaiement='Payé' WHERE NumSerie='"+ temp.getNumSerie() +"'";
                MySQLBean.SQLExecute(requete);
            }
            MySQLBean.SQLCommit();*/
            
            rep = new ReponseClient();
            rep.setCode(1);
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            //MySQLBean.SQLClose();
            
            
        
        /*} catch (SQLException ex) {
            System.err.println("Erreur de la base de donnée: "+ ex.getMessage());
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Delivery_Good">
    private void traiteDeliveryGood(Socket s, String chargeUtile) {
        Article temp;
        ResultSet Result;
        ReponseClient rep;
        
        
        try { 
            BeanSQL MySQLBean = ConnexionBD();
            MySQLBean.SQLStartTransaction();
            for(int i = 0;i<BuyArticle.size();i++)
            {
                temp = BuyArticle.get(i);
                String requete = "UPDATE appareils SET EtatPaiement='Payé' WHERE NumSerie='"+ temp.getNumSerie() +"'";
                MySQLBean.SQLExecute(requete);
            }
            
            MySQLBean.SQLCommit();
            
            StringTokenizer st = new StringTokenizer(chargeUtile,"#");
            String IDClient = st.nextToken();
            String Delivery = st.nextToken();
            
            if(Delivery.equals(" "))
            {
                Delivery = "Embarquement immédiat";
            }
            
            for(int j = 0;j<BuyArticle.size();j++)
            {
                temp = BuyArticle.get(j);
                String requete = "INSERT INTO factures (AppaNumSerie,Client,TVA,AdresseLivraison) VALUES ('"+ temp.getNumSerie() +"', '"+ IDClient + "', '21', '"+ Delivery +"')";
                MySQLBean.SQLExecute(requete);
                ListSales.add(temp);
            }
            MySQLBean.SQLCommit();
            
            BuyArticle.removeAll(BuyArticle);
            
            
            rep = new ReponseClient();
            rep.setCode(1);
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            MySQLBean.SQLClose();
            
            
        
        } catch (SQLException ex) {
            System.err.println("Erreur de la base de donnée: "+ ex.getMessage());
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="All_Client">
    private void traiteSearchAllClient(Socket s) {
        ResultSet Result;
        ReponseClient rep = new ReponseClient();
        try {
            List<Client> listClient = new ArrayList<Client>();
            
            BeanSQL MySQLBean = ConnexionBD();
            
            String requete = "Select * from clients";
            Result = MySQLBean.SQLExecuteQuery(requete);
            
            while(Result.next())
            {
                Client temp = new Client();
                temp.setNumClient(Result.getInt("IDClient"));
                temp.setNom(Result.getString("Nom"));
                temp.setPrenom(Result.getString("Prenom"));
                
                listClient.add(temp);
            }
            
            
            rep.setListClient(listClient);
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            MySQLBean.SQLClose();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Search_Client">
    private void traiteSearchClient(Socket s, String chargeUtile) {
        ResultSet Result;
        ReponseClient rep = new ReponseClient();
        try {
            List<Client> listClient = new ArrayList<Client>();
            
            BeanSQL MySQLBean = ConnexionBD();
            
            String requete = "Select * from clients where IDClient = '" + chargeUtile +"'";
            Result = MySQLBean.SQLExecuteQuery(requete);
            
            while(Result.next())
            {
                Client temp = new Client();
                temp.setNumClient(Result.getInt("IDClient"));
                temp.setNom(Result.getString("Nom"));
                temp.setPrenom(Result.getString("Prenom"));
                temp.setAdresse(Result.getString("Adresse"));
                temp.setEmail(Result.getString("AdresseEmail"));
                temp.setNumTel(Result.getString("NumTelephone"));
                temp.setLogin(Result.getString("Login"));
                temp.setPassWord(Result.getString("Password"));
                
                listClient.add(temp);
            }
            
            
            rep.setListClient(listClient);
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            MySQLBean.SQLClose();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="New_Client">
    private void traiteNewClient(Socket s, String chargeUtile) {
        
        ResultSet Result;
        ReponseClient rep = new ReponseClient();
        StringTokenizer st = new StringTokenizer(chargeUtile,"#");
        
        Client c = new Client();
        c.setNom(st.nextToken());
        c.setPrenom(st.nextToken());
        c.setAdresse(st.nextToken());
        c.setNumTel(st.nextToken());
        c.setEmail(st.nextToken());
        c.setLogin(st.nextToken());
        c.setPassWord(st.nextToken());
        
        
        String Requete = "INSERT INTO Clients(Nom,Prenom,Adresse,NumTelephone,AdresseEmail,Login,Password) VALUES ('"+ c.getNom() +"', '" + c.getPrenom() + "', '" + c.getAdresse() +"', '" +c.getNumTel()  + "', '"+c.getEmail()+"', '" +c.getLogin()+"', '"+c.getPassWord()+"')";
        
        try {
            BeanSQL MySQLBean = ConnexionBD();
            MySQLBean.SQLStartTransaction();
            MySQLBean.SQLExecute(Requete);
            MySQLBean.SQLCommit();
            
            Requete = "Select IDCient from Clients where Login = '" + c.getLogin()+ "'";
            Result = MySQLBean.SQLExecuteQuery(Requete);
            Result.next();
            String IDClient = Result.getString("IDClient");
            
            
            rep.setChargeUtile(IDClient);
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            MySQLBean.SQLClose();
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Logout">
    private void traiteLogout(Socket s, String Chu) {
                ResultSet Result;
        
        ReponseClient rep;

        try {
            BeanSQL MySQLBean =  ConnexionBD();
            StringTokenizer st = new StringTokenizer(Chu,"#");
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
                    rep = new ReponseClient(1,"OK");
                    etat = DISCONNECT;
                }
                else
                {
                    rep = new ReponseClient(2,"Mot de passe invalide");
                }
            }
            else
                rep = new ReponseClient(2,"Login invalide");
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }
            
            
            MySQLBean.SQLClose();
            
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
        etat = DISCONNECT;
        for(int l= 0;l<SocketClient.size();l++)
        {
            ClientSocket temp = SocketClient.get(l);
            if(temp.getCSocket() == s)
            {
                SocketClient.remove(temp);
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="ListSales">
    private void traiteListSales(Socket s) {
        ReponseClient rep = new ReponseClient();
        
        rep.setListArticle(ListSales);
        
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch(IOException e)
        {
            System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
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
    
    // <editor-fold defaultstate="collapsed" desc="VidePanier">
    private void traiteVidePanier(Socket s) {
        Article temp;
        ResultSet Result;
        BeanSQL MySQLBean;
        ReponseClient rep;
        
        try { 
            
            MySQLBean = ConnexionBD();
            MySQLBean.SQLStartTransaction();
            for(int i = 0;i < BuyArticle.size();i++)
            {
                temp = BuyArticle.get(i);
                String requete = "UPDATE appareils SET EtatPaiement='Libre' WHERE NumSerie='"+ temp.getNumSerie() +"'";
                MySQLBean.SQLExecute(requete);
            }
            MySQLBean.SQLCommit();
            
            BuyArticle.removeAll(BuyArticle);
            
            rep = new ReponseClient();
            rep.setCode(1);
            
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(rep);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }

            MySQLBean.SQLClose();
            
            
        
        } 
        catch (SQLException ex) 
        {
            System.err.println("Erreur de la base de donnée: "+ ex.getMessage());
        } 
        catch (ClassNotFoundException ex) 
        {
                Logger.getLogger(ClientAppareil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold>    
}
