/**
 * Receiver Server class for network connection b/w Sender and Receiver.
 * Receives xml document from Client for deserialization
 */

import java.io.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.*;
public class Receiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;

    public Receiver(int port){
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(100000);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){

        while(true){
            try{
                System.out.println("Receiver (Server) running on " + serverSocket.getLocalPort());

                socket = serverSocket.accept();
                System.out.println("Receiver (Server) connected to " + socket.getRemoteSocketAddress());

                File file =  new File("serializedObject.xml");

                //io streams
                InputStream inputStream = socket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                //receive file from Sender client as byte array
                byte[] fileBytes = new byte[1024 * 1024];
                int bytesRead = 0;
                while((bytesRead =  inputStream.read(fileBytes)) > 0){
                    fileOutputStream.write(fileBytes, 0, bytesRead);
                    break;
                }

                //build object from file
                //deserialize XML document with SAXBuilder to build objects
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = (Document)saxBuilder.build(file);
                Object obj = Deserializer.deserialize(document);

                //visualize object (INSPECTOR FROM ASSIGNMENT 2)



                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args){
        int port = 8000;

        try{
            Thread serverThread = new Receiver(port);
            serverThread.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}
