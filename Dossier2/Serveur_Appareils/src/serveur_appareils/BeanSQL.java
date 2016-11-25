/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;



public class BeanSQL implements Serializable {
    
        private String url;
        private String user;
        private String pwd;
        private String Driver;
        private Connection conn;
        private java.sql.Statement st;
        
        public BeanSQL()
        {}
        
        public String getUrl()
        {
            return this.url;
        }
        public String getUser()
        {
            return this.user;
        }
        public String getPwd()
        {
            return this.pwd;
        }
        public String getDriver()
        {
            return this.Driver;
        }
        public Connection getConn()
        {
            return this.conn;
        }
        public java.sql.Statement getSt()
        {
            return this.st;
        }
        public void setUrl(String value)
        {
            this.url = value;
        }
        public void setUser(String value)
        {
            this.user = value;
        }
        public void setPwd(String value)
        {
            this.pwd = value;
        }
        public void setDriver(String value)
        {
            this.Driver = value;
        }
        public void setConn(Connection value)
        {
            conn = value;
        }
        public void setSt(java.sql.Statement value)
        {
            st = value;
        }
    
        public synchronized void SQLExecute(String SQLRequest) throws SQLException
        {
                st.execute(SQLRequest);
        }
        
        public synchronized ResultSet SQLExecuteQuery(String SQLRequest) throws SQLException
        {
            ResultSet result = null;
            result = st.executeQuery(SQLRequest);
            return result;
        }
        
        public synchronized void SQLConnect() throws ClassNotFoundException, SQLException
        {
            Class.forName(Driver);
            conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            st = conn.createStatement();
        }
        
        public void SQLCommit() throws SQLException
        {
            SQLExecute("commit;");
        }
        
        public void SQLRollback() throws SQLException
        {
            SQLExecute("rollback;");
        }
        
        public synchronized void SQLStartTransaction() throws SQLException
        {
            String SQLRequest;
            if(Driver.equalsIgnoreCase("com.mysql.jdbc.Driver"))
            {
                SQLRequest = "START TRANSACTION;";
                SQLExecute(SQLRequest);
            }
            if(Driver.equalsIgnoreCase("oracle.jdbc.driver"))
            {
                SQLRequest = "SET TRANSACTION;";
                SQLExecute(SQLRequest);
            }
        }
        
        public synchronized void SQLClose() throws SQLException
        {
                conn.close();
                st.close();
        }
}

