package ch.unibe.inkml;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.eindermu.utils.StringMap;


public abstract class InkAnnotatedElement extends InkUniqueElement {

	
	private Map<String,String> annotation = new StringMap<String>();
	
	public InkAnnotatedElement(InkInk ink) {
		super(ink);
	}

	public InkAnnotatedElement(InkInk ink, String id) {
		super(ink,id);
	}

	public void annotate(String key, String value) {
		annotation.put(key, value);
	}
	public String getAnnotation(String key){
		return annotation.get(key);
	}
	public boolean containsAnnotation(String key){
		return annotation.containsKey(key);
	}
	public void removeAnnotation(String key){
		annotation.remove(key);
	}
	
    
	
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element el = (Element)child;
			if(el.getNodeName().equals("annotation")){
				String name = el.getAttribute("type");
				String value = el.getTextContent();
				this.annotate(name,value);
			}else if (el.getNodeName().equals("annotateXML")){
				this.loadCustomXmlAnnotation(el);
			}
		}
	}

	private void loadCustomXmlAnnotation(Element el) {
		// To override
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void exportToInkML(Element node) throws InkMLComplianceException{
		super.exportToInkML(node);
		for(String key : this.annotation.keySet()){
			Element a = node.getOwnerDocument().createElement("annotation");
			a.setTextContent(this.annotation.get(key));
			a.setAttribute("type", key);
			node.appendChild(a);
		}
		this.customXMLAnnotationToXML(node);
	}

	private void customXMLAnnotationToXML(Element node) {
		//to override
	}

	public Set<String> getAnnotationNames() {
		return this.annotation.keySet();
	}
	public boolean testAnnotation(String type, String string) {
		return containsAnnotation(type) && getAnnotation(type).equals(string);
	}
}
