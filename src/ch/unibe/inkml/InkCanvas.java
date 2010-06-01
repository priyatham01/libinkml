package ch.unibe.inkml;

import org.w3c.dom.Element;

public class InkCanvas extends InkUniqueElement{

	private static InkCanvas defaultCanvas;
	public static InkCanvas getDefaultCanvas(InkInk ink){
		if(defaultCanvas == null){
			defaultCanvas = new InkCanvas(ink,"DefaultCanvas");
			defaultCanvas.setInkTraceFormat(InkTraceFormat.getDefaultTraceFormat(ink));
		}
		return defaultCanvas;
	}
	
	public static InkCanvas createInkAnnoCanvas(InkInk ink) {
		InkCanvas canvas = new InkCanvas(ink,"inkAnnoCanvas");
		InkTraceFormat format = new InkTraceFormat(ink,"inkAnnoCanvasFormat");
		try {
			InkChannel c = InkChannel.channelFactory(InkChannel.Type.DECIMAL, ink);
			c.setName(InkChannel.Name.X);
			c.setOrientation(InkChannel.Orientation.P);
			format.addChannel(c);
		
			c = InkChannel.channelFactory(InkChannel.Type.DECIMAL, ink);
			c.setName(InkChannel.Name.Y);
			c.setOrientation(InkChannel.Orientation.P);
			format.addChannel(c);
			
			c = InkChannel.channelFactory(InkChannel.Type.DECIMAL, ink);
			c.setName(InkChannel.Name.T);
			c.setOrientation(InkChannel.Orientation.P);
			format.addChannel(c);
			
			c = InkChannel.channelFactory(InkChannel.Type.INTEGER, ink);
			c.setName(InkChannel.Name.F);
			c.setOrientation(InkChannel.Orientation.P);
			format.addIntermittentChannel(c);
		} catch (InkMLComplianceException e) {
			System.err.println("Its a Bug, please fix it, or contact developer");
			e.printStackTrace();
			//Will not happen here, unless it is a bug
		}
		canvas.setInkTraceFormat(format);
		return canvas;
	}
	
	private InkTraceFormat inkTraceFormat;
	private String traceFormatRef;

	public InkCanvas(InkInk ink,String id){
		super(ink,id);
	}
	
	public InkCanvas(InkInk ink) {
		super(ink);
	}

	public InkTraceFormat getTraceFormat() {
		if(this.inkTraceFormat != null){
			return this.inkTraceFormat;
		}else{
			return (InkTraceFormat) this.getInk().getDefinitions().get(traceFormatRef);
		}
	}

	public void setInkTraceFormat(InkTraceFormat inkTraceFormat) {
		this.inkTraceFormat = inkTraceFormat;
	}
	
	public void exportToInkML(Element parent) throws InkMLComplianceException{
		Element c = parent.getOwnerDocument().createElement("canvas");
		c.setAttribute("xml:id", this.getId());
		if(this.inkTraceFormat != null){
			this.getTraceFormat().exportToInkML(c);
		}else{
			c.setAttribute("traceFormatRef", traceFormatRef);
		}
		parent.appendChild(c);
		
	}
	@Override
	public void buildFromXMLNode(Element node)
			throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		if(node.hasAttribute("traceFormatRef")){
			this.traceFormatRef = node.getAttribute("traceFormatRef");
		}else if(node.getElementsByTagName("traceFormat").getLength() == 1){
			InkTraceFormat f = new InkTraceFormat(this.getInk());
			f.buildFromXMLNode((Element)node.getElementsByTagName("traceFormat").item(0));
			this.inkTraceFormat = f;
		}else{
			throw new InkMLComplianceException("Each canvas must eather contain a traceFormat, or link to one with the attribute traceFormatRef");
		}
	}
}
