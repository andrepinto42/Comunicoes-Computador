package TP2;
import java.net.*;
import java.io.*;

public class ComunicateWithServer {

	
    public static boolean SendMessageToServer(String message,String address, int port)
	{
		try {
		var tempSocket = new Socket(address, port);
		var tempOut = new DataOutputStream(tempSocket.getOutputStream());
		
		tempOut.writeUTF(message);
		tempOut.close();		
		tempSocket.close();
		}catch (Exception e) {
			System.out.println("Ocorreu um erro a tentar enviar a Mensagem ao Servidor");
			return true;
		}
		return true;
	}

	public static boolean WriteFileNoConnection(String inputFile,String outputFile) {
        try {
            var fis = new FileInputStream(inputFile);
            var fos = new FileOutputStream(outputFile);

			var inStream = new DataInputStream(new BufferedInputStream(fis));
            var outStream = new DataOutputStream(new BufferedOutputStream(fos));


            byte[] arr = inStream.readAllBytes();
            outStream.write(arr, 0,arr.length);

            outStream.close();
            inStream.close();
			return true;
        } 
        catch (Exception e) {
            System.out.println(e);
			return false;
        }
    }

	public static boolean WriteFileWithConnection(byte[] arr,DataOutputStream outStream) {
        try {
            outStream.write(arr, 0,arr.length);
            outStream.close();
			return true;
        } 
        catch (Exception e) {
            System.out.println(e);
			return false;
        }
    }
    
	public static boolean WriteFileWithConnection(String s,DataOutputStream outStream) {
        try {
            outStream.writeBytes(s);
            outStream.close();
			return true;
        } 
        catch (Exception e) {
            System.out.println(e);
			return false;
        }
    }

    private static void PrintCurrentFilesDirectory() {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		File[] directory = new File(System.getProperty("user.dir")).listFiles();
		
		for (File file : directory) {
			
			if(file.isFile()){
				System.out.println( file.getName());
			}
		}
	}

}
