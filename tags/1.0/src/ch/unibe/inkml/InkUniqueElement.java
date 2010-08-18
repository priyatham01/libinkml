package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.NotImplementedException;




public abstract class InkUniqueElement extends InkElement {
	
	private String id;
	
	public InkUniqueElement(InkInk ink) {
		super(ink);
		// TODO Auto-generated constructor stub
	}
	
	public InkUniqueElement(InkInk ink, String id) {
		this(ink);
		this.setId(id);
	}

	public void setId(String id){
		if(this.id == null){
			this.id = id;
			this.getInk().getDefinitions().put(this);
		}else{
			throw new NotImplementedException();
		}
	}
	public String getId(){
		return id;
	}
	/**
	 * Returns the key of this element. If there is no key
	 * a new key is assigned to this element, and it is registered
	 * in the definition element.
	 * Prefix is the string that will prefix the id, if it must be created.
	 * @param prefix
	 * @return returns the key string of this element.
	 */
	public String getIdNow(String prefix){
		if(id == null){
			this.id = this.getInk().getDefinitions().uniqueId(prefix);
			this.getInk().getDefinitions().put(this);
		}
		return this.id;
	}
	


	@Override
	public void exportToInkML(Element node) throws InkMLComplianceException{
		if(this.id != null){
			node.setAttribute("xml:id", this.getId());
		}
	}


	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException{
		if(node.hasAttribute("xml:id")){
			this.setId(node.getAttribute("xml:id"));
		}
	}
	public String getLabel() {
		String result =  super.getLabel();
		if(this.getId() != null){
			result = result+"("+this.getId()+")";
		}
		return result;
	}

	
}
