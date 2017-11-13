/**
 * Object class with a field that is an array of object references
 * @author Johnny Chung
 */
public class ReferenceArrayObject {

    private Object[] fieldObjArray;

    //no-arg constructor (need for Deserializer)
    public ReferenceArrayObject(){}

    public ReferenceArrayObject(Object[] paramObjArray){
        fieldObjArray = paramObjArray;
    }
}
