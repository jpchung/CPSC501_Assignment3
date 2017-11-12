/**
 * Sender Client class for network connection b/w Sender and Receiver.
 * Sends serialized object as xml document to Server
 * @author Johnny Chung
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Sender
{

    private static ArrayList<Object> objList;

    public static void main(String[] args){
        String ipAddress = "127.0.0.1"; //localhost
        int port = 8000;

        objList =  new ArrayList<>();

        //loop object selection/creation menu until user decidoes to
        boolean quit = false;
        while(!quit){
            int objChoice = promptObjectSelection();

            if(objChoice == 0){
                System.out.println("Quitting program...");
                System.exit(0);
            }
            else if(0 < objChoice && objChoice < 6 ){
                createObject(objChoice);

            }
            else if (objChoice == 6){
                //quit loop and serialize
                quit = true;
            }
            else{
                System.out.println("Integer out of range");
            }
        }



        try{
            System.out.println("Connecting to Server " + ipAddress + "on port: " + port);

            Socket client = new Socket(ipAddress, port);
            System.out.println("Sender (Client) connected to " + client.getRemoteSocketAddress());

            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            //REPLACE LATER: send xml document from client to Server instead of string message
            outputStream.writeUTF("String message to Server from Sender (Client)");

            DataInputStream inputStream =  new DataInputStream(client.getInputStream());
            System.out.println("Server message: " + inputStream.readUTF());

            client.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private static int promptObjectSelection(){
        /*
        * Objects to be able to create:
        *   - simple object with primitive instance variables
        *       - field values should be settable by user
        *   - object containing reference to other objects
        *       - referenced objects must be created at same time
        *       - primitives should be settable by user
        *       - deal with circular references
        *   - object with array of primitives
        *       - allow user to set values for array elements to arbitrary values
        *   - object with array of object references
        *       - referenced objects must be created at same time
        *   - object that uses an instance of one of Java's collection classes that refer multiple objects
        *       - referenced objects must be created at same time
        * */

        int objChoice;
        System.out.println("======================================================");
        System.out.println("\nLIST OF OBJECTS: \n" +
                "1| SimpleObject (object with primitives)\n" +
                "2| ReferenceObject (object with reference to simpleObject)\n" +
                "3| SimpleArrayObject (object with primitive array)\n" +
                "4| ReferenceArrayObject (object with array of references)\n" +
                "5| CollectionObject (object using Java's Collection class)\n" +
                "(Enter 0 to QUIT, 6 to SERIALIZE)");
        System.out.println("======================================================");

        System.out.print("Enter an object to create: ");

        Scanner input = new Scanner(System.in);

        while(!input.hasNextInt()){
            input.next();
            System.out.print("Enter a valid integer:");
        }

        objChoice = input.nextInt();

        return objChoice;

    }

    private static void createObject(int objChoice){
        switch(objChoice){
            case 1:
                objList.add(createSimpleObject());
                break;
            case 2:
                SimpleObject simpleObj =  createSimpleObject();
                objList.add(new ReferenceObject(simpleObj));
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                break;

        }

    }

    private static SimpleObject createSimpleObject(){
        SimpleObject simpleObj = null;
        try{

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return simpleObj;

    }
}
