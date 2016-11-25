/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APPLICATION_TEST_JDBC;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPasswordField;

/**
 *
 * @author Gauvain KLUG
 */
public class APPLICATION_TEST_JDBC_GUI extends javax.swing.JFrame {
    Connection conn = null;
    String url = "jdbc:mysql://localhost/bd_societe";
    String user = "root", password = "4242";
    BeanSQL MySQLBean = null;
    /**
     * Creates new form APPLICATION_TEST_JDBC_GUI
     */
    public APPLICATION_TEST_JDBC_GUI() {
        initComponents(); 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DB_RBGroup = new javax.swing.ButtonGroup();
        RBGroupTypeRequest = new javax.swing.ButtonGroup();
        UserField = new javax.swing.JTextField();
        ORACLE_DBRB = new javax.swing.JRadioButton();
        MySQL_DBRB = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        SQLInput = new javax.swing.JTextArea();
        Output_win = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        ConnectButton = new javax.swing.JButton();
        RBSelect = new javax.swing.JRadioButton();
        RBManipData = new javax.swing.JRadioButton();
        DisconnectButton = new javax.swing.JButton();
        ExecuteButton = new javax.swing.JButton();
        CommitButton = new javax.swing.JButton();
        RollbackButton = new javax.swing.JButton();
        TransactionButton = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();
        TFNBCol = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        UserField.setText("root");

        DB_RBGroup.add(ORACLE_DBRB);
        ORACLE_DBRB.setText("Oracle");

        DB_RBGroup.add(MySQL_DBRB);
        MySQL_DBRB.setSelected(true);
        MySQL_DBRB.setText("MySQL");

        SQLInput.setColumns(20);
        SQLInput.setRows(5);
        jScrollPane1.setViewportView(SQLInput);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        Output_win.setViewportView(jTextArea2);

        ConnectButton.setText("Connect");
        ConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectButtonActionPerformed(evt);
            }
        });

        RBGroupTypeRequest.add(RBSelect);
        RBSelect.setSelected(true);
        RBSelect.setText("Select");

        RBGroupTypeRequest.add(RBManipData);
        RBManipData.setText("ManipData");

        DisconnectButton.setText("Disconnect");
        DisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectButtonActionPerformed(evt);
            }
        });

        ExecuteButton.setText("Execute");
        ExecuteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExecuteButtonActionPerformed(evt);
            }
        });

        CommitButton.setText("Commit");
        CommitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CommitButtonActionPerformed(evt);
            }
        });

        RollbackButton.setText("Rollback");
        RollbackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RollbackButtonActionPerformed(evt);
            }
        });

        TransactionButton.setText("New Transaction");
        TransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TransactionButtonActionPerformed(evt);
            }
        });

        jPasswordField1.setText("4242");

        TFNBCol.setText("3");

        jLabel1.setText("NBColonne");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ExecuteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CommitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RollbackButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TransactionButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TFNBCol, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ORACLE_DBRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(UserField))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(MySQL_DBRB)
                                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(RBSelect)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(RBManipData))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(ConnectButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DisconnectButton))))
                            .addComponent(Output_win, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ORACLE_DBRB)
                    .addComponent(MySQL_DBRB)
                    .addComponent(RBSelect)
                    .addComponent(RBManipData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ConnectButton)
                    .addComponent(DisconnectButton)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExecuteButton)
                    .addComponent(CommitButton)
                    .addComponent(RollbackButton)
                    .addComponent(TransactionButton)
                    .addComponent(TFNBCol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Output_win, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectButtonActionPerformed
            MySQLBean = new BeanSQL();
            
            
            if(MySQL_DBRB.isSelected())
            {
                MySQLBean.setDriver("com.mysql.jdbc.Driver");
                url = "jdbc:mysql://localhost/bd_societe";
            }
            if(ORACLE_DBRB.isSelected())
            {
                MySQLBean.setDriver("oracle.jdbc.driver.OracleDriver");
                url = "jdbc:oracle:thin://@192.168.213.128:1521/orcl";
            }

            MySQLBean.setUser(UserField.getText());
            MySQLBean.setPwd(new String(jPasswordField1.getPassword()));
            MySQLBean.setUrl(url);

            MySQLBean.SQLConnect();
            
            
    }//GEN-LAST:event_ConnectButtonActionPerformed

    private void DisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectButtonActionPerformed
        MySQLBean.SQLClose();
        MySQLBean = null;
    }//GEN-LAST:event_DisconnectButtonActionPerformed

    private void ExecuteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExecuteButtonActionPerformed
        ResultSet rs;
        String SQLRequest = SQLInput.getText();
        int nbCol = Integer.parseInt(TFNBCol.getText());    
            if(RBSelect.isSelected())
            {    
                rs = MySQLBean.SQLExecuteQuery(SQLRequest);
                try {
                    while(rs.next())
                    {
                        String SQLResult = "";
                        for(int i = 1; i <= nbCol; i++)
                            SQLResult = SQLResult + rs.getString(i) + "\t";
                        
                        jTextArea2.append(SQLResult);
                        jTextArea2.append("\r\n");
                    }
                } catch (SQLException ex) {
                    jTextArea2.append(ex.getMessage());
                    jTextArea2.append("\r\n");
                }
                
            }
            if(RBManipData.isSelected())
            {
                MySQLBean.SQLExecute(SQLRequest);
            }            
    }//GEN-LAST:event_ExecuteButtonActionPerformed

    private void CommitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CommitButtonActionPerformed
        MySQLBean.SQLCommit();
    }//GEN-LAST:event_CommitButtonActionPerformed

    private void RollbackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RollbackButtonActionPerformed
        MySQLBean.SQLRollback();
    }//GEN-LAST:event_RollbackButtonActionPerformed

    private void TransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TransactionButtonActionPerformed
        MySQLBean.SQLStartTransaction();
    }//GEN-LAST:event_TransactionButtonActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(APPLICATION_TEST_JDBC_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(APPLICATION_TEST_JDBC_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(APPLICATION_TEST_JDBC_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(APPLICATION_TEST_JDBC_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new APPLICATION_TEST_JDBC_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CommitButton;
    private javax.swing.JButton ConnectButton;
    private javax.swing.ButtonGroup DB_RBGroup;
    private javax.swing.JButton DisconnectButton;
    private javax.swing.JButton ExecuteButton;
    private javax.swing.JRadioButton MySQL_DBRB;
    private javax.swing.JRadioButton ORACLE_DBRB;
    private javax.swing.JScrollPane Output_win;
    private javax.swing.ButtonGroup RBGroupTypeRequest;
    private javax.swing.JRadioButton RBManipData;
    private javax.swing.JRadioButton RBSelect;
    private javax.swing.JButton RollbackButton;
    private javax.swing.JTextArea SQLInput;
    private javax.swing.JTextField TFNBCol;
    private javax.swing.JButton TransactionButton;
    private javax.swing.JTextField UserField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
