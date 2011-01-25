package ch.unibe.inkml;

import ch.unibe.inkml.InkChannel.ChannelName;

public class InkBind {
    
    public static final String INKML_NAME = "bind";
    public static final String INKML_ATTR_SOURCE = "source";
    public static final String INKML_ATTR_TARGET = "target";
    public static final String INKML_ATTR_COLUMN = "column";
    public static final String INKML_ATTR_VARIABLE = "variable";
    

	public InkChannel.ChannelName source,target;
	public String column,variable;

	public ChannelName getSource(InkTraceFormat format){
		return getValue(null,source,format);
	}
	public ChannelName getTarget(ChannelName s, InkTraceFormat format){
		return getValue(s,target,format);
	}		
	private ChannelName getValue(ChannelName standard, ChannelName value, InkTraceFormat format){
		if(value == null || !format.containsChannel(value)){
			if(standard != null){
				return standard;
			}
		}
		return value;
	}
	public boolean hasSource() {
		return source != null;
	}
	public boolean hasTarget() {
		return target != null;
	}
	public ChannelName getTarget(InkTraceFormat format) {
		return getTarget(null,format);
	}
	
	public InkBind clone(){
	    InkBind newBind = new InkBind();
	    newBind.target = target;
	    newBind.column = column;
	    newBind.source = source;
	    newBind.variable = variable;
	    return newBind;
	}
}
