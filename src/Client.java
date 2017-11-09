/**
 * Client class for network connection b/w Sender and Receiver.
 * Sends serialized object as xml document to Server
 * @author Johnny Chung
 */

import java.io.*;
import java.net.*;
public class Client
{


    public static void main(String[] args){
        String ipAddress = "127.0.0.1";
        int port = 8000;

        try{
            System.out.println("Connecting to Server " + ipAddress + "on port: " + port);

            Socket client = new Socket(ipAddress, port);
            System.out.println("Client connected to " + client.getRemoteSocketAddress());

            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            //REPLACE LATER: send xml document from client to Server instead of string message
            outputStream.writeUTF("String message to Server from Client");

            DataInputStream inputStream =  new DataInputStream(client.getInputStream());
            System.out.println("Server message: " + inputStream.readUTF());

            client.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
