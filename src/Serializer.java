/**
 * Serializer class for Sender program
 * @author Johnny Chung
 */

import java.lang.reflect.*;
import org.jdom.*;
public class Serializer {

    public Document serialize(Object obj){

        //give object unique identifier (hashmap/hashset)

        //get list of all object fields

        // uniquely identify each field (declaring class  + field name)

        /*
        get value for each field
            -if primitive, just store it to be retrieved later
            -if non-array object, RECURSIVELY serialize object
                -use object's unique id as a reference
                -store reference as the field in the originating object
                -only serialize object ONCE (beware of multiple references to same object)
            -if array object, serialize it
                -then serialize EACH ELEMENT of array
                    -use recursion if element is an object
         */




        //REMOVE LATER: placeholder return
        return new Document();
    }
}
