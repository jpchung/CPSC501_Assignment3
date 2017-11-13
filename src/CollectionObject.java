/**
 *  Object class using an instance of one of Java's Collection classes (ArrayList) to refer multiple objects
 *  @author Johnny Chung
 */

import java.util.*;
public class CollectionObject {

    private ArrayList fieldCollection;

    //no-arg constructor (need for Deserializer)
    public CollectionObject(){}

    public CollectionObject(ArrayList paramCollection){
        fieldCollection = paramCollection;
    }
}
