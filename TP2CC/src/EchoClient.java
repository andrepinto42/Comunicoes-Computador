package bin;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {

public static String DirectoryToSync = "/syncFolder";
public static final int porta = 12345;

//Endereco IP 127.0.0.1
public static InetAddress IPAddress;
public static DatagramSocket socketClient;

    public static void main(String[] args) {
        try {
            try { IPAddress= InetAddress.getByName("localhost");}
            catch (Exception e) { e.printStackTrace();}

            socketClient = new DatagramSocket();
            
            //Change this message to become a input message from the user
            String msg = "Hello there server";

            //Enviar a primeira mensagem que queremos comunicar com o servidor
            SendMessageServer(msg);

            String receivedString = GetMessageServer();
            
            /*PARSING THE FIRST MESSAGE
                @nameAllFiles -> contem o nome dos ficheiros que tem de ser criados
            */
            String[] nameAllFiles = ParseFirstMessage(receivedString);
            
            for (int i = 0; i < nameAllFiles.length; i++) {

                //Espera receber uma resposta do servidor com o conteudo do ficheiro
                String receivedStringI = GetMessageServer();

                String fileNameLocal = DirectoryToSync + "/" + nameAllFiles[i];
                File newFileI = new File( System.getProperty("user.dir").toString() + fileNameLocal);
                if ( newFileI.createNewFile() ) System.out.println("Created new file + " + fileNameLocal);
                else                            System.out.println("Updated file -> " + fileNameLocal);
                
                PrintWriter outFileI = new PrintWriter(newFileI);         
                
                //Cliente recebe o conteudo do ficheiro vindo do servidor e guarda noutro ficheiro
                outFileI.print(receivedStringI);
                
                System.out.println("Written " + fileNameLocal + " sucessfully");
                //Assegurar que o conteudo é todo enviado para o ficheiro
                outFileI.flush();
                outFileI.close();
            }
            
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String GetMessageServer() throws IOException {
        //Por enquanto tem um tamanho predefinido de 256, 
        //sendo que nao consegue receber mensagens maiores do que isso
        byte[] bufferReceiver = new byte[256];
        DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver, bufferReceiver.length);

        //Espera receber uma resposta do servidor
        socketClient.receive(receivedPacket);
        String receivedString = new String( receivedPacket.getData(), 0, receivedPacket.getLength());
        return receivedString;
    }

    private static void SendMessageServer(String msg) throws IOException {
        byte[] mensagem = msg.getBytes();
        DatagramPacket packet   = new DatagramPacket(mensagem, mensagem.length, IPAddress, porta);
        
        socketClient.send(packet);
    }

    private static String[] ParseFirstMessage(String receivedString) {
        Scanner parserScanner = new Scanner(receivedString);
        parserScanner.useDelimiter(";");
        
        int numberFiles = Integer.parseInt( parserScanner.next());
        String[] fileNames  = new String[numberFiles];
        for (int i = 0; i < numberFiles; i++) {
             fileNames[i] = parserScanner.next();

        }
            
         
        System.out.println("Received from server nºFiles =" + numberFiles);
        for (int i = 0; i < numberFiles; i++) {
            System.out.println(fileNames[i]);
        }
        
        parserScanner.close();
        return fileNames;
    }
}