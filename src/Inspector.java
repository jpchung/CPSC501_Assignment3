/**
 * CPSC 501 Assignment 2 (Java R
 * @author Johnny Chung
 *
 * Inspector class to recursively introspect on objects
 */
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.reflect.*;
import java.util.HashSet;

public class Inspector {

    /**
     * object info to find:
     * - name of declaring class
     * - name of immediate superclass
     * - name of interfaces the class implements
     * - methods the class declares
     *      (includes: exceptions thrown, parameter types, return type, modifiers)
     * - constructors the class declares
     *      (includes: parameter types, modifiers)
     * - the fields the class declares
     *      (includes: type, modifiers)
     * - the current value of each field
     *      - if field is object reference, print object's class  and "ID hash code"
     *
     * Must also traverse inheritance hierarchy to find all constructors/methods/fields/field values
     * that each superclass/superinterface declares
     *
     * Also print name, component type, length  and contents of any arrays
     *
     */


    //HashSet for checking unique object inspection
    private HashSet<Integer> objectHash;


    /***
     * Default constructor to initialize HashSet
     */
    public Inspector(){
        this.objectHash = new HashSet<Integer>();
    }


    /***
     * Introspect on the passed object and print info as standard output.
     * If recursive boolean true, also fully inspect every field that is an object
     * @param obj -  instantiated object
     * @param recursive - boolean for recursive introspection
     * @author Johnny Chung
     */
    public void inspect(Object obj, boolean recursive){

        //add unique hashcode of object to classHash upon inspection
        //will check this later to prevent inspect() method from infinite recursion in case of object loop
        HashSet<Integer> objectHash = this.getObjectHash();
        objectHash.add(obj.hashCode());

        //get metaobject for instantiated base level object
        Class classObject = null;

        System.out.println();
        try{
            classObject = obj.getClass();

            //get fields declared by the class
            Field fieldObjects[] = classObject.getDeclaredFields();

            //get class
            inspectClass(obj, classObject, fieldObjects);

            //traverse hierarchy get constructors/methods/field values that superclass declares
            Class superClassObject = classObject.getSuperclass();
            inspectSuperclass(obj, superClassObject);

            //traverse hierarchy again for interfaces
            Class[] interfaceObjects = classObject.getInterfaces();
            for(Class i: interfaceObjects){
                inspectInterface(obj, i);
            }

            //check if need to introspect recursively on field objects
            if(recursive){
                System.out.printf("\n==== RECURSION ON FIELD OBJECTS IN %s: START ====\n", classObject.getName());
                inspectFields(fieldObjects, obj, recursive);
                System.out.printf("\n==== RECURSION ON FIELD OBJECTS IN %s: END ====\n\n", classObject.getName());
            }

        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }


    /***
     * Introspect and display declarations/properties of the class,
     * including: name, superclasses, interfaces, method signatures, and field values.
     * If superclasses/interfaces present, traverse up hierarchy and display declarations
     * @param obj -  instantiated object
     * @param classObject - class meta-object of object
     * @param fieldObjects - array of field objects for object
     */
    public void inspectClass(Object obj, Class classObject, Field[] fieldObjects){
        //get name of declaring class
        System.out.println("Declaring class: " + obj);

        //get superclass
        Class superClassObject =  classObject.getSuperclass();
        System.out.println("Superclass: " + superClassObject.getName());

        //get interfaces
        Class interfaceObjects[] = classObject.getInterfaces();
        System.out.print("Interfaces: ");
        displayClassTypeObjects(interfaceObjects);
        System.out.println();

        //get methods
        Method methodObjects[] = classObject.getDeclaredMethods();
        inspectMethods(methodObjects);

        //get constructors
        Constructor constructorObjects[] = classObject.getConstructors();
        inspectConstructors(constructorObjects);

        //get fields
        inspectFieldValues(obj, fieldObjects);

        //display contents if initial object is an array
        if(classObject.isArray()){
            Class arrayType = classObject.getComponentType();
            System.out.println("Component Type: " + arrayType.getName());

            Object arrayElements[]  = getObjectArray(obj);
            displayArrayElements(arrayElements);
        }


    }


    /***
     * Method to recursively introspect on Field objects and Array elements (if element is an object)
     * @param fieldObjects - array of Field objects to recurse on
     * @param obj - instantiated object from which the fields came from
     * @param recursive - boolean for recursive introspection
     */
    public void inspectFields(Field[] fieldObjects, Object obj, boolean recursive){

        System.out.println();
        for(Field f : fieldObjects){
            try{
                Class fieldType = f.getType();

                //field is an array, check array if elements are objects to recurse on
                if(fieldType.isArray()){
                    System.out.print("Field: " + f.getName() + " - Array");

                    Object fieldValue = f.get(obj);
                    int arrayLength = Array.getLength(fieldValue);
                    Object arrayElements[] = getObjectArray(fieldValue);
                    Class arrayType= fieldType.getComponentType();

                    //array consists of objects, recurse if not empty on non-null elements
                    if(!arrayType.isPrimitive()){
                        System.out.printf(" (%s)\n", arrayType.getTypeName());

                        //check if array has  non-null elements to recurse on
                        if(arrayElements.length >0) {
                            for(int i = 0; i < arrayLength; i++){
                                Object element =  arrayElements[i];
                                String elementTypeString = null;

                                //recurse on non-null unique element
                                if(element != null){
                                    //check if already inspected this object
                                    if(alreadyInspected(element)){
                                        System.out.println("Already inspected this object...");
                                    }
                                    else{inspect(element, recursive);}

                                }
                                else{System.out.println("      object is null...");}
                            }
                        }
                        else{System.out.println("      Array is empty...");}

                    }
                    //otherwise primitive array, don't recurse on elements
                    else{System.out.println(" (Primitive)");}

                }
                //field is an object, recurse on it if not null and unique
                else if(!fieldType.isPrimitive()){
                    System.out.println("Field: " + f.getName() + " - Object");

                    if(f.get(obj) != null){
                        //check if already inspected this object
                        if(alreadyInspected(f.get(obj))){
                            System.out.println("Already inspected this object...");
                        }
                        else{inspect(f.get(obj), recursive);}

                    }
                    else{System.out.println("      object is null...\n");}
                }
                //otherwise field is primitive, don't recurse
                else{System.out.println("Field: " + f.getName() + " - Primitive");}

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    /***
     * general method to display names of items in class Object arrays
     * @param classTypeObjects -  array of Class typed objects
     */
    private void displayClassTypeObjects(Class[] classTypeObjects){
        if(classTypeObjects.length > 0){
            for(Class c : classTypeObjects){
                System.out.print(c.getName());

                //add a comma if not last element in array
                if(classTypeObjects[classTypeObjects.length -1] != c)
                    System.out.print(", ");
            }
        }
        else{System.out.print("");}
    }


    /***
     * Method to inspect and display method objects,
     * including modifiers, parameters/return type, and exceptions
     * @param methodObjects - array of method objects to inspect
     */
    private void inspectMethods(Method[] methodObjects){
        for(Method m : methodObjects){

            //query Method object for exception types
            Class methodExceptionTypes[] = m.getExceptionTypes();

            //query Method object for parameter types
            Class methodParameterTypes[] = m.getParameterTypes();

            //query Method object for return type
            Class returnType = m.getReturnType();

            //query Method object for modifiers
            int modifiers = m.getModifiers();
            String modifierString = Modifier.toString(modifiers);

            //display Method object as single line output (method signature)
            System.out.print("Method: " +
                    modifierString + " " +
                    returnType.getName() + " " +
                    m.getName() + "(");
            displayClassTypeObjects(methodParameterTypes);
            System.out.print(")");

            //check if need to print exceptions
            if(methodExceptionTypes.length > 0){
                System.out.print(" throws ");
                displayClassTypeObjects(methodExceptionTypes);
            }
            System.out.println();

        }
    }


    /***
     * Method to inspect and display Constructor objects,
     * including modifiers and parameter types
     * @param constructorObjects - array of Constructor objects to inspect
     */
    public void inspectConstructors(Constructor[] constructorObjects){
        for(Constructor c : constructorObjects){
            //query Constructor objects for name, parameter types, and modifier
            Class constructorParameterTypes[] = c.getParameterTypes();

            int modifiers = c.getModifiers();
            String modifierString = Modifier.toString(modifiers);

            //display Constructor as single line output (method signature)
            System.out.print("Constructor: " + modifierString + " " + c.getName() +"(");
            displayClassTypeObjects(constructorParameterTypes);
            System.out.println(")");
        }
    }


    /***
     * Method to inspect and display the field values of an object
     * @param obj - instantiated object, the field values of which we want to inspect
     * @param fieldObjects - array of Field objects to inspect values for
     */
    public void inspectFieldValues(Object obj, Field[] fieldObjects){
        for(Field f : fieldObjects){
            try{
                f.setAccessible(true);
                Object fieldValue = f.get(obj);

                //query for field modifiers
                int modifiers = f.getModifiers();
                String modifierString = Modifier.toString(modifiers);

                String fieldTypeString = null;
                String fieldValueString = null;

                //query Field object for type, and value if not an array
                Class fieldType = f.getType();
                if(fieldType.isArray()){
                    Class arrayType= fieldType.getComponentType();
                    int arrayLength = Array.getLength(fieldValue);
                    fieldTypeString = arrayType.getName() + "[" + arrayLength + "]";
                }
                else{
                    fieldTypeString = fieldType.toString();
                    //display value if field is primitive
                    if(fieldType.isPrimitive()){ fieldValueString = fieldValue.toString();}
                    //let value be object's class and id hashcode if instantiated
                    else if(fieldValue != null){ fieldValueString = fieldValue.getClass().getName() + " " + fieldValue.hashCode();}

                    //otherwise defaults to null
                }
                System.out.println("Field: " + modifierString + " " + fieldTypeString + " " + f.getName());

                //print contents if field is an array
                if(fieldType.isArray()){
                    Object arrayElements[] = getObjectArray(fieldValue);
                    displayArrayElements(arrayElements);
                }
                //otherwise just print value
                else{System.out.println("      Value: " + fieldValueString);}

            } catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    /***
     * generic method to display elements in an object array
     * @param arrayElements - array of objects
     */
    public void displayArrayElements(Object[] arrayElements){

        for(int i =0; i < arrayElements.length; i++){
            Object element = arrayElements[i];

            String elementDisplay = null;
            if(element != null)
                elementDisplay =  getElementValue(element);

            System.out.println("      index " + i + ": " + elementDisplay);

        }
    }


    /***
     * Method to return value of element object as a string
     * @param element - object, element of an object array
     * @return string of the element's value
     */
    public String getElementValue(Object element){
        String value =  "";

        if(element != null){
            //check if wrapper class instance
            if(element instanceof Character ||
                    element instanceof Integer ||
                    element instanceof Float ||
                    element instanceof Long ||
                    element instanceof Short ||
                    element instanceof Double ||
                    element instanceof Byte ||
                    element instanceof Boolean)
                value += String.valueOf(element);
            else{
                Class elementClass = element.getClass();
                value = elementClass.getName() + " " + element.hashCode();
            }
        }

        return value;
    }


    /***
     * Method to get Object array from an Object that has Array class type
     * @param obj - instantiated object that has Array class type
     * @return objArray - object array
     */
    public Object[] getObjectArray(Object obj){

        Object[] objArray;
        //if obj is already an object array, just cast and return
        if(obj instanceof Object[])
            objArray =  (Object[]) obj;

        //otherwise obj is a primitive array, so get elements by index and wrap primitives
        else{
            int arrayLength = Array.getLength(obj);
            objArray = new Object[arrayLength];
            for(int i = 0; i < arrayLength; i++){
                objArray[i] = Array.get(obj, i);
            }
        }
        return objArray;

    }


    /***
     * Method to inspect an object's superclass,
     * including further superclasses, interfaces, methods and field values.
     * Method will traverse up hierarchy via recursion if superclass has superclass
     * @param obj - instantiated object for which its superclass will be inspected
     * @param superClass - Class meta-object of object's superclass
     */
    private void inspectSuperclass(Object obj,Class superClass){
        System.out.printf("\n---- INSPECTING SUPERCLASS %s: START ----\n\n", superClass.getName());

        //check if superclass has another superclass in hierarchy
        Class nextSuperClass = null;
        if(superClass.getSuperclass() != null){
            nextSuperClass = superClass.getSuperclass();
            System.out.println("Superclass: " + nextSuperClass.getName());
        }

        //check if superclass has implemented interfaces
        Class superInterfaces[] = null;
        if(superClass.getInterfaces() != null) {
            superInterfaces = superClass.getInterfaces();
            System.out.print("Interfaces: ");
            displayClassTypeObjects(superInterfaces);
            System.out.println();
        }

        Constructor superConstructors[] = superClass.getConstructors();
        inspectConstructors(superConstructors);

        Method superMethods[] = superClass.getDeclaredMethods();
        inspectMethods(superMethods);

        Field superFields[] = superClass.getDeclaredFields();
        inspectFieldValues(obj,superFields);

        //recursively inspect up hierarchy if superclass has superclass
        if(nextSuperClass != null){
            System.out.printf("\nsuperclass %s has a superclass %s!\n",superClass.getName(), nextSuperClass.getName() );
            inspectSuperclass(obj, nextSuperClass);
        }
        else{System.out.printf("\nsuperclass %s has no other superclass...\n", superClass.getName());}

        //inspect up hierarchy for interfaces as well
        if(superInterfaces.length > 0) {
            for(Class i: superInterfaces){
                System.out.printf("\nsuperclass %s has interface %s!\n", superClass.getName(), i.getName());
                inspectInterface(obj, i);
            }
        }
        else{System.out.printf("\nsuperclass %s has no interfaces...\n", superClass.getName());}

        System.out.printf("\n---- INSPECTING SUPERCLASS %s: END ----\n\n", superClass.getName());

    }


    /***
     * Method to inspect Interfaces implemented by an object's class,
     * including fields and methods.
     * Method will traverse up hierarchy if Interfaces inherits any superclasses.
     * Interfaces can't implement other interfaces by their nature,
     * so no point to inspect for that.
     * @param obj - instantiated object for which its implemented interface will be inspected
     * @param interfaceClass - Class meta-object of Interface to inspect
     */
    private void inspectInterface(Object obj, Class interfaceClass){
        System.out.printf("\n---- INSPECTING INTERFACE %s: START ----\n\n", interfaceClass.getName());

        //check for inherited superclasses
        Class interfaceSuperClass = null;
        if(interfaceClass.getSuperclass() != null){
            interfaceSuperClass = interfaceClass.getSuperclass();
            System.out.println("Superclass: " + interfaceSuperClass.getName());
        }

        Method interfaceMethods[] = interfaceClass.getDeclaredMethods();
        inspectMethods(interfaceMethods);

        Field interfaceFields[] = interfaceClass.getDeclaredFields();
        inspectFieldValues(obj, interfaceFields);

        //inspect up hierarchy for superclasses of this interface
        if(interfaceSuperClass != null){
            System.out.printf("\nInterface %s has a superclass %s!\n", interfaceClass.getName(), interfaceSuperClass.getName());
            inspectSuperclass(obj, interfaceSuperClass);
        }
        else{System.out.printf("\nInterface %s has no superclass...\n", interfaceClass.getName());}

        System.out.printf("\n---- INSPECTING INTERFACE %s: END ----\n\n", interfaceClass.getName());

    }


    /***
     * Method to check if object has already been inspected
     * @param obj - instantiated object to check hashcode for
     * @return true if already inspected i.e. classHash already contains object's hashCode
     * @return false if not inspected yet i.e. classHash doesn't have object hashCode
     */
    public boolean alreadyInspected(Object obj){
        HashSet<Integer> objectHash = this.getObjectHash();
        if(objectHash.contains(obj.hashCode()))
            return true;
        else
            return false;
    }

    /***
     * Accessor method for private HashSet for object hashcodes
     * @return HashSet<Integer>
     */
    public HashSet<Integer> getObjectHash(){return this.objectHash;}


}
