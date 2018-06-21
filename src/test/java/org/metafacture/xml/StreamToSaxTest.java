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
package org.metafacture.xml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.XmlReceiver;
import org.metafacture.xml.mockito.AttributeMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class StreamToSaxTest
{
    private StreamToSax handler;

    @Mock
    private XmlReceiver receiver;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        handler = new StreamToSax();
        handler.setReceiver(receiver);
    }

    @After
    public void cleanUp()
    {
        handler.closeStream();
    }

    @Test
    public void convertLiteralToXmlEvent() throws Exception
    {
        handler.literal("literal-name", "literal-value");

        verify(receiver).startElement(eq(""), eq("literal-name"), eq("literal-name"),
                argThat(AttributeMatcher.hasNoAttributes()));

        String value = "literal-value";
        verify(receiver).characters(value.toCharArray(), 0, value.length());

        verify(receiver).endElement("", "literal-name", "literal-name");
    }

    @Test
    public void convertEntityToSax() throws Exception
    {
        handler.startEntity("entity-name");
        handler.endEntity();

        verify(receiver).startElement(eq(""), eq("entity-name"), eq("entity-name"),
                argThat(AttributeMatcher.hasNoAttributes()));
        verify(receiver).endElement("", "entity-name", "entity-name");
    }

    @Test
    public void convertRecordToSax() throws Exception
    {
        handler.startRecord("identifier");
        handler.endRecord();

        verify(receiver).startDocument();
        verify(receiver).startElement(eq(""), eq("record"), eq("record"),
                argThat(AttributeMatcher.hasSingleAttribute("", "id", "id", "ID", "identifier")));
        verify(receiver).endElement("", "record", "record");
        verify(receiver).endDocument();
    }
}

