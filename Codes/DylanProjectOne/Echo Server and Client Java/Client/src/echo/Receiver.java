/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;
import utils.NodeInfo;

/**
 *
 * @author Dylan
 */
public class Receiver extends Thread
{
    // Create server socket
    static ServerSocket receiverSocket;
    
    public Receiver()
    {
        // try to start the server socket on receiver port
        try
        {
            receiverSocket = new ServerSocket(MessageClient.myNodeInfo.getPort());
            System.out.println("Created receiver socket. Listening on port " + MessageClient.myNodeInfo.getPort());
        }
        catch(IOException ex)
        {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Creating the receiver socket has failed", ex);
        }
    }
    
    @Override
    public void run()
    {  
        // loop forever
        while (true)
        {
            // try accepting new connections in the receiver worker class
            try
            {
                (new ReceiverWorker(receiverSocket.accept())).start();
            }
            catch(IOException e)
            {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Cannot accept connection on receiver socket", e);
            }
        }
    }
}
