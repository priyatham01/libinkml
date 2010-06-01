package ch.unibe.inkml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InkContext extends InkUniqueElement{

	
	
	private String contextRef, canvasRef, canvasTransformRef, traceFormatRef, inkSourceRef, brushRef;
	private InkTraceFormat format;

	
	public InkContext(InkInk ink, String id) {
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
		this.contextRef = loadAttribute(node, "contextRef", null);
		this.canvasRef = loadAttribute(node, "canvasRef", null);
		this.canvasTransformRef = loadAttribute(node, "canvasTransformRef", null);
		this.traceFormatRef = loadAttribute(node, "traceFormatRef", null);
		this.inkSourceRef = loadAttribute(node, "inkSourceRef", null);
		this.brushRef = loadAttribute(node, "brushRef", null);
		
		for(Node child = node.getFirstChild(); child!= null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String n = node.getNodeName();
			if(n.equals("brush")){
				InkBrush brush = new InkBrush(this.getInk(),this.getInk().getDefinitions().uniqueId("brush"));
				brush.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(brush);
				this.brushRef = brush.getId();
			}else if(n.equals("canvas")){
				InkCanvas canvas = new InkCanvas(this.getInk(),this.getInk().getDefinitions().uniqueId("canvas"));
				canvas.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(canvas);
				this.canvasRef = canvas.getId();
			}else if(n.equals("canvasTransform")){
				InkCanvasTransform item = new InkCanvasTransform(this.getInk(),this.getInk().getDefinitions().uniqueId("canvasTransform"));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(item);
				this.canvasTransformRef = item.getId();
			}else if(n.equals("traceFormat")){
				InkTraceFormat item = new InkTraceFormat(this.getInk(),this.getInk().getDefinitions().uniqueId("traceFormat"));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(item);
				this.traceFormatRef = item.getId();
			}if(n.equals("traceFormat")){
				InkTraceFormat item = new InkTraceFormat(this.getInk(),this.getInk().getDefinitions().uniqueId("traceFormat"));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(item);
				this.traceFormatRef = item.getId();
			}if(n.equals("inkSource")){
				InkInkSource item = new InkInkSource(this.getInk(),this.getInk().getDefinitions().uniqueId("inkSource"));
				item.buildFromXMLNode((Element)node);
				this.getInk().getDefinitions().enter(item);
				this.inkSourceRef = item.getId();
			}
		}
	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		if(parent.getNodeName().equals("ink")){
			this.getInk().setCurrentContext(this);
		}
		Element contextNode = parent.getOwnerDocument().createElement("context");
		parent.appendChild(contextNode);
		writeAttribute(contextNode, "xml:id", this.getId(), "");
		writeAttribute(contextNode, "contextRef", contextRef, "");
		writeAttribute(contextNode, "canvasRef", canvasRef, "");
		writeAttribute(contextNode, "canvasTransformRef", canvasTransformRef, "");
		writeAttribute(contextNode, "traceFormatRef", traceFormatRef, "");
		writeAttribute(contextNode, "inkSourceRef", inkSourceRef, "");
		writeAttribute(contextNode, "brushRef", brushRef, "");
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

	public void setInkSource(InkInkSource source) {
		this.inkSourceRef = source.getIdNow("Inksource");
	}

	public void setTraceFormat(InkTraceFormat format) {
		this.traceFormatRef = format.getIdNow("format");
	}

	public void setCanvas(InkCanvas canvas) {
		this.canvasRef = canvas.getIdNow("canvas");
	}

	public void setCanvasTransform(InkCanvasTransform transform) {
		this.canvasTransformRef = transform.getIdNow("transform");
	}

	public void setBrush(InkBrush b) {
		this.brushRef = b.getId();
		
	}

	public boolean hasBrush() {
		return this.getBrush()!=null;
	}
	
	

}
