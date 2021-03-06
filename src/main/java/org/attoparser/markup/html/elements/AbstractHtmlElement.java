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
package org.attoparser.markup.html.elements;








/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class AbstractHtmlElement implements IHtmlElement {

    
    private final int nameLen;
    private final String name;
    private final char[] normalizedName;
    


    
    
    
    protected AbstractHtmlElement(final String name) {
        
        super();
        
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        
        this.name = name;
        this.normalizedName = name.toCharArray();
        this.nameLen = this.normalizedName.length;

        // Normalizing: set to lower-case
        for (int i = 0; i < this.nameLen; i++) {
            final char c = this.normalizedName[i]; 
            if (c >= 'A' && c <= 'Z') {
                this.normalizedName[i] = (char)(c + HtmlElements.CASE_DIFF);
            }
        }

        HtmlElements.registerElement(this);
        
    }

    
    
    public final String getName() {
        return this.name;
    }
    
    
    
    
    public final boolean matches(final String elementName) {

        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        final int len = elementName.length();
        if (len != this.nameLen) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = elementName.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c = (char)(c + HtmlElements.CASE_DIFF);
            }
            if (c != this.normalizedName[i]) {
                return false;
            }
        }
        return true;
        
    }

    
    public final boolean matches(final char[] elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        return matches(elementName, 0, elementName.length);
    }

    
    public final boolean matches(final char[] elementNameBuffer, final int offset, final int len) {
        
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        
        if (len != this.nameLen) {
            return false;
        }
        final int max = offset + len;
        for (int i = offset; i < max; i++) {
            char c = elementNameBuffer[i];
            if (c >= 'A' && c <= 'Z') {
                c = (char)(c + HtmlElements.CASE_DIFF);
            }
            if (c != this.normalizedName[i - offset]) {
                return false;
            }
        }
        return true;
        
    }



    
    

    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('<');
        strBuilder.append(this.name);
        strBuilder.append('>');
        return strBuilder.toString();
    }
    
    
    
    
    
}