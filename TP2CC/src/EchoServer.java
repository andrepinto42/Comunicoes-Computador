package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


class HandleClient implements Runnable{

    Socket socketClient;
    public HandleClient(Socket s)
    {
        socketClient = s;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}

public class EchoServer {
    
    private static String DirectoryToShare = "/mainFolder";

    public EchoServer(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(12345);
            File[] allDirectoryFiles = new File( System.getProperty("user.dir")+ DirectoryToShare ).listFiles();
            
            List<File> allFilesToSend = new ArrayList<File>();            
            for (File file : allDirectoryFiles) {
                if (allDirectoryFiles[i].isFile())
                    allFilesToSend.add(file);

            }
            
            File TestFile = null;
            for (int i = 0; i < allDirectoryFiles.length; i++) {
                if ( allDirectoryFiles[i].isFile()){
                    TestFile = allDirectoryFiles[i];
                }
            }

            while (true) {
                System.out.println("Esperando por Cliente...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente foi aceito com sucesso...");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outputSocket = new PrintWriter(clientSocket.getOutputStream());
                
                /*
                 Servidor espera receber uma linha do cliente para começar a enviar mensagens
                */
                String line;
                while ((line = in.readLine()) != null) {

                    outputSocket.println(TestFile.getName());

                    Pair<String,Integer> pairFile = FileToString(TestFile);
                    
                    String ContentString =  pairFile.getKey();
                    Integer size = pairFile.getValue();
                    outputSocket.println(size);

                    System.out.print("Este é o conteudo da messagem :\n" + ContentString);

                    outputSocket.print(ContentString);
                    outputSocket.flush();
                }
                System.out.println("Terminando Conexão com o Cliente");
                clientSocket.shutdownOutput();
                clientSocket.shutdownInput();
                clientSocket.close();
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

    public Pair<String,Integer> FileToString(File file)
    {
        StringBuilder sb = new StringBuilder();
        int size = 0;
        try {
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                size++;
              String data = myReader.nextLine();
              sb.append(data).append("\n");
            }

            myReader.close();
          } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }

          return new Pair<String,Integer>((String) sb.toString(),(Integer) size);
    }
    public static void main(String[] args) {
        new EchoServer();
    }
}