/**
 * Serializer class for Sender program
 * @author Johnny Chung
 *
 * References:
 * <p/> IdentityHashMap: https://docs.oracle.com/javase/7/docs/api/java/util/IdentityHashMap.html
 * <p/> JDOM Document,Element: http://www.jdom.org/docs/apidocs.1.1/
 * <p/> JDOM Output: http://www.jdom.org/docs/apidocs.1.1/org/jdom/output/package-summary.html
 */


import java.lang.reflect.*;
import org.jdom.*;
import java.util.*;

public class Serializer {

    /**
     * Serializes input Object into a Document to be output as an XML file and sent over a network connection
     * @param obj - Object to serialize
     * @return Document of serialized object
     */
    public static Document serialize(Object obj){
        //initialize Document with root element (tag name: serialized)
        Element rootElement = new Element("serialized");
        Document document = new Document(rootElement);

        IdentityHashMap objMap = new IdentityHashMap<>();

        return serializeObject(obj, document, objMap);
    }


    /**
     * Helper method for Object serialization
     * @param obj - Object to be serialized
     * @param document - Document to serialize into
     * @param objMap - IdentityHashMap of serialized objects
     * @return
     */
    private static Document serializeObject(Object obj, Document document, IdentityHashMap objMap){

        Element objElement;
        Element fieldElement;
        Element valueElement;
        Element referenceElement;

        ArrayList<Element> elementContents = new ArrayList<>();
        ArrayList<Element> arrayValues = new ArrayList<>();
        ArrayList<Element> arrayReferences = new ArrayList<>();
        //ArrayList<Element> arrayFields = new ArrayList<>();

        Class objClass = obj.getClass();

        try {
            //give object unique identifier
            String objId = Integer.toString(objMap.size()); //use index/size of IdHashMap as id for each object
            objMap.put(objId, obj);

            //create object element (nested within root element) with class and id attributes
            objElement = new Element("object");
            objElement.setAttribute("class", objClass.getName());
            objElement.setAttribute("id", objId);
            document.getRootElement().addContent(objElement);


            //check if object is array type (in cases of recursive call))
            if(objClass.isArray()){
                //add additional length attribute to object element
                String arrayLength = String.valueOf(Array.getLength(obj));
                objElement.setAttribute("length", arrayLength);

                //each element of array will be stored as content
                //value or reference element depends on component type
                Class arrayType = objClass.getComponentType();

                for(int i=0; i < Array.getLength(obj); i++){
                    if(arrayType.isPrimitive()){
                        valueElement = new Element("value");
                        String elementValue =  String.valueOf(Array.get(obj, i));
                        valueElement.addContent(elementValue);

                        arrayValues.add(valueElement);
                        elementContents = arrayValues;

                    }
                    else {
                        referenceElement = new Element("reference");
                        //store object id of array element as content for reference element
                        String arrayObjId = Integer.toString(objMap.size());
                        referenceElement.addContent(arrayObjId);

                        arrayReferences.add(referenceElement);
                        elementContents = arrayReferences;

                        //recursively serialize objects on reference elements
                        if(!objMap.containsKey(arrayObjId)){
                            Object arrayElementObj = Array.get(obj, i);
                            serializeObject(arrayElementObj, document, objMap);
                        }
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
                    //arrayFields.add(fieldElement);
                    System.out.println(f.getName());

                    //objElement.addContent(arrayFields);
                    objElement.addContent(fieldElement);


            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        //return serialized object as Document
        return document;
    }

    /**
     * serialize the fields of Object to be serialized
     * @param field - field class to serialize
     * @param fieldObj - object instance of field to serialize
     * @param document - document to serialize objects into
     * @param objMap - IdentityHashMap of serialized objects
     * @return Element of serialized field to add to Document
     */
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

                Class fieldType = field.getType();

                if(!fieldType.isPrimitive()){
                    //field is reference to another object, will serialize that object after storing reference as content
                    String fieldObjId = Integer.toString(objMap.size());
                    referenceElement.addContent(fieldObjId);
                    fieldElement.setContent(referenceElement);
                    //arrayFields.add(fieldElement);

                    //recursive call
                    serializeObject(fieldObj, document, objMap);

                }
                else{
                    //field is primitive/wrapper, just store value as content
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


}
