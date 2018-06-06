package org.metafacture.xml;

import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.XmlReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultXmlPipe;
import org.xml.sax.*;

import java.io.IOException;

@In(XmlReceiver.class)
@Out(XmlReceiver.class)
@Description("Yields a new document for each child element underneath the document root.")
@FluxCommand("split-sax")
public class SaxSplitter extends DefaultXmlPipe<XmlReceiver>
{
    private boolean isRoot;
    private String rootUri;
    private String rootLocalName;
    private String rootQName;
    private Attributes rootAtts;

    private int depth;

    public SaxSplitter()
    {
        this.isRoot = true;
        this.depth = 0;
    }

    @Override
    public void startDocument() throws SAXException
    {
        // pass
    }

    @Override
    public void endDocument() throws SAXException
    {
        // pass
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (isRoot)
        {
            isRoot = false;

            rootUri = uri;
            rootLocalName = localName;
            rootQName = qName;
            rootAtts = atts;
        }
        else
        {
            if (depth == 0) {
                getReceiver().startDocument();
                getReceiver().startElement(rootUri, rootLocalName, rootQName, rootAtts);
            }

            if (!localName.equals(rootLocalName)) getReceiver().startElement(uri, localName, qName, atts);

            depth++;
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException
    {
        depth--;

        if (!localName.equals(rootLocalName)) getReceiver().endElement(uri, localName, qName);

        if (depth == 0)
        {
            getReceiver().endElement(rootUri, rootLocalName, rootQName);
            getReceiver().endDocument();
        }
    }

    @Override
    public void setDocumentLocator(final Locator locator)
    {
        getReceiver().setDocumentLocator(locator);
    }

    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException
    {
        getReceiver().startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(final String prefix) throws SAXException
    {
        getReceiver().endPrefixMapping(prefix);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
        getReceiver().characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException
    {
        getReceiver().ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(final String target, final String data) throws SAXException
    {
        getReceiver().processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(final String name) throws SAXException
    {
        getReceiver().skippedEntity(name);
    }

    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException
    {
        getReceiver().notationDecl(name, publicId, systemId);
    }

    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        getReceiver().unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException
    {
        return getReceiver().resolveEntity(publicId, systemId);
    }

    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        getReceiver().warning(exception);
    }

    @Override
    public void error(final SAXParseException exception) throws SAXException {
        getReceiver().error(exception);
    }

    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        getReceiver().fatalError(exception);
    }

    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        getReceiver().startDTD(name, publicId, systemId);
    }

    @Override
    public void endDTD() throws SAXException {
        getReceiver().endDTD();
    }

    @Override
    public void startEntity(final String name) throws SAXException {
        getReceiver().startEntity(name);
    }

    @Override
    public void endEntity(final String name) throws SAXException {
        getReceiver().endEntity(name);
    }

    @Override
    public void startCDATA() throws SAXException {
        getReceiver().startCDATA();
    }

    @Override
    public void endCDATA() throws SAXException {
        getReceiver().endCDATA();
    }

    @Override
    public void comment(final char[] chars, final int start, final int length) throws SAXException {
        getReceiver().comment(chars, start, length);
    }
}
