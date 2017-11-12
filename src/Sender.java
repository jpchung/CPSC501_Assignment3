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

        //loop object selection/creation menu until user decides to quit
        boolean quit = false;
        while(!quit){
            int objChoice = promptObjectSelection();

            switch(objChoice){
                case 0:
                    System.out.println("Quitting program...");
                    System.exit(0);
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    createObject(objChoice);
                    break;
                case 6:
                    quit = true;
                    break;
                case 7:
                    //print list of created objects
                    System.out.println(objList.toString());
                    break;
                default:
                    System.out.println("Integer out of range");
                    break;
            }

        }

        //quit loop



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
        System.out.println("\n======================================================");
        System.out.println("LIST OF OBJECTS: \n" +
                "1| SimpleObject (object with int, double, bool primitives)\n" +
                "2| ReferenceObject (object with reference to simpleObject)\n" +
                "3| SimpleArrayObject (object with primitive int array)\n" +
                "4| ReferenceArrayObject (object with array of references)\n" +
                "5| CollectionObject (object using Java's Collection class)\n\n" +
                "Enter 0 to QUIT\nEnter 6 to SERIALIZE and SEND object list to Receiver\nEnter 7 to show object list");
        System.out.println("======================================================");

        System.out.println("Enter an object to create: ");

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
                objList.add(createReferenceObject());
                break;
            case 3:
                objList.add(createSimpleArrayObject());
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
        System.out.println("SimpleObject(int paramInt, double paramDouble, boolean paramBoolean)");
        SimpleObject simpleObj = null;
        try{
            Scanner input = new Scanner(System.in);

            //prompt user to set paramInt
            System.out.println("Enter an integer for paramInt: ");
            while(!input.hasNextInt()){
                input.next();
                System.out.println("Enter a valid integer for paramInt:");
            }
            int paramInt = input.nextInt();

            //prompt user to set paramDouble
            System.out.println("Enter a double for paramDouble: ");
            while(!input.hasNextDouble()){
                input.next();
                System.out.println("Enter a valid double for paramDouble");
            }
            double paramDouble = input.nextDouble();

            //prompt user to set paramBoolean
            System.out.println("Enter a boolean (true/false) for paramBoolean:");
            while(!input.hasNextBoolean()){
                input.next();
                System.out.println("Enter a valid boolean (true/false) for paramBoolean");
            }
            boolean paramBoolean = input.nextBoolean();

            simpleObj = new SimpleObject(paramInt, paramDouble, paramBoolean);
            System.out.println("SimpleObject created!");


        }
        catch(Exception e){
            e.printStackTrace();
        }

        return simpleObj;

    }

    private static ReferenceObject createReferenceObject(){
        System.out.println("ReferenceObject(SimpleObject simpleObj)");

        //create simpleObj to pass to referenceObj
        SimpleObject simpleObj =  createSimpleObject();
        ReferenceObject referenceObj = new ReferenceObject(simpleObj);
        System.out.println("ReferenceObject created!");

        return referenceObj;

    }

    private static SimpleArrayObject createSimpleArrayObject(){
        System.out.println("SimpleArrayObject(int[] paramIntArray)");

        Scanner input = new Scanner(System.in);

        //prompt user for size of array
        System.out.println("Enter array length for paramIntArray");
        while(!input.hasNextInt()){
            input.next();
            System.out.println("Enter a valid integer:");
        }
        int arrayLength  = input.nextInt();
        int[] paramIntArray =  new int[arrayLength];

        //prompt users to set values for each index
        for(int i = 0; i < arrayLength; i++){

            System.out.printf("Enter an integer for index %d:\n", i);
            while(!input.hasNextInt()){
                input.next();
                System.out.println("Enter a valid integer:");
            }
            paramIntArray[i] = input.nextInt();
        }

        SimpleArrayObject simpleArrayObj = new SimpleArrayObject(paramIntArray);
        System.out.println("SimpleArrayObject created!");
        return simpleArrayObj;

    }
}
