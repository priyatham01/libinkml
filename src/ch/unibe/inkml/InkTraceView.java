package ch.unibe.inkml;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observable;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;
import ch.unibe.inkml.util.ViewTreeManipulationException;


public abstract class InkTraceView extends InkTraceLike<InkTraceView> implements Observable, Comparable<InkTraceView> {

 	/**
	 * This aspect marks an event which occures if the data represented by this View changes,
	 * but the View tree stays constant.
	 */
    public static final Aspect ON_DATA_CHANGE = new Aspect();

    /**
     * Indicates that children will be removed from the subject of the event.
     * This event is submitted as subject of an ON_CHANGE event.
     * Be sure to emit a ON_CHILD_REMOVE event after you have removed the children.
     * This new event must use exactly the same object.
     * 
     * This is done this way because for JTree information must be collected
     * which is only available befor child removal. This information must however be
     * used AFTER the child has been remove. So to keep track of this process
     * it must be guaranteed that the same event object is used.  
     */
    public static final Aspect ON_CHILD_PRE_REMOVE = new Aspect();
    
    /**
     * Indicates that a child has been added to the subject of the event.
     * This event is submitted as subject of an ON_CHANGE event.
     */
    public static final Aspect ON_CHILD_ADD = new Aspect();

    /**
     * Indicates that one single node has been changed (its annotations)
     * but the hierarchy of the tree is the same.
     * This event is submitted as subject of an ON_CHANGE event.
     */
    public static final Aspect ON_NODE_CHANGE = new Aspect();

    /**
     * Indicates that one or more children has been removed from the subject of the event.
     * This event is submitted as subject of an ON_CHANGE event.
     * This event type is used in combination with ON_CHILD_PRE_REMOVE.
     */
    public static final Aspect ON_CHILD_REMOVE = new Aspect();

    /**
     * This event occures if any aspect of the view tree changes, including
     * annotation removales, creations, or editions.
     * The subject of this event is a more detailed event containing aspects
     * suc as ON_CHILD_REMOVE, ON_NODE_CHANGE, ON_CHILD_ADD, ON_CHILD_PRE_REMOVE
     */
    public static final Aspect ON_CHANGE = new Aspect();
	
	/**
	 * The parent element within the TraceView tree. If this element has no parent (is the root of the tree)
	 * this property remains null.
	 */
	private InkTraceViewContainer parent;
	
	/**
	 * Creates a TraceView element from a InkML(XML)-node. This factory method distinguish between 
	 * a leaf element (which refer to a trace) and a non-leaf element (which contains other TraceViews).  
	 * @param ink Ink is the ink document, this trace view belongs to.
	 * @param parent Parent is the parent TraceView in the tree, if this element is root, parent must be null.
	 * @param node the InkML(XML)-node representing this TraceView.
	 * @return The newly factored InkTraceView  
	 * @throws InkMLComplianceException
	 */
	public static InkTraceView createTraceView(InkInk ink, InkTraceViewContainer parent, Element node) throws InkMLComplianceException{
		InkTraceView result = null;
		if(node.getNodeName().equals(InkTraceViewContainer.INKML_NAME)){
		    if(node.getElementsByTagName(InkTraceLeaf.INKML_NAME).getLength()>0){
		        throw new InkMLComplianceException("libInkML does not support trace elements in trace groups mixed with traceView elements");
		    }
		    result = new InkTraceViewContainer(ink,parent);
		}else
		if(node.getNodeName().equals(InkTraceViewLeaf.INKML_NAME) && node.hasAttribute(InkTraceViewLeaf.INKML_ATTR_TRACEDATA_REF)){ // is allways the case
			String traceDataRef = node.getAttribute(InkTraceViewLeaf.INKML_ATTR_TRACEDATA_REF).replace("#", "");
			InkTraceLike l = (InkTraceLike)ink.getDefinitions().get(traceDataRef);
			if(l.isView()){
				throw new InkMLComplianceException("libInkML does not support traceView referencing to traceViews yet.");
			}else{
				result = new InkTraceViewLeaf(ink,parent);
			}
		}else 
	    if(node.getNodeName().equals(InkTraceViewLeaf.INKML_NAME)){
		    //System.err.println("Warning: Depricated use of traceView as container element. Its treated as traceGroup. Please use traceGroup instead.");
			result = new InkTraceViewContainer(ink,parent);
		}else{
		    throw new InkMLComplianceException(String.format("Unexpected node with name '%s' discovered in traceView context.",node.getNodeName()) );
		}
		result.buildFromXMLNode(node);
		return result;
	}
	
	
    
