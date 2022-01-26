/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;
import utils.Message;

/**
 *
 * @author Dylan
 */
public class ReceiverWorker extends Thread
{
    // Class variables
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;
    
    Socket socket;
    
    Object receivedObject;
    
    public ReceiverWorker(Socket socket)
    {
        this.socket = socket;
    }
    
    @Override
    public void run()
    {
        try
        {
            //System.out.println("Creating the receiving streams");
            // Create the object streams
            toServer         = new ObjectOutputStream(socket.getOutputStream());
            fromServer       = new ObjectInputStream (socket.getInputStream ());
            
            //System.out.println("Trying to read object from server");
            // Try getting the object from the new connection
            receivedObject = fromServer.readObject();
            
            //System.out.println("Extracting information from object received");
            // Get the type of the message
            Message.messageTypes type = ((Message) receivedObject).getType();
            
            // Confirm that the message is a note
            if (type == Message.messageTypes.NOTE)
            {
                // get the string contained in the note and print it
                String note = (String) ((Message) receivedObject).getContent();
                System.out.println(note);
            }
        }
        catch(IOException e)
        {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Cannot send object to server", e);
        }
        catch(ClassNotFoundException ex)
        {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Cannot find object class", ex);
        }
    }
}
