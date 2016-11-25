/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application_appareils;

import RequeteClient.ReponseClient;
import RequeteClient.RequeteClient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Alex
 */
public class Applic_Appareil_Login extends javax.swing.JFrame {

    /**
     * Creates new form Applic_Appareil_Login
     */
    public Applic_Appareil_Login() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MessageLabel = new javax.swing.JLabel();
        IdentificationLabel = new javax.swing.JLabel();
        LoginLabel = new javax.swing.JLabel();
        LoginTextField = new javax.swing.JTextField();
        PasswordLabel = new javax.swing.JLabel();
        OkButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        PasswordTextField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Connexion à l'application Appareil");

        MessageLabel.setText("Connexion à l'application !");

        IdentificationLabel.setText("Veuillez vous identifier: ");

        LoginLabel.setText("Login: ");

        PasswordLabel.setText("Password: ");

        OkButton.setText("OK");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(MessageLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(IdentificationLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(LoginLabel))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(PasswordLabel)
                                .addComponent(LoginTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addComponent(PasswordTextField))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(OkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(MessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(IdentificationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LoginLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LoginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PasswordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkButton)
                    .addComponent(CancelButton))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        String Login,Password,Message = null;
        ReponseClient rep = null;
        
        
        Login = LoginTextField.getText();
        Password = PasswordTextField.getText();
        
        if(Login.isEmpty() || Password.isEmpty())
        {
            if(Login.isEmpty() && Password.isEmpty())
                Message = "Login/Password manquants. Veuillez rentrer un login et un password !";
            else
            {
                if(Login.isEmpty())
                {
                    Message = "Login manquant. Veuillez rentrer un login !";
                }
                if(Password.isEmpty())
                {
                    Message = "Password manquant. Veuillez rentrer un password !";
                }
            }
            JOptionPane.showMessageDialog(this,Message,"Erreur !",JOptionPane.WARNING_MESSAGE,null);
        }
        else
        {
            Socket cliSock = null;
        
            try
            {
                cliSock = new Socket("127.0.0.1", 50000);
                System.out.println("Client connecté : " + cliSock.getInetAddress().toString());
            }
            catch (UnknownHostException e)
            { System.err.println("Erreur ! Host non trouvé [" + e + "]"); }
            catch (IOException e)
            { System.err.println("Erreur ! Pas de connexion ? [" + e + "]"); }

            String chu = Login + "#" + Password;

            RequeteClient req = new RequeteClient(1,chu);
            ObjectOutputStream oos = null;

            try
            {
                oos = new ObjectOutputStream(cliSock.getOutputStream());
                oos.writeObject(req);
                oos.flush();
            }
            catch(IOException e)
            {
                System.err.println("Erreur réseau ? ["+e.getMessage()+"]");
            }
            
            ObjectInputStream ois = null;
            try
            {
                ois = new ObjectInputStream(cliSock.getInputStream());
                rep = (ReponseClient) ois.readObject();   
            } catch (IOException ex) {
                System.err.println("Erreur réseau ? ["+ex.getMessage()+"]");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Applic_Appareil_Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(rep.getCode() == -1)
            {
                JOptionPane.showMessageDialog(this,rep.getChargeUtile()+"\nVeuillez quitter l'appliaction","Erreur !",JOptionPane.WARNING_MESSAGE,null);
            }
            else
            {
                if(rep.getChargeUtile().equals("OK"))
                {
                    Applic_Appareil app = new Applic_Appareil(cliSock,Login,Password);
                    app.setVisible(true);
                    this.setVisible(false);
                }
                else
                {
                    JOptionPane.showMessageDialog(this,rep.getChargeUtile(),"Erreur !",JOptionPane.WARNING_MESSAGE,null);
                    try {
                        cliSock.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Applic_Appareil_Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        
    }//GEN-LAST:event_OkButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Applic_Appareil_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Applic_Appareil_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Applic_Appareil_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Applic_Appareil_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Applic_Appareil_Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JLabel IdentificationLabel;
    private javax.swing.JLabel LoginLabel;
    private javax.swing.JTextField LoginTextField;
    private javax.swing.JLabel MessageLabel;
    private javax.swing.JButton OkButton;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JPasswordField PasswordTextField;
    // End of variables declaration//GEN-END:variables
}
