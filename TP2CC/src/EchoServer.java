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
import java.util.Arrays;
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
        long startTimer = System.currentTimeMillis();

        String temp = "";
                
        temp += allDirectoryFiles.size() + ";";
        for (File f : allDirectoryFiles) {
            temp += f.getName() +";" + f.length() + ";";
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
            //Só consegue obter em array 2^32 bytes de um ficheiro
            byte[] mensagemBytes = EchoServer.FileToString(f);

            int numberPackets =(int) Math.ceil( mensagemBytes.length / (float) EchoServer.bufferSize);
            for (int i = 0; i < numberPackets; i++) {
                int offset = i * EchoServer.bufferSize;
                int length = (i + 1 == numberPackets) ? (mensagemBytes.length % EchoServer.bufferSize) : EchoServer.bufferSize ; 
                DatagramPacket packetFicheiroI = 
                new DatagramPacket(mensagemBytes ,offset, length, enderecoCliente, porta);
                try {
                    serverSocket.send(packetFicheiroI);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to send the 2nd stage message to client :(");
                }      
            }
            //Inicialmente estava a se executar uma copia do array para obter os elementos de um certo intervalo
            //Foi descoberto que o datagrampacket Construtor pode levar um parametro offset do Array           
        }
        long endTimer  = System.currentTimeMillis();
        long timeExecution = endTimer - startTimer;
        System.out.println("Thread de envio do conteudo da pasta concluido Tempo = " +timeExecution + " ms");
    }
}

public class EchoServer {

    private static String DirectoryToShare = "/mainFolder";
    private static final int portaServidor = 12345;
    public static final int bufferSize = 1024;
    public EchoServer(){    
        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(portaServidor);

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
                
                //Espera receber um pacote UDP pelo Cliente
                byte[] bufferReceiver = new byte[bufferSize];
                DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver,bufferReceiver.length);
                serverSocket.receive(receivedPacket);

                String recebido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                
                System.out.println("\nSERVIDOR recebeu um " + recebido);
                
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
