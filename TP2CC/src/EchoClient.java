package bin;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.RepaintManager;

public class EchoClient {

public static String DirectoryToSync = "/syncFolder";
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader inputSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputSocket = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {

                if (outputSocket == null) break;
                
                outputSocket.println(userInput);
                outputSocket.flush();
                
                /*
                Read response from server and store it in a File
                */
                String response;
                String FileName = inputSocket.readLine();
                Integer FileNumberLines = Integer.parseInt( inputSocket.readLine());
                
                System.out.println("This is the FileName "+ FileName);
                
                File newFile = new File( System.getProperty("user.dir").toString() + DirectoryToSync + "/" + FileName);
                if ( newFile.createNewFile() ) System.out.println("Created new file");

                PrintWriter outFile = new PrintWriter(newFile);         
                for (int i = 0; i < FileNumberLines; i++) {
                    response = inputSocket.readLine();
                    outFile.println(response);
                }               
                outFile.flush();

                System.out.println("Server ended message");
                outFile.close();            
            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}