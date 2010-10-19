/*
 * 
 * Copyright (C) 2007  Emanuel Indermühle <eindermu@iam.unibe.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 */
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
