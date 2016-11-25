/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gauvain Klug
 */
public class TCPAccess {
    public static String readLikeC(Socket CS)
    {
        InputStream InpStr = null;
        DataInputStream ois = null;
        StringBuffer rep= new StringBuffer();
        byte b=0;
        try {
            InpStr = CS.getInputStream();
            ois = new DataInputStream(new BufferedInputStream(InpStr));
            
            do
            {
                b = ois.readByte();
                if(b != '#')
                    rep.append((char) b);
            }while( b != (byte)'#' );
            
        } catch (IOException ex) {
            System.out.println("MumService.TCPAccess.readLikeC() : " +ex.getMessage());
        } 
        return new String(rep);
    }
    
    public static void writeLikeC(Socket CS, String s)
    {
        DataOutputStream oos = null;
        OutputStream OutStr = null;
        s = s + '#';
        try {
            OutStr = CS.getOutputStream();
            oos = new DataOutputStream(new BufferedOutputStream(OutStr));
            byte [] bytearray = s.getBytes();
            for(int i = 0; i < bytearray.length; i++)
                oos.writeByte(bytearray[i]);
            oos.flush();
            OutStr.flush();
            Thread.sleep(50);
        } catch (IOException ex) {
            System.out.println("MumService.TCPAccess.writeLikeC() : " +ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("MumService.TCPAccess.writeLikeC() : " +ex.getMessage());
        } 
                
    }
}
