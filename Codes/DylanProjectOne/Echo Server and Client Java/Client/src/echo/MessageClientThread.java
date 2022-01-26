/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;

/**
 *
 * @author Dylan
 */
public class MessageClientThread implements Runnable
{
    ServerSocket receivingServerSocket = null;
    Socket serverConnection = null;
    
    ObjectInputStream fromServer;
    ObjectOutputStream toServer;
    
    Object objectFromServer;
    Object objectToSend;
    
    public String serverIP;
    public int serverPort;
    public int receiverPort;
    
    public boolean receiving;
    
    /**
     * Constructor, initializes the connection to EchoServer and opens up stdin
     * 
     * @param serverIP Server IP
     * @param serverPort Server port
     */
    public MessageClientThread(Socket socket) 
    {
        System.out.println("In constructor on MessageClientThread (Receiving)");
        this.serverConnection = socket;
        this.receiving = true;
    }
    
    /**
     * Constructor, initializes the connection to EchoServer and opens up stdin
     * 
     * @param serverIP Server IP
     * @param serverPort Server port
     */
    public MessageClientThread(String serverIP, int serverPort, Object objectToSend) 
    {
        System.out.println("In constructor on MessageClientThread (Sending)");
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.objectToSend = objectToSend;
        this.receiving = false;
    }
    
    @Override
    public void run() 
    {
        if (receiving)
        {
            try
            {
                System.out.println("Creating the receiving streams");
                toServer         = new ObjectOutputStream(serverConnection.getOutputStream());
                fromServer       = new ObjectInputStream (serverConnection.getInputStream ());
                System.out.println("Trying to read object from server");
                objectFromServer = fromServer.readObject();
                System.out.println("Extracting information from object received");
                Message.messageTypes type = ((Message) objectFromServer).getType();
                if (type == Message.messageTypes.NOTE)
                {
                    String note = (String) ((Message) objectFromServer).getContent();
                    System.out.println(note);
                }
            }
            catch(IOException e)
            {
                Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot send object to server", e);
            }
            catch(ClassNotFoundException ex)
            {
                Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot find object class", ex);
            }
        }
        else
        {
            // open server socket
            try
            {
                serverConnection = new Socket(this.serverIP, this.serverPort);
                fromServer       = new ObjectInputStream (serverConnection.getInputStream ());
                toServer         = new ObjectOutputStream(serverConnection.getOutputStream());
            }
            catch(IOException ex)
            {
                Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot connect to server", ex);
                System.exit(1);
            }

            while(true)
            {
                try
                {
                    toServer.writeObject(objectToSend);
                    System.out.println("Object has been sent to the server");
                    System.out.println(objectToSend);
                    serverConnection.close();
                    break;
                }
                catch (IOException e)
                {
                    Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot send object to server", e);
                }
            }
        }
    }
}
