/**
 * Sender Client class for network connection b/w Sender and Receiver.
 * Sends serialized object as xml document to Server
 * @author Johnny Chung
 */

import java.io.*;
import java.net.*;
import java.util.*;
import org.jdom.*;
import org.jdom.output.*;

public class Sender
{

    private static ArrayList<Object> objList;

    public static void main(String[] args){
        String host = "localhost";
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

        //quit loop when done creating objects and want to serialize/send
        serializeObjects(host, port);

    }


    private static int promptObjectSelection(){

        int objChoice;
        System.out.println("\n======================================================");
        System.out.println("LIST OF OBJECTS: \n" +
                "1| SimpleObject (object with int, double, bool primitives)\n" +
                "2| ReferenceObject (object with field reference to SimpleObject)\n" +
                "3| SimpleArrayObject (object with primitive int array)\n" +
                "4| ReferenceArrayObject (object with array of SimpleObject references)\n" +
                "5| CollectionObject (object using Java's Collection class)\n\n" +
                "Enter 0 to QUIT" +
                "\nEnter 6 to SERIALIZE and SEND object list to Receiver" +
                "\nEnter 7 to SHOW LIST of created objects");
        System.out.println("======================================================");

        System.out.println("Enter an object to create: ");

        Scanner input = new Scanner(System.in);

        //check valid int input
        while(!input.hasNextInt()){
            input.next();
            System.out.println("Enter a valid integer:");
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
                objList.add(createRefArrayObject());
                break;
            case 5:
                objList.add(createCollectionObject());
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
        System.out.println("Enter array length for paramIntArray:");
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



    private static ReferenceArrayObject createRefArrayObject(){
        System.out.println("ReferenceArrayObject(Object[] paramObjArray)");

        Scanner input = new Scanner(System.in);

        //prompt user for size of array
        System.out.println("Enter array length for paramObjArray");
        while(!input.hasNextInt()){
            input.next();
            System.out.println("Enter a valid integer:");
        }
        int arrayLength  = input.nextInt();

        Object[] paramObjArray = new Object[arrayLength];

        //instantiate array with simpleObjects
        for(int i = 0; i < arrayLength; i++){
            System.out.printf("index %d:\n", i);
            SimpleObject simpleObj =  createSimpleObject();
            paramObjArray[i] = simpleObj;
        }

        ReferenceArrayObject refArrayObj = new ReferenceArrayObject(paramObjArray);
        System.out.println("ReferenceArrayObject created!");
        return refArrayObj;

    }


    private static CollectionObject createCollectionObject(){
        System.out.println("CollectionObject(ArrayList paramCollection)");

        Scanner input = new Scanner(System.in);

        CollectionObject collectionObj = null;
        ArrayList<SimpleObject> paramCollection = new ArrayList<>();
        //continue adding to collection until user decides to quit
        boolean quit = false;
        while(!quit){

            System.out.println("Add an object to the collection (yes/no)?");
            String collectionChoice = input.nextLine();
            if(collectionChoice.equalsIgnoreCase("yes")){
                SimpleObject simpleObj =  createSimpleObject();
                paramCollection.add(simpleObj);
            }
            else if(collectionChoice.equalsIgnoreCase("no")){
                collectionObj = new CollectionObject(paramCollection);
                quit = true;
            }
            else{
                System.out.println("Invalid input. Enter yes or no.");
            }

        }

        System.out.println("CollectionObject created!");
        return collectionObj;

    }

    private static File createXMLFile(Document document) {
        File file = new File("serializedObject.xml");
        try{
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            FileWriter fileWriter = new FileWriter(file);
            xmlOutputter.output(document, fileWriter);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return file;

    }

    private static void sendFile(String host, int port, File file){
        try{
            System.out.println("Connecting to " + host + "on port: " + port);

            //create socket to send file
            Socket socket = new Socket(host, port);
            System.out.println("Sender connected to " + socket.getRemoteSocketAddress());

            //open io streams
            OutputStream outputStream = socket.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);

            //send file as byte array stream
            byte[] fileBytes = new byte[1024 * 1024];
            int bytesRead = 0;
            while((bytesRead = fileInputStream.read(fileBytes))> 0){
                outputStream.write(fileBytes, 0, bytesRead);
            }

            //close streams/sockets
            fileInputStream.close();
            outputStream.close();
            socket.close();

            System.out.println("File sent!");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void serializeObjects(String host, int port){
        //serialize and send created objects list
        for(Object obj : objList){
            try{
                System.out.println("Serializing object...");
                Document document  = Serializer.serialize(obj);

                System.out.println("Creating file...");
                File file = createXMLFile(document);

                System.out.println("Sending file...");
                sendFile(host, port, file);

            }
            catch(Exception e){
                e.printStackTrace();
            }

        }


    }
}
