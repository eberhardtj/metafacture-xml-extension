package org.metafacture.xml;

import org.junit.Test;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.io.ObjectJavaIoWriter;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class XmlSplitterTest {

    @Test
    public void shouldSplitTwoRecordInSeparateDocuments() {
        String declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String rootStart = "<root>";
        String elementStart = "<element>";
        String characters = "chars";
        String elementEnd = "</element>";
        String rootEnd = "</root>";

        final String childDocument = elementStart +
                elementStart +
                characters +
                elementEnd +
                elementEnd;

        final String xmlDocument = declaration +
                rootStart +
                childDocument +
                childDocument +
                rootEnd;

        final String singleDocument = declaration +
                rootStart +
                childDocument +
                rootEnd;

        StringWriter resultCollector = new StringWriter();

        XmlSplitter xmlSplitter = new XmlSplitter();
        xmlSplitter.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        xmlSplitter.process(new StringReader(xmlDocument));
        xmlSplitter.closeStream();

        String expected = singleDocument + "\n" + singleDocument + "\n";
        String actual = resultCollector.toString();

        assertEquals(expected, actual);
    }
}