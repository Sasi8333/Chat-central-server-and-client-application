/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;
import echo.Receiver;
import echo.Sender;
import utils.NodeInfo;

/**
 *
 * @author Dylan
 */
public class MessageClient implements Runnable
{
    // Class variables
    public Receiver receiver;
    public Sender sender;
    public static int receiverPort;
    public static NodeInfo myNodeInfo;
    public static NodeInfo serverNodeInfo;
    public static boolean hasJoined;
    
    public MessageClient(String propertiesFile)
    {
        Properties properties = null;
        hasJoined = false;
        
        // open properties
        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        }
        
        // get receiver port number
        try
        {
            receiverPort = Integer.parseInt(properties.getProperty("RECEIVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, "Cannot read server receiver port", ex);
            System.exit(1);
        }
        
        // Creating node information for receiver server socket
        this.myNodeInfo = new NodeInfo("127.0.0.1", receiverPort);
    }
    
    @Override
    /**
     * Implementation of interface Runnable
     * 
     * Called by main() to start the EchoClientThread
     */
    public void run()
    {
        // Start the receiver thread operations
        (receiver = new Receiver()).start();

        // Start the sender thread operations
        (sender = new Sender()).start();
    }
    
    public static void main(String[] args) throws IOException {

        String propertiesFile = null;
        
        try
        {
            propertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            propertiesFile = "config/Server.properties";
        }

        // start MessageClient
        (new MessageClient(propertiesFile)).run();
    }
}
