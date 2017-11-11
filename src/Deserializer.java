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
        Object obj = null;

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

                    //set values for each array element
                    Class arrayType =  objClass.getComponentType();
                    for(int j= 0; j < objChildrenList.size(); j++){
                        Element arrayContentElement = (Element) objChildrenList.get(j);

                        Object arrayContent = deserializeContentElement(arrayType, arrayContentElement, objMap);

                        Array.set(objInstance, j, arrayContent);

                    }
                }
                else{
                    //non-array object, assign values to all fields
                    for(int j = 0; j < objChildrenList.size(); j++){
                        Element fieldElement = (Element) objChildrenList.get(j);

                        //get declaring class (via field attribute) and load class dynamically
                        Class declaringClass =  Class.forName(fieldElement.getAttributeValue("declaringclass"));

                        //get field name (field attribute)
                        String fieldName = fieldElement.getAttributeValue("name");

                        //find Field metaobject in declaring class
                        Field field = declaringClass.getDeclaredField(fieldName);

                        if(!Modifier.isPublic(field.getModifiers())){
                            field.setAccessible(true);
                        }

                        //check field element content for value/reference and set accordingly
                        Class fieldType = field.getType();
                        Element fieldContentElement = (Element) fieldElement.getChildren().get(0);

                        Object fieldContent = deserializeContentElement(fieldType, fieldContentElement, objMap);

                        field.set(objInstance, fieldContent);
                    }

                }


            }
            //end of loop, object list should be deserialized and instantiated

            //first object in object HashMap should be main object that was serialized
            obj = objMap.get("0");

        }
        catch(Exception e){
            e.printStackTrace();
        }


        //return deserialized object
        return obj;
    }

    //get value from field element content
    private static Object deserializeFieldValue(Class fieldType, Element valueElement){

        Object valueObject = null;

        if(fieldType.equals(int.class))
            valueObject = Integer.valueOf(valueElement.getText());
        else if(fieldType.equals(byte.class))
            valueObject = Byte.valueOf(valueElement.getText());
        else if(fieldType.equals(short.class))
            valueObject = Short.valueOf(valueElement.getText());
        else if(fieldType.equals(long.class))
            valueObject = Long.valueOf(valueElement.getText());
        else if(fieldType.equals(float.class))
            valueObject = Float.valueOf(valueElement.getText());
        else if(fieldType.equals(double.class))
            valueObject = Double.valueOf(valueElement.getText());
        else if(fieldType.equals(boolean.class)){

            String boolString = valueElement.getText();

            if(boolString.equals("true"))
                valueObject = Boolean.TRUE;
            else
                valueObject = Boolean.FALSE;
        }

        return valueObject;
    }

    private static Object deserializeContentElement(Class classType, Element contentElement, HashMap objMap){
        Object contentObject;

        String contentType = contentElement.getName();

        if(contentType.equals("reference"))
            contentObject = objMap.get(contentElement.getText());
        else if(contentType.equals("value"))
            contentObject = deserializeFieldValue(classType, contentElement);
        else
            contentObject = null;


        return contentObject;
    }

}
