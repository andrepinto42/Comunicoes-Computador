package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class HandleClient implements Runnable{
    DatagramSocket serverSocket;
    int porta;
    InetAddress enderecoCliente;
    List<File> allDirectoryFiles;

    public HandleClient(DatagramSocket serverSocket, DatagramPacket pacote,List<File> allFiles)
    {
        this.serverSocket = serverSocket;
        porta = pacote.getPort();
        enderecoCliente = pacote.getAddress();
        allDirectoryFiles = allFiles;
    }
    @Override
    public void run() {
        String temp = "";
                
        temp += allDirectoryFiles.size() + ";";
        for (File f : allDirectoryFiles) {
            temp += f.getName() +";";
        }
        byte[] mensagemServidor = temp.getBytes();
        
        DatagramPacket packetMessageClient = new DatagramPacket(mensagemServidor, mensagemServidor.length, enderecoCliente, porta);
        try {
            
            serverSocket.send(packetMessageClient);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to send the first message to the client :(");
        }
        
        /*Fase 2
            Enviar em cada pacote o conteudo do ficheiro num array de bytes       
        */
        for (File f : allDirectoryFiles) {
            byte[] mensagemBytes = EchoServer.FileToString(f);
            DatagramPacket ficheiroStringI = 
            new DatagramPacket(mensagemBytes, mensagemBytes.length, enderecoCliente, porta);
            try {
                serverSocket.send(ficheiroStringI);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to send the 2nd stage message to client :(");
            }
        }

        System.out.println("Thread de envio do conteudo da pasta acabou por terminar :)");
    }
}

public class EchoServer {

    private static String DirectoryToShare = "/mainFolder";

    public EchoServer(){    
        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(12345);

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
                System.out.println("Esperando por Cliente...");
                
                //Espera receber um pacote UDP pelo Cliente
                byte[] bufferReceiver = new byte[256];
                DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver,bufferReceiver.length);
                serverSocket.receive(receivedPacket);

                String recebido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                
                System.out.println("SERVIDOR recebeu um " + recebido);
                
                //Caso o cliente deseje interromper o servidor
                if (recebido.equals("over")) break;
            
                HandleClient threadClient = new HandleClient(serverSocket,receivedPacket,allFilesToSend);
                threadClient.run();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                serverSocket.close();
                System.out.println("Servidor Encerrado");
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
    public static void main(String[] args) {
        new EchoServer();
    }
}