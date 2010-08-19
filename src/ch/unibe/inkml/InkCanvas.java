package ch.unibe.inkml;

import org.w3c.dom.Element;

public class InkCanvas extends InkUniqueElement{

	private static InkCanvas defaultCanvas;
	public static final String ID_DEFAULT_CANVAS = "DefaultCanvas";
    public static final String INKML_NAME = "canvas";
    public static final String INKML_ATTR_TRACEFORMATREF = "traceFormatRef";
    public static final String ID_PREFIX = "cv";
	
	public static InkCanvas getDefaultCanvas(InkInk ink){
	    if(defaultCanvas == null){
    	    if(ink.getDefinitions().containsKey(ID_DEFAULT_CANVAS)){
    	        return (InkCanvas) ink.getDefinitions().get(ID_DEFAULT_CANVAS);
            }else{
                try {
                    defaultCanvas = new InkCanvas(ink,ID_DEFAULT_CANVAS);
                    defaultCanvas.setInkTraceFormat(new DefaultInkTraceFormat(ink));
                } catch (InkMLComplianceException e) {
                    // Should not occure, we have already tested above.
                    throw new Error(e);
                }
            }
		}
		return defaultCanvas;
	}
	
	private InkTraceFormat inkTraceFormat;
	private String traceFormatRef;

	public InkCanvas(InkInk ink,String id) throws InkMLComplianceException{
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
		Element c = parent.getOwnerDocument().createElement(INKML_NAME);
		c.setAttribute(InkUniqueElement.INKML_ATTR_ID, this.getId());
		if(this.inkTraceFormat != null){
			this.getTraceFormat().exportToInkML(c);
		}else{
			c.setAttribute(INKML_ATTR_TRACEFORMATREF, traceFormatRef);
		}
		parent.appendChild(c);
		
	}
	@Override
	public void buildFromXMLNode(Element node)
			throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		if(node.hasAttribute(INKML_ATTR_TRACEFORMATREF)){
			this.traceFormatRef = node.getAttribute(INKML_ATTR_TRACEFORMATREF);
		}else if(node.getElementsByTagName(InkTraceFormat.INKML_NAME).getLength() == 1){
			InkTraceFormat f = new InkTraceFormat(this.getInk());
			f.buildFromXMLNode((Element)node.getElementsByTagName(InkTraceFormat.INKML_NAME).item(0));
			this.inkTraceFormat = f;
		}else{
			throw new InkMLComplianceException("Each canvas must eather contain a traceFormat, or link to one with the attribute traceFormatRef");
		}
	}

    /**
     * @param canvas
     * @throws InkMLComplianceException 
     */
    public void acceptAsCompatible(InkCanvas canvas,boolean strict) throws InkMLComplianceException {
        if(getTraceFormat()!=null){
            if(canvas.getTraceFormat() == null){
                throw new InkMLComplianceException("Documents canvas has no traceFormat");
            }else{
                getTraceFormat().acceptAsCompatible(canvas.getTraceFormat(), strict);
            }
        }
    }
}