    public static InkTraceView getCommonAncestor(List<? extends InkTraceView> traces){
        List<InkTraceView> l = new ArrayList<InkTraceView>();
        {
            InkTraceView current = traces.get(0);
            while(current != null) {
                l.add(current);
                current = current.getParent();
            }
        }
        int pi = 0;
        for(int i = 1;i<traces.size();i++){
            InkTraceView current = traces.get(i);
            while(current != null) {
                if(l.contains(current)){
                    int npi = l.indexOf(current);
                    pi = Math.max(npi, pi);
                    break;
                }
                current = current.getParent();
            }
        }
        return l.get(pi);
    }
	
	
	/**
	 * Constructor to create an empty InkTraceView.
	 * @param ink Ink is the ink document, this trace view belongs to.
     * @param parent Parent is the parent TraceView in the tree, if this element is root, parent must be null.
	 */
	public InkTraceView(InkInk ink,InkTraceViewContainer parent) {
		this(ink);
		this.setParent(parent);
		if(parent != null){
			parent.addTrace(this);
		}
	}

	/**
     * Constructor to create an empty InkTraceView.
     * @param ink Ink is the ink document, this trace view belongs to.
     */
	public InkTraceView(InkInk ink) {
		super(ink);
	}

	
	/**
     * Of this and an other TraceLike objects the nearest common ancestor is returned.
     * If both traceLike objects are not in the same tree, null is returned.
     * @param other The other trace like object
     * @return The nearest common ancestor, null if there is none
     */
    public InkTraceView getCommonAncestor(InkTraceView other) {
        List<InkTraceView> l = new ArrayList<InkTraceView>();
        l.add(this);
        l.add(other);
        return InkTraceView.getCommonAncestor(l);
    }
    
	
    /**
	 * Sets the view tree parent to this view. If this view
	 * has already a parent, this view gets removed from that former parent.
	 * However, it will not be added to the new parent, this has to be done explicitly. 
	 * @param parent
	 */
	protected void setParent(InkTraceViewContainer parent) {
	    if(parent == this.parent){
			return;
		}
		this.parent = parent;
		if(parent != null){
		    registerFor(ON_DATA_CHANGE, parent);
		    registerFor(ON_CHANGE,parent);
		}
	}

	@Override
	public void annotate(String key,String value){
	    if(!containsAnnotation(key) || !value.equals(getAnnotation(key))){
	        super.annotate(key, value);
	        notifyObserver(ON_CHANGE,new TreeEvent(ON_NODE_CHANGE,this));
	        //System.err.println("Change sended");
	    }
	}
	
	@Override
	public void removeAnnotation(String key){
	    if(containsAnnotation(key)){
	        super.removeAnnotation(key);
            notifyObserver(ON_CHANGE,new TreeEvent(ON_NODE_CHANGE,this));
	    }
	}


	/**
	 * If a ViewElement is exportet, its context must first be stored (if not allready done).
	 * @param parent xml node
	 * @throws InkMLComplianceException
	 */
	protected void prepairForExport(Element parent) throws InkMLComplianceException{
		if(this.isRoot() && parent.getNodeName().equals("ink") && this.getCurrentContext() != this.getInk().getCurrentContext()){
			this.getCurrentContext().exportToInkML(parent);
		}
	}
	
	/**
	 * Calculates the distance between the represented traces and the point p.
	 * @param p Point
	 * @return distance values
	 */
	public double distance(Point2D p) {
        return InkTracePoint.distanceToPoint(this.getPoints(), p);
    }

    /**
     * @param inkTraceView
     * @return
     */
    public double distance(InkTraceView inkTraceView) {
        if(this == inkTraceView){
            return 0;
        }
        return InkTracePoint.distanceTraceToTrace(this.getPoints(), inkTraceView.getPoints());
    }
	
	/**
	 * Calculates the center of gravity of the traces represented by this point.
	 * @return center of gravity point
	 */
	public Point2D getCenterOfGravity() {
        return InkTracePoint.getCenterOfGravity(this.getPoints());
    }


	/**
	 * Returns true if this object is a leaf in the TraceViewTree (leafs are represented by the InkTraceViewLeaf class).
	 */
	public abstract boolean isLeaf();

