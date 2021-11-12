package TP2;

public class MySocket {
    public String IpAddress;
    public int Port;

    public MySocket(String ip,int port)
    {
        IpAddress = ip;
        Port = port;
    }
    
    public MySocket()
    {
        IpAddress = "127.0.0.1";
        Port = 5000; 
    }
}
