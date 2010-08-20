package ch.unibe.inkml;

import org.w3c.dom.Element;

public final class InkDefaultContext extends InkContext {

	public InkDefaultContext(InkInk ink) throws InkMLComplianceException {
		super(ink,"DefaultContext");
	}
	
	public boolean isDefaultContext(){
		return true;
	}
	public void exportToInkML(Element parent) throws InkMLComplianceException {
	}
}
