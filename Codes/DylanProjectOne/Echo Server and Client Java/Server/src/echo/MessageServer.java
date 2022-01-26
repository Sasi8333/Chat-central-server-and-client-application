/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.util.ArrayList;
import echo.MessageServerThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.NodeInfo;
import utils.Message;
import utils.PropertyHandler;


/**
 *
 * @author Dylan
 */
public class MessageServer 
{
    // Chat participants
    public static ArrayList<NodeInfo> participants;
    
    // Global socket handlers
    public ServerSocket serverSocket;
    public static int senderPort;
    public int receiverPort;
    
    /*
        Responsible for initializing the message server object
    */
    public MessageServer(String propertiesFile)
    {
        // initializing array list to hold all of the participants connection info
        participants = new ArrayList<NodeInfo>();
        Properties properties = null;
        
        // open properties
        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        } 

        // get server port number
        try
        {
            receiverPort = Integer.parseInt(properties.getProperty("SERVER_RECEIVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "Cannot read server receiver port", ex);
            System.exit(1);
        }
        
        // get server port number
        try
        {
            senderPort = Integer.parseInt(properties.getProperty("SERVER_SENDER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "Cannot read server sender port", ex);
            System.exit(1);
        }
        
        // open server socket
        try
        {
            serverSocket = new ServerSocket(receiverPort);
        }
        catch(IOException ex)
        {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "Cannot start server socket", ex);
        }
    }
    
    /*
        Responsible for running the server
    Entails:
      - Opening the object intake stream (Receiver)
      - Opening the object outtake stream (Sender)
    */
    /**
     * Convenience method to be called on an instance of EchoServer in main()
     * 
     * @throws IOException 
     */
    public void runServerLoop() throws IOException {
	while (true) 
        {
            System.out.println("Waiting for connections on port #" + receiverPort); 
            
            // Create a new thread that will listen for incoming connections
            //    and work on handling said connections
            (new Thread(new MessageServerThread(serverSocket.accept()))).run();
	}
    }
    
    // Helper method that will print out the participants on the server
    public static void printParticipants()
    {
        for (int i = 0; i < MessageServer.participants.size(); i++)
        {
            NodeInfo info = MessageServer.participants.get(i);
            System.out.println(info);
        }
    }
    
    /**
     * Getting the ball rolling
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {
        // create instance of echo server
        // note that hardcoding the port is bad, here we do it just for simplicity reasons
        MessageServer messageServer = new MessageServer("config/Server.properties");
        
        // fire up server loop
        messageServer.runServerLoop();
    }
}
