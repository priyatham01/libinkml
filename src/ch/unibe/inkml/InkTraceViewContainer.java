package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observable;
import ch.unibe.eindermu.utils.Observer;
import ch.unibe.inkml.util.AbstractTraceFilter;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceViewFilter;
import ch.unibe.inkml.util.TraceVisitor;
import ch.unibe.inkml.util.TraceViewTreeManipulationException;


public class InkTraceViewContainer extends InkTraceView implements Observer {

	public static final String ID_PREFIX = "tg";
	public static final String INKML_NAME = "traceGroup";
	
    private List<InkTraceView> content =  new ArrayList<InkTraceView>();
    private Timespan timespan;
    private TraceBound bounds;
	
	 /**
     * Constructor to create an empty InkTraceView.
     * @param ink Ink is the ink document, this trace view belongs to.
     * @param parent Parent is the parent TraceView in the tree, if this element is root, parent must be null.
     */
	public InkTraceViewContainer(InkInk ink, InkTraceViewContainer parent) {
		super(ink, parent);
	}

	/**
     * Constructor to create an empty InkTraceView.
     * @param ink Ink is the ink document, this trace view belongs to.
     */
	public InkTraceViewContainer(InkInk ink) {
		super(ink);
	}

	
	public void accept(TraceVisitor visitor){
		visitor.visit(this);
	}
	
