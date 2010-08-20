package ch.unibe.inkml;


import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.AbstractObservable;



abstract public class InkElement extends AbstractObservable implements InkElementInterface{
	private InkInk ink;
	
	public InkElement(InkInk ink){
		this.ink = ink;
	}

    /**
     * Given an XML node corresponding to the inheriting class, this method
     * will initialize the object with all information given by the XML node.
     * @param node The XML node.
     * @throws InkMLComplianceExcpetion if the XML represented by the node is not copliant to the InkML standard.
     */
	public abstract void buildFromXMLNode(Element node) throws InkMLComplianceException;

	/**
     * Given the parent XML node, this method will serialize the this object to valid InkML XML.
     * @param parent
     * @throws InkMLComplianceException
     */
	public abstract void exportToInkML(Element parent) throws InkMLComplianceException;
	

	public InkInk getInk(){
		return this.ink;
	}

	/**
	 * Return the value of an XML attribute as string.
	 * If a reference is made, the leading '#' is stripped. 
	 * @param node XML element
	 * @param name Name of the attribute
	 * @param defaultValue if the attribute is not given this value will be returned.
	 * @return
	 */
	protected String loadAttribute(Element node, String name, String defaultValue) {
		if(node.hasAttribute(name)){
			if(name.matches(".*Ref$")){
				return node.getAttribute(name).replace("#","");
			}else{
				return node.getAttribute(name);
			}
		}
		return defaultValue;
	}

	protected void writeAttribute(Element node, String name, String value,
			String defaultValue) {
				if(value != null && !value.equals(defaultValue)){
					if(name.matches(".*Ref$")){
						node.setAttribute(name, "#"+value);
					}else{
						node.setAttribute(name, value);
					}
				}
			}

	public String getLabel() {
		String result = this.getClass().getName();
		return result.substring(result.lastIndexOf('.')+4);
	}
}
