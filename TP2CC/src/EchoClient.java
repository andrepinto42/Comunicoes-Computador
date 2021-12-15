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
    //Endereco IP 127.0.0.1
    public static InetAddress IPAddress;
    public static DatagramSocket socketClient;

    public EchoClient()
    {
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
            Pair<String[],Integer[]> mypair =  ParseFirstMessage(receivedString); 
            String[] nameAllFiles = mypair.getKey();
            Integer[] sizeFiles = mypair.getValue();
            
            //Criar nova diretoria se for necessário
            File newDirectory = new File( System.getProperty("user.dir").toString() + DirectoryToSync+"/");
            if(newDirectory.mkdirs()) System.out.println("Created a new directory ->" + DirectoryToSync);
            
            //Verificar o tamanho dos novos ficheiros após a sua transferencia
            long[] sizeAllFilesTransfered = new long[nameAllFiles.length];
           
            for (int i = 0; i < nameAllFiles.length; i++) {

                String fileNameLocal = DirectoryToSync + "/" + nameAllFiles[i];
                File newFileI = new File( System.getProperty("user.dir").toString() + fileNameLocal);
                if ( newFileI.createNewFile() ) System.out.println("Created new file + " + fileNameLocal);
                else                            System.out.println("Updated file -> " + fileNameLocal);
                
                //Inicializar o escritor para o ficheiro
                PrintWriter outFileI = new PrintWriter(newFileI);         
                
                //Numero de pacotes por enquanto é TamFicheiro / 1024
                int numberPackets =(int) Math.ceil( sizeFiles[i] / (float) EchoServer.bufferSize);

                for (int j = 0; j < numberPackets; j++) {
                    //Espera receber array de bytes até 1024 vindo do servidor                
                    String receivedStringJ = GetMessageServer();
                    
                    //Cliente recebe o conteudo do ficheiro vindo do servidor e guarda noutro ficheiro
                    outFileI.print(receivedStringJ);   
                }
                
                System.out.println("Written " + fileNameLocal + " sucessfully");
                //Assegurar que o conteudo é todo enviado para o ficheiro
                outFileI.flush();
                outFileI.close();

                sizeAllFilesTransfered[i] = newFileI.length();
            }
            
            ValidationFiles(nameAllFiles, sizeAllFilesTransfered);
           
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidationFiles(String[] nameAllFiles, long[] sizeAllFilesTransfered) throws IOException {
        System.out.println("\n\n\nValidando ficheiros recebidos...");
        
        StringBuilder sbValidation = new StringBuilder();
        for (int i = 0; i < sizeAllFilesTransfered.length; i++) {
            sbValidation.append(nameAllFiles[i]).append(";");
            sbValidation.append(sizeAllFilesTransfered[i]).append(";");
        }

        SendMessageServer(sbValidation.toString());
        System.out.println("\n\n");
        
        //Cliente espera por verificacao do servidor, se os ficheiros estiverem incorretos o servidor tem de reenviar
        String messageServerValidation = GetMessageServer();
        
        if (! messageServerValidation.equals("200"))
        {
            System.out.println("Ocorreu um erro na transferencia de ficheiros :(");
            HandleInvalidFile();
        }
        else
        System.out.println("Validação completa com sucesso");
    }

    public void HandleInvalidFile()
    {

    }

    public  static String DirectoryToSync = "/syncFolder";
    public static int porta = 12345;
    public static void main(String[] args) {
        if (args.length >= 1)
        DirectoryToSync = "/" + args[0];

        if (args.length >= 2){
            try {
                porta = Integer.parseInt(args[1]);
            } catch (Exception e) { porta = 12345;}
        }

        new EchoClient();
    }

    private  String GetMessageServer() throws IOException {
        //Por enquanto tem um tamanho predefinido de 256, 
        //sendo que nao consegue receber mensagens maiores do que isso
        byte[] bufferReceiver = new byte[EchoServer.bufferSize];
        DatagramPacket receivedPacket = new DatagramPacket(bufferReceiver, EchoServer.bufferSize);

        //Espera receber uma resposta do servidor
        socketClient.receive(receivedPacket);
        String receivedString = new String( receivedPacket.getData(), 0, receivedPacket.getLength());
        return receivedString;
    }

    private  void SendMessageServer(String msg) throws IOException {
        byte[] mensagem = msg.getBytes();
        DatagramPacket packet   = new DatagramPacket(mensagem, mensagem.length, IPAddress, porta);
        
        socketClient.send(packet);
    }

    //Parsing of the protocol FT-Rapid
    private static Pair<String[],Integer[]> ParseFirstMessage(String receivedString) {
        Scanner parserScanner = new Scanner(receivedString);
        parserScanner.useDelimiter(";");
        
        int numberFiles = Integer.parseInt( parserScanner.next());
        String[] fileNames  = new String[numberFiles];
        Integer[] fileSizes = new Integer[numberFiles];
        for (int i = 0; i < numberFiles; i++) {
             fileNames[i] = parserScanner.next();
             fileSizes[i] = Integer.parseInt( parserScanner.next());
        }     
         
        System.out.println("Received from server nºFiles =" + numberFiles);
        for (int i = 0; i < numberFiles; i++) {
            System.out.println("Sync " + fileNames[i]+ " size = " + fileSizes[i]+"bits");
        }
        Pair<String[],Integer[]> mypair = new Pair<>(fileNames,fileSizes);
        parserScanner.close();
        return mypair;
    }
    
}

class Pair<T1, T2> {
    private final T1 key;
    private final T2 value;
    public Pair(T1 first, T2 second) {
        this.key = first;
        this.value = second;
    }
    public T1 getKey() {
        return key;
    }
    public T2 getValue() {
        return value;
    }
}