	public void delegateVisitor(TraceVisitor visitor){
		for(InkTraceView view : this.getContent()){
			view.accept(visitor);
		}
		
	}
	
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		
		for(Node childnode = node.getFirstChild();childnode != null;childnode = childnode.getNextSibling()){
			if(childnode.getNodeType() != Node.ELEMENT_NODE  
			        || !(childnode.getNodeName().equals(InkTraceViewLeaf.INKML_NAME) || childnode.getNodeName().equals(INKML_NAME))){
			    if(childnode.getNodeName().equals(InkTraceLeaf.INKML_NAME)){
			        throw new InkMLComplianceException("libInkML does not support trace elements in trace groups mixed with traceView elements");
			    }
				continue;
			}
			Element child = (Element) childnode;
			//create list of child elements if needed. 
			if(content == null){
				content = new ArrayList<InkTraceView>();
			}
			InkTraceView v = InkTraceView.createTraceView(getInk(), this, child);
			try {
                addTrace(v);
            } catch (TraceViewTreeManipulationException e) {
                e.printStackTrace();
                throw new InkMLComplianceException("There has been a ViewTreeManipulation exception, this should not happen.");
            }
		}
	}
	
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		if(this.isEmpty()){
			return;
		}
		Element traceViewNode = parent.getOwnerDocument().createElement(INKML_NAME);
        parent.appendChild(traceViewNode);
        prepairForExport(parent);
        
		super.exportToInkML(traceViewNode);
		if(!this.content.isEmpty()){
			for(InkTraceView child : this.content){
				child.exportToInkML(traceViewNode);
			}
		}
	}

	/**
	 * Adds each InkTraceView of the list to this container 
	 * @param viewList list of InkTraceView

	 * @throws TraceViewTreeManipulationException 
	 */
	public void addTraces(Collection<InkTraceView> viewList){
		if(this.content == null){
			this.content = new ArrayList<InkTraceView>();
		}
		//collect existing parents
		HashMap<InkTraceViewContainer,Set<InkTraceView>> map = new HashMap<InkTraceViewContainer, Set<InkTraceView>>();
        for(InkTraceView v : viewList){
            if(v.getParent() != null && v.getParent() != this){
                if(!map.containsKey(v.getParent())){
                    map.put(v.getParent(), new HashSet<InkTraceView>());
                }
                map.get(v.getParent()).add(v);
            }
        }
        //Send event befor removing children
        for(InkTraceViewContainer parent : map.keySet()){
            TreeEvent e = new TreeEvent(InkTraceView.ON_CHILD_PRE_REMOVE);
            e.target = parent;
            e.children.addAll(map.get(parent));
            parent.notifyFor(ON_CHANGE, e);
            
            //acctually removing them
            for(InkTraceView v : map.get(parent)){
                parent.acctuallyRemove(v);
            }
            
            e.aspect = InkTraceView.ON_CHILD_REMOVE;
            parent.notifyFor(ON_CHANGE, e);
        }
        //acctually then adding children
        for(InkTraceView v : viewList){
            acctuallyAdd(v);
        }

        //send event to notify the adding of the children
        {
            TreeEvent e = new TreeEvent(InkTraceView.ON_CHILD_ADD);
            e.target = this;
            e.children.addAll(viewList);
            notifyFor(ON_CHANGE, e);
        }
		
	}
	
	/**
	 * Adds a trace view element to this trace view container.
	 * If the new trace view is empty (no content, and not refering to trace) it will be droped.
	 * If the new trace view has allready a parent, it is removed from this parent.
	 * @param tv new trace view added to this container.
	 * @throws TraceViewTreeManipulationException 
	 */
	public void addTrace(InkTraceView tv) throws TraceViewTreeManipulationException {
	    List<InkTraceView> l = new ArrayList<InkTraceView>();
	    l.add(tv);
	    addTraces(l);
	} 
	

	private void acctuallyAdd(InkTraceView tv) throws TraceViewTreeManipulationException{
        if(tv == null){
            return;
        }
        if(tv.getParent() != null && tv.getParent() != this){
            tv.getParent().acctuallyRemove(tv);
        }
        if(!this.content.contains(tv)){
            if(tv.getParent() != this){
                tv.setParent(this);
            }
            this.content.add(tv);
            Collections.sort(content);
        }
    }

    /**
     * Creates a child container of this object, which will contain all view
     * specified by the parameter
     * @param viewList list of views which will be children of the container returned
     * @return new container which is child of this object and parent of the views in the list
     * @throws TraceViewTreeManipulationException 
     */
    public InkTraceView createChildContainer(Collection<InkTraceView> viewList) throws TraceViewTreeManipulationException {
    	InkTraceViewContainer tv = new InkTraceViewContainer(this.getInk());
    	tv.addTraces(viewList);
    	acctuallyAdd(tv);
    	{
        	TreeEvent e= new TreeEvent(InkTraceView.ON_CHILD_ADD);
            e.target = this;
            e.children.add(tv);
            notifyFor(ON_CHANGE, e);
    	}
    	
    	return tv;
    }

    /**
     * removes this view and all its children from the view tree.
     * @throws TraceViewTreeManipulationException 
     */
    public void remove() throws TraceViewTreeManipulationException {
        if(this == getRoot()){
            System.err.println("can not remove root view");
            return;
        }
        
        TreeEvent e= new TreeEvent(InkTraceView.ON_CHILD_PRE_REMOVE);
        e.target = getParent();
        e.children.add(this);
        e.target.notifyObserver(ON_CHANGE, e);
        
        getParent().acctuallyRemove(this);
        acctuallyMakeEmpty();
        
        e.aspect = InkTraceView.ON_CHILD_REMOVE;
        e.target.notifyObserver(ON_CHANGE, e);
    }

    /**
     * Just removes the specified view from the internal list.
     * This method should not be called from outside, 
     * just from within a manipulation transaction.
     * Make sure that the child you will remove does not contain any children,
     * otherwise they will stay in memory as cyclic dependencies
     * @param inkTraceView
     */
    void acctuallyRemove(InkTraceView inkTraceView) {
    	if(content.contains(inkTraceView)){
    		this.content.remove(inkTraceView);
    	}
    	inkTraceView.setParent(null);
    	inkTraceView.unregisterFor(Observable.ON_ALL, this);
    }
    
    private void acctuallyMakeEmpty(){
        while(!content.isEmpty()){
            InkTraceView child = content.get(0);
            if(!child.isLeaf()){
                ((InkTraceViewContainer)child).acctuallyMakeEmpty();
            }
            acctuallyRemove(child);
        }
    }
    

    /**
     * removes this view from the view tree. Its children are transfered
     * to the parent.
     * @throws TraceViewTreeManipulationException 
     */
    public void resect() throws TraceViewTreeManipulationException {
    	if(this.isRoot()){
              System.err.println("can not resect root view");
              return;
    	}
    	getParent().addTraces(new HashSet<InkTraceView>(content));
    	remove();
    }

    /**
     * completely removes this view and all its children from the view tree.
     * It also removes all traces, that are reffered by these views. 
     * It is actually used to remove traces. 
     * @throws TraceViewTreeManipulationException 
     */
    public void removeCompletely() throws TraceViewTreeManipulationException {
    	if(isRoot()){         
    	    System.err.println("can not completely remove the root view");
    		return;
    	}
    	for(InkTraceViewLeaf leaf : getFlattenedTraceLeafs(null)){
    	    leaf.removeCompletely();
    	}
    	remove();
    }

    /**
     * Returns the list of contained views 
     * @return requested list
     */
    public List<InkTraceView> getContent() {
    	return new ArrayList<InkTraceView>(this.content);
    }

    /**
     *  
     * Returns the iterator over all contained views
     * this method make InkTraceViewContainer comply the Interable interface
     */
    public Iterator<InkTracePoint> iterator() {
        return this.pointIterable().iterator();
    }

    /**
     * Returns the number of elements contained by this object
     * @return number
     */
    protected int getContentLength() {
    	return this.content.size();
    }

    /**
     * Returns the list of all traceViewLeafs within all successors below this object
     * @return requested list
     */
    public List<InkTraceViewLeaf> getFlattenedTraceLeafs(TraceViewFilter filter) {
    	List<InkTraceViewLeaf> s = new ArrayList<InkTraceViewLeaf>();
    	if(filter != null && !filter.pass(this)){
    		return s;
    	}
    	for(InkTraceView v:this.content){
    		if(filter != null && !filter.pass(v)){
    			continue;
    		}
    		if(v.isLeaf()){
    			s.add((InkTraceViewLeaf) v);
    		}else{
    			s.addAll(((InkTraceViewContainer) v).getFlattenedTraceLeafs(filter));
    		}
    	}
    	return s;
    	
    }

    /**
     * Returns the list of all successors bewlow this object
     * @param filter 
     * @return requested list
     */
    public List<InkTraceView> getFlattenedViews(TraceViewFilter filter) {
    	List<InkTraceView> result = new ArrayList<InkTraceView>();
    	if(filter != null && !filter.pass(this)){
    		result.add(this);
    	}
    	for(InkTraceView v : this.content){
    		if(filter != null && !filter.pass(v)){
    			continue;
    		}
    		if(!v.isLeaf()){
    			result.addAll(((InkTraceViewContainer) v).getFlattenedViews(filter));
    		}else{
    			result.add(v);
    		}
    	}
    	return result;
    }

    @Override
    public List<InkTracePoint> getPoints(String from, String to) {
        List<InkTracePoint> pointList= new ArrayList<InkTracePoint>();
        String[] fromSplitted = from.split("\\.", 1);
        String[] toSplitted = null;
        int toOnThisLevel = this.getContentLength();
        if(to != null){
            toSplitted = to.split("\\.", 1);
            toOnThisLevel = Integer.parseInt(toSplitted[0]);
        }
        int fromOnThisLevel = Integer.parseInt(fromSplitted[0]);
        int childrenIndex = 0;
        String fromForChildren;
        String toForChildren;
        for(InkTraceView element : this.content){
            childrenIndex++;
            if(childrenIndex < fromOnThisLevel || childrenIndex > toOnThisLevel){
                continue;
            }
            fromForChildren = "1";
            toForChildren = null;
            if(childrenIndex == fromOnThisLevel && fromSplitted.length>1){
                fromForChildren = fromSplitted[1];
            }
            if(toSplitted != null && childrenIndex == toOnThisLevel && toSplitted.length>1){
                toForChildren = toSplitted[1];
            }
            pointList.addAll(element.getPoints(fromForChildren,toForChildren));
        }
        return pointList;
    }

    @Override
    public InkTracePoint getPoint(int i) {
        int offset = 0;
        for(InkTraceView view : this.content){
            if(view.getPointCount() + offset > i ){
                return view.getPoint(i-offset);
            }
            offset += view.getPointCount();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getPointCount() {
        int res = 0;
        for(InkTraceView view : this.content){
            res += view.getPointCount();
        }
        return res;
    }

    @Override
    public Iterable<InkTracePoint> pointIterable(){
        return new Iterable<InkTracePoint>() {
            Iterator<InkTracePoint> it  = new Iterator<InkTracePoint> (){
                private Iterator<InkTraceView> traceIterator = getContent().iterator();
                private Iterator<InkTracePoint> current = null; 
                public boolean hasNext() {
                    while(current == null || !current.hasNext()){
                        if(traceIterator.hasNext()){
                            current = traceIterator.next().pointIterable().iterator();
                        }else{
                            return false;
                        }
                    }
                    return true;
                }
                public InkTracePoint next() {
                    return current.next();
                }
    
                public void remove() {
                    current.remove();
                }
            };
            public Iterator<InkTracePoint> iterator() {
                return it;
            }
        };
    }

    @Override
    public TraceBound getBounds() {
    	if(bounds == null){
    		for(InkTraceView v : this.content){
    			if(bounds == null){
    				bounds = new TraceBound(v.getBounds());
    			}else{
    				bounds.add(v.getBounds());
    			}
    		}
    	}
    	return bounds;
    }

    @Override
    public Timespan getTimeSpan() {
    	if(timespan == null){
    		if(isEmpty()){
    			return null;
    		}
    		for(InkTraceView v : this.content){
    			if(timespan == null){
    				if(v.getTimeSpan() != null){
    					timespan = new Timespan(v.getTimeSpan());
    				}
    			}else{
    				timespan.add(v.getTimeSpan());
    			}
    		}
    	}
    	return timespan;
    }

    public InkCanvas getCanvas() {
    	return this.getContext().getCanvas();
    }

    /**
	 * Test if this container contains exactly the same elements as provided by this list
	 * @param other
	 * @return true of false
	 */
	public boolean containsOnly(List<InkTraceView> other) {
		return content.containsAll(other) && other.size() == content.size();
	}
	

	@Override
	public boolean isLeaf() {
		return false;
	}

	/**
	 * Return true if no InkTraceView is contained, 
	 * This is a reason to delete this element 
	 * {@inheritDoc}
	 */
    public boolean isEmpty(){
    	return this.content == null || this.content.isEmpty();
    }
    

    @Override
    public void notifyFor(Aspect event, Object subject) {
        if(event == ON_DATA_CHANGE || event == ON_CHANGE){
            
            if(!content.isEmpty()){ 
                //The reason for this test is:
                //An empty content is actually not a valid state, such a container must be removed.
                //However it is possible that such container might occure during complex operation (namely resect())
                //so if in these cases the bound and timesspan is recalculated, this leads to a new order of the
                //element in the parent container. This again is problematic during an operation, so the bounds
                //must be kept in its last state if content is emtpy. such a container will be removed
                //anyway.
                bounds = null;
                timespan = null;
            }
            if( (subject instanceof TreeEvent && ((TreeEvent)subject).aspect != ON_NODE_CHANGE && ((TreeEvent)subject).aspect != ON_CHILD_PRE_REMOVE)
                 || event ==  ON_DATA_CHANGE){
                Collections.sort(content);
            }
            notifyObserver(event, subject);
        }
        
    }
}
