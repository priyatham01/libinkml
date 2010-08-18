package ch.unibe.inkml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ch.unibe.inkml.InkChannel.Name;

public class InkCanvasTransform extends InkUniqueElement {

	
	public static InkCanvasTransform getIdentityTransform(InkInk ink,String id,InkTraceFormat source, InkTraceFormat target) {
		InkCanvasTransform result = new InkCanvasTransform(ink,id);
		result.invertible = true;
		InkMapping m1 = new InkIdentityMapping(ink);
		result.setForewardMapping(m1);
		return  result;
	}
	
	
	private boolean invertible = true;
	private InkMapping foreward,backward;
	
	public InkCanvasTransform(InkInk ink) {
		super(ink);
	}

	public InkCanvasTransform(InkInk ink, String id) {
		super(ink,id);
	}

	public void setForewardMapping(InkMapping foreward){
		this.foreward = foreward;
	}
	public void setBackwardMapping(InkMapping backward){
		this.backward = backward;
	}
	
	@Override
	public void buildFromXMLNode(Element node)
			throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		if(node.hasAttribute("inverible")){
			this.invertible = Boolean.parseBoolean(node.getAttribute("invertible"));
		}
		NodeList mappings = node.getElementsByTagName("mapping");
		if(mappings.getLength() == 0){
			throw new InkMLComplianceException("A canvasTransform must contain a mapping");
		}
		InkMapping m = InkMapping.mappingFactory(getInk(), (Element)mappings.item(0));
		this.setForewardMapping(m);
		if(mappings.getLength() > 1){
			m = InkMapping.mappingFactory(getInk(), (Element)mappings.item(1));
			this.setBackwardMapping(m);
		}
	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element t = parent.getOwnerDocument().createElement("canvasTransform");
		parent.appendChild(t);
		super.exportToInkML(t);
		if (foreward.isInvertible()){
			this.invertible = true;
		}
		if(!this.invertible){
			t.setAttribute("invertible", "false");
		}
		this.foreward.exportToInkML(t);
		
		if(!this.invertible && this.backward != null){
			this.backward.exportToInkML(t);
		}
	}


/*
	public InkTracePoint transform(InkTracePoint point,InkTraceFormat sourceFormat,InkTraceFormat targetFormat) throws InkMLComplianceException {
		InkTracePoint result = new InkTracePoint();
		this.foreward.transform(point,result,sourceFormat,targetFormat);
		return result;
	}
	*/
	public void flipAxis(InkTraceFormat sourceFormat,InkTraceFormat targetFormat){
		foreward = InkMapping.flipAxis(foreward, sourceFormat, targetFormat);
		if(backward != null){
			backward = InkMapping.flipAxis(backward, targetFormat,sourceFormat);
		}
	}
	public void invertAxis(InkTraceFormat sourceFormat,
			InkTraceFormat targetFormat, Name axis) {
		foreward = InkMapping.invertAxis(foreward,sourceFormat,targetFormat,axis);
		if(backward != null){
			backward = InkMapping.invertAxis(backward, targetFormat,sourceFormat,axis);
		}
	}
/*
	public InkTracePoint backTransform(InkTracePoint canvasPoint,InkTracePoint sourcePoint,
			InkTraceFormat canvasFormat, InkTraceFormat sourceFormat) throws InkMLComplianceException {
		if(foreward.isInvertible()){
			foreward.backTransform(sourcePoint,canvasPoint,canvasFormat,sourceFormat);
		}
		else if(backward != null){
			backward.transform(canvasPoint,sourcePoint,canvasFormat,sourceFormat);
		}else{
			throw new UnsupportedOperationException("Backwards transformation is not given, and foreward transformation is not invertible.");
		}
		return sourcePoint;
		
	}
*/
	public InkMapping getForwardMapping() {
		return this.foreward;
	}

    /**
     * @param sourcePoints
     * @param points
     * @param sourceFormat
     * @param targetFormat
     * @throws InkMLComplianceException 
     */
    public void transform(double[][] sourcePoints, double[][] points,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat) throws InkMLComplianceException {
        foreward.transform(sourcePoints, points, sourceFormat, targetFormat);
    }

    /**
     * @param points
     * @param sourcePoints
     * @param canvasFormat
     * @param sourceFormat
     * @throws InkMLComplianceException 
     */
    public void backTransform(double[][] points, double[][] sourcePoints,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat) throws InkMLComplianceException {
        if (foreward.isInvertible()){
            foreward.backTransform(sourcePoints, points, canvasFormat, sourceFormat);
        }else if(backward != null){
            backward.transform(sourcePoints, sourcePoints, canvasFormat, sourceFormat);
        }else{
            throw new UnsupportedOperationException("Backwards transformation is not given, and foreward transformation is not invertible.");
        }
    }

	

}
