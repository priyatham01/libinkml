/*
 * Created on 23.07.2007
 *
 * Copyright (C) 2007  Emanuel Indermühle <emanuel@inthemill.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 */
package ch.unibe.inkml;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observer;
import ch.unibe.inkml.InkChannel.Name;
import ch.unibe.inkml.util.Formatter;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;

public class InkTraceLeaf extends InkTrace implements Iterable<InkTracePoint> {

    /**
     * trace points, how they are stored in InkML
     */
    //private List<InkTracePoint> sourcePoints = new LinkedList<InkTracePoint>();
    private double[][] sourcePoints;
    /**
     * trace points, how they are displayed in the canvas
     */
    //private List<InkTracePoint> points = new LinkedList<InkTracePoint>();
    private double[][] points;
    
    private int size = 0;

    public enum Type {
        PEN_DOWN, PEN_UP, INDETERMINATE;
        public String toString() {
            switch (this) {
            case PEN_DOWN:
                return "penDown";
            case PEN_UP:
                return "penUp";
            case INDETERMINATE:
                return "indeterminate";
            default:
                return super.toString();
            }
        }

        public static Type getValue(String name) {
            for (Type t : Type.values()) {
                if (t.toString().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        }
    };

    /**
     * Type of this trace, this can be "penDown" | "penUp" | "indeterminate".
     * 
     * Explanation given by the InkML definition:
     * The type attribute of a Trace indicates the pen contact state (either
     * "penUp" or "penDown") during its recording. A value of "indeterminate" is
     * used if the contact-state is neither pen-up nor pen-down, and may be
     * either unknown or variable within the trace. For example, a signature may
     * be captured as a single indeterminate trace containing both the actual
     * writing and the trajectory of the pen between strokes.
     * 
     */
    private Type type;

    public enum Continuation {
        BEGIN, END, MIDDLE;
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static Type getValue(String name) {
            for (Type t : Type.values()) {
                if (t.toString().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        }

    };

    /**
     * This attribute indicates whether this trace is a continuation trace, and
     * if it is the case, where this trace is located in the set of continuation
     * traces, it takes the values "begin" | "end" | "middle".
     * 
     * Explanation given by the InkML definition: If a <code>continuation</code>
     * attribute is present, it indicates that the current trace is a
     * continuation trace, i.e. its points are a temporally contiguous
     * continuation of (and thus should be connected to) another trace element.
     * The possible values of the attribute are:
     * <ul>
     * <li><code>begin</code>: the current trace is the first of the set of
     * continuation traces</li>
     * <li><code>end</code>: the current trace is the last of the set of
     * continuation traces</li>
     * <li><code>middle</code>: the current trace is a continuation trace, but
     * is neither the first nor the last in the set of traces</li>
     * </ul>
     * 
     */

    private Continuation continuation;

    private String priorRef;

    private String brushRef;

    private Double duration;

    private Double timeOffset;

    /**
     * caches the bounding time span of this trace
     */
    private Timespan timespan;

    /**
     * caches the bounding box of this trace
     */
    private TraceBound bound;

    /**
     * Caches the center of gravity of this trace
     */
    private Point2D centerOfGravity = new Point2D.Double();
    
    /**
     * Fast access to source index
     */
    private Map<Name,Integer> sourceIndex;
    
    private boolean tainted = false;
    
    private InkTraceFormat targetFormat;

    
    public class ProxyInkTracePoint extends InkTracePoint {
        private int i = 0;
        public ProxyInkTracePoint(int index){
            i = index;
        }
        
        /**
         * {@inheritDoc}
         * This method will notify the observers registered for {@link InkTrace#ON_CHANGE} on the trace responsible
         * for this point.
         */
        public void set(Name name, Object value) {
            set(name,doubleize(name,value));
        }
        
        /**
         * {@inheritDoc}
         * This method will notify the observers registered for {@link InkTrace#ON_CHANGE} on the trace responsible
         * for this point.
         */
        public void set(Name name, double d) {
            points[i][getIndex(name)] = d;
            taint();
            notifyObserver(InkInk.ON_CHANGE);
        }

        public Object getObject(Name name) {
            return objectify(name,get(name));
        }
        
        public double get(Name t) {
            return points[i][getIndex(t)];
        }
        
        public int index(){
            return i;
        }
    }
    
    
    
    public InkTraceLeaf(InkInk ink, InkTraceGroup parent) {
        super(ink, parent);
        sourceIndex = getSourceFormat().getIndex();
    }

    protected void initialize() {
        super.initialize();
        registerFor(ON_CHANGE, new Observer() {
            @Override
            public void notifyFor(Aspect event, Object subject) {
                renewCache();
                if(isRoot()){
                    getInk().notifyObserver(InkInk.ON_CHANGE, subject);
                }else{
                    getParent().notifyObserver(InkTrace.ON_CHANGE, subject);
                }
                notifyObserver(InkTraceView.ON_DATA_CHANGE, subject);
            }
        });
    }

    private void taint(){
        tainted = true;
    }
    
    private void renewCache() {
        // new center of gravity
        centerOfGravity = InkTracePoint.getCenterOfGravity(this);
        
        //new timespan
        if (getPointCount() > 0) {
            if (!getTargetFormat().containsChannel(Name.T)) {
                System.err.println("point has no time coordinates can not deliver timeSpan");
                //this.testFormat(this.getCanvasFormat());
            }
            int t = getIndex(Name.T);
            timespan = new Timespan(points[0][t], points[size-1][t]);
        }
        
        //new bound
        bound = new TraceBound(getPoint(0));
        for(InkTracePoint p: this){
            bound.add(p);
        }
	}

	public TraceBound getBounds() {
	    return bound;
    }
	
    /**
     * Returns the center of gravity of all points
     * @return
     */
    public Point2D getCenterOfGravity() {
        return centerOfGravity;
    }

    public Timespan getTimeSpan() {
        return timespan;
    }

    public void backTransformPoints() throws InkMLComplianceException {
        getCanvasTransform().backTransform(points,sourcePoints,getTargetFormat(),getSourceFormat());
    }

    public InkCanvasTransform getCanvasTransform() {
        return this.getContext().getCanvasTransform();
    }

    /**
     * Returns the brush responsible to draw this trace
     * @return a brush
     */
    public InkBrush getBrush() {
        if (this.brushRef != null) {
            return (InkBrush) this.getInk().getDefinitions().get(this.brushRef);
        } else if (this.hasLocalContext()
                && this.getLocalContext().getBrush() != null) {
            return this.getLocalContext().getBrush();
        } else if (!this.isRoot()) {
            return this.getParent().getBrush();
        } else {
            return this.getContext().getBrush();
        }
    }

    /**
     * returns the format which was used to read this trace from the archive
     * and which will be used to write the trace back.
     * @return
     */
    public InkTraceFormat getSourceFormat() {
        return this.getContext().getSourceFormat();
    }
    
    /**
     * return the format which is used to access the trace points
     * @return
     */
    private InkTraceFormat getTargetFormat() {
        if(targetFormat == null){
            targetFormat = getCanvasFormat();
        }
        return targetFormat;
    }
    
    /**
     * Returns the format which is used to access the trace points.
     * Same as {@link InkTraceLeaf#getTargetFormat()} but without caching
     * @return
     */
    public InkTraceFormat getCanvasFormat() {
        return this.getContext().getCanvasTraceFormat();
    }

    
    public List<InkTracePoint> getPoints() {
        List<InkTracePoint> l =  new LinkedList<InkTracePoint>();
        for(int i = 0;i<getPointCount();i++){
            l.add(getPoint(i));
        }
        return l;
    }
    
    
    public Iterable<InkTracePoint> pointIterable(){
        return this;
    }
    
    
    public Iterator<InkTracePoint> iterator(){
        return new Iterator<InkTracePoint>(){
            private int pos = 0;
            public boolean hasNext() {
                return pos < getPointCount();
            }

            public InkTracePoint next() {
                return getPoint(pos++);
            }

            public void remove() {
                throw new NotImplementedException();
            }
            
        };
    }

    /**
     * returns the index of the specified channel to access to data 
     * @param name
     * @return
     */
    protected int getIndex(Name name){
        return getTargetFormat().indexOf(name);
    }
    
    /**
     * Turns the specified datapoint into the object which it acctualy represent.
     * This is specified by the channel specified by name
     * @param name Channel name to lookup the correc type
     * @param d value to transform
     * @return object in the correct type.
     */
    protected Object objectify(Name name,double d){
        return getTargetFormat().objectify(name,d);
    }
    /**
     * Turns the object into a double, according to the channel specified by name
     * @param name name of the channel which does the conversion
     * @param o object to convert
     * @return resulting double
     */
    protected double doubleize(Name name,Object o){
        return getTargetFormat().doubleize(name,o);
    }
    


    
    @Override
    public List<InkTracePoint> getPoints(String from, String to) {
        int f = Integer.parseInt(from) - 1;
        int t = (to != null) ? Integer.parseInt(to) : getPointCount();
        return getPoints().subList(f, t);
    }

    public int getPointCount() {
        return this.size;
    }

    public InkTracePoint getPoint(final int pos) {
        return new ProxyInkTracePoint(pos);
    }

    public void drawPolyLine(Graphics2D g) {
        Polygon p = getPolygon();
        g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
    }

    public boolean isLeaf() {
        return true;
    }

    /**
     * Returns the distance between p and the nearest point in this trace
     * @param p other point
     * @return euclidian distance
     */
    public double distance(Point p) {
        return InkTracePoint.distanceToPoint(this, p);
    }

    


    /**
     * {@inheritDoc}
     * This method will notify the observers registered for {@link InkTrace#ON_CHANGE}.
     */
    public void buildFromXMLNode(Element node) throws InkMLComplianceException {
        super.buildFromXMLNode(node);
        if (node.hasAttribute("type")) {
            this.type = Type.valueOf(loadAttribute(node, "type", null));
        }
        if (node.hasAttribute("continuation")) {
            this.continuation = Continuation.valueOf(node
                    .getAttribute("continuation"));
        }
        if (this.continuation == Continuation.END
                || this.continuation == Continuation.MIDDLE) {
            this.priorRef = loadAttribute(node, "priorRef", null);
        }
        this.brushRef = loadAttribute(node, "brushRef", null);
        if (node.hasAttribute("duration")) {
            this.duration = Double.parseDouble(node.getAttribute("duration"));
        }
        if (node.hasAttribute("timeOffset")) {
            this.duration = Double.parseDouble(node.getAttribute("timeOffset"));
        }
        final List<Formatter> formatter = new ArrayList<Formatter>();
        for (InkChannel c : this.getSourceFormat()) {
            formatter.add(c.formatterFactory());
        }
        final String input = node.getTextContent().trim();
        int total = 0;
        char[] chars = input.toCharArray();
        boolean onemore = false;
        for(int i = 0;i<chars.length;i++){
            switch(chars[i]){
            case ',':
                total++;
                onemore = false;
                break;
            case ' ':
            case '\n':
            case '\t':
            case '\r':
                break;
            default:
                onemore = true;
            }
        }
        if(onemore) total++;
        addPoints(new PointConstructionBlock(total) {
            public void addPoints() throws InkMLComplianceException {
                Scanner stringScanner = new Scanner(input);
                Pattern pattern = Pattern.compile(",|F|T|\\*|\\?|[\"'!]?-?(\\.[0-9]+|[0-9]+\\.[0-9]+|[0-9]+)");
                int i = 0;
                while (true) {
                    String result = stringScanner.findWithinHorizon(pattern, input
                            .length());
                    if (result == null) { // finished
                        break;
                    }
                    if (result.equals(",") || i >= formatter.size()) { //if new point begins, but not all coordinates of the old one are set.
                        next();i = 0;continue;
                    }
                    set(formatter.get(i).getChannel().getName(),formatter.get(i).consume(result));
                    i++;
                }
            }
        });
    }

    @Override
    public void exportToInkML(Element parent) throws InkMLComplianceException {
        if (this.isRoot()
                && parent.getNodeName().equals("ink")
                && this.getCurrentContext() != this.getInk()
                        .getCurrentContext()) {
            this.getCurrentContext().exportToInkML(parent);
        }
        if(tainted){
            backTransformPoints();
        }
        Element traceNode = parent.getOwnerDocument().createElement("trace");
        parent.appendChild(traceNode);
        super.exportToInkML(traceNode);
        writeAttribute(traceNode, "type", this.getType().toString(),
                Type.PEN_DOWN.toString());
        if (getContinuation() != null) {
            writeAttribute(traceNode, "continuation", getContinuation()
                    .toString(), null);
        }
        writeAttribute(traceNode, "priorRef", priorRef, "");
        writeAttribute(traceNode, "brushRef", brushRef, null);
        if (duration != null)
            writeAttribute(traceNode, "duration", duration.toString(), null);
        if (timeOffset != null)
            writeAttribute(traceNode, "timeOffset", timeOffset.toString(), null);

        StringBuffer pointString = new StringBuffer();
        List<Formatter> formatter = new ArrayList<Formatter>();
        for (InkChannel c : this.getSourceFormat()) {
            formatter.add(c.formatterFactory());
        }
        for (int i = 0;i<getPointCount();i++) {
            for (int d = 0;d<formatter.size();d++) {
                pointString.append(formatter.get(d).getNext(sourcePoints[i][d]));
            }
            pointString.append(",");
        }
        pointString.deleteCharAt(pointString.length() - 1);
        traceNode.setTextContent(pointString.toString());

    }

    /**
     * Specify whether this trace is a continuation trace, and
     * if it is the case, where this trace is located in the set of continuation
     * traces, it takes the values "begin" | "end" | "middle".
     * If this trace is not a continuation trace, then null is returned 
     * 
     * @see #continuation
     * @return null | "begin" | "end" | "middle".
     */
    public InkTraceLeaf.Continuation getContinuation() {
        return this.continuation;
    }

    /**
     * Returns a list containing the {@link InkTraceView} representing the points from "from" to "to"
     * @param tw {@link InkTraceViewContainer} which shall contain the new {@link InkTraceView}
     * @param from List of integers specifing the boundaries of the points 
     * @param to List of integers specifing the boundaries of the points
     * @return list of {@link InkTraceView}s
     */
    public List<InkTraceView> getSubSet(InkTraceViewContainer tw,
            List<Integer> from, List<Integer> to) {
        final InkTraceViewLeaf tv = new InkTraceViewLeaf(this.getInk(), tw);
        tv.setTraceDataRef(this.getIdNow("t"));
        if (!from.isEmpty()) {
            tv.setFrom(from.get(0).toString());
        }
        if (!to.isEmpty()) {
            tv.setTo(to.get(0).toString());
        }
        List<InkTraceView> l = new ArrayList<InkTraceView>();
        l.add(tv);
        return l;
    }

    /**
     * Returns an {@link InkTraceViewLeaf} representing all points contained by this trace
     * @return the InkTraaceViewLeaf
     */
    public InkTraceViewLeaf createView() {
        final InkTraceViewLeaf i = new InkTraceViewLeaf(this.getInk(), null);
        i.setTraceDataRef(this.getIdNow("t"));
        return i;
    }

    /**
     * Constructing method: sets the brush reponsible to draw this trace, if it is different than the
     * brush specified by the responsible context which is accesible by {@link #getContext()}
     * It is expected the this brush is registered in the <code>definitions</code> of the {@link InkInk}
     * @param b the new brush
     */
    public void setBrush(InkBrush b) {
        this.brushRef = b.getIdNow("brush");
    }

    /**
     * Returns the type of this trace. See {@link #type} for more information
     * @see #type
     * @return "penDown" | "penUp" | "indeterminate".
     */
    public InkTraceLeaf.Type getType() {
        if (this.type == null) {
            return InkTraceLeaf.Type.PEN_DOWN;
        }
        return this.type;
    }

    
    @Override
    public boolean isView() {
        return false;
    }

    public Polygon getPolygon() {
        int[] xpoints = new int[getPointCount()];
        int[] ypoints = new int[getPointCount()];
        int x = getIndex(Name.X);
        int y = getIndex(Name.Y);
        for(int i = 0;i<xpoints.length;i++){
            xpoints[i] = (int) points[i][x];
            ypoints[i] = (int) points[i][y];
        }
        return new Polygon(xpoints, ypoints, getPointCount());
        
    }

    /**
     * Retransforms the points from the source. This discards all changes to the trace made till now.
     * This is usefull if the {@link InkCanvasTransform} has been changed and therefor the points
     * on the canvas change their location.
     * This method will notify the observers registered for {@link InkTrace#ON_CHANGE}.
     * @throws InkMLComplianceException
     */
    public void reloadPoints() throws InkMLComplianceException {
    	transform();
    }
    
    /**
     * Transforms the source points to the target points with the {@link InkCanvasTransform} reponsible for this trace.
     * This method will notify the observers registered for {@link InkTrace#ON_CHANGE}.
     * @throws InkMLComplianceException
     */
    private void transform() throws InkMLComplianceException{
        if(points == null){
            points = new double[size][sourcePoints[0].length];
        }
        getCanvasTransform().transform(sourcePoints, points,getSourceFormat(), getTargetFormat());
        notifyObserver(ON_CHANGE);
    }


    /**
     * Returns the index of the point within this trace.
     * This Method returns -1 if this point can not be found in this trace
     * 
     * @param point
     * @return int The index of the point within this trace
     */
    public int indexOfPoint(InkTracePoint point) {
        if(point instanceof ProxyInkTracePoint){
            return ((ProxyInkTracePoint)point).index();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean testFormat(InkTraceFormat canvasTraceFormat) {
        // TODO Auto-generated method stub
        return true;
    }


    /**
     * The point construction block encapsulate the construction of a trace
     * and ensures that at the end, the transformation is executed. 
     * @author emanuel
     *
     */
    public abstract class PointConstructionBlock{
        private int i = 0;
        public PointConstructionBlock(int length){
            sourcePoints = new double[length][sourceIndex.size()];
            size = length;
        }
        /**
         * In this methods all points are added using the methods {@link #set()}, {@link #next()}, and {@link #reduce()}
         * @throws InkMLComplianceException
         */
        abstract public void addPoints() throws InkMLComplianceException;
        
        /**
         * sets the value of the Channel name to the current point
         * @param name Channel name
         * @param value value of the point's channel
         */
        public void set(Name name, double value) {
            sourcePoints[i][sourceIndex.get(name)] = value;
        }
        
        /**
         * proceed to the next point. Unset, intermittent channels are filled with the value "unknown"
         * which internally is represented by Double.NaN
         */
        public void next(){
            if (i>=size){
                throw new IndexOutOfBoundsException("Index "+i+" is larger than Bound: "+size);
            }
            i++;
            if(i<size){
                for(int c = 0;c<sourcePoints[i-1].length;c++){
                    sourcePoints[i][c] = Double.NaN;
                }
            }
        }
        /**
         * If a point can not be added, because it is corrupt or so, this method can bee callen, then the
         * the length of the trace specified in the constructor is reduced by 1
         */
        public void reduce(){
            size--;
        }
        
        private void finish() {
        }
    }



    /**
     * Accepts an anonymous class implementing a {@link PointConstructionBlock} which when callen 
     * the method {@link PointConstructionBlock#addPoints()} adds the points.
     * This method will notify the observer registerd for {@link InkTrace#ON_CHANGE}.
     * @param pointConstructionBlock
     * @throws InkMLComplianceException 
     */
    public void addPoints(PointConstructionBlock block) throws InkMLComplianceException {
        block.addPoints();
        block.finish();
        transform();//will call observers
    }

    public void accept(TraceVisitor visitor) {
        visitor.visit(this);
    }


}
