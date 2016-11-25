package APPLICATION_TEST_JDBC;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import APPLICATION_TEST_JDBC.APPLICATION_TEST_JDBC_GUI;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gauvain KLUG
 */
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
    
        public void SQLExecute(String SQLRequest)
        {
            try 
            {
                st.execute(SQLRequest);
            } 
            catch (SQLException ex) 
            {
               System.out.println(ex.getMessage());
            }
        }
        
        public ResultSet SQLExecuteQuery(String SQLRequest)
        {
            ResultSet result = null;
            try 
            {
                result = st.executeQuery(SQLRequest);
            } 
            catch (SQLException ex) 
            {
               System.out.println(ex.getMessage());
            }
            
            return result;
        }
        
        public void SQLConnect()
        {
            try 
            {
                Class.forName(Driver);
                conn = DriverManager.getConnection(url, user, pwd);
                st = conn.createStatement();
            } 
            catch (ClassNotFoundException ex) 
            {
                System.out.println(ex.getMessage());
            } 
            catch (SQLException ex) 
            {
                System.out.println(ex.getMessage());
            }
        }
        
        public void SQLCommit()
        {
            SQLExecute("commit;");
        }
        
        public void SQLRollback()
        {
            SQLExecute("rollback;");
        }
        
        public void SQLStartTransaction()
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
        
        public void SQLClose()
        {
            try 
            {
                conn.close();
                st.close();
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
            }
        }
}
