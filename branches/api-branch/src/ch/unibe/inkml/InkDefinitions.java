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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.eindermu.Messenger;

/**
 * This class represents the definitions element within InkML documents. 
 * See <a href="http://www.w3.org/TR/2010/WD-InkML-20100527/#definitions">here</a>.
 * It contains definitions of different inkml elements which then can be referenced 
 * within the document. Elements specified here have no effect until referenced. 
 * 
 * This class serves an other purpose. It is a <b>directory</b> to all elements which can be 
 * identified by an id, see {@link InkUniqueElement}. These elements are not nessesairaly
 * defined within the definitions element, but are managed by this class.
 * 
 * @author emanuel
 *
 */
public class InkDefinitions extends HashMap<String,InkUniqueElement> implements InkElementInterface{
	
	private static final long serialVersionUID = -6448563169075416272L;

	public static final String INKML_NAME = "definitions";
	
	/**
	 * contains all elements that are contained by the the definitions element in the inkml tree.
	 * The directory task which is provieded by this class is handled by the Hashmap this class is extending. 
	 * To access the element contained by the definitions element
	 * use the methods enterElement, containsElement and getElement
	 */
	private List<InkUniqueElement> content = new ArrayList<InkUniqueElement>();
	
	/**
	 * The {@link InkInk} this definition is defined in.
	 */
	private InkInk ink;
	
	
	public InkDefinitions(InkInk ink) {
		this.ink = ink;
		ink.setDefinitions(this);
	}
	
	/**
	 * puts a document to the directory.
	 * @param el
	 */
	public void put (InkUniqueElement el){
		put(el.getId(),el);
	}
	
	/**
	 * removes a element from the definitions, and also from the directory. 
	 * @param key
	 */
	public void remove(String key){
	    this.content.remove(get(key));
	    super.remove(key);
	}
	
	
	/**
	 * Returns an element stored in the directory.
	 * Thest befor if the element is availabel with {@link #containsKey(Object)}.
	 * @param key Key referencing to the element in question.
	 * @return the requested element if its avaliable, throws NullPointerExeption if the 
	 * element is not availabel.
	 */
	public InkUniqueElement get(String key){
		if(!containsKey(key)){
			throw new NullPointerException("Element with key "+key+" is not yet defined in this document");
		}
		return super.get(key);
	}
	
	/**
	 * adds a element to the definitions. When exported to InkML this element will be written
	 * within the definitions element. It resides here, and only here.
	 * @param el
	 */
	public void enterElement(InkUniqueElement el){
		if(!el.hasId()){
			Messenger.warn(String.format("Element '%s' is defined content of element '%s' but has no xml:id attribute. This make no sense.", el.getLabel(),INKML_NAME));
		}
		this.content.add(el);
	}
	
	public boolean containsElement(String id){
		for(InkUniqueElement c : content){
			if(c.hasId() && c.getId().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	public InkUniqueElement getElement(String id){
		for(InkUniqueElement c : content){
			if(c.hasId() && c.getId().equals(id)){
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Create a unique Id (unique for this document) with the specified prefix.
	 * @param prefix
	 * @return
	 */
	public String createUniqueId(String prefix) {
		int count = 0;
		while(containsKey(prefix+count)){
			count ++;
		}
		return prefix+count;
	}

	/**
	 * @see InkElement#buildFromXMLNode(Element)
	 * @param node
	 * @throws InkMLComplianceException
	 */
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		for(Node child = node.getFirstChild(); child!= null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String n = child.getNodeName();
			if(n.equals(InkInkSource.INKML_NAME)){
				InkInkSource b = new InkInkSource(this.getInk());
				b.buildFromXMLNode((Element)child);
				this.enterElement(b);
			}
			else if(n.equals(InkBrush.INKML_NAME)){
				InkBrush b = new InkBrush(this.getInk());
				b.buildFromXMLNode((Element)child);
				this.enterElement(b);
			}
			else if(n.equals(InkCanvas.INKML_NAME)){
				InkCanvas c = new InkCanvas(this.getInk());
				c.buildFromXMLNode((Element)child);
				this.enterElement(c);
			}
			else if(n.equals(InkCanvasTransform.INKML_NAME)){
				InkCanvasTransform f = new InkCanvasTransform(this.getInk());
				f.buildFromXMLNode((Element)child);
				this.enterElement(f);
			}
			else if(n.equals(InkTraceFormat.INKML_NAME)){
				InkTraceFormat f = new InkTraceFormat(this.getInk());
				f.buildFromXMLNode((Element)child);
				this.enterElement(f);
			}
			else if(n.equals(InkMapping.INKML_NAME)){
				this.enterElement(InkMapping.mappingFactory(this.getInk(),(Element)child));
			}
			else if(n.equals(InkContext.INKML_NAME)){
				InkContext context =new InkContext(getInk());
				context.buildFromXMLNode((Element)child);
				this.enterElement(context);
			}
		}
	}


	/**
	 * @see InkElement#exportToInkML(Element)
	 * @param parent
	 * @throws InkMLComplianceException
	 */
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element definitionNode = parent.getOwnerDocument().createElement(INKML_NAME);
		parent.appendChild(definitionNode);
		for(InkElement i : this.content){
			i.exportToInkML(definitionNode);
		}
	}

	@Override
	public InkInk getInk() {
		return ink;
	}
}
