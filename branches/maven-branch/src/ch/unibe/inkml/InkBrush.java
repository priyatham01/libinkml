package ch.unibe.inkml;

import org.w3c.dom.Element;

/**
 * InkBrush represents the brush element in InkML
 * its contained by the context element, as well as by description element
 * and in this way the brush of a trace is specified.
 * 
 * InkML has no further specification of what the brush could define.
 * Obviously color could be such a information. 
 * This class accepts a color string as an annotation to this element 
 * @author emanuel indermühle
 *
 */

public class InkBrush extends InkAnnotatedElement{
    public static final String INKML_NAME = "brush";
    
	public static final String COLOR = "color";
	public static final String COLOR_ERASER = "eraser";
	public static final String ID_PREFIX = "brush";
	
	
	public InkBrush(InkInk ink,String id) throws InkMLComplianceException{
		super(ink,id);

	}
	public InkBrush(InkInk ink) {
		super(ink);
	}

	/**
	 * Returns true if the brush has erasing functionality
	 * @return
	 */
	public boolean isEraser(){
		return hasColor() && getColor().equals(COLOR_ERASER); 
	}

	/**
	 * Test if this brush has a color specified
	 * @return True if a colo is specified
	 */
	public boolean hasColor() {
		return containsAnnotation(COLOR);
	}
	
	/**
	 * Returns the string representation of the color of this
	 * brush. If no color is specified, null is returned
	 * @return 
	 */
	public String getColor(){
		return getAnnotation(COLOR);
	}

	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element brush = parent.getOwnerDocument().createElement(INKML_NAME);
		brush.setAttribute(INKML_ATTR_ID, this.getId());
		super.exportToInkML(brush);
		parent.appendChild(brush);
	}
	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
	}
}
