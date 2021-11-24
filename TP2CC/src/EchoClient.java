package bin;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

public static String DirectoryToSync = "/syncFolder";
public static String IPAdress = "localhost";
public static int portConnection = 12345;
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(IPAdress, portConnection);

            BufferedReader inputSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputSocket = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {

                if (outputSocket == null) break;
                
                outputSocket.println(userInput);
                outputSocket.flush();

                Integer NumberOfFiles=0;
                try {
                    NumberOfFiles = Integer.parseInt(inputSocket.readLine());                    
                } catch (Exception e) {
                    System.out.println("Não foi possivel converter o numero de ficheiros para ler");
                    break;
                }

                System.out.println("Vou executar " + NumberOfFiles + " vezes");
                
                for (int i = 0; i < NumberOfFiles; i++) {
                    
                    /* 
                        Read response from server and store it in a File
                    */
                    String response;
                    
                    //Serve para saber o nome do ficheiro que se está a receber
                    String FileName = inputSocket.readLine();
                    Integer FileNumberLines = 0;
                    try {
                        FileNumberLines = Integer.parseInt( inputSocket.readLine());
                    }    
                     catch (Exception e) {
                        System.out.println("Nao foi possivel converter para um numero a LINHA lida do Socket");
                        break;
                    }

                    String fileNameLocal = DirectoryToSync + "/" + FileName;
                    File newFile = new File( System.getProperty("user.dir").toString() + fileNameLocal);
                    if ( newFile.createNewFile() ) System.out.println("Created new file + " + fileNameLocal);

                    PrintWriter outFile = new PrintWriter(newFile);         
                    
                    //Cliente recebe o conteudo do ficheiro do Servidor linha a linha e converte para um ficheiro
                    for (int j = 0; j < FileNumberLines; j++) {
                        response = inputSocket.readLine();
                        outFile.println(response);
                        System.out.println(response);
                    }
                    System.out.println("Written " + fileNameLocal + " sucessfully");
                    //Assegurar que o conteudo é todo enviado para o ficheiro
                    outFile.flush();
                    outFile.close();
                }
                System.out.println("Server synchronization completed :)");
            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}