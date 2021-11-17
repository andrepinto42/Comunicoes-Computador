package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
    
    private static String FileNameToCreate = "test03.txt";

    public static String FileTransferName = "test.txt";   

    File[] allDirectoryFiles;
    ServerSocket ss;
    public EchoServer(){
        try {
            ss = new ServerSocket(12345);
            allDirectoryFiles = new File(System.getProperty("user.dir")).listFiles();
            
            File TestFile = null;
            for (int i = 0; i < allDirectoryFiles.length; i++) {

                if (allDirectoryFiles[i].getName().compareTo(FileTransferName) == 0)
                    TestFile = allDirectoryFiles[i]; 
                }

            while (true) {
                System.out.println("Esperando por Cliente...");
                Socket socket = ss.accept();
                System.out.println("Cliente foi aceito com sucesso...");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                
                /*
                Tratar do cliente
                */
                String line;
                while ((line = in.readLine()) != null) {
                    out.println(FileNameToCreate);
                    
                    String ContentString =  FileToString(TestFile).getKey();
                    Integer size = FileToString(TestFile).getValue();
                    out.println(size);

                    System.out.print(ContentString);

                    out.print(ContentString);
                    out.flush();
                }
                System.out.println("Terminando Conexão com o Cliente");
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                ss.close();                
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