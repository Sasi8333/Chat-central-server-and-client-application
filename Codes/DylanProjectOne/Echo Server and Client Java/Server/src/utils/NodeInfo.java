package utils;

import java.io.Serializable;

/**
 *
 * @author dylan
 */
public class NodeInfo implements Serializable 
{
    // Class variables
    public String address;
    public int port;
    public String name = null;
    
    public NodeInfo(String address, int port)
    {
        this.address = address;
        this.port = port;
    }
    
    public NodeInfo(String address, int port, String name)
    {
        this.address = address;
        this.port = port;
        this.name = name;
    }
    
    public String getAddress()
    {
        return this.address;
    }
    
    public int getPort()
    {
        return this.port;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    /*
        Overrided method that will compare two nodeinfo objects and return true
          iff both the address and port are the same.
    */
    public boolean equals(Object other)
    {
        boolean equalAddr = this.address.equals(((NodeInfo) other).getAddress());
        boolean equalPort = this.port == (((NodeInfo) other).getPort());
        return (equalAddr && equalPort);
    }
    
    /*
        Method will return the information in nodeinfo as a string
    */
    public String toString()
    {
        return "Name: " + this.name + ", Address: " + this.address + ", Port: " + this.port + ".";
    }
}