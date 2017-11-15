/**
 * Class for unit testing of Sender class
 * @author Johnny Chung
 */
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import org.jdom.*;
import java.io.*;
public class TestSender {
    Class senderClass;


    @Before
    public void createSenderClass(){
        try{
            senderClass = Class.forName("Sender");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateXMLFile(){
        try{
            Method m = senderClass.getDeclaredMethod("createXMLFile", new Class[]{Document.class});
            m.setAccessible(true);

            SimpleObject obj = new SimpleObject(1,1);
            Document document = Serializer.serialize(obj);
            assertNotNull(m.invoke(null, new Object[]{document}));
            File file = (File) m.invoke(null, new Object[]{document});
            assertNotNull(file);
            assertTrue(file.length()>0);

            //read file to see if matches expected output
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer fileOutput = new StringBuffer();

            while(bufferedReader.ready()){
                fileOutput.append((char) bufferedReader.read());
            }
            String fileOutputString = fileOutput.toString();

            String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                    "<serialized>\r\n" +
                    "  <object class=\"SimpleObject\" id=\"0\">\r\n" +
                    "    <field name=\"fieldInt\" declaringclass=\"SimpleObject\">\r\n" +
                    "      <value>1</value>\r\n" +
                    "    </field>\r\n" +
                    "    <field name=\"fieldDouble\" declaringclass=\"SimpleObject\">\r\n" +
                    "      <value>1.0</value>\r\n" +
                    "    </field>\r\n" +
                    "  </object>\r\n" +
                    "</serialized>\r\n" +
                    "\r\n";

            assertEquals(expectedOutput, fileOutputString);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
