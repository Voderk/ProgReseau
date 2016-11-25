/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

/**
 *
 * @author Gauvain KLUG
 */
public class ThreadUDP extends Thread{
        JTextArea chatArea;
        JComboBox cbTAG;
        MulticastSocket ds;
        private Boolean st = false;
        private Boolean servermode = false;
        File logs;
        public ThreadUDP(MulticastSocket DS, JTextArea chat, JComboBox cb ) 
        {
            ds = DS;
            chatArea = chat;
            cbTAG = cb;
            servermode = false;
        }
        public ThreadUDP(MulticastSocket DS, String logfile)
        {
            logs = new File(logfile);
            ds = DS;
            servermode = true;
        }
    
    public void run()
    {
        if(servermode)
        {
            if(ds == null)
                return;
            
            while(!st)
            {
                String str = UDPAccess.receiveUDPLikeC(ds);
                if( str != null)
                    UDPLogger.writeUDPLog(logs, str);
            }
            return;
        }
        else
        {    
            if(ds == null ||chatArea == null || cbTAG == null)
                return;
            while(!st)
            {
                String str = UDPAccess.receiveUDPLikeC(ds);
                
                if( str != null)
                {   
                    if(Protocol.ExtractProtocol(str).equals(Protocol.protocols.POST_QUESTION.toString()))
                        cbTAG.addItem("Q" + Protocol.ExtractTag(str));
                    if(Protocol.ExtractProtocol(str).equals(Protocol.protocols.ANSWER_QUESTION.toString()))
                    {
                        Boolean breaker = false;
                        for(int i = 0; i < cbTAG.getItemCount()-1; i++ )
                        {
                            if(("RQ" + Protocol.ExtractTag(str)).equals((String)cbTAG.getItemAt(i)) )
                            {
                                breaker = true;
                                break;
                            }
                        }
                        if(!breaker)
                            cbTAG.addItem("Q" + Protocol.ExtractTag(str));
                        cbTAG.invalidate();
                    }
                    chatArea.append(Protocol.ExtractMessage(str) + "\n");   
                }
            }
            
            return;
        }
    }

    /**
     * @param st the st to set
     */
    public void setSt(Boolean st) {
        this.st = st;
    }
    
}
