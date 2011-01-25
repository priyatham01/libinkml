package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class InkProductMapping extends InkMapping {

	private List<InkMapping> mappings;

	public InkProductMapping(InkInk ink) {
		super(ink);
		mappings = new ArrayList<InkMapping>();
	}

	@Override
	public Type getType() {
		return Type.PRODUCT;
	}

	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		NodeList mappings = node.getElementsByTagName("mapping");
		if(mappings.getLength() == 0){
			throw new InkMLComplianceException("A product mapping must contain at least one mapping");
		}
		this.mappings = new ArrayList<InkMapping>();
		for(int i= 0; i < mappings.getLength(); i++){
			this.mappings.add(InkMapping.mappingFactory(getInk(), (Element)mappings.item(i)));
		}
	}
	
	@Override
	protected void exportToInkMLHook(Element mappingNode)
			throws InkMLComplianceException {
		for(InkMapping m : this.mappings){
			m.exportToInkML(mappingNode);
		}
	}
	@Override
	public boolean isInvertible() {
		for(InkMapping m : mappings){
			if(!m.isInvertible()){
				return false;
			}
		}
		return true;
	}

    @Override
    public void backTransform(double[][] sourcePoints, double[][] points,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat)
            throws InkMLComplianceException {
        for(InkMapping m : mappings){
            m.backTransform(sourcePoints, points, canvasFormat, sourceFormat);
        }
    }

    @Override
    public void transform(double[][] sourcePoints, double[][] points,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat)
            throws InkMLComplianceException {
        for(InkMapping m : mappings){
            m.transform(sourcePoints, points, sourceFormat, targetFormat);
        }
    }

    public InkMapping clone(InkInk ink){
        InkProductMapping n = (InkProductMapping) super.clone(ink);
        for(InkMapping m : mappings){
            n.mappings.add(m.clone(ink));
        }
        return n;
    }
    
}
