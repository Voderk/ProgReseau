/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Gauvain Klug
 */
public class UDPLogger {
    public static void writeUDPLog(File logFile,String st)
    {
        FileWriter log = null;
        try {
            if(!logFile.exists())
                logFile.createNewFile();
            log = new FileWriter(logFile);
            log.write(st);
            log.flush();
        } catch (IOException ex) {
            Logger.getLogger(UDPLogger.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                log.close();
            } catch (IOException ex) {
                Logger.getLogger(UDPLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
}
