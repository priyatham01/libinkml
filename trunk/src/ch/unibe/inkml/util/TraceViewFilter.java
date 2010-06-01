package ch.unibe.inkml.util;

import java.util.Collection;
import java.util.List;

import ch.unibe.inkml.InkTraceView;

/**
 * A Trace filter filters some traces from a list. This is used to remove e.g. markings from the content,
 * or to display only the traces within a time frame.
 * @author emanuel
 *
 */
public interface TraceViewFilter {
    /**
     * Decides for a single traceView if it passes the filter.
     * Returns true if filter is passed.
     * @param view The trace view to test.
     * @return true if test is succesfull
     */
	public boolean pass(InkTraceView view);
	
	/**
	 * Filters a list of trace views
	 * @param <L> Type of InkTraceView to handle, InkTraceViewLeaf or InkTraceView or InkTraceViewContainer
	 * @param list List of traceviews to test
	 * @return Filtered list of views 
	 */
    public <L extends InkTraceView> List<L> filter(Collection<L> list);
}
