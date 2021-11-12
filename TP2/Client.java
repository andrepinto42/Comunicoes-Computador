package TP2;

// A Java program for a Client
import java.net.*;
import java.io.*;
import TP2.ReadConfigurations;

public class Client
{
	// initialize socket and input output streams
	private Socket socket		 = null;
	BufferedReader readerSystem = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStreamSocket	 = null;
	private BufferedReader inputWriterSocket = null;

	// constructor to put ip address and port
	public Client(String address, int port)
	{
		// establish a connection
		CreateClientConnection(address,port);
		
		if (outputStreamSocket == null) return;
			
		ReadUserInput();
	}

	public Client()
	{
		// establish a connection
		CreateClientConnection(null,-1);

		if (outputStreamSocket == null) return;
			
		ReadUserInput();
	}

	//keep reading until "Over" is input
	private void ReadUserInput() {
		String line = "";
		while (!line.equals("Over"))
		{
			try
			{
				if (outputStreamSocket == null)
				{
					System.out.println("Output is null please restart !");
					break;
				}

				line = readerSystem.readLine();
				
				//Sending the message to the Server	
				outputStreamSocket.writeUTF(line);
			
				System.out.println( "Servidor respondeu = " + inputWriterSocket.readLine());
				
			}
			catch(IOException i)
			{
				System.out.println(i);
			}
			finally{
				CloseConnection();
			}
		}		
	}
	private void CloseConnection(){
		try{
			if (inputStream!= null) inputStream.close();
			if (outputStreamSocket != null) outputStreamSocket.close();
			if (socket!= null) socket.close();
			System.out.println("Conexão Terminada com o Servidor");

		}catch (Exception e){
			System.out.println("Erro ao fechar a conexão " + e);
		}
	}

	public boolean SendMessageToServer(String message)
	{
		try {
		if (outputStreamSocket != null) outputStreamSocket.writeUTF(message);
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

	

	private void CreateClientConnection(String address, int port) {
		socket = OpenSocket(address,port);
		try
		{
			System.out.println("Connected to -> " + socket.getInetAddress().getHostName());
			// takes input from terminal
			readerSystem = new BufferedReader(new InputStreamReader(System.in));

			inputWriterSocket = new BufferedReader( new InputStreamReader(socket.getInputStream()));

			outputStreamSocket = new DataOutputStream(socket.getOutputStream());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public Socket OpenSocket(String address,int port)
	{
		Socket s = null;

		try{
			if (address == null || port == -1)
			{
				MySocket mysocket = ReadConfigurations.ReadSocketConfiguration();
				s = new Socket(mysocket.IpAddress,mysocket.Port);
			}
			else
			{
				s = new Socket(address,port);
			}

		} catch (Exception e){
			System.out.println("Falha ao abrir Socket");
			System.out.println(e);
		}
		return s;
	}


	public static void main(String args[])
	{
		// PrintCurrentFilesDirectory();
		Client client = new Client("127.0.0.1", 5000);
	}
	
}

