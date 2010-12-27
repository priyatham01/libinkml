package ch.unibe.inkml;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

import org.w3c.dom.Element;

import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;

/**
 * The InkTraceLike class is an abstract class specifying some behavior which 
 * InkTrace and InkTraceView class has in common.
 * Please note that the comments of the methods apply to the two different 
 * inheriters according to there nature.
 * 
 * This class introduces methods to handle a hierarchy which in traces and trace views
 * can be found. This class is actually a template class with parameter C which correspond
 * to the class of parent object in this hierarchy. For traces its an InkTrace, for trace views
 * it is an InkTraceView.
 * 
 * A second behavior which is defined by this class is the handling of context.
 * Context (<code>InkContext</code>)
 * 
 * @author emanuel
 *
 * @param <C> Type of the parent of such a trace like class. Mostly the same 
 * as the direct descendant of this class
 */


public abstract class InkTraceLike<C extends InkTraceLike<C>> extends InkAnnotatedElement implements Iterable<InkTracePoint>{

	
	private InkContext currentContext;
	
	private String contextRef;
	
	/**
	 * Constructor with enclosing ink object.
	 * @param ink
	 */
	public InkTraceLike(InkInk ink) {
		super(ink);
		this.initialize();
	}

	/**
	 * Constructor with enclosing ink object and id.
	 * @param ink
	 * @param id
	 * @throws InkMLComplianceException 
	 */
	public InkTraceLike(InkInk ink, String id) throws InkMLComplianceException {
		super(ink, id);
		this.initialize();
	}
	
	/**
	 * Initializes the InkTrace generating a link to the context, which is active in the InkML document
	 * at the moment of the object construction.
	 */
	protected void initialize(){
		this.setCurrentContext(this.getInk().getCurrentContext());
	}
	
	/**
	 * @return True if this object is not a container of other InkTraceLike objects
	 */
    public abstract boolean isLeaf();
    
    /**
     * Sets the specified context as the "local context" of this object.
     * This is not changing the current global context of the ink document.
     * However overwrites its value. 
     * @param context
     */
    public void setContext(InkContext context) {
		this.contextRef = context.getIdNow("context");
	}

    
    /**
     * @return True if this object has a local context applied overwriting the global context.
     */
	public boolean hasLocalContext() {
		return this.contextRef != null;
	}
	
	/**
	 * @return Returns the local context of this element. Null if there no local context is specified.
	 */
	public InkContext getLocalContext(){
		if(this.hasLocalContext()){
			return (InkContext) this.getInk().getDefinitions().get(this.contextRef);
		}
		return null;
	}
    
	/**
	 * Returns the context (global or local), which is applicable to this object.
	 * @return
	 */
    public InkContext getContext() {
		if(this.hasLocalContext()){
			return getLocalContext();
		}else if(!this.isRoot()){
			return this.getParent().getContext();
		}else{
			return this.getCurrentContext();
		}
	}
	
    /**
     * @return The current global Context object.
     */
    public InkContext getCurrentContext() {
		return this.currentContext;
	}
    
    /**
     * Sets the current global context of this object (which can be overwritten by the local context).
     * Each TraceLike element must have a currentContext. If no one is specified then the DefaultContext have
     * to be set.
     */
	public void setCurrentContext(InkContext context) {
		this.currentContext = context;
	}
    
    /**
     * Returns the InkTraceLike object which contains this object.
     * InkTraceLike objects can be nested. InkTraceGroups (which are InkTraceLike objects) can contain
     * InkTrace's and InkTraceGroups.
     * So can InkTraceView's (which are InkTraceLike objects) contain InkTraceViews.
     * This hierarchy is not to compare with the XML hierarchy in InkML, allthough it is represented
     * this way in InkML. 
     * @return The InkTraceLike object which contains this object or Null if there is no parent.
     */
    public abstract C getParent();

    /**
     * Return true if this element is root of a trace tree, or a trace view tree.
     * This means, it is directly contained by the ink element in the InkML XML.
     * @return True if this element has no parent, and therefore is root of a trace tree. 
     */
	public boolean isRoot(){
    	return this.getParent() == null;
    }
    
	/**
	 * @return The root object of the trace tree or the trace view tree.
	 */
	public abstract C getRoot();
	

    public void exportToInkML(Element node) throws InkMLComplianceException {
    	super.exportToInkML(node);
    	writeAttribute(node, "contextRef", this.contextRef, null);
    }

    public void buildFromXMLNode(Element node) throws InkMLComplianceException {
    	super.buildFromXMLNode(node);
    	this.setCurrentContext(this.getInk().getCurrentContext());
    	this.contextRef = loadAttribute(node, "contextRef", null);
    }


  
    /**
     * @return True if this is object inherits from InkTraceView rather than InkTrace
     */

    public abstract boolean isView();
    
    
    /**
     * Tests if this TraceViewElement has the specified annotation of one of its precedor;
     * @param name
     * @param value
     */
    public boolean testAnnotationTree(String name, String value) {
        return testAnnotation(name , value) || (!this.isRoot() && this.getParent().testAnnotationTree(name, value));
    }
    
    
    

    public List<InkTracePoint> getPoints(){
        return getPoints("1",null);
    }
    
    public Polygon getPolygon() {
        return InkTracePoint.getPolygon(this.getPoints());
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
     * @param from
     * @param to
     * @return
     */
    abstract public List<InkTracePoint> getPoints(String from, String to);

    /**
     * @return
     */
    public abstract Iterable<InkTracePoint> pointIterable();

    /**
     * @param i
     * @return
     */
    public abstract InkTracePoint getPoint(int i);

    /**
     * @return
     */
    public abstract int getPointCount();

    /**
     * @return
     */
    public abstract Timespan getTimeSpan();

    /**
     * @return
     */
    public abstract TraceBound getBounds();

    /**
     * @param visitor
     */
    public abstract void accept(TraceVisitor visitor);

}
