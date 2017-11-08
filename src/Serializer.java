/**
 * Serializer class for Sender program
 * @author Johnny Chung
 */

import java.lang.reflect.*;
import org.jdom.*;
import java.util.*;
public class Serializer {



    public Document serialize(Object obj){

        //initialize Document with root element (tag name: serialized)
        Element rootElement = new Element("serialized");
        Document document = new Document(rootElement);

        Class objClass = obj.getClass();

        try {
            //give object unique identifier
            IdentityHashMap objMap = new IdentityHashMap<>();
            String objId = Integer.toString(objMap.size()); //use index/size of IdHashMap as id for each object
            objMap.put(obj, objId);

            //create object element (nested within root element) with class and id attributes
            Element objElement = new Element("object");
            objElement.setAttribute("class", objClass.getName());
            objElement.setAttribute("id", objId);

            //get list of all object fields
            Field objFields[] = objClass.getDeclaredFields();

            for (Field f : objFields){
                f.setAccessible(true);

                // uniquely identify each field element (declaring class  + field name)
                Element fieldElement = new Element("field");
                Class declaringClass =  f.getDeclaringClass();
                String fieldName = f.getName();
                fieldElement.setAttribute("name", fieldName);
                fieldElement.setAttribute("declaringclass", declaringClass.getName());


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
                if(f.getType().isArray()){
                    //field is array object, serialize it

                    //then serialize each element (recursive if element is object)
                }
                else if(!f.getType().isPrimitive()){
                    //non-array object, recursively serialize

                }
                else{
                    //field is primitive, just store
                }

            }




        }
        catch(Exception e){
            e.printStackTrace();
        }




        //REMOVE LATER: placeholder return
        return document;
    }
}
