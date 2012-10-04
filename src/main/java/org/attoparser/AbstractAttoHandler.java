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
package org.attoparser;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractAttoHandler implements IAttoHandler, ITimedDocumentHandling {

    private long parsingStartTimeNanos = -1L;
    private long parsingEndTimeNanos = -1L;
    
    
    protected AbstractAttoHandler() {
        super();
    }
    
    
    public final void handleDocumentStart() throws AttoParseException {
        this.parsingStartTimeNanos = System.nanoTime();
        handleDocumentStart(this.parsingStartTimeNanos);
    }

    
    public final void handleDocumentEnd() throws AttoParseException {
        this.parsingEndTimeNanos = System.nanoTime();
        final long totalTimeNanos = this.parsingEndTimeNanos - this.parsingStartTimeNanos;
        handleDocumentEnd(this.parsingEndTimeNanos, totalTimeNanos);
    }


    public void handleText(final char[] buffer, final int offset, final int len, 
            final int line, final int col)
            throws AttoParseException {
        // Nothing to be done here
    }

    public void handleStructure(final char[] buffer, final int offset, final int len, 
            final int line, final int col)
            throws AttoParseException {
        // Nothing to be done here
    }
    
    
    public void handleDocumentStart(final long startTimeNanos) 
            throws AttoParseException {
        // Nothing to be done here
    }

    
    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos)
           throws AttoParseException {
        // Nothing to be done here
    }

    

    public final long getStartTimeNanos() {
        return this.parsingStartTimeNanos;
    }


    public final long getEndTimeNanos() {
        return this.parsingEndTimeNanos;
    }


    public final long getTotalTimeNanos() {
        return this.parsingEndTimeNanos - this.parsingStartTimeNanos;
    }
    
}