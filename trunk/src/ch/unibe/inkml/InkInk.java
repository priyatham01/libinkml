package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observer;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceViewTreeManipulationException;


public class InkInk extends InkAnnotatedElement implements Observer {
	
	public static final Aspect ON_CHANGE = new Aspect(){};

	/**
	 * The definitions element, enables access to all elements with referenceIds 
	 * not only those which are defined in definintions
	 * @see InkDefinitions
	 */
    private InkDefinitions definitions;	

    public static final Aspect ON_TRACE_REMOVED = new Aspect(){};

    public static final String INKML_NAME = "ink";

    public static final String INKML_ATTR_DOCUMENT_ID = "documentID";

	public static final String INKML_NAMESPACE = "http://www.w3.org/2003/InkML";

	public static final String XML_ATTR_NAMESPACE = "xmlns";


	/**
	 * List of traces (leaf or containers) that are direct children of ink (have no parent)
	 */
	private List<InkTrace> traces;

	/**
	 * List of trace views (leaf or containers), that are direct children of ink (have no parent).
	 */
	private List<InkTraceView> views;
	
	/**
	 * This is the current InkContext used when new traces are added.
	 * if no context is defined this is an instance of InkDefaultContext
	 */
	private InkContext currentContext;

	/**
	 * A URI that uniquely identifies this document. No two documents 
	 * with a distinct application intent may have the same documentID 
	 * contents. The value of this property is an opaque URI whose 
	 * interpretation is not defined in this specification.
	 * See <a href="http://www.w3.org/TR/2010/WD-InkML-20100527/#inkElement">here</a>
	 */
	private String documentId;

	
	/**
	 * Constructs the actual InkML tree, without Ink, no document can exist.
	 */
	public InkInk() {
		super(null);
		this.traces = new ArrayList<InkTrace>();
		this.views = new ArrayList<InkTraceView>();
	}

	/**
	 * Returns the context which is currently active.
	 * If no context is active, the default context, as defined by InkML is 
	 * added.
	 * @return
	 */
	public InkContext getCurrentContext() {
		if(currentContext==null){
			try {
				currentContext = new InkDefaultContext(this);
			} catch (InkMLComplianceException e) {
				e.printStackTrace();
			}
		}
		return currentContext;
	}

	/**
	 * Sets a context active. This means that all traces created
	 * afterwards will have this context as the context they live in.
	 * Formerly created traces keep their context.
	 * @param currentContext
	 */
	public void setCurrentContext(InkContext currentContext) {
		this.currentContext = currentContext;
	}

	/**
	 * Gets the current brush. This is a shortcut for
	 * getCurrentContext().getBrush() since brush can only be refered to
	 * within a context, or on the Trace(View) directly.
	 * @return
	 */
	public InkBrush getCurrentBrush() {
		return getCurrentContext().getBrush();
	}


	public void setDefinitions(InkDefinitions inkDefinitions) {
		this.definitions = inkDefinitions;
	}


	/**
	 * Returns the definitions object of this document. 
	 * @see InkDefinitions
	 * @return
	 */
	public InkDefinitions getDefinitions() {
		return definitions;
	}

	/**
	 * Adds a traceView element to this document (this will be one of the views returned by {@link #getViewRoots()}).
	 * @param inkTraceView
	 */
	public void addView(InkTraceView inkTraceView) {
		views.add(inkTraceView);
		inkTraceView.registerFor(InkTraceView.ON_CHANGE, this);
	}
	
