package utils;

import java.io.Serializable;
import utils.NodeInfo;

/**
 *
 * @author dylan
 */
public class Message implements Serializable 
{
    // The types that a message type variable can have
    public enum messageTypes
    {
        JOIN,
        LEAVE,
        NOTE,
        SHUTDOWN
    };
    
    // class variables
    public messageTypes type;
    public Object content;
    
    public Message(messageTypes type, Object content)
    {
        this.type = type;
        this.content = content;
    }

    public messageTypes getType()
    {
        return this.type;
    }
    
    public Object getContent()
    {
        return this.content;
    }
    
    public void setType(messageTypes type)
    {
        this.type = type;
    }
    
    public void setContent(Object content)
    {
        this.content = content;
    }
}