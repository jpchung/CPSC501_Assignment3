/**
 * Deserializer class for Receiver program
 * @author Johnny Chung
 *
 * References:
 * <p/> JDOM Document,Element: http://www.jdom.org/docs/apidocs.1.1/
 * <p/> JDOM input: http://www.jdom.org/docs/apidocs.1.1/org/jdom/input/package-summary.html
 */

import java.lang.reflect.*;
import org.jdom.*;
import org.jdom.input.*;
import java.io.*;
import java.util.*;
public class Deserializer {

    public static Object deserialize(Document document){

        //Object to be instantiated via deserialization
        Object obj;

        HashMap objMap =  new HashMap();

        //MOVE LATER: deserialize XML document with SAXBuilder to get list of objects
        //SAXBuilder saxBuilder = new SAXBuilder();
        //File xmlFile = new File("serializedObject.xml");

        try{
            //MOVE LATER: Document document = (Document)saxBuilder.build(xmlFile);
            //assume document built properly

            //get root element and list of nested object elements
            Element rootElement = document.getRootElement();
            List objList = rootElement.getChildren("object");

            for(int i =0; i < objList.size(); i++){
                Element objElement = (Element) objList.get(i);

                //create uninitialized instance using element attribute
                Class objClass =  Class.forName(objElement.getAttributeValue("class"));

                //check for class type then create new instance
                Object objInstance;
                if(objClass.isArray()){
                    //get length (via element attributes) and component type of array object instantiation
                    int arrayLength = Array.getLength(objElement.getAttributeValue("length"));
                    Class arrayType = objClass.getComponentType();

                    objInstance = Array.newInstance(arrayType, arrayLength);

                }
                else{
                    //non-array object, instantiate with no arg constructor
                    Constructor constructor =  objClass.getConstructor(null);
                    //check constructor modifiers, just in case
                    if(!Modifier.isPublic(constructor.getModifiers())){
                        constructor.setAccessible(true);
                    }

                    objInstance = constructor.newInstance(null);
                }

                //associate the new instance with the object's unique id (element attribute)
                String objId = objElement.getAttributeValue("id");
                objMap.put(objId, objInstance);



                //get list of all children of object element (fields if non-array, elements if array)
                List objChildrenList = objElement.getChildren();

                //WRITE FIRST, REFACTOR LATER:
                // if array object, set value of each element
                // if non-array object, assign values to all fields/instance variables
                if(objClass.isArray()){

                    //get component type
                    Class arrayType =  objClass.getComponentType();

                    for(int j= 0; j < objChildrenList.size(); j++){

                    }
                }
                else{
                    //non-array object, assign values to all fields
                    for(int j = 0; j < objChildrenList.size(); j++){
                        Element fieldElement = (Element) objChildrenList.get(i);

                        //get declaring class (via field attribute) and load class dynamically
                        Class declaringClass =  Class.forName(fieldElement.getAttributeValue("declaringclass"));

                        //get field name (field attribute)
                        String fieldName = fieldElement.getAttributeValue("name");

                        //find Field metaobject in declaring class
                        Field field = declaringClass.getDeclaredField(fieldName);

                        if(!Modifier.isPublic(field.getModifiers())){
                            field.setAccessible(true);
                        }

                        //set value for field
                        Class fieldType = field.getType();
                        if(!fieldType.isArray() && !Serializer.isWrapperClass(fieldType)){

                        }
                        else{

                        }


                    }






                }


            }


        }
        catch(Exception e){
            e.printStackTrace();
        }

        /*
        For each object,
            - create an uninitialized instance
                - class name is attribute of object element in XML, so dynamicaly load its class w/ forName()
            - create an instance of the class
                - if non-array object, get declared no-arg constructor, then use newInstance()
                    - may need to set accessible
                - if array object, use Array.newInstance()
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



        //REMOVE LATER: placeholder return
        return new Object();
    }
}
