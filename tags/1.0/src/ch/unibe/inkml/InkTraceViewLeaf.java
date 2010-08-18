package ch.unibe.inkml;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omg.CORBA.Bounds;
import org.w3c.dom.Element;

import ch.unibe.eindermu.euclidian.Vector;
import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observer;
import ch.unibe.inkml.InkTraceView.TreeEvent;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;
import ch.unibe.inkml.util.ViewTreeManipulationException;

public class InkTraceViewLeaf extends InkTraceView {
    /**
     * The trace or trace group represented
     */
    private String traceDataRef;
    
    /**
     * The index of the first point that this <traceView> element annotates.
     * Required: no, Default: the index of the first referenced point.
     * If the represented tracedataref is a trace group then it indicates the first trace/trace group contained by trace group.
     * Default is 1
     */ 
    private String from;
    /**
     * The index of the last point in the trace or trace group that this <traceView> element annotates.
     * Required: no, Default: the index of the last referenced point.
     */
    private String to;

	
	/** 
	 * @see InkTraceView#InkTraceView
	 */
	public InkTraceViewLeaf(InkInk ink, InkTraceViewContainer parent) {
		super(ink, parent);
	}

	/**  
	 * {@inheritDoc}
	 */
    public void buildFromXMLNode(Element node) throws InkMLComplianceException {
        super.buildFromXMLNode(node);
        if(!node.hasAttribute("traceDataRef")){
            throw new InkMLComplianceException("A TraceView (represented by InkTraceViewLeaf) must contain the 'traceDataRef' attribute.");
        }
        setTraceDataRef(node.getAttribute("traceDataRef").replace("#", ""));

        if(node.hasAttribute("from")){
            this.from = node.getAttribute("from");
        }
        if(node.hasAttribute("to")){
            this.to = node.getAttribute("to");
        }
     }
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void exportToInkML(Element parent) throws InkMLComplianceException {
        if(getTraceDataRef()==null){
            return;
        }
        Element traceViewNode = parent.getOwnerDocument().createElement("traceView");
        parent.appendChild(traceViewNode);
        prepairForExport(parent);
        super.exportToInkML(traceViewNode);
        writeAttribute(traceViewNode,"traceDataRef",getTraceDataRef(),"");
        if(!getFrom().equals("1")){
            writeAttribute(traceViewNode,"from",getFrom(),"1");
        }
        if(getTo() != null){
            writeAttribute(traceViewNode,"to",getTo(),"");
        }
    }
    
    /**
     * Set the 'to' property
     * @see InkTraceViewLeaf#to
     * @param string
     */
    public void setTo(String string) {
        this.to = string;
    }
    
    /**
     * Set the 'from' property
     * @see InkTraceViewLeaf#from
     * @param string
     */
    public void setFrom(String string) {
        this.from = string;
    }
    
    public void setTraceDataRef(String id) {
        this.traceDataRef = id;
        getTrace().registerFor(InkTraceView.ON_DATA_CHANGE, new Observer() {
            @Override
            public void notifyFor(Aspect event, Object subject) {
                notifyObserver(ON_DATA_CHANGE,subject);
            }
        });
    }
    
    String getTraceDataRef() {
        return traceDataRef;
    }
    
    public String getFrom() {
        if(this.from != null){
            return this.from;
        }
        return "1";
    }

    public String getTo() {
        return this.to;
    }
    
    protected List<Integer> getFromList() {
        List<Integer> l = this.getFromToList(this.from);
        return l;
    }

    protected List<Integer> getToList() {
        return this.getFromToList(this.to);
    }
    private List<Integer> getFromToList(String fromTo){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(String i : fromTo.split(":")){
            if(!i.isEmpty()){
                try{
                    list.add(Integer.parseInt(i));
                }catch(NumberFormatException f){
                    
                }
            }
        }
        return list;
    }
	
	

	
	public boolean isLeaf(){
		return true;
	}
	
    public void drawPolyLine(Graphics2D g) {
        Polygon p = this.getPolygon();
        g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
    }
	
    @SuppressWarnings("unchecked")
    public List<InkTracePoint> getPoints() {
        return this.getTrace().getPoints(getFrom(), getTo());
    }
	@SuppressWarnings("unchecked")
    public List<InkTracePoint> getPoints(String from, String to) {
		return this.getTrace().getPoints(from, to);
	}

	/**
	 * Returns the trace, trace group, or traceview represented by this TraceView.
	 * @return the referenced trace
	 */
	@SuppressWarnings("unchecked")
    public InkTraceLike getTrace() {
		return (InkTraceLike) this.getInk().getDefinitions().get(this.getTraceDataRef());
	}

	 /**
     * {@inheritDoc}
     */
    @Override
	public boolean isEmpty(){
		return this.getTraceDataRef()==null;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public void accept(TraceVisitor visitor) {
		visitor.visit(this);
	}

    @SuppressWarnings("unchecked")
    @Override
    public void removeCompletely() throws ViewTreeManipulationException {
        //remove associated trace 
        InkTraceLike tl= getTrace();
        if(tl.isView()){
            ((InkTraceView)tl).removeCompletely();
        }else{
            getInk().removeTrace((InkTrace)tl);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timespan getTimeSpan() {
        return getTrace().getTimeSpan();
    }
    @Override
    public TraceBound getBounds() {
        return getTrace().getBounds();
    }

    /**
     * Returns an Iterable to iterate over all points
     * @return Iterable
     */
    @SuppressWarnings("unchecked")
    public Iterable<InkTracePoint> pointIterable() {
        return getTrace().pointIterable();
    }

    @Override
    public InkTracePoint getPoint(int i) {
        return getTrace().getPoint(i);
    }

   @Override
    public int getPointCount() {
        return getTrace().getPointCount();
    }

    @Override
    public void remove() throws ViewTreeManipulationException {
        if(this == getRoot()){
            System.err.println("can not remove root view");
            return;
        }
        TreeEvent e= new TreeEvent(InkTraceView.ON_CHILD_PRE_REMOVE);
        e.target = getParent();
        e.children.add(this);
        notifyObserver(ON_CHANGE, e);
   
        getParent().acctuallyRemove(this);
        e.aspect = InkTraceView.ON_CHILD_REMOVE;
        notifyObserver(ON_CHANGE, e);
    }


}
