/*
 * =============================================================================
 * 
 *   Copyright (c) 2012, The ATTOPARSER team (http://www.attoparser.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.attoparser.markup.html.trace;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.attoparser.AttoParseException;
import org.attoparser.markup.html.AbstractDetailedNonValidatingHtmlAttoHandler;
import org.attoparser.markup.html.HtmlParsingConfiguration;
import org.attoparser.markup.html.elements.IHtmlElement;







/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class HtmlCodeDisplayAttoHandler extends AbstractDetailedNonValidatingHtmlAttoHandler {

    private static final String OPEN_TAG_START = "&lt;";
    private static final String OPEN_TAG_END = "&gt;";
    private static final String CLOSE_TAG_START = "&lt;/";
    private static final String CLOSE_TAG_END = "&gt;";
    private static final String MINIMIZED_TAG_END = "/&gt;";
    
    
    private static final String DOCUMENT_STYLES = "\n" +
            "div.atto_source {\n" +
            "    font-family: Courier, 'Courier New', monospace;\n" +
            "    font-size: 12px;\n" +
            "}\n";

    
    private static final String FRAGMENT_STYLES = "\n" +
            "@@ .element {\n" +
            "    font-weight: bold;\n" + 
            "    color: black;\n" + 
            "}\n" +
            "@@ .attr-name {\n" +
            "    font-weight: normal;\n" + 
            "    color: red;\n" + 
            "}\n" +
            "@@ .attr-value {\n" +
            "    font-weight: normal;\n" + 
            "    color: blue;\n" + 
            "}\n" +
            "@@ .doctype {\n" +
            "    font-weight: bold;\n" + 
            "    font-style: italics;\n" + 
            "    color: #888;\n" + 
            "}\n" +
            "@@ .comment {\n" +
            "    font-style: italic;\n" + 
            "    color: black;\n" + 
            "    background: #ddd;\n" + 
            "}\n" +
            "@@ .xml-declaration {\n" +
            "    font-weight: bold;\n" + 
            "    color: olivedrab;\n" + 
            "}\n" +
            "@@ .processing-instruction {\n" +
            "    color: white;\n" + 
            "    background: black;\n" + 
            "}\n" +
            "@@ .text {\n" +
            "    color: #444;\n" + 
            "    background: white;\n" + 
            "}\n" +
            "\n";
    
    
    private static final String STYLE_DOCTYPE = "doctype";
    private static final String STYLE_COMMENT = "comment";
    private static final String STYLE_CDATA = "cdata";
    private static final String STYLE_XML_DECLARATION = "xml-declaration";
    private static final String STYLE_PROCESSING_INSTRUCTION = "processing-instruction";
    private static final String STYLE_ELEMENT = "element";
    private static final String STYLE_ATTR_NAME = "attr-name";
    private static final String STYLE_ATTR_VALUE = "attr-value";
    private static final String STYLE_TEXT = "text";
    
    private static final String TAG_FORMAT_START = "<span class=\"%1$s\">";
    private static final String TAG_FORMAT_END = "</span>";

    
    
    private final String documentName;
    private final String documentId;
    private final Writer writer;
    private final boolean createHtmlAsFragment;
    
    
    
    
    public HtmlCodeDisplayAttoHandler(final String documentName, final Writer writer, final boolean createHtmlAsFragment) {
        super(new HtmlParsingConfiguration());
        this.documentName = documentName;
        this.documentId = tokenify(this.documentName);
        this.writer = writer;
        this.createHtmlAsFragment = createHtmlAsFragment;
    }

    
    public HtmlCodeDisplayAttoHandler(final String documentName, final Writer writer, final HtmlParsingConfiguration configuration, final boolean createHtmlFragment) {
        super(configuration);
        this.documentName = documentName;
        this.documentId = tokenify(this.documentName);
        this.writer = writer;
        this.createHtmlAsFragment = createHtmlFragment;
    }
    
    
    
    
    private void writeEscaped(final char[] buffer, final int offset, final int len) throws IOException {
        

        final int maxi = offset + len;
        for (int i = offset; i < maxi; i++) {
            final char c = buffer[i];
            if (c == '\n') {
                this.writer.write("<br />");
            } else if (c == ' ') {
                this.writer.write("&nbsp;");
            } else {
                this.writer.write(c);
            }
        }

    }
    
    
    
    private void openStyle(final String style) throws IOException {
        openStyles(Collections.singletonList(style));
    }
    
    private void openStyles(final List<String> styles) throws IOException {
        final StringBuilder strBuilder = new StringBuilder();
        final Iterator<String> stylesIter = styles.iterator();
        strBuilder.append(stylesIter.next());
        while (stylesIter.hasNext()) {
            strBuilder.append(' ');
            strBuilder.append(stylesIter.next());
        }
        this.writer.write(String.format(TAG_FORMAT_START, strBuilder.toString()));
    }
    
    private void closeStyle() throws IOException {
        this.writer.write(TAG_FORMAT_END);
    }
    
    
    
    
    public String tokenify(final String text) {
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }
    
    
    
    
    @Override
    public void handleDocumentStart(final long startTimeNanos, 
            final int line, final int col,
            final HtmlParsingConfiguration configuration)
            throws AttoParseException {
        
        try {
            
            if (!this.createHtmlAsFragment) {
                this.writer.write("<!DOCTYPE html>\n");
                this.writer.write("<html>\n");
                this.writer.write("<head>\n");
                this.writer.write("<title>Document output: " + this.documentName + "</title>\n");
                this.writer.write("<style>" + DOCUMENT_STYLES + "</style>\n");
                this.writer.write("</head>\n");
                this.writer.write("<body>\n");
            }
            
            this.writer.write("<div class=\"atto_source\" id=\"atto_source_" + this.documentId + "\">\n");
            this.writer.write("<style>\n" + FRAGMENT_STYLES.replaceAll("@@", "#atto_source_content_" + this.documentId ) + "</style>\n");
            this.writer.write("<div class=\"atto_source_content\" id=\"atto_source_content_" + this.documentId + "\">");
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    
    @Override
    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos, 
            final int line, final int col, 
            final HtmlParsingConfiguration configuration)
            throws AttoParseException {
        
        try {
            
            this.writer.write("</div>");
            
            if (!this.createHtmlAsFragment) {
                this.writer.write("</body>\n");
                this.writer.write("</html>\n");
            }
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }
    
    

    
    @Override
    public void handleHtmlStandaloneElementStart(
            final IHtmlElement element,
            final boolean minimized,
            final char[] buffer,
            final int offset, final int len, 
            final int line, final int col) 
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_ELEMENT);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, offset, len);
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    @Override
    public void handleHtmlStandaloneElementEnd(
            final IHtmlElement element,
            final boolean minimized,
            final int line, final int col)
            throws AttoParseException {
        
        try {
            
            this.writer.write((minimized? MINIMIZED_TAG_END : OPEN_TAG_END));
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    
    
    @Override
    public void handleHtmlOpenElementStart(
            final IHtmlElement element,
            final char[] buffer, 
            final int offset, final int len,
            final int line, final int col)
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_ELEMENT);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, offset, len);
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    @Override
    public void handleHtmlOpenElementEnd(
            final IHtmlElement element,
            final int line, final int col) 
            throws AttoParseException {
        
        try {
            
            this.writer.write(OPEN_TAG_END);
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    
    
    @Override
    public void handleHtmlCloseElementStart(
            final IHtmlElement element,
            final char[] buffer, 
            final int offset, final int len,
            final int line, final int col) 
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_ELEMENT);
            this.writer.write(CLOSE_TAG_START);
            this.writer.write(buffer, offset, len);
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    @Override
    public void handleHtmlCloseElementEnd(
            final IHtmlElement element,
            final int line, final int col) 
            throws AttoParseException {
        
        try {
            
            this.writer.write(CLOSE_TAG_END);
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }




    @Override
    public void handleHtmlAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_ATTR_NAME);
            this.writer.write(buffer, nameOffset, nameLen);
            closeStyle();
            
            this.writer.write(buffer, operatorOffset, operatorLen);
            
            openStyle(STYLE_ATTR_VALUE);
            this.writer.write(buffer, valueOuterOffset, valueOuterLen);
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }


    @Override
    public void handleHtmlInnerWhiteSpace(
            final char[] buffer, 
            final int offset, final int len, 
            final int line, final int col)
            throws AttoParseException {
        
        try {
            
            this.writer.write(buffer, offset, len);
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }


    

    
    
    @Override
    public void handleText(final char[] buffer, final int offset, final int len, 
            final int line, final int col)
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_TEXT);
            writeEscaped(buffer, offset, len);
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }


    
    @Override
    public void handleComment(
            final char[] buffer, 
            final int contentOffset, final int contentLen, 
            final int outerOffset, final int outerLen, 
            final int line, final int col)
            throws AttoParseException {
        
        try {

            openStyle(STYLE_COMMENT);
            this.writer.write("&lt;!--");
            this.writer.write(buffer, contentOffset, contentLen);
            this.writer.write("--&gt;");
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    @Override
    public void handleCDATASection(
            final char[] buffer, 
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_CDATA);
            this.writer.write("&lt;![CDATA[");
            this.writer.write(buffer, contentOffset, contentLen);
            this.writer.write("]]&gt;");
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    
    
    @Override
    public void handleXmlDeclarationDetail(
            final char[] buffer, 
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int versionOffset, final int versionLen,
            final int versionLine, final int versionCol,
            final int encodingOffset, final int encodingLen,
            final int encodingLine, final int encodingCol,
            final int standaloneOffset, final int standaloneLen,
            final int standaloneLine, final int standaloneCol,
            final int outerOffset, final int outerLen,
            final int line,final int col) 
            throws AttoParseException {
        
        try {

            final int outerContentEnd = (outerOffset  + outerLen) - 2;
            
            openStyle(STYLE_XML_DECLARATION);
            this.writer.write("&lt;");
            this.writer.write('?');
            this.writer.write(buffer, keywordOffset, keywordLen);

            /*
             * VERSION (required) 
             */
            int lastStructureEnd = keywordOffset + keywordLen;
            int thisStructureOffset = versionOffset;
            int thisStructureLen = versionLen;
            int thisStructureEnd = thisStructureOffset + thisStructureLen;
            
            this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
            this.writer.write(buffer, thisStructureOffset, thisStructureLen);

            /*
             * ENCODING (optional)
             */
            if (encodingLen > 0)  {
                
                lastStructureEnd = thisStructureEnd;
                thisStructureOffset = encodingOffset;
                thisStructureLen = encodingLen;
                thisStructureEnd = thisStructureOffset + thisStructureLen;
            
                this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
                this.writer.write(buffer, thisStructureOffset, thisStructureLen);

            }

            /*
             * STANDALONE (optional)
             */
            
            if (standaloneLen > 0) {
                
                lastStructureEnd = thisStructureEnd;
                thisStructureOffset = standaloneOffset;
                thisStructureLen = standaloneLen;
                thisStructureEnd = thisStructureOffset + thisStructureLen;
            
                this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
                this.writer.write(buffer, thisStructureOffset, thisStructureLen);
                
            }
            
            this.writer.write(buffer, thisStructureEnd, (outerContentEnd - thisStructureEnd));
            
            this.writer.write('?');
            this.writer.write("&gt;");
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    



    @Override
    public void handleDocType(
            final char[] buffer, 
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int elementNameOffset, final int elementNameLen,
            final int elementNameLine, final int elementNameCol,
            final int typeOffset, final int typeLen,
            final int typeLine, final int typeCol,
            final int publicIdOffset, final int publicIdLen,
            final int publicIdLine, final int publicIdCol,
            final int systemIdOffset, final int systemIdLen,
            final int systemIdLine, final int systemIdCol,
            final int internalSubsetOffset, final int internalSubsetLen,
            final int internalSubsetLine, final int internalSubsetCol,
            final int outerOffset, final int outerLen,
            final int outerLine, final int outerCol) 
            throws AttoParseException {
        
        try {
            
            openStyle(STYLE_DOCTYPE);
            this.writer.write("&lt;");
            this.writer.write(buffer, outerOffset + 1, outerLen - 2);
            this.writer.write("&gt;");
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }





    @Override
    public void handleProcessingInstruction(
            final char[] buffer, 
            final int targetOffset, final int targetLen, 
            final int targetLine, final int targetCol,
            final int contentOffset, final int contentLen,
            final int contentLine, final int contentCol,
            final int outerOffset, final int outerLen, 
            final int line, final int col)
            throws AttoParseException {
        
        try {

            openStyle(STYLE_PROCESSING_INSTRUCTION);
            this.writer.write("&lt;");
            this.writer.write('?');
            this.writer.write(buffer, targetOffset, targetLen);
            if (contentLen > 0)  {
                this.writer.write(buffer, (targetOffset + targetLen), contentOffset - (targetOffset + targetLen));
                this.writer.write(buffer, contentOffset, contentLen);
            } else {
                this.writer.write(buffer, (targetOffset + targetLen), ((outerOffset  + outerLen) - 2) - (targetOffset + targetLen));
            }
            this.writer.write('?');
            this.writer.write("&gt;");
            closeStyle();
            
        } catch (final Exception e) {
            throw new AttoParseException(e);
        }
        
    }

    
    

    
    
}