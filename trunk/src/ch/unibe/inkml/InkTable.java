package ch.unibe.inkml;

import java.util.Vector;

import org.w3c.dom.Element;

public class InkTable extends InkTableLike {
	
	public enum Interpolation {floor,middle,ceiling,linear,cubic};
	private Interpolation interpolation;
	
	public enum Apply {absolute, relative};
	private Apply apply;
	
	public InkTable(InkInk ink) {
		super(ink);
	}



	public InkTable(InkInk ink, String id) {
		super(ink,id);
	}



	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		this.interpolation = Interpolation.valueOf(node.getAttribute("interpolation"));
		if(this.interpolation == null){
			this.interpolation = Interpolation.linear;
		}
		this.apply = Apply.valueOf(node.getAttribute("apply"));
		if(this.apply == null){
			this.apply = Apply.absolute;
		}
		this.buildTable(node.getTextContent());
	}


	@Override
	public void exportToInkML(Element parent) {
		Element tableNode = parent.getOwnerDocument().getElementById("table");
		if(!this.getId().equals("")){
			tableNode.setAttribute("xml:id", this.getId());
		}
		if(this.apply != Apply.absolute){
			tableNode.setAttribute("apply", "relative");
		}
		if(this.interpolation != Interpolation.linear){
			tableNode.setAttribute("interpolate", this.interpolation.toString());
		}
		tableNode.setTextContent(this.tableToString());
	}



	@Override
	public Vector<Object> transform(Vector<Object> point) {
		// TODO Auto-generated method stub
		return null;
	}
}
