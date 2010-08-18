/**
 * 
 */
package ch.unibe.inkml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.unibe.inkml.util.TraceVisitor;
import ch.unibe.inkml.util.ViewTreeManipulationException;

/**
 * @author emanuel
 *
 */
public interface InkTraceContainer<T extends InkTracePointList> extends Iterable<T> ,InkTracePointList{

        /**
     * Adds each InkTraceView of the list to this container 
     * @param viewList list of InkTraceView
    
     * @throws ViewTreeManipulationException 
     */
    public abstract void addTraces(Collection<T> viewList);

    /**
     * Adds a trace view element to this trace view container.
     * If the new trace view is empty (no content, and not refering to trace) it will be droped.
     * If the new trace view has allready a parent, it is removed from this parent.
     * @param tv new trace view added to this container.
     * @throws ViewTreeManipulationException 
     */
    public abstract void addTrace(T tv);

    /**
     * Returns the list of contained views 
     * @return requested list
     */
    public abstract List<T> getContent();

    /**
     *  
     * Returns the iterator over all contained views
     * this method make InkTraceViewContainer comply the Interable interface
     */
    public abstract Iterator<T> iterator();

    /**
     * Returns the list of all successors bewlow this object
     * @return requested list
     */
    public abstract List<T> getFlattenedTraces();

    /**
     * Return true if no InkTraceView is contained, 
     * This is a reason to delete this element 
     * {@inheritDoc}
     */
    public abstract boolean isEmpty();

    /**
     * @return
     */
    public abstract List<T> getFlattenedTraceLeafs();

}