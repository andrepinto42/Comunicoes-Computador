package bin;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.RepaintManager;

public class EchoClient {


    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {

                out.println(userInput);
                out.flush();

                
                 /*
                Read response from server and store it in a File
                */
                String response;
                String FileName = in.readLine();
                Integer FileNumberLines = Integer.parseInt( in.readLine());
                
                System.out.println("This is the FileName "+ FileName);
                System.out.println("This is the Size "+ FileNumberLines);
                
                File newFile = new File( System.getProperty("user.dir").toString() + "/" + FileName);
                if ( newFile.createNewFile() ) System.out.println("Created new file");


                PrintWriter outFile = new PrintWriter(newFile);                
                for (int i = 0; i < FileNumberLines; i++) {
                    response = in.readLine();
                    outFile.println(response);
                    outFile.flush();
                    System.out.println(response);
                }               

                // while( (response = in.readLine()) != null)
                // {
                //     outFile.println(response);
                //     outFile.flush();
                //     System.out.println(response);
                // }
                System.out.println("Server responsed ");
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