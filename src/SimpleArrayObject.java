/**
 * Object class with a primitive array field
 * @author Johnny Chung
 */
public class SimpleArrayObject {

    private int[] fieldIntArray;

    //no-arg constructor (need for Deserializer)
    public SimpleArrayObject(){}

    public SimpleArrayObject(int[] paramIntArray){
        fieldIntArray = paramIntArray;
    }

}
