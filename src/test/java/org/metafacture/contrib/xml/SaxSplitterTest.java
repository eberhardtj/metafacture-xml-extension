/*
 * Copyright 2018 Deutsche Nationalbibliothek
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.metafacture.contrib.xml;

import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.XmlReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import static org.mockito.Mockito.inOrder;

public class SaxSplitterTest
{
    private SaxSplitter splitter;

    @Mock
    private XmlReceiver receiver;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        splitter = new SaxSplitter();
        splitter.setReceiver(receiver);
    }

    @Test
    public void splitTwoRecordInSeparateDocuments() throws Exception
    {
        splitter.startDocument();
        Attributes rootAtts = new AttributesImpl();
        splitter.startElement("", "root", "root", rootAtts);

        Attributes recordOneAtts = new AttributesImpl();
        splitter.startElement("", "record", "record", recordOneAtts);
        splitter.endElement("", "record", "record");

        Attributes recordTwoAtts = new AttributesImpl();
        splitter.startElement("", "record", "record", recordTwoAtts);
        splitter.endElement("", "record", "record");

        splitter.endElement("", "root", "root");
        splitter.endDocument();

        final InOrder ordered = inOrder(receiver);
        ordered.verify(receiver).startDocument();
        ordered.verify(receiver).startElement("", "root", "root", rootAtts);
        ordered.verify(receiver).startElement("", "record", "record", recordOneAtts);
        ordered.verify(receiver).endElement("", "record", "record");
        ordered.verify(receiver).endElement("", "root", "root");
        ordered.verify(receiver).endDocument();
        ordered.verify(receiver).startDocument();
        ordered.verify(receiver).startElement("", "root", "root", rootAtts);
        ordered.verify(receiver).startElement("", "record", "record", recordTwoAtts);
        ordered.verify(receiver).endElement("", "record", "record");
        ordered.verify(receiver).endElement("", "root", "root");
        ordered.verify(receiver).endDocument();
        ordered.verifyNoMoreInteractions();
    }
}