    @Override
    public void notifyFor(Aspect event, Object subject) {
        if(event == InkTraceView.ON_CHANGE){
            //System.err.println("Ink has recieved change");
            notifyObserver(InkInk.ON_CHANGE, subject);
        }
    }


	
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		if(node.hasAttribute(INKML_ATTR_DOCUMENT_ID)){
		    documentId = node.getAttribute(INKML_ATTR_DOCUMENT_ID);
		}
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			if(child.getNodeType() == Node.ELEMENT_NODE){
				stepNode((Element)child);
			}
			
		}
		
	}

	private void stepNode(Element node) throws InkMLComplianceException{
	    String n = node.getNodeName();
		if(n.equals(InkDefinitions.INKML_NAME) || n.equals("definition") /*backwards compatibility for definition-instead-of-definitions bug*/){ 
			definitions = new InkDefinitions(this.getInk());
			definitions.buildFromXMLNode(node);
		}else if(n.equals(InkContext.INKML_NAME)){
			InkContext context = new InkContext(this);
			context.buildFromXMLNode((Element)node);
			if(!context.hasId()){
				this.getInk().getDefinitions().put(context);
			}
			this.getInk().setCurrentContext(context);
		}else if(n.equals(InkTraceViewLeaf.INKML_NAME)){
		    addView(InkTraceView.createTraceView(getInk(),null,node));
		}else if(n.equals(InkTraceLeaf.INKML_NAME)){
			InkTraceLeaf t = new InkTraceLeaf(this.getInk(),null);
			t.buildFromXMLNode(node);
			this.addTrace(t);
		}else if(n.equals(InkTraceViewContainer.INKML_NAME)){
		    int traceCount = node.getElementsByTagName(InkTraceLeaf.INKML_NAME).getLength();
		    int viewCount = node.getElementsByTagName(InkTraceViewLeaf.INKML_NAME).getLength();
		    if(traceCount > 0 && viewCount > 0){
		        throw new InkMLComplianceException("libinkml does not support traces and traceViews been mixed within a traceGroup");
		    }
		    if(viewCount > 0){
		        InkTraceViewContainer view = new InkTraceViewContainer(getInk(), null);
		        view.buildFromXMLNode(node);
		        addView(view);
		    }else{
		        InkTraceGroup g = new InkTraceGroup(this.getInk(),null);
		        g.buildFromXMLNode(node);
		        this.addTrace(g);
		    }
		}
	}

	/**
	 * Adds a new trace to this document.
	 * @param inkTrace New trace to be added to this document
	 */
	public void addTrace(InkTrace inkTrace) {
		if(!inkTrace.testFormat(inkTrace.getContext().getCanvasTraceFormat())){
			System.err.println("trace is not well formated");
			inkTrace.testFormat(inkTrace.getContext().getCanvasTraceFormat());
		}
		this.traces.add(inkTrace);
	}



	@Override
	public void exportToInkML(Element node) throws InkMLComplianceException {
		super.exportToInkML(node);
		if(documentId != null && !documentId.isEmpty()){
		    node.setAttribute(INKML_ATTR_DOCUMENT_ID, documentId);
		    node.setAttribute(XML_ATTR_NAMESPACE, INKML_NAMESPACE);
		}
		this.getDefinitions().exportToInkML(node);
		if(this.currentContext!= null){
			this.currentContext.exportToInkML(node);
		}
		for(InkTrace t: this.traces){
			t.exportToInkML(node);
		}
		for(InkTraceView t: this.views){
			t.exportToInkML(node);
		}
	}



	/**
	 * Returns all traces which are direct children of this document.
	 * @return
	 */
	public List<InkTrace> getTraces() {
		return new ArrayList<InkTrace>(traces);
	}
	
	/**
	 * returns all traces of this document, ignoring the structuring by traceGroups.
	 * @return
	 */
	public List<InkTrace> getFlatTraces(){
	    ArrayList<InkTrace> leafs = new ArrayList<InkTrace>();
	    for(InkTrace trace:traces){
	        if(trace.isLeaf()){
	            leafs.add((InkTraceLeaf) trace);
	        }else{
	            leafs.addAll(((InkTraceGroup)trace).getFlattenedTraceLeafs());
	        }
	    }
		return leafs;
	}

	/**
	 * Returns the bounding box of the document taking every trace into account.
	 * @return
	 */
	public TraceBound getBounds() {
		TraceBound bound = new TraceBound();
		for(InkTrace s : getTraces()){
			bound.add(s.getBounds());
		}
		return bound;
	}

	
	/**
	 * Returns all traceViews of the {@link #getViewRoot()} which directly references to a trace. 
	 * @return
	 */
	public List<InkTraceViewLeaf> getFlatTraceViewLeafs() {
		return this.getViewRoot().getFlattenedTraceLeafs();
	}

	/**
	 * Returns the first traceView element that is a direct child of the ink element.
	 * It's assumed that the first traceView is the most important.
	 * @return
	 */
	public InkTraceViewContainer getViewRoot() {
		if(this.views.size() > 0){
			return (InkTraceViewContainer) this.views.get(0);
		}
		else{
			InkTraceViewContainer tv = new InkTraceViewContainer(this,null);
			addView(tv);
			return tv;
		}
	}
	
	/**
	 * Returns all traceView elements that are direct children of {@link InkInk}.
	 * These views often show different aspects of the document.
	 * @return
	 */
	public List<InkTraceView> getViewRoots(){
	    return views;
	}

	@Override
	public InkInk getInk(){
		return this;
	}

	public void exportToInkML(org.w3c.dom.Document xmlDocument) throws InkMLComplianceException {
		Element ink = xmlDocument.createElement(INKML_NAME);
		xmlDocument.appendChild(ink);
		this.exportToInkML(ink);
	}


	/**
	 * Returns the Timspan this document is covering.
	 * @return The timespan begining with point in time where the first trace has been written until
	 * the point in time when the last trace has been finished.
	 */
	public Timespan getTimeSpan() {
		Timespan ts = new Timespan();
		for(InkTrace l : this.getTraces()){
			ts.add(l.getTimeSpan());
		}
		return ts;
	}

	public void removeView(InkTraceView inkTraceViewContainer) {
		views.remove(inkTraceViewContainer);
		inkTraceViewContainer.unregisterFor(ON_ALL, this);
	}


	public void annotate(String name, String value){
	    super.annotate(name,value);
	    notifyObserver(ON_CHANGE,this);
	}



	public void reloadTraces() throws InkMLComplianceException {
		for(InkTrace l : this.getFlatTraces()){
			((InkTraceLeaf)l).reloadPoints();
		}
		
	}

	public void removeTrace(InkTrace trace) {
		if(traces.contains(trace)){
			traces.remove(trace);
		}
		if(!trace.isRoot()){
			trace.getParent().remove(trace);
		}

		for(InkTraceView view : getFlatTraceViewLeafs()){
			if(((InkTraceViewLeaf) view).getTrace() == trace){
				if(!view.isRoot()){
					try {
                        view.remove();
                    } catch (TraceViewTreeManipulationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
				}
			}
		}
		if(this.definitions.containsValue(trace)){
			this.definitions.remove(trace.getId());
		}
	}





	public static InkInk loadFromXMLDocument(Document document) throws InkMLComplianceException {
		Node node = document.getDocumentElement();
		if(node != null && node.getNodeName().equals(INKML_NAME)){
			InkInk ink = new InkInk();
			ink.buildFromXMLNode((Element) node);
			return ink;
		}else{
			throw new InkMLComplianceException("XML tree do not contain 'ink' root element");
		}
	}

    /**
     * Sets the unique document id of this document.
     * @see InkInk#documentId
     * @param The new document id
     */
    public void setDocumentId(String docId) {
        documentId = docId;
        this.notifyObserver(ON_CHANGE, this);
    }
    
    /**
     * Returns the unique document id of this document.
     * @see InkInk#documentId
     * @return The unique document id.
     */
    public String getDocumentId(){
        return documentId;
    }

    /**
     * Returns the id generated from {@link #getDocumentId()} by stripping
     * all but the last part of the URI. This is then unique within the current set of documents.
     * @return The unique id of this document.
     */
    public String getId(){
        return getDocumentId().substring(getDocumentId().lastIndexOf("/")+1);
    }


}
