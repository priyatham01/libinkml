package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.inkml.util.Formatter;
import ch.unibe.inkml.util.NumberFormatter;

public class InkChannelDouble extends InkChannel {

	private double defaultValue;
	private double max;
	private double min;

	public InkChannelDouble(InkInk ink) {
		super(ink);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
    @Override
	public Formatter formatterFactory() {
		return new NumberFormatter(this);
	}

	@Override
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		if(!defaultValue.isEmpty()){
			this.defaultValue = Double.parseDouble(defaultValue);
		}else{
			this.defaultValue = 0;
		}
		
	}

	@Override
	public double getMax() {
		return this.max;
	}
		@Override
	public double getMin() {
		return this.min;
	}
	

	@Override
	public void setMax(String max) {
		if(!max.isEmpty()){
			this.max = Double.parseDouble(max);
			this.isMax = true;
		}else{
			this.isMax = false;
		}
	}

	@Override
	public void setMin(String min) {
		if(!min.isEmpty()){
			this.min = Double.parseDouble(min);
			this.isMin = true;
		}else{
			this.isMin = false;
		}
	}

	@Override
	public Object parse(String value) {
		return (Object) Double.parseDouble(value);
	}
	
	public Type getType() {
		return InkChannel.Type.DECIMAL;
	}

	@Override
	protected void exportDefaultToInkML(Element c) {
		if(Math.abs(this.defaultValue) > 0.000001){
			c.setAttribute("default", Double.toString(this.defaultValue));
		}
		
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public double doublize(Object o) {
        return (Double) o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object objectify(double d) {
        return (Object)d;
    }

}
