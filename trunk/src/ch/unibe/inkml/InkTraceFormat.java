package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.inkml.InkChannel.Name;

public class InkTraceFormat extends InkUniqueElement implements Iterable<InkChannel>{
	
	private static InkTraceFormat defaultTraceFormat;
	public static InkTraceFormat getDefaultTraceFormat(InkInk ink) {
		if(defaultTraceFormat == null){
			try {
				InkTraceFormat format= new InkTraceFormat(ink);
				InkChannel c1 = new InkChannelDouble(ink);
				c1.setName(InkChannel.Name.X);
				c1.setUnits("em");
				format.addChannel(c1);
				InkChannel c2 = new InkChannelDouble(ink);
				c2.setName(InkChannel.Name.Y);
				c2.setUnits("em");
				format.addChannel(c2);
				defaultTraceFormat = format;
			} catch (InkMLComplianceException e) {
				System.err.println("Its a Bug, please fix it, or contact developer");
				e.printStackTrace();
				//Will not happen here, unless it is a bug
			}
		}
		return defaultTraceFormat;
	}
	
	private List<InkChannel> channels;
	private Map<InkChannel.Name,Integer> index;
	
	public InkTraceFormat(InkInk ink,String id){
		super(ink, id);
		this.initialize();
	}
	
	public InkTraceFormat(InkInk ink) {
		super(ink);
		this.initialize();
	}

	private void initialize(){
		channels = new ArrayList<InkChannel>();
		index = new HashMap<InkChannel.Name,Integer>();
	}
	
	

	public boolean testInkAnnoCompliance(){
		if(channels.size() < 1){
			return false;
		}
		int i = 0;
		for(InkChannel c : channels){
			if(c.getName() == InkChannel.Name.X){
					i = i & 1;
			}else if(c.getName() == InkChannel.Name.Y){
					i = i & 2;
			}
		}
		if(i != 3){
			return false;
		}
		return true;
	}
	
	
	public void addChannel(InkChannel channel) throws InkMLComplianceException{
	    if(channels.size() > 0 && !channel.isIntermittent() &&  channels.get(channels.size()-1).isIntermittent()){
	        throw new InkMLComplianceException("Non intermittent channels can not be added after intermittent channels");
	    }
		channels.add(channel);
		try{
		    this.addName(channel.getName(),channels.size()-1);
		}catch(InkMLComplianceException e){
		    channels.remove(channel);
		    throw e;
		}
		
	}
	public void addIntermittentChannel(InkChannel channel) throws InkMLComplianceException{
	    channel.setIntermittent(true);
	    addChannel(channel);
	}
	private void addName (InkChannel.Name name,int i) throws InkMLComplianceException{
		if(index.containsKey(name)){
			throw new InkMLComplianceException("More than one channel with same name specified in InkTraceFormat");
		}
		index.put(name,i);
	}

	public void exportToInkML(Element definition) throws InkMLComplianceException {
		Document d = definition.getOwnerDocument();
		Element tf = d.createElement("traceFormat");
		tf.setAttribute("xml:id", this.getId());
		definition.appendChild(tf);
		boolean noIntChannelsUntilNow = true;
		Element i = d.createElement("intermittentChannels");
		
		for(InkChannel c : channels){
            if(c.isIntermittent()){
                if(noIntChannelsUntilNow){
                    noIntChannelsUntilNow = false;
                    tf.appendChild(i);
                }
                c.exportToInkML(i);
			}else{
			    c.exportToInkML(tf);
			}
		}
	}


	public Iterator<InkChannel> iterator() {
		return getChannels().iterator();
	}

	@Override
	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element el = (Element)child;
			if(el.getNodeName().equals("channel")){
				InkChannel c = InkChannel.channelFactory(this.getInk(),el);
				this.addChannel(c);
				
			}else if (el.getNodeName().equals("intermittentChannels")){
				for(Node ichild = el.getFirstChild(); ichild != null; ichild = ichild.getNextSibling()){
					if(ichild.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					Element iel = (Element)ichild;
					if(iel.getNodeName().equals("channel")){
						InkChannel c = InkChannel.channelFactory(this.getInk(),iel);
						this.addIntermittentChannel(c);
					}
				}
			}
		}
	}

	public InkChannel getChannel(Name x) {
		for(InkChannel c : getChannels()){
			if(c.getName() == x){
				return c;
			}
		}
		return null;
	}

	public List<InkChannel> getChannels() {
		return new ArrayList<InkChannel>(this.channels);
	}

	public List<InkChannel> getMandatoryChannels() {
		return new ArrayList<InkChannel>(this.channels);
	}

	public boolean containsChannel(Name s) {
		for(InkChannel c : this.getChannels()){
			if(c.getName() == s){
				return true;
			}
		}
		return false;
	}

	public int getChannelCount() {
		return this.channels.size();
	}

    /**
     * @param name
     * @param o
     * @return
     */
    public double doubleize(Name name, Object o) {
        return channels.get(index.get(name)).doublize(o);
    }
    
    public Object objectify(Name name,double d){
        return channels.get(index.get(name)).objectify(d);
    }

    /**
     * @param name
     * @return
     */
    public int indexOf(Name name) {
        return index.get(name);
    }
    
    public Map<Name,Integer> getIndex(){
        return index;
    }
}
