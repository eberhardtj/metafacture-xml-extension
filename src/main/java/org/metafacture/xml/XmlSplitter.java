package org.metafacture.xml;

import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultObjectPipe;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@In(Reader.class)
@Out(String.class)
@Description("Yields a new document for each child element underneath the document root.")
@FluxCommand("split-xml")
public class XmlSplitter extends DefaultObjectPipe<Reader, ObjectReceiver<String>>
{

    private StartDocument findDocumentStart(XMLEventReader eventReader) throws XMLStreamException
    {
        while (eventReader.peek() != null && !eventReader.peek().isStartDocument()) {
            eventReader.nextEvent();
        }

        if (eventReader.hasNext()) {
            return (StartDocument) eventReader.nextEvent();
        } else {
            throw new NoSuchElementException();
        }
    }

    private StartElement findNextStartElement(XMLEventReader eventReader) throws XMLStreamException
    {
        while (eventReader.peek() != null && !eventReader.peek().isStartElement()) {
            eventReader.nextEvent();
        }

        if (eventReader.hasNext()) {
            return eventReader.nextEvent().asStartElement();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void process(final Reader reader)
    {
        assert !isClosed();
        assert null != reader;

        XMLEventFactory  eventFactory = XMLEventFactory.newInstance();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        int depth = 0;

        try {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);

            final StartDocument declaration = findDocumentStart(eventReader);
            String encoding = declaration.getCharacterEncodingScheme();
            if (encoding == null) encoding = "UTF-8";

            final StartDocument startDocument = eventFactory.createStartDocument(encoding, declaration.getVersion());
            final EndDocument endDocument = eventFactory.createEndDocument();

            final StartElement rootStart = findNextStartElement(eventReader);
            final EndElement rootEnd = eventFactory.createEndElement(rootStart.getName(), rootStart.getAttributes());

            StartElement childStart = findNextStartElement(eventReader);
            depth++;

            List<XMLEvent> document = Stream.of(startDocument, rootStart, childStart).collect(Collectors.toList());

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) depth++;

                document.add(event);

                if (depth == 1 && event.isEndElement()) {
                    document.add(rootEnd);
                    document.add(endDocument);

                    String xml = asString(document);
                    getReceiver().process(xml);

                    try {
                        childStart = findNextStartElement(eventReader);
                        depth++;
                        document = Stream.of(startDocument, rootStart, childStart).collect(Collectors.toList());
                    } catch (NoSuchElementException e) {
                        break;
                    }
                }

                if (event.isEndElement()) depth--;
            }
        } catch (XMLStreamException|IOException e) {
            throw new MetafactureException(e);
        }
    }

    private String asString(List<XMLEvent> eventList) throws XMLStreamException, IOException {
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(stringWriter);

        for (XMLEvent e: eventList) {
            eventWriter.add(e);
        }

        eventWriter.close();
        stringWriter.close();
        return stringWriter.toString();
    }
}
