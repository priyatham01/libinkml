package ch.unibe.inkml;

import org.w3c.dom.Element;

public class InkInkSource extends InkUniqueElement{
	public InkInkSource(InkInk ink) {
		super(ink);
	}
	public InkInkSource(InkInk ink, String id) {
		super(ink,id);
	}
	private String manufacturer;
	private String model;
	private String serialNo;
	private String description;
	private InkTraceFormat traceFormat;
	
	
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		if(node.getElementsByTagName("traceFormat").getLength() == 1){
			this.traceFormat = new InkTraceFormat(this.getInk());
			this.traceFormat.buildFromXMLNode((Element)node.getElementsByTagName("traceFormat").item(0));
		}
		this.model = this.loadAttribute(node, "model", "unknown");
		this.manufacturer = this.loadAttribute(node, "manufacturer", "unknown");
		this.serialNo = this.loadAttribute(node, "serialNo", "unknown");
		this.description = this.loadAttribute(node, "description", "unknown");
	}
	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element inkSourceNode = parent.getOwnerDocument().createElement("inkSource");
		parent.appendChild(inkSourceNode);
		writeAttribute(inkSourceNode, "xml:id", this.getId(), "");
		writeAttribute(inkSourceNode, "model", model, "unkown");
		writeAttribute(inkSourceNode, "manufacturer", manufacturer, "unkown");
		writeAttribute(inkSourceNode, "serialNo", serialNo, "unkown");
		writeAttribute(inkSourceNode, "description", description, "unkown");
	}
}
