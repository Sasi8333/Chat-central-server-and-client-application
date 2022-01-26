package echo;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;
import utils.NodeInfo;

// objects of this class work on one request
class MessageServerThread implements Runnable {
    Socket client;
    
    ObjectInputStream fromClient;
    ObjectOutputStream toClient;
    
    Object objectFromClient;
    Object objectToClient;

    // output streams to destination web server and requesting client
    MessageServerThread(Socket client) 
    {
        //System.out.println("The messageServerThread has accepted a client");
        this.client = client;
    }

    @Override
    public void run() 
    {
        
        // first get the streams
        try 
        {
            if (client == null)
            {
                System.out.println("Client is null in messageServerThread");
            }
            
            //System.out.println("The client sockets are being created");
            toClient   = new ObjectOutputStream(client.getOutputStream());
            fromClient = new ObjectInputStream (client.getInputStream ());
        } catch (IOException e) 
        {
            Logger.getLogger(MessageServerThread.class.getName()).log(Level.SEVERE, null, e);
        }

        // now talk to the client
        while (true) 
        {
            try 
            {
                //System.out.println("Waiting to receiver object from receiving socket");
                this.objectFromClient = fromClient.readObject();
            } catch (IOException e) 
            {
                Logger.getLogger(MessageServerThread.class.getName()).log(Level.SEVERE, null, e);
            } catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(MessageServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (this.objectFromClient != null)
            {

                //System.out.println("Processing received message");
                // Send the message to be processed
                processMessage();
                break;
            }
        }
    }
    
    public void processMessage()
    {
        //System.out.println("In processMessage");
        Message messageObj = (Message) this.objectFromClient;
        Message.messageTypes type = messageObj.getType();
 
        // Determine required action based on message type
        switch (type) {
            case JOIN:
                System.out.println("Adding participant to chat room. Current Participants: ");
                MessageServer.participants.add((NodeInfo) messageObj.getContent());
                MessageServer.printParticipants();
                break;
            case LEAVE:
                System.out.println("Removing participant from chat room. Remaining Participants: ");
                MessageServer.participants.remove((NodeInfo) messageObj.getContent());
                MessageServer.printParticipants();
                break;
            case SHUTDOWN:
                System.out.println("Removing participant from chat room (SHUTDOWN). Remaining Participants: ");
                MessageServer.participants.remove((NodeInfo) messageObj.getContent());
                MessageServer.printParticipants();
                break;
            case NOTE:
                // Initializing variables
                ObjectInputStream objInStream;
                ObjectOutputStream objOutStream;
                Socket serverSocket;
                
                // looping over the participants array
                for (int i = 0; i < MessageServer.participants.size(); i++)
                {
                    // Getting current participant
                    NodeInfo info = MessageServer.participants.get(i);
                    System.out.println("Sending Note to " + info);

                    try
                    {
                        //System.out.println("Creating socket");
                        serverSocket = new Socket(info.getAddress(), info.getPort());

                        //System.out.println("Creating streams");
                        objInStream = new ObjectInputStream(serverSocket.getInputStream());
                        objOutStream = new ObjectOutputStream(serverSocket.getOutputStream());

                        //System.out.println("Trying to write to output stream");
                        objOutStream.writeObject(messageObj);

                        // Closing socket
                        serverSocket.close();
                    }
                    catch (UnknownHostException e)
                    {
                        Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "UnknownHostException in run of sender", e);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, "IOException in run of sender", ex);
                    }
                }
                break;
            default:
                break;
        }
    }
}
