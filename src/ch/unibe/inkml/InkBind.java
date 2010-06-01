package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.inkml.InkChannel.Name;

public class InkBind extends InkElement {

	public InkBind(InkInk ink) {
		super(ink);
	}

	public InkChannel.Name source,target;
	public String column,variable;

	public Name getSource(InkTraceFormat format) throws InkMLComplianceException {
		return getValue(null,source,format);
	}
	public Name getTarget(Name s, InkTraceFormat format) throws InkMLComplianceException {
		return getValue(s,target,format);
	}		
	private Name getValue(Name standard, Name value, InkTraceFormat format) throws InkMLComplianceException{
		if(value == null || !format.containsChannel(value)){
			if(standard != null){
				return standard;
			}else{
				throw new InkMLComplianceException("Tranformation error, cannot bind to channel "+value);
			}
		}
		return value;
	}
	public boolean hasSource() {
		return source != null;
	}
	public boolean hasTarget() {
		return target != null;
	}
	public Name getTarget(InkTraceFormat format) throws InkMLComplianceException {
		return getTarget(null,format);
	}
	
	
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		// TODO Auto-generated method stub

	}

}
