/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gauvain KLUG
 */
public class UDPAccess {
    public static void sendUDPLikeC(MulticastSocket ds, String s, InetAddress addr, int port)
    {
        try {
            ds.send(new DatagramPacket(s.getBytes(), s.length(), addr , port));
        } catch (IOException ex) {
            Logger.getLogger(UDPAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String receiveUDPLikeC(MulticastSocket ds)
    {
        try {
            DatagramPacket dp;
            byte[] buf = new byte[512];
            dp = new DatagramPacket(buf, 512);
            ds.receive(dp);
            return new String(dp.getData());
        } catch (IOException ex) {
           return null;
        }
        
    }
}
