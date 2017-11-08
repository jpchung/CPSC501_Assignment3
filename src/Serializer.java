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
        Element objElement;
        Element fieldElement;
        Element valueElement;
        Element referenceElement;

        ArrayList<Element> elementContents = new ArrayList<>();
        ArrayList<Element> arrayValues = new ArrayList<>();
        ArrayList<Element> arrayReferences = new ArrayList<>();


        Class objClass = obj.getClass();

        try {
            //give object unique identifier
            IdentityHashMap objMap = new IdentityHashMap<>();
            String objId = Integer.toString(objMap.size()); //use index/size of IdHashMap as id for each object
            objMap.put(obj, objId);

            //create object element (nested within root element) with class and id attributes
            objElement = new Element("object");
            objElement.setAttribute("class", objClass.getName());
            objElement.setAttribute("id", objId);

            //check if object is array type
            if(objClass.isArray()){
                //add additional length attribute to object element
                String arrayLength = String.valueOf(Array.getLength(obj));
                objElement.setAttribute("length", arrayLength);

                //each element of array will be stored as content
                //value or reference element depends on component type
                Class arrayType = objClass.getComponentType();

                for(int i=0; i < Array.getLength(obj); i++){
                    if(arrayType.isPrimitive() || isWrapperClass(arrayType)){
                        valueElement = new Element("value");
                        String elementValue =  String.valueOf(Array.get(obj, i));
                        valueElement.addContent(elementValue);

                        arrayValues.add(valueElement);
                        elementContents = arrayValues;

                    }
                    else {
                        referenceElement = new Element("reference");
                        //store object id of array element as content for reference element
                        //also add to object identity hashmap
                        String arrayObjId = Integer.toString(objMap.size());
                        objMap.put(Array.get(obj, i), arrayObjId);
                        referenceElement.addContent(arrayObjId);

                        arrayReferences.add(referenceElement);
                        elementContents = arrayReferences;
                    }
                }

                //add array elements as content for array object element
                objElement.setContent(elementContents);

            }

            //get list of all object fields
            Field objFields[] = objClass.getDeclaredFields();

            for (Field f : objFields){
                f.setAccessible(true);
                Class fieldType = f.getType();

                // uniquely identify each field element (declaring class  + field name)
                fieldElement = new Element("field");
                String declaringClass =  f.getDeclaringClass().getName();
                String fieldName = f.getName();
                fieldElement.setAttribute("name", fieldName);
                fieldElement.setAttribute("declaringclass", declaringClass);

                valueElement = new Element("value");
                referenceElement = new Element("reference");

                String fieldValue;

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
                if(fieldType.isArray()){
                    //field is array object, serialize it

                    //then serialize each element (recursive if element is object)
                }
                else if(!fieldType.isPrimitive() && !isWrapperClass(fieldType)){
                    //non-array object, recursively serialize

                }
                else{
                    //field is primitive/wrapper, just store value as content
                    fieldValue =  f.get(obj).toString();
                    valueElement.addContent(fieldValue);

                    fieldElement.setContent(valueElement);

                }

            }


        }
        catch(Exception e){
            e.printStackTrace();
        }

        //XML outputter logic goes here

        //return serialized object as XML Document
        return document;
    }



    public boolean isWrapperClass(Class objClass){
        boolean isWrapper = false;

        try {
            Object objInstance = objClass.newInstance();

            if (objInstance instanceof Integer ||
                    objInstance instanceof Float ||
                    objInstance instanceof Long ||
                    objInstance instanceof Short ||
                    objInstance instanceof Byte ||
                    objInstance instanceof Double ||
                    objInstance instanceof Boolean)
                isWrapper = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return isWrapper;
    }
}
