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
package org.attoparser.markup;




/**
 * <p>
 *   Class containing utility methods for general markup parsing.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class MarkupParsingUtil {


    

    
    private MarkupParsingUtil() {
        super();
    }

    
    

    
    
    

    
    
    
    
    static int findNextStructureEndAvoidQuotes(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {
        
        boolean inQuotes = false;
        boolean inApos = false;

        int colIndex = offset;
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '"' && !inApos) {
                inQuotes = !inQuotes;
            } else if (c == '\'' && !inQuotes) {
                inApos = !inApos;
            } else if (c == '>' && !inQuotes && !inApos) {
                locator[1] += (i - colIndex);
                return i;
            }
            
        }
            
        locator[1] += (maxi - colIndex);
        return -1;
        
    }
    
    
    static int findNextStructureEndDontAvoidQuotes(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {
        
        int colIndex = offset;
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '>') {
                locator[1] += (i - colIndex);
                return i;
            }
            
        }
            
        locator[1] += (maxi - colIndex);
        return -1;
        
    }

    
    static int findNextStructureStart(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {

        int colIndex = offset;
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '<') {
                locator[1] += (i - colIndex);
                return i;
            }
            
        }
            
        locator[1] += (maxi - colIndex);
        return -1;
        
    }

    
    static int findNextWhitespaceCharWildcard(
            final char[] text, final int offset, final int maxi, 
            final boolean avoidQuotes, final int[] locator) {
        
        boolean inQuotes = false;
        boolean inApos = false;

        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (avoidQuotes && !inApos && c == '"') {
                inQuotes = !inQuotes;
            } else if (avoidQuotes && !inQuotes && c == '\'') {
                inApos = !inApos;
            } else if (!inQuotes && !inApos && (c == ' ' || c == '\n' || Character.isWhitespace(c))) {
                return i;
            }

            LocatorUtils.countChar(locator, c);
            
        }
            
        return -1;
        
    }

    
    static int findNextNonWhitespaceCharWildcard(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {
        
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            final boolean isWhitespace = (c == ' ' || c == '\n' || Character.isWhitespace(c));
            
            if (!isWhitespace) {
                return i;
            }

            LocatorUtils.countChar(locator, c);
            
        }
            
        return -1;
        
    }

    
    static int findNextOperatorCharWildcard(
            final char[] text, final int offset, final int maxi,  
            final int[] locator) {
        
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (c == '=' || (c == ' ' || c == '\n' || Character.isWhitespace(c))) {
                return i;
            }

            LocatorUtils.countChar(locator, c);
            
        }
            
        return -1;
        
    }

    
    static int findNextNonOperatorCharWildcard(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {
        
        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (c != '=' && !(c == ' ' || c == '\n' || Character.isWhitespace(c))) {
                return i;
            }

            LocatorUtils.countChar(locator, c);
            
        }
            
        return -1;
        
    }

    
    static int findNextAnyCharAvoidQuotesWildcard(
            final char[] text, final int offset, final int maxi,  
            final int[] locator) {
        
        boolean inQuotes = false;
        boolean inApos = false;

        for (int i = offset; i < maxi; i++) {
            
            final char c = text[i];
            
            if (!inApos && c == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes && c == '\'') {
                inApos = !inApos;
            } else if (!inQuotes && !inApos) {
                return i;
            }

            LocatorUtils.countChar(locator, c);
            
        }
            
        return -1;
        
    }

    
    
    
}
