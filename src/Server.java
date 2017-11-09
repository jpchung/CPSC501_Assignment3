/**
 * Server class for network connection b/w Sender and Receiver.
 * Receives xml document from Client for deserialization
 */

import java.io.*;
import java.net.*;
public class Server extends Thread {

    private Socket server;
    private ServerSocket serverSocket;

    public Server(int port){
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
                System.out.println("Server running on " + serverSocket.getLocalPort());
                server = serverSocket.accept();

                System.out.println("Server connected to " + server.getRemoteSocketAddress());

                DataInputStream inputStream = new DataInputStream(server.getInputStream());

                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                System.out.println("Closing Server...");

                server.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args){
        int port = 8000;

        try{
            Thread serverThread = new Server(port);
            serverThread.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}
