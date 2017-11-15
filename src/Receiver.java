/**
 * Receiver Server class for network connection b/w Sender and Receiver.
 * Receives xml document from Sender (Client) for deserialization and visualization
 */

import java.io.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.*;
public class Receiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;

    /**
     * Constructor to initialize server socket and timeout
     * @param port oort number
     */
    public Receiver(int port){
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(200000);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Override the Thread run method, will be what each serverThread executes.
     * Receives file from Sender, deserializes it into a Document, then builds and visualizes object
     */
    public void run(){

        while(true){
            try{
                System.out.println("Receiver (Server) running on " + serverSocket.getLocalPort());

                socket = serverSocket.accept();
                System.out.println("Receiver (Server) connected to " + socket.getRemoteSocketAddress());

                File file =  new File("receivedFile.xml");

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
                System.out.println("received file!");

                //build object from file
                //deserialize XML document with SAXBuilder to build objects
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = (Document) saxBuilder.build(file);
                Object obj = Deserializer.deserialize(document);

                //visualize object (via reflective Inspector class)
                System.out.println("\n======================================================");
                Inspector inspectorGadget = new Inspector();
                inspectorGadget.inspect(obj, false);
                System.out.println("\n======================================================");

                //close socket
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * main method for running Receiver
     * @param args
     */
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
