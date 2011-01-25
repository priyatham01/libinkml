package ch.unibe.inkml;

import org.w3c.dom.Element;




public abstract class InkUniqueElement extends InkElement {
	
    public static final String INKML_ATTR_ID = "xml:id";
    
	private String id = null;
	
	public InkUniqueElement(InkInk ink) {
		super(ink);
		// TODO Auto-generated constructor stub
	}
	
	public InkUniqueElement(InkInk ink, String id) throws InkMLComplianceException {
		this(ink);
		this.setId(id);
	}

	public void setId(String id) throws InkMLComplianceException{
	    if(!id.equals(this.id) && getInk().getDefinitions().containsKey(id)){
            throw new InkMLComplianceException("The id '"+id+"' is already in use, you can not use it.");
        }
		if(this.id == null){
			this.id = id;
			getInk().getDefinitions().put(this);
		}else{
		    if(!this.id.equals(id)){
		        getInk().getDefinitions().remove(this.id);
		        this.id = id;
		        getInk().getDefinitions().put(this);
		    }
		}
	}
	
	/**
	 * Returns the ID of this element. If the element has no ID, null will be returned.
	 * @return
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Returns true if this object has an id assigned
	 * @return
	 */
	public boolean hasId() {
		return getId() != null;
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
			this.id = this.getInk().getDefinitions().createUniqueId(prefix);
			this.getInk().getDefinitions().put(this);
		}
		return this.id;
	}
	


	@Override
	public void exportToInkML(Element node) throws InkMLComplianceException{
		if(this.id != null){
			node.setAttribute(INKML_ATTR_ID, this.getId());
		}
	}


	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException{
		if(node.hasAttribute(INKML_ATTR_ID)){
			setId(node.getAttribute(INKML_ATTR_ID));
		}
	}
	
	
	public String getLabel() {
		String result =  super.getLabel();
		if(this.hasId()){
			result = result+"("+this.getId()+")";
		}
		return result;
	}
}
