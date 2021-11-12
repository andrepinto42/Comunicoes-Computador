package TP2;
// A Java program for a Server
import java.net.*;
import java.util.*;
import java.io.*;
import TP2.*;
public class Server
{
	//initialize socket and input stream
	private ServerSocket server = null;
	private DataOutputStream outStream = null;
	private PrintWriter outWriter= null;
	// constructor with port
	public Server(int port)
	{
		OpenFile();
		// starts server and waits for a connection
		try
		{
			server = new ServerSocket(port);
			System.out.println("Server started");


			while(true)
			{
				System.out.println("Waiting for a client ...");
				Socket socket = server.accept();

				//Creating a new outputStream
				outWriter = new PrintWriter(socket.getOutputStream());
				outWriter.print("OLha a mensagem");
				outWriter.flush();


				System.out.println("Client accepted");
				
				HandleClient(socket);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		finally
		{
			try {
				outStream.close();
				server.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void HandleClient(Socket socket) throws IOException {		
		// takes input from the client socket
		DataInputStream inputStream = new DataInputStream(
		new BufferedInputStream(socket.getInputStream()));
		
		HandleClientMessages(inputStream);
		
		System.out.println("Closing connection");
		// close connection
		socket.close();
		inputStream.close();
		outWriter.close();
	}

	private void HandleClientMessages(DataInputStream inputStream) {
		String line = "";
		// reads message from client until "Over" is sent

		List<String> arr = new ArrayList<String>();
		while (!line.equals("Over"))
		{
			try
			{
				line = inputStream.readUTF();
				arr.add(line);
				System.out.println(line);

			}
			catch(Exception e)
			{
				System.out.println(e);
				break;
			}
		}
		
		StringBuilder builder = new StringBuilder();
		for (String b : arr) {
			builder.append(b).append(" ");
		}
		System.out.println("Array convertido para string " + builder.toString() );

		ComunicateWithServer.WriteFileWithConnection(builder.toString(),outStream);
		
	}

	private void OpenFile() {
		try{		
			String fileName = "test1.txt";
			FileOutputStream fos = new FileOutputStream(fileName);
			outStream = new DataOutputStream(new BufferedOutputStream(fos));
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	public static void main(String args[])
	{
		Server server = new Server(5000);
	}
}
