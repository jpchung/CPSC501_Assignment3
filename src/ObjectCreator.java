/**
 * Class to allow users to create objects from an arbitrary list
 * @author Johnny Chung
 */

import java.util.*;
public class ObjectCreator {


    public static void main(String[] args){

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

        int objChoice = -1;
        System.out.println("======================================================");
        System.out.println("\nLIST OF OBJECTS: \n" +
                            "1| \n" +
                            "2| \n" +
                            "3| \n" +
                            "4| \n" +
                            "5| \n" +
                            "(Enter 0 to quit)");
        System.out.println("======================================================");

        System.out.print("Enter an object to create: ");

        Scanner input = new Scanner(System.in);

        while(!input.hasNextInt()){
            input.next();
            System.out.print("Enter a valid integer:");
        }

        objChoice = input.nextInt();
        if(objChoice == 0){
            System.out.println("Quitting program...");
            System.exit(0);
        }
        else{

        }

    }
}
