/**
 * Simple Object class with primitive instance variables, created for Serialization/Deserialization testing
 * @author Johnny Chung
 */
public class SimpleObject {

    private int fieldInt;
    private double fieldDouble;
    //private boolean fieldBoolean;

    //no-arg constructor (need for Deserializer)
    public SimpleObject(){}


    public SimpleObject(int paramInt, double paramDouble){
        fieldInt =  paramInt;
        fieldDouble =  paramDouble;
        //fieldBoolean = paramBoolean;
    }


}
