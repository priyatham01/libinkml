package ch.unibe.inkml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InkContext extends InkUniqueElement{

	
	
	public static final String INKML_NAME = "context";

    private static final String INKML_ATTR_CONTEXTREF = "contextRef";

    private static final String INKML_ATTR_CANVASREF = "canvasRef";

    private static final String INKML_ATTR_CANVASTRANSFORMREF = "canvasTransformRef";

    private static final String INKML_ATTR_TRACEFORMATREF = "traceFormatRef";

    private static final String INKML_ATTR_INKSOURCEREF = "inkSourceRef";

    private static final String INKML_ATTR_BRUSHREF = "brushRef";

    private String contextRef, canvasRef, canvasTransformRef, traceFormatRef, inkSourceRef, brushRef;
	
	private InkInkSource inkSource;
	
	private InkTraceFormat format;

	
	public InkContext(InkInk ink, String id) throws InkMLComplianceException {
		super(ink,id);
	}

	public InkContext(InkInk inkInk) {
		super(inkInk);
	}

	public void setInkTraceFormat(InkTraceFormat format) {
		this.format = format;
	}
	
	public boolean isDefaultContext(){
		return false;
	}
	
	public boolean isCurrentContext() throws InkMLComplianceException{
		return this.equals(this.getInk().getCurrentContext());
	}
	
	
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		this.contextRef = loadAttribute(node, INKML_ATTR_CONTEXTREF, null);
		this.canvasRef = loadAttribute(node, INKML_ATTR_CANVASREF, null);
		this.canvasTransformRef = loadAttribute(node, INKML_ATTR_CANVASTRANSFORMREF, null);
		this.traceFormatRef = loadAttribute(node, INKML_ATTR_TRACEFORMATREF, null);
		this.inkSourceRef = loadAttribute(node, INKML_ATTR_INKSOURCEREF, null);
		this.brushRef = loadAttribute(node, INKML_ATTR_BRUSHREF, null);
		
		for(Node child = node.getFirstChild(); child!= null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String n = node.getNodeName();
			if(n.equals(InkBrush.INKML_NAME)){
				InkBrush brush = new InkBrush(this.getInk(),this.getInk().getDefinitions().createUniqueId(InkBrush.ID_PREFIX));
				brush.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enterElement(brush);
				this.brushRef = brush.getId();
			}else if(n.equals(InkCanvas.INKML_NAME)){
				InkCanvas canvas = new InkCanvas(this.getInk(),this.getInk().getDefinitions().createUniqueId(InkCanvas.ID_PREFIX));
				canvas.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enterElement(canvas);
				this.canvasRef = canvas.getId();
			}else if(n.equals(InkCanvasTransform.INKML_NAME)){
				InkCanvasTransform item = new InkCanvasTransform(this.getInk(),this.getInk().getDefinitions().createUniqueId(InkCanvasTransform.ID_PREFIX));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enterElement(item);
				this.canvasTransformRef = item.getId();
			}else if(n.equals(InkTraceFormat.INKML_NAME)){
				InkTraceFormat item = new InkTraceFormat(this.getInk(),this.getInk().getDefinitions().createUniqueId(InkTraceFormat.ID_PREFIX));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enterElement(item);
				this.traceFormatRef = item.getId();
			}else if(n.equals(InkInkSource.INKML_NAME)){
				InkInkSource item = new InkInkSource(this.getInk(),this.getInk().getDefinitions().createUniqueId(InkInkSource.ID_PREFIX));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enterElement(item);
				this.inkSourceRef = item.getId();
			}
		}
	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		if(parent.getNodeName().equals(InkInk.INKML_NAME)){
			this.getInk().setCurrentContext(this);
		}
		Element contextNode = parent.getOwnerDocument().createElement(INKML_NAME);
		parent.appendChild(contextNode);
		if(!isReferenceOnly() &&  getInk().getDefinitions().containsElement(getId()) && !parent.getNodeName().equals(InkDefinitions.INKML_NAME)){
			writeAttribute(contextNode, INKML_ATTR_CONTEXTREF, "#"+getId(),"");
		}else{
			writeAttribute(contextNode, INKML_ATTR_ID, this.getId(), "");
			writeAttribute(contextNode, INKML_ATTR_CONTEXTREF, contextRef, "");
			writeAttribute(contextNode, INKML_ATTR_CANVASREF, canvasRef, "");
			writeAttribute(contextNode, INKML_ATTR_CANVASTRANSFORMREF, canvasTransformRef, "");
			writeAttribute(contextNode, INKML_ATTR_TRACEFORMATREF, traceFormatRef, "");
			writeAttribute(contextNode, INKML_ATTR_INKSOURCEREF, inkSourceRef, "");
			writeAttribute(contextNode, INKML_ATTR_BRUSHREF, brushRef, "");
		}
	}
	
	
	public boolean hasParentContext(){
		return this.contextRef != null;
	}
	
	public InkContext getParentContext(){
		return ((InkContext)this.getInk().getDefinitions().get(this.contextRef));
	}

	public InkCanvasTransform getCanvasTransform() {
		if(this.canvasTransformRef != null){
			return (InkCanvasTransform) this.getInk().getDefinitions().get(this.canvasTransformRef);
		}else if(this.contextRef != null){
			return this.getParentContext().getCanvasTransform();
		}else{
			return InkCanvasTransform.getIdentityTransform(this.getInk(),"identity",getSourceFormat(),getCanvasTraceFormat());
		}
		
	}

	public InkBrush getBrush() {
		if(this.brushRef != null){
			return (InkBrush) this.getInk().getDefinitions().get(this.brushRef);
		}else if(this.hasParentContext()){
			return this.getParentContext().getBrush();
		}else{
			return null;
		}
	}

	public InkTraceFormat getSourceFormat() {
		if(this.traceFormatRef != null){
			return (InkTraceFormat) this.getInk().getDefinitions().get(this.traceFormatRef);
		}else if(this.format != null){
			return this.format;
		}else if(this.contextRef != null){
			return this.getParentContext().getSourceFormat();
		}else{
			return InkTraceFormat.getDefaultTraceFormat(this.getInk());
		}
	}
	
	public InkTraceFormat getCanvasTraceFormat(){
		return this.getCanvas().getTraceFormat();
	}
	
	public InkCanvas getCanvas(){
		if(this.canvasRef!=null){
			return (InkCanvas) this.getInk().getDefinitions().get(this.canvasRef);
		}else if(this.hasParentContext()){
			return this.getParentContext().getCanvas();
		}else{
			return InkCanvas.getDefaultCanvas(this.getInk());
		}
	}
	
	/**
	 * Returns the inkSource which is given by this context. 
	 * To get the appropriate inkSource the following order is considered:
	 * <ul>
	 *     <li>child element</li>
	 *     <li>inkSourceRef</li>
	 *     <li>inherited from parent context</li>
	 * </ul>
	 * 
     * If no inkSource could have been found in this order, then 
     * null is returned. InkML plans for a default device source, but does
     * delivers no definition.
	 *  
	 * @return te inkSource
	 */
    public InkInkSource getInkSource() {
        if(inkSource != null){
            return inkSource;
        }else if(inkSourceRef != null){
            return (InkInkSource) getInk().getDefinitions().get(inkSourceRef);
        }else if(hasParentContext()){
            return getParentContext().getInkSource();
        }else{
            return null;
        }
        
    }

	public void setInkSourceByRef(InkInkSource source) {
	    inkSource = null;
		inkSourceRef = source.getIdNow(InkInkSource.ID_PREFIX);
	}

	public void setInkSource(InkInkSource source) {
	    inkSourceRef = null;
        inkSource = source;
    }

	
	public void setTraceFormat(InkTraceFormat format) {
		this.traceFormatRef = format.getIdNow(InkTraceFormat.ID_PREFIX);
	}

	public void setCanvas(InkCanvas canvas) {
		this.canvasRef = canvas.getIdNow(InkCanvas.ID_PREFIX);
	}

	public void setCanvasTransform(InkCanvasTransform transform) {
		this.canvasTransformRef = transform.getIdNow(InkCanvasTransform.ID_PREFIX);
	}

	public void setBrush(InkBrush b) {
		this.brushRef = b.getId();
		
	}

	public boolean hasBrush() {
		return this.getBrush()!=null;
	}

	public boolean isReferenceOnly() {
		return (contextRef != null
				&& canvasRef == null
				&& canvasTransformRef == null
				&& traceFormatRef == null
				&& inkSourceRef == null
				&& brushRef == null
				);
	}


	
	

}