	/**
	 * return true if this element is the root in a TraceView tree.
	 */
	public boolean isRoot() {
		return this.getParent() == null;
	}


	@Override
    public boolean isView() {
        return true;
    }
	
	/**
	 * return parent of this element in the TraceView tree. If this element is root <code>null</code> will be returned.
	 */
	public InkTraceViewContainer getParent() {
		return this.parent;
	}

	
	/**
	 * Returns the bounding rectangle of the traces represented by this TraceView.
	 * if no traces are represented, null will be returned. 
	 */
	public abstract TraceBound getBounds();

	/**
	 * Return the bounding time span of the traces represendted by this TraceView.
	 * If no traces are represented, null will be returned.
	 */
    public abstract Timespan getTimeSpan();
	
	/**
	 * If a contextRef attribute is given in this element, or in a ancestore element 
	 * then it overrides the context of the referenced trace data.
	 * If it is not given null will be returned. Then context of the referenced trace data is in charge. 
	 * 
	 * @return
	 */
	public InkBrush getBrush() {
		if(this.hasLocalContext() && this.getLocalContext().hasBrush()){
			return this.getLocalContext().getBrush();
		}
		else if(!this.isRoot()){
			return this.getParent().getBrush();
		}else{
			return null;
		}
	}

    /**
     * Compairs position within the chronological order of the traces.
     * The relevant time is the time of the first point of all traces represented by the TraceViews.
     * If this traceView is written earlier than the traceView 'o' a value >= 1 is returned.
     * If the other TraceView is written erlier, a value <= -1 is returned. If the are started at the same
     * time 0 is returned. 
     */
	public int compareTo(InkTraceView o) {
	    Timespan t1 = getTimeSpan(),t2=o.getTimeSpan();
		if(t1 == null){
			return -1;
		}else if(t2 == null){
			return 1;
		}
		int res = (int)((t1.start - t2.start) * 100); 
		if(res== 0 && o != this){
		    return hashCode() - o.hashCode();
		}
        return res;
	}
	
	/**
	 * Returns the root of the TraceView tree.
	 */
	public InkTraceView getRoot() {
		if(this.isRoot()){
			return this;
		}
		return this.getParent().getRoot();
	}

	/**
	 * @return true if the TraceView does no represent a trace.
	 */
	public abstract boolean isEmpty();
    

	/**
	 * Tree events are used to indicate manipulations of the tree.
	 * @author emanuel
	 *
	 */
	public class TreeEvent implements Comparable<TreeEvent>{
	    /**
	     * Type of the event.
	     */
		public Aspect aspect;
		/**
		 * subject of the event.
		 */
		public InkTraceView target;
		
		/**
		 * removed or added child tracesviews 
		 */
		public Set<InkTraceView> children = new HashSet<InkTraceView>();
		
		/**
		 * constructor
		 * @param event type
		 * @param subject 
		 */
		public TreeEvent(Aspect event) {
			aspect = event;
		}
		
		/**
         * @param onNodeChange
         * @param inkTraceView
         */
        public TreeEvent(Aspect event, InkTraceView subject) {
            this(event);
            target = subject;
        }

        /**
		 * @see java.lang.Object.hashcode
		 */
		public int hashCode(){
		    if(target == null){
		        return aspect.hashCode();
		    }else{
		        return aspect.hashCode() ^ target.hashCode() ^ children.hashCode();
		    }
		}
		
		/**
		 * compares two tree events and returns 0 if the have
		 * the same subject and aspect.
		 * @param o other three event
		 * @return 0 if this and o are equal
		 */
		public int compareTo(TreeEvent o) {
			return hashCode() - o.hashCode();
		}
	}
	
	/**
	 * Removes this TraceView and all its children from the traceView tree.
	 * If <code>this</code> is the root, nothing happens
	 * @throws ViewTreeManipulationException 
	 */
	public abstract void remove() throws ViewTreeManipulationException;

	/**
	 * Removes this TraceView, all its children, and all their referenced traces from the document.
     * If <code>this</code> is the root, nothing happens
	 * @throws ViewTreeManipulationException
	 */
    public abstract void removeCompletely() throws ViewTreeManipulationException;

    @Override
    public abstract InkTracePoint getPoint(int i);
    
    @Override
    public abstract int getPointCount();

    @Override
    public abstract void accept(TraceVisitor visitor);
}
