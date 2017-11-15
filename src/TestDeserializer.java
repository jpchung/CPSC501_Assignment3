/**
 * Class for unit testing of Receiver class
 * @author Johnny Chung
 */
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import org.jdom.*;
public class TestDeserializer {


    @Test
    public void testDeserialize(){
        SimpleObject simpleObj =  new SimpleObject(1,1);
        Document document = Serializer.serialize(simpleObj);


        assertNotNull(Deserializer.deserialize(document));
        SimpleObject documentObj = (SimpleObject) Deserializer.deserialize(document);

        //if Deserializer working as intended,
        //object deserialized from document should have same fields and field values as object serialized
        Class simpleObjClass = simpleObj.getClass();
        Class documentObjClass = documentObj.getClass();
        Field[] simpleObjFields = simpleObjClass.getDeclaredFields();
        Field[] documentObjFields = documentObjClass.getDeclaredFields();

        assertEquals(simpleObjFields.length, documentObjFields.length);
        for(int i =0; i < simpleObjFields.length; i++){
            Field simpleField = simpleObjFields[i];
            Field documentField =  documentObjFields[i];

            simpleField.setAccessible(true);
            documentField.setAccessible(true);
            assertTrue(documentField.getType().isPrimitive());
            assertEquals(simpleField.getName(), documentField.getName());
            assertEquals(simpleField.getType(), documentField.getType());

            try{
                Object simpleFieldValue = simpleField.get(simpleObj);
                Object documentFieldValue = documentField.get(documentObj);

                assertEquals(simpleFieldValue.toString(),documentFieldValue.toString());
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }



    }
}
