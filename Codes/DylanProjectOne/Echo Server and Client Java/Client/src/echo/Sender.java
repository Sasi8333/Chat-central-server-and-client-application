/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;
import utils.NodeInfo;

/**
 *
 * @author Dylan
 */
public class Sender extends Thread
{
    // Class variables
    public Socket socket;
    public int senderPort;
    public ObjectInputStream objInStream;
    public ObjectOutputStream objOutStream;
    private Scanner userInput;
    private String inputLine;
    
    public Sender()
    {
        // initialize the user input scanner
        userInput = new Scanner(System.in);
        inputLine = null;
    }
    
    /*
        Runs the client sender
    */
    public void run()
    {
        // initialize variables
        boolean invalidInput = false;
        
        while (true)
        {
            // get next line from user input
            inputLine = userInput.nextLine();
            Message messageToSend = null;
            invalidInput = false;
            
            // If JOIN Command
            if (inputLine.startsWith("JOIN"))
            {
                // If client already joined
                if (MessageClient.hasJoined)
                {
                    System.err.println("You are already connected to a chat server.");
                    continue;
                }
                
                // Get the server connection info from command line
                String[] connectionInfo = inputLine.split("[ ]+");
                try
                {
                    // Create server node info for future use
                    MessageClient.serverNodeInfo = new NodeInfo(connectionInfo[1], Integer.parseInt(connectionInfo[2]));
                    // Add name to myNodeInfo
                    MessageClient.myNodeInfo.setName(connectionInfo[3]);
                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                    Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, "Invalid connection information / Not enough information", ex);
                }
                
                System.out.println("Server Node has been created with ip = " + MessageClient.serverNodeInfo.address + " and port = " + MessageClient.serverNodeInfo.port);
                
                // Create JOIN message
                messageToSend = new Message(Message.messageTypes.JOIN, MessageClient.myNodeInfo);
                //System.out.println("Join Message has been created");
                // Update joined variable
                MessageClient.hasJoined = true;
                //System.out.println("Message sent to server");
            }
            // NOTE Command
            else if (inputLine.startsWith("NOTE"))
            {
                // If you haven't joined the chat
                if (!MessageClient.hasJoined)
                {
                    System.err.println("You are not connected to a chat server. Join a chat room to use this command");
                    continue;
                }
                
                // Split the message by only the first space
                String[] noteInfo = inputLine.split(" ", 2);
                // Create the note message to send
                messageToSend = new Message(Message.messageTypes.NOTE, (Object) ("<" + MessageClient.myNodeInfo.getName() + "> " + noteInfo[1]));
                //System.out.println("Note Message has been created");

            } else if (inputLine.startsWith("LEAVE"))
            {
                // If you haven't joined the chat
                if (!MessageClient.hasJoined)
                {
                    System.err.println("You are not connected to a chat server. Join a chat room to use this command");
                    continue;
                }
                
                // Create the leave message
                messageToSend = new Message(Message.messageTypes.LEAVE, MessageClient.myNodeInfo);
                //System.out.println("Leave Message has been created");
                MessageClient.hasJoined = false;
            } else if (inputLine.startsWith("SHUTDOWN"))
            {
                // If you haven't joined the chat
                if (!MessageClient.hasJoined)
                {
                    System.err.println("You are not connected to a chat server. Join a chat room to use this command");
                    continue;
                }
                // Create the SHUTDOWN command message
                messageToSend = new Message(Message.messageTypes.SHUTDOWN, MessageClient.myNodeInfo);
                //System.out.println("Leave Message has been created");
                MessageClient.hasJoined = false;
            }
            else
            {
                // When users don't provide a valid command
                System.out.println("The input provided is an invalid command. Try: JOIN {serverIp} {port} {name}, NOTE {message}, LEAVE, SHUTDOWN");
                invalidInput = true;
            }
            
            // If the command given is valid
            if (!invalidInput)
            {
                // Then send the created message to the server
                send(messageToSend);
            }
            
            // If shutdown command given
            if (inputLine.startsWith("SHUTDOWN"))
            {
                // Then terminate the while loop
                System.out.println("The SHUTDOWN command has been executed. Good Bye!");
                System.exit(0);
            }
        }
    }
    
    /*
        Function is responsible for sending any message objects to the server
    */
    public void send(Object objToSend)
    {
        try
        {
            // Create the socket and streams. Then write the object to the out stream and close the connection.
            socket = new Socket(MessageClient.serverNodeInfo.getAddress(), MessageClient.serverNodeInfo.getPort());
            objInStream       = new ObjectInputStream (socket.getInputStream ());
            objOutStream         = new ObjectOutputStream(socket.getOutputStream());
            //System.out.println("Socket and streams created");
            objOutStream.writeObject(objToSend);
            socket.close();
        }
        catch (UnknownHostException e)
        {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, "Unknown host exception thrown in send method of sender", e);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, "IOException thrown in send method of sender", ex);
        }
    }
}
