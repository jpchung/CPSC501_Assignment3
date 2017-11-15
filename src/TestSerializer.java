/**
 * Class for unit testing of Serializer class
 * @author Johnny Chung
 */

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.FileWriter;
import java.lang.reflect.*;
import java.util.*;

import org.jdom.*;
import org.jdom.output.*;
public class TestSerializer {

    @Test
    public void testSerialize(){

        Object obj = new SimpleObject(1, 1.0);
        Document document = Serializer.serialize(obj);
        assertNotNull(document);

        assertNotNull(document.getRootElement());
        Element rootElement = document.getRootElement();
        assertTrue(rootElement.getName().equals("serialized"));
        assertNotNull(rootElement.getChildren());

        List objList = rootElement.getChildren();
        assertNotNull(objList);
        assertNotNull(objList.get(0));

        Element objElement = (Element) objList.get(0);
        assertTrue(objElement.getName().equals("object"));
        assertNotNull(objElement.getAttribute("class"));
        assertNotNull(objElement.getAttribute("id"));
        assertNull(objElement.getAttribute("length"));
        assertEquals("0", objElement.getAttributeValue("id"));
        assertNotNull(objElement.getChildren());


        List objContents = objElement.getChildren();
        for (int i =0; i < objContents.size(); i++){
            Element contentElement = (Element) objContents.get(i);
            assertEquals("SimpleObject", contentElement.getAttributeValue("declaringclass"));
            assertNotNull(contentElement.getValue());

            if(contentElement.getName().equals("fieldInt"))
                assertEquals("1", contentElement.getValue());
            if(contentElement.getName().equals(("fieldDouble")))
                assertEquals("1.0", contentElement.getValue());
        }


    }

}
