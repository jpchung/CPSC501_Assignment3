/**
 * Deserializer class for Receiver program
 * @author Johnny Chung
 */

import java.lang.reflect.*;
import org.jdom.*;
import java.util.*;
public class Deserializer {

    public Object deserialize(org.jdom.Document document){

        //get list of objects stored in XML document

        /*
        For each object,
            - create an uninitialized instance
                - class name is attribute of object element in XML, so dynamicaly load its class w/ forName()
            - create an instance of the class
                - if non-array object, get declared no-arg constructor, then use newInstance()
                    - may need to set accessible
                - if non-array object, use Array.newInstance()
                    - use getComponentType() for element type
                    - array length is attribute of object element
            - associate the new instance with the object's unique identifier using a table
                - HashMap : key = id, value =  object reference
            - assign values to all instance variables in each non-array object
                - get list of child elements (i.e. fields) via getChildren() of Element class
                - iterate through each field in list
                    - get declaring class (attribute of field element)
                    - load class dynamically with forName()
                    - find field name (attribute of field element)
                    - use getDeclaredField to find Field metaobject
                    - initialize the value of the field using set()
                        - if primitive, use stored value (via getText() and appropriate Wrapper object)
                        - if reference, use unique identifier to find corresponding instance in table
                            - may need to set accessible
            - if array object
                - find element type via getComponentType()
                - iterate through each element of the array
                    - set element's value using Array.set()
                    - check if element primitive or reference and treat accordingly
        */

        //for each object, create an uninitialized instance
            //class name should be attribute of object element,
            //so dynamically load its class using forName()



        //REMOVE LATER: placeholder return
        return new Object();
    }
}
