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


public class HandleClient implements Runnable{
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
        long allFileSizesLong =0;
        temp += allDirectoryFiles.size() + ";";
        for (File f : allDirectoryFiles) {
            temp += f.getName() +";" + f.length() + ";";
            allFileSizesLong += f.length();
        }

        // tamanho;nomeFicheiro1;tamanhoFicheiro1;nomeFicheiro2;tamanhoFicheiro2;........
        SendMessageClient(temp);
        
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
                
                SendMessageClient(mensagemBytes, length, offset);    
            }
            //Inicialmente estava a se executar uma copia do array para obter os elementos de um certo intervalo
            //Foi descoberto que o datagrampacket Construtor pode levar um parametro offset do Array           
        }
        long endTimer  = System.currentTimeMillis();
        long timeExecution = endTimer - startTimer;
        System.out.println("Thread de envio do conteudo da pasta concluido Tempo = " +timeExecution + " ms");
        
        System.out.println("Debito " + (allFileSizesLong/ timeExecution) + " bits/s");

        boolean isValid = WaitforValidation(allDirectoryFiles);
        SendMessageClient(isValid ? "200" : "Error");
    }
    
    public boolean WaitforValidation(List<File> allFiles)
    {
        DatagramPacket receivedPacket = EchoServer.GetMessageClient();

        String recebido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

        return ParseValidation(recebido,allFiles);

    }

    private  boolean ParseValidation(String receivedString,List<File> allFiles) {
        int size = allFiles.size();
        
        Scanner parserScanner = new Scanner(receivedString);
        parserScanner.useDelimiter(";");
        
        String[] fileNames  = new String[size];
        Integer[] fileSizes = new Integer[size];
        for (int i = 0; i < size; i++) {
             fileNames[i] = parserScanner.next();
             fileSizes[i] = Integer.parseInt( parserScanner.next());
        }     
        parserScanner.close();

        /*
            Verificar se tem ambos o mesmo conteudo
        */
        for (int i = 0; i < size; i++) {
            if ( !fileNames[i].equals(allFiles.get(i).getName()) || fileSizes[i] != allFiles.get(i).length())
            {
                System.out.println("Transmissão foi feita com insucesso no ficheiro " + fileNames[i]);
                return false;
            }
        }
        System.out.println("Transmissão feita com sucesso :) ");
        return true;
    }

    private void SendMessageClient(String temp) {
        byte[] mensagemServidor = temp.getBytes();
        
        DatagramPacket packetMessageClient = new DatagramPacket(mensagemServidor, mensagemServidor.length, enderecoCliente, porta);
        try {
            serverSocket.send(packetMessageClient);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable  message to the client :(");
        }
    }
    private void SendMessageClient(byte[] mensagemServidor,int length,int offset) {
        DatagramPacket packetMessageClient = new DatagramPacket(mensagemServidor, offset,length, enderecoCliente, porta);
        try {
            serverSocket.send(packetMessageClient);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable  message to the client :(");
        }
    }
}