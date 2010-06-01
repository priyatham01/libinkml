package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.inkml.InkChannel.Name;

abstract public class InkMapping extends InkUniqueElement {

	public enum Type{IDENTITY,LOOKUP,AFFINE,MATHML,PRODUCT,UNKOWN;
		public String toString(){
			return super.toString().toLowerCase();
		}
		public static Type getValue(String name){
			for(Type t : Type.values()){
				if(t.toString().equals(name)){
					return t;
				}
			}
			return null;
		}
	};
	
	
	public static InkMapping mappingFactory(InkInk ink,Element node) throws InkMLComplianceException{
		Type type = null;
		if(node.hasAttribute("type")){
			type = Type.getValue(node.getAttribute("type"));
		}
		if(type == null){
			type = Type.UNKOWN;
		}
		InkMapping m = mappingFactory(ink, type);
		m.buildFromXMLNode(node);
		return m;
	}
	
	public static InkMapping mappingFactory(InkInk ink,Type type){
		switch(type){
		case IDENTITY: 
			return new InkIdentityMapping(ink);
		case LOOKUP:
			return new InkLookUpMapping(ink);
		case AFFINE:
			return new InkAffineMapping(ink);
		case MATHML:
			return new InkMathMLMapping(ink);
		case PRODUCT:
			return new InkProductMapping(ink);
		default :
			return new InkUnknownMapping(ink);
		}
	}
	

	private List<InkBind> binds;

//	private List<InkMapping> mappings;
	
	public InkMapping(InkInk ink) {
		super(ink);
		binds = new ArrayList<InkBind>();
	}

	abstract public Type getType();
	
	@Override
	public void buildFromXMLNode(Element node)throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		this.loadBinds(node);
	}


	protected void loadBinds(Element node) {
		this.binds = new ArrayList<InkBind>();
		for (Node child = node.getFirstChild(); child != null;child = child.getNextSibling()){
			if(child.getNodeName().equals("bind")){
				Element el = (Element)child;
				InkBind b = new InkBind(getInk());
				if(!el.getAttribute("source").equals("")){
					b.source = Name.valueOf(el.getAttribute("source"));
				}
				if(!el.getAttribute("target").equals("")){
					b.target = Name.valueOf(el.getAttribute("target"));
				}
				if(!el.getAttribute("column").equals("")){
					b.column = el.getAttribute("column");
				}
				if(!el.getAttribute("variable").equals("")){
					b.variable = el.getAttribute("variable");
				}
				this.binds.add(b);
			}
		}
	}
	public  List<InkBind> getBinds() {
		return this.binds;
	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element mappingNode = parent.getOwnerDocument().createElement("mapping");
		super.exportToInkML(mappingNode);
		parent.appendChild(mappingNode);
		if(this.getType()!= Type.UNKOWN){
			mappingNode.setAttribute("type", this.getType().toString());
		}
		saveBinds(mappingNode);
		exportToInkMLHook(mappingNode);
	}

	abstract protected void exportToInkMLHook(Element mappingNode) throws InkMLComplianceException;

	protected void saveBinds(Element mappingNode) {
		for(InkBind bind : binds){
			Element bindNode = mappingNode.getOwnerDocument().createElement("bind");
			if(bind.hasSource()){
				bindNode.setAttribute("source", bind.source.toString());
			}
			if(bind.hasTarget()){
				bindNode.setAttribute("target", bind.target.toString());
			}
			if(bind.column!= null && !bind.column.equals("")){
				bindNode.setAttribute("column", bind.column);
			}
			if(bind.variable!= null && !bind.variable.equals("")){
				bindNode.setAttribute("variable", bind.variable);
			}
			mappingNode.appendChild(bindNode);
		}
		
	}


	public static InkMapping flipAxis(InkMapping mapping,InkTraceFormat source, InkTraceFormat target) {
		switch (mapping.getType()) {
		case IDENTITY:
			InkMapping id = InkAffineMapping.createIdentityInkAffinMapping(mapping.getInk(), source, target);
			return flipAxis(id, source, target);
		case AFFINE:
			int i = 0, x = 0, y = 0;
			for(InkBind b : mapping.binds){
				if(b.hasTarget()){
					if(b.target == Name.X){
						x = i++;
					}else if(b.target == Name.Y){
						y = i++;
					}
				}
			}
			((InkAffineMapping)mapping).getInkMatrix().flipAxis(x,y);
			return mapping;
		default:
			return mapping;
		}
	}
	
	public static InkMapping invertAxis(InkMapping mapping,InkTraceFormat source, InkTraceFormat target, Name axis) {
		switch (mapping.getType()) {
			case IDENTITY:	
				InkMapping id = InkAffineMapping.createIdentityInkAffinMapping(mapping.getInk(), source, target);
				return invertAxis(id, source, target,axis);
			case AFFINE:
				int x = 0,i=0;
				for(InkBind b : mapping.binds){
					if(b.hasTarget()){
						if(b.target == axis){
							x = i;
							break;
						}
						i++;
					}
				}
				((InkAffineMapping)mapping).getInkMatrix().invertAxis(x);
				return mapping;
			default:
				return mapping;
		}
	}

	public void addBind(InkBind b) {
		binds.add(b);
		
	}

	abstract public boolean isInvertible();

    /**
     * @param sourcePoints
     * @param points
     * @param sourceFormat
     * @param targetFormat
     * @throws InkMLComplianceException 
     */
    public abstract void transform(double[][] sourcePoints, double[][] points,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat) throws InkMLComplianceException;

    /**
     * @param sourcePoints
     * @param points
     * @param canvasFormat
     * @param sourceFormat
     * @throws InkMLComplianceException 
     */
    public abstract void backTransform(double[][] sourcePoints, double[][] points,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat) throws InkMLComplianceException;

	

}
