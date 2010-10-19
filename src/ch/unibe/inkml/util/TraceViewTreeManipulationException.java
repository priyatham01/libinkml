/*
 * 
 */

package ch.unibe.inkml.util;

/**
 * This Exception is thrown if an error occured while manipulating the trace view tree of an 
 * ink Document
 * @author emanuel
 */
public class TraceViewTreeManipulationException extends Error {

    public TraceViewTreeManipulationException(String string) {
        super(string);
    }

    private static final long serialVersionUID = -3207032475013332278L;

}
