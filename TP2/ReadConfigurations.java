package TP2;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.NoSuchElementException;
import java.util.Scanner; // Import the Scanner class to read text files

import TP2.MySocket;


public class ReadConfigurations {
    public final static String nameFile = "configFile.config";

    public static MySocket ReadSocketConfiguration()
    {
        MySocket socket = null;
        try {
            File myObj = new File(nameFile);

            Scanner myReader = new Scanner(myObj);
            
            socket = new MySocket(myReader.nextLine(),Integer.parseInt(myReader.nextLine()));
            System.out.println("Socket criada com IP : " + socket.IpAddress + 
                               " e porta = " + socket.Port);
            myReader.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File doenst exist man :(");
            e.printStackTrace();
        }
        catch (NoSuchElementException e){
            System.out.println("Couldnt read the line :(");
        }

        return socket;
        
    }
}
