/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application_admin;

import RequeteClient.RequeteAdmin;
import RequeteReponseAdmin.ReponseAdmin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

/**
 *
 * @author Gauvain Klug
 */
public class ApplicAdmin extends javax.swing.JFrame {

    /**
     * Creates new form GUI
     */
    public ApplicAdmin() {
        initComponents();
    }
    Boolean connected = false;
    String AdminLogin = null;
    Socket AdminSocket = null;
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ListeClients = new javax.swing.JList<>();
        LabelList = new javax.swing.JLabel();
        StopServer = new javax.swing.JButton();
        Refresh = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        LoginButton = new javax.swing.JMenu();
        LogoutButton = new javax.swing.JMenu();
        ExitButton = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ListeClients.setEnabled(false);
        jScrollPane1.setViewportView(ListeClients);

        LabelList.setText("Clients Connectés");
        LabelList.setEnabled(false);

        StopServer.setText("Stop Server");
        StopServer.setEnabled(false);
        StopServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StopServerMouseClicked(evt);
            }
        });

        Refresh.setText("Refresh List");
        Refresh.setEnabled(false);
        Refresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RefreshMouseClicked(evt);
            }
        });
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });

        LoginButton.setText("Login");
        LoginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoginButtonMouseClicked(evt);
            }
        });
        jMenuBar1.add(LoginButton);

        LogoutButton.setText("Logout");
        LogoutButton.setEnabled(false);
        LogoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogoutButtonMouseClicked(evt);
            }
        });
        jMenuBar1.add(LogoutButton);

        ExitButton.setText("Exit");
        ExitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ExitButtonMouseClicked(evt);
            }
        });
        jMenuBar1.add(ExitButton);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LabelList)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(Refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(StopServer)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StopServer)
                    .addComponent(Refresh))
                .addContainerGap(91, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoginButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginButtonMouseClicked
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
        ReponseAdmin rep = null;
        if(login.getDialogResult() == state.OK)
        {
            
            try 
            {
                AdminSocket = new Socket("127.0.0.1", 50010);
                System.out.println("Client connecté : " + AdminSocket.getInetAddress().toString());
                String chu = login.getLogin() + "#" + login.getPwd();

                RequeteAdmin req = new RequeteAdmin(RequeteAdmin.LOGINA,chu);
                ObjectOutputStream oos = null;

               
                oos = new ObjectOutputStream(AdminSocket.getOutputStream());
                oos.writeObject(req);
                oos.flush();
               
                
             
            
                ObjectInputStream ois = null;
                
                ois = new ObjectInputStream(AdminSocket.getInputStream());
                rep = (ReponseAdmin) ois.readObject();   
                

                if(rep.getCode() == -1)
                {
                    JOptionPane.showMessageDialog(this,rep.getChargeUtile()+"\nVeuillez quitter l'appliaction","Erreur !",JOptionPane.WARNING_MESSAGE,null);
                }
                else
                {
                    if(rep.getChargeUtile().equals("OK"))
                    {
                        RefreshMouseClicked(evt);
                        SwitchState(true);
                        connected = true;
                        this.Refresh.setEnabled(true);
                        this.StopServer.setEnabled(true);
                        
                        req.setType(RequeteAdmin.LCLIENTS);
                        oos = new ObjectOutputStream(AdminSocket.getOutputStream());
                        oos.writeObject(req);
                        oos.flush();
                              
                
                        ois = new ObjectInputStream(AdminSocket.getInputStream());
                        rep = (ReponseAdmin) ois.readObject(); 
                        
                        initListClient(rep.getListClient());
                        
                        
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this,rep.getChargeUtile(),"Erreur !",JOptionPane.WARNING_MESSAGE,null);
                        AdminSocket.close();
                    }
                }  
            }
            catch (UnknownHostException e)
            { 
                System.err.println("Erreur ! Host non trouvé [" + e + "]"); 
            }
            catch (IOException e)
            { 
                System.err.println("Erreur ! Pas de connexion ? [" + e + "]"); 
            } 
            catch (ClassNotFoundException e) 
            {
                System.err.println("Erreur ! Class not found ? [" + e + "]");
            }
        }
        
    }//GEN-LAST:event_LoginButtonMouseClicked

    private void LogoutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutButtonMouseClicked
        SwitchState(false);
        ListeClients.removeAll();
        connected = false;
        AdminLogin = null;
        /*Envoi message de deconnexion param AdminLogin*/
    }//GEN-LAST:event_LogoutButtonMouseClicked

    private void RefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RefreshMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_RefreshMouseClicked

    private void StopServerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StopServerMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_StopServerMouseClicked

    private void ExitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitButtonMouseClicked
        if(connected)
        {
            LogoutButtonMouseClicked(evt);
        }
        System.exit(0);
    }//GEN-LAST:event_ExitButtonMouseClicked

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        RequeteAdmin req = new RequeteAdmin(RequeteAdmin.LCLIENTS,"");
        ReponseAdmin rep = null;
        try 
        {
            ObjectOutputStream oos = new ObjectOutputStream(AdminSocket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
            
            
            ObjectInputStream ois = new ObjectInputStream(AdminSocket.getInputStream());
            rep = (ReponseAdmin) ois.readObject();
            
            initListClient(rep.getListClient());
        } 
        catch (IOException ex) {
            System.err.println("Erreur ! Pas de connexion ? [" + ex + "]");
        } 
        catch (ClassNotFoundException ex) {
            System.err.println("Erreur ! Class not found ? [" + ex + "]");
        }
    }//GEN-LAST:event_RefreshActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ApplicAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ApplicAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ApplicAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ApplicAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApplicAdmin().setVisible(true);
            }
        });
    }
    
    void SwitchState(Boolean boo)
    {
        LabelList.setEnabled(boo);
        LoginButton.setEnabled(!boo);
        LogoutButton.setEnabled(boo);
        ListeClients.setEnabled(boo);
        Refresh.setEnabled(boo);
        StopServer.setEnabled(boo);
    }
    
    private void initListClient(List<String> listClient) {
        String Client;
        String temp;
        Vector Socket = new Vector();
        for(int i = 0; i<listClient.size();i++)
        {
            temp = listClient.get(i);
            Client = "Client n°"+ (i+1) +":" + temp;
            Socket.add(Client);
            
        }
        this.ListeClients.setListData(Socket);
        ListeClients.revalidate();
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu ExitButton;
    private javax.swing.JLabel LabelList;
    private javax.swing.JList<String> ListeClients;
    private javax.swing.JMenu LoginButton;
    private javax.swing.JMenu LogoutButton;
    private javax.swing.JButton Refresh;
    private javax.swing.JButton StopServer;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    
}
