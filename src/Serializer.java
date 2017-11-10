/**
 * Serializer class for Sender program
 * @author Johnny Chung
 *
 * References:
 * <p/> IdentityHashMap: https://docs.oracle.com/javase/7/docs/api/java/util/IdentityHashMap.html
 * <p/> JDOM Document,Element: http://www.jdom.org/docs/apidocs.1.1/
 * <p/> JDOM Output: http://www.jdom.org/docs/apidocs.1.1/org/jdom/output/package-summary.html
 */

import java.io.FileWriter;
import java.lang.reflect.*;
import org.jdom.*;
import org.jdom.output.*;

import java.util.*;

public class Serializer {

    public static Document serialize(Object obj){
        //initialize Document with root element (tag name: serialized)
        Element rootElement = new Element("serialized");
        Document document = new Document(rootElement);

        IdentityHashMap objMap = new IdentityHashMap<>();

        Document serializedDocument = serializeObject(obj, document, objMap);

        return serializedDocument;

    }


    private static Document serializeObject(Object obj, Document document, IdentityHashMap objMap){

        //Element rootElement = new Element("serialized");
        //Document document = new Document(rootElement);
        Element objElement;
        Element fieldElement;
        Element valueElement;
        Element referenceElement;

        ArrayList<Element> elementContents = new ArrayList<>();
        ArrayList<Element> arrayValues = new ArrayList<>();
        ArrayList<Element> arrayReferences = new ArrayList<>();
        ArrayList<Element> arrayFields = new ArrayList<>();


        Class objClass = obj.getClass();

        try {
            //give object unique identifier
            //IdentityHashMap objMap = new IdentityHashMap<>();
            String objId = Integer.toString(objMap.size()); //use index/size of IdHashMap as id for each object
            //objMap.put(obj, objId);
            objMap.put(objId, obj);

            //create object element (nested within root element) with class and id attributes
            objElement = new Element("object");
            objElement.setAttribute("class", objClass.getName());
            objElement.setAttribute("id", objId);


            /* */
            //check if object is array type (in cases of recursive call))
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
                        //objMap.put(Array.get(obj, i), arrayObjId);
                        objMap.put(arrayObjId, Array.get(obj, i));
                        referenceElement.addContent(arrayObjId);

                        arrayReferences.add(referenceElement);
                        elementContents = arrayReferences;
                    }
                }

                //add array elements as content for array object element
                objElement.setContent(elementContents);

            }

            System.out.println("Serializing fields...");
            //get list of all object fields
            Field objFields[] = objClass.getDeclaredFields();

            for (Field f : objFields){

                if(!Modifier.isPublic(f.getModifiers())){
                    f.setAccessible(true);
                }

                Object fieldObj = f.get(obj);
                fieldElement = serializeField(f, fieldObj, document, objMap);
                arrayFields.add(fieldElement);

                objElement.setContent(arrayFields);

            }

            //add serialized object to root element
            //rootElement.addContent(objElement);
            document.getRootElement().addContent(objElement);
            System.out.println("Object serialization complete, writing to file...");


            //use XMLOutputter to format document as xml and write to file
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            FileWriter fileWriter = new FileWriter("serializedObject.xml");
            xmlOutputter.output(document, fileWriter);

            System.out.println("Writing to file complete!");

        }
        catch(Exception e){
            e.printStackTrace();
        }

        //return serialized object as XML Document
        return document;
    }

    private static Element serializeField(Field field, Object fieldObj, Document document, IdentityHashMap objMap){

        if(!Modifier.isPublic(field.getModifiers())){
            field.setAccessible(true);
        }

        Element fieldElement;

        if(fieldObj != null){
            fieldElement = new Element("field");

            try{
                // uniquely identify each field element (declaring class  + field name)
                String declaringClass =  field.getDeclaringClass().getName();
                String fieldName = field.getName();
                fieldElement.setAttribute("name", fieldName);
                fieldElement.setAttribute("declaringclass", declaringClass);

                Element valueElement = new Element("value");
                Element referenceElement = new Element("reference");


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

                Class fieldType = field.getType();

                if(!fieldType.isPrimitive() && !isWrapperClass(fieldType)){
                    //field is reference to another object, will serialize that object after storing reference as content
                    String fieldObjId = Integer.toString(objMap.size());
                    //objMap.put(f.get(obj), fieldObjId);
                    //objMap.put(fieldObjId, field.get(obj));
                    objMap.put(fieldObjId, fieldObj);
                    referenceElement.addContent(fieldObjId);

                    fieldElement.setContent(referenceElement);
                    //arrayFields.add(fieldElement);

                    //recursive call
                    serializeObject(fieldObj, document, objMap);

                }
                else{
                    //field is primitive/wrapper, just store value as content
                    //String fieldValue =  field.get(obj).toString();
                    String fieldValue = fieldObj.toString();
                    valueElement.addContent(fieldValue);

                    fieldElement.setContent(valueElement);
                    //arrayFields.add(fieldElement);

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else {
            fieldElement = new Element("null");
        }

        return fieldElement;
    }



    public static boolean isWrapperClass(Class objClass){
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
