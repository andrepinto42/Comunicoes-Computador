package bin;
import java.io.BufferedReader;
import java.io.File;
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
    public static void main(String[] args) {
        try {
            //Criar a socket pela qual o cliente vai comunicar com o Servidor

            //Endereco IP 127.0.0.1
            InetAddress IPAddress = InetAddress.getByName("localhost");

            //Mensagem que pretendemos enviar ao servidor
            String msg = "este e o meu ficheiro de teste \nasdasda \n20 \n42 \nasda asdasda \no ficheiro chegou ao fim;;;;;";

            File newFile = new File( System.getProperty("user.dir").toString() + "/novito.txt");
            
            if ( newFile.createNewFile() ) System.out.println("Created new file + ");
            PrintWriter outFile = new PrintWriter(newFile);   
            outFile.write(msg);
            outFile.flush();
            
            byte[] mensagem = msg.getBytes();

            DatagramPacket packet   = new DatagramPacket(mensagem, mensagem.length, IPAddress, porta);
            
            DatagramSocket socketClient = new DatagramSocket();
            
            //Enviar a mensagem
            socketClient.send(packet);

            //Por enquanto tem um tamanho predefinido de 256, 
            //sendo que nao consegue receber mensagens maiores do que isso
            byte[] bufferReceiver = new byte[256];
            DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver, bufferReceiver.length);

            //Espera receber uma resposta do servidor
            socketClient.receive(receivedPacket);
            String receivedString = new String( receivedPacket.getData(), 0, receivedPacket.getLength());
            
            //PARSING THE FIRST MESSAGE
            String[] nameAllFiles = ParseFirstMessage(receivedString);
            
            
            for (int i = 0; i < nameAllFiles.length; i++) {
                byte[] bufferReceiverI = new byte[256];
                
                DatagramPacket receivedPacketI = new DatagramPacket(bufferReceiverI, bufferReceiverI.length);

                //Espera receber uma resposta do servidor
                socketClient.receive(receivedPacketI);
                String receivedStringI = new String( receivedPacketI.getData(), 0, receivedPacketI.getLength());
                
                String fileNameLocal = DirectoryToSync + "/" + nameAllFiles[i];
                File newFileI = new File( System.getProperty("user.dir").toString() + fileNameLocal);
                if ( newFileI.createNewFile() ) System.out.println("Created new file + " + fileNameLocal);
                PrintWriter outFileI = new PrintWriter(newFileI);         
                
                //Cliente recebe o conteudo do ficheiro vindo do servidor e guarda noutro ficheiro
                outFileI.print(receivedStringI);
                
                System.out.println("Written " + fileNameLocal + " sucessfully");
                //Assegurar que o conteudo é todo enviado para o ficheiro
                outFileI.flush();
                outFileI.close();
            }
            
            outFile.close();    
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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