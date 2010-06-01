/**
 * 
 */
package ch.unibe.inkml;

import java.awt.Polygon;
import java.awt.geom.RectangularShape;
import java.util.List;

import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;

/**
 * @author emanuel
 * 
 */
public interface InkTracePointList {

    /**
     * @return The Bounds of the traces which are contained or referred to by this object.
     */
    public abstract TraceBound getBounds();

    /**
     * @return The Timespan enclosing the traces contained by or referred to by this object.
     */
    public abstract Timespan getTimeSpan();

    /**
     * Returns a list of trace points of the traces which are contained or
     * referred to by this object. The list is constrained by the
     * <code>from</code> and <code>to</code>. For a better understanding the
     * definition of InkML is included here:
     * 
     * Any value of a <code>from</code> or <code>to</code> attribute is a colon-separated list of
     * integers, whose meaning is defined as follows: An empty list of integers
     * selects the entire referenced object (point, <trace>, <traceGroup> or
     * <traceView>). If the list is non-empty, then its first element is taken
     * as a 1-based index into the referenced object, and the remaining list is
     * used to select within the object. It is an error to try to select within
     * a single point.
     * A missing from attribute is equivalent to selecting the first point in
     * the (recursively) first child of the referenced element. A missing to
     * attribute is equivalent to selecting the last point in the (recursively)
     * last child of the referenced element. With these defaults, the
     * <traceView> selects the portion of the referenced element from the first
     * point to the last point, inclusive. If neither a to nor from attribute is
     * given, this implies the entire referenced element is selected.
     * 
     * @param from constrains the points from the start
     * @param to constrains the points from the end
     * @return a list of trace points
     */
    /**
     * Returns all points which are containd by the traces represented by this traceview.
     * The points are ordered according to their timestamp.
     * With parameter from and to, its possible to get a subset of the points all points.
     * The parameters are used the follwing way:
     *     - from: an integer specify the child trace/point to start with (indexing starts with 1)
     *         e.g, "3" means start extracting from the third subtrace (if this view is a container) else the third point
     *         With a point indices of the points/childtraces on a sublevel can be specified.
     *         e.g. "3.2" means start with the third subtrace, from there start with the second subtrace/point
     *         default is "1" meaning start at the beginning
     *     - to is the same but specifies the point where to stop (default is null which means stop at the end)
     * @param from
     * @param to
     * @return
     */
    public abstract List<InkTracePoint> getPoints(String from, String to);

    /**
     * Returns all points which are containd by the traces represented by this traceview.
     * The points are ordered according to their timestamp.
     * @return
     */
    public abstract List<InkTracePoint> getPoints();

    public abstract Iterable<InkTracePoint> pointIterable();


    /**
     * return a polygon from all points represented.
     * @return
     */
    public abstract Polygon getPolygon();

    /**
     * Returns the i-th point of the trace  or traces represented by this view
     * @param i
     * @return
     */
    abstract public InkTracePoint getPoint(int i);

    /**
     * Returns the number of points of the trace or traces represented by this view
     * @return
     */
    public abstract int getPointCount();
    
}