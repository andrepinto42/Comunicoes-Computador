package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class EchoServer {


    public EchoServer(){    
        try {
          
            //Busca todos os ficheiros da diretoria para partilhar
            File[] allDirectoryFiles = new File( System.getProperty("user.dir")+ DirectoryToShare ).listFiles();
            
            //Guarda os ficheiros que não são pastas
            List<File> allFilesToSend = new ArrayList<File>();            
            for (File file : allDirectoryFiles) {
                if (file.isFile())
                    allFilesToSend.add(file);
            }

            while (true) 
            {

                System.out.print("\n-----------------\n\nEsperando por Cliente...");

                DatagramPacket receivedPacket = GetMessageClient();
                String recebido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                
                System.out.println("\nSERVIDOR recebeu um " + recebido);
                
                //Caso o cliente deseje interromper o servidor
                if (recebido.equals("over")) break;
            
                HandleClient threadClient = new HandleClient(serverSocket,receivedPacket,allFilesToSend);
                threadClient.run();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            serverSocket.close();
            System.out.println("Servidor Encerrado");
        }
        finally{
            try {
            serverSocket.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static byte[] FileToString(File file)
    {
        StringBuilder sb = new StringBuilder();
        
        try {
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              sb.append(data).append("\n");
            }
            myReader.close();
          } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
          return sb.toString().getBytes();
    }

    public static DatagramPacket GetMessageClient(){
        //Espera receber um pacote UDP pelo Cliente
        byte[] bufferReceiver = new byte[bufferSize];
        DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver,bufferReceiver.length);
        try {
            serverSocket.receive(receivedPacket);
        } catch (Exception e) {}
        return receivedPacket;
    }

    private static String DirectoryToShare = "/mainFolder";
    public static String IpAdressNumber = "127.0.0.1";
    private static final int portaServidor = 12345;
    public static final int bufferSize = 1024;
    static DatagramSocket serverSocket = null;
    public static void main(String[] args) throws SocketException {
        
        
        if (args.length >= 1)
            DirectoryToShare = "/" + args[0];
        if (args.length >=2)
            IpAdressNumber = args[1];
        
        serverSocket = new DatagramSocket(null);
        InetSocketAddress address = new InetSocketAddress(IpAdressNumber, portaServidor);
        serverSocket.bind(address);

        System.out.println("Server connected to port " + portaServidor + " and IP: " + IpAdressNumber);
        new EchoServer();
    }

}
