package TP2;

// A Java program for a Client
import java.net.*;
import java.io.*;
import TP2.ReadConfigurations;

public class Client
{
	// initialize socket and input output streams
	private Socket socket		 = null;
	BufferedReader reader = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream	 = null;
	private BufferedReader inputWriterSocket = null;

	// constructor to put ip address and port
	public Client(String address, int port)
	{
		socket = OpenSocket(address,port);
		
		System.out.println("Waiting for a specific message :)");
		try {
			inputWriterSocket = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			String response = inputWriterSocket.readLine();
			System.out.println(response);
		}catch (Exception e){
		}
		
		// establish a connection
		outputStream = CreateClientConnection();

		

		if (outputStream == null) return;
			
		ReadUserInput();
	}
	public Client()
	{
		socket = OpenSocket();
		
		// establish a connection
		outputStream = CreateClientConnection();

		if (outputStream == null) return;
			
		ReadUserInput();
	}

	//keep reading until "Over" is input
	private void ReadUserInput() {
		String line = "";
		while (!line.equals("Over"))
		{
			try
			{
				line = reader.readLine();
				
				//Sending the message to the Server	
				if (outputStream != null) outputStream.writeUTF(line);
				else
				{
					System.out.println("Output is null please restart !");
					break;
				}
			}
			catch(IOException i)
			{
				System.out.println(i);
			}
		}
		// close the connection
		try
		{
			if (inputStream!= null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (socket!= null) socket.close();
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}

	public boolean SendMessageToServer(String message)
	{
		try {
		if (outputStream != null) outputStream.writeUTF(message);
		else
		{
			System.out.println("Output is null please restart !");
			return false;
		}
		}catch (Exception e) {
			System.out.println("Não consegui enviar ao servidor");
		}
		return true;
	}

	

	private DataOutputStream CreateClientConnection() {
		try
		{

			System.out.println("Connected to -> " + socket.getInetAddress().getHostName());
			// takes input from terminal
			reader = new BufferedReader(new InputStreamReader(System.in));

			// sends output to the socket
			return new DataOutputStream(socket.getOutputStream());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return  null;
	}

	public Socket OpenSocket(String address,int port)
	{
		Socket s = null;

		try{
			s = new Socket(address,port);
		} catch (Exception e){
			System.out.println("Falha ao abrir Socket");
			System.out.println(e);
		}
		return s;
	}
	public Socket OpenSocket()
	{
		Socket s = null;

		MySocket mysocket = ReadConfigurations.ReadSocketConfiguration();
		try{
			s = new Socket(mysocket.IpAddress,mysocket.Port);
		} catch (Exception e){
			System.out.println("Falha ao abrir Socket ");
			System.out.println(e);
		}
		return s;
	}

	private static void PrintCurrentFilesDirectory() {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		File[] directory = new File(System.getProperty("user.dir")).listFiles();
		
		for (File file : directory) {
			
			if(file.isFile()){
				System.out.println( file.getName());
			}
		}
	}
	public static void main(String args[])
	{
		// PrintCurrentFilesDirectory();
		Client client = new Client("127.0.0.1", 5000);
	}
	
}

