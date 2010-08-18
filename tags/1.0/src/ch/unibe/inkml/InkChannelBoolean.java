package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.inkml.util.BooleanFormatter;
import ch.unibe.inkml.util.Formatter;


public class InkChannelBoolean extends InkChannel {

	public InkChannelBoolean(InkInk ink) {
		super(ink);
	}

	private boolean defaultValue;
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
    @Override
	public Formatter formatterFactory() {
		return new BooleanFormatter(this);
	}

	@Override
	public Object getDefaultValue() {
		return new Boolean(this.defaultValue);
	}

	@Override
	public double getMax() throws InkMLComplianceException {
		throw new InkMLComplianceException("A boolean channel has no maximum or minimum");
	}

	@Override
	public double getMin() throws InkMLComplianceException {
		throw new InkMLComplianceException("A boolean channel has no maximum or minimum");
	}

	@Override
	public Object parse(String value) {
		return (Object) Boolean.parseBoolean(value.substring(0,1));
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = (Boolean) parse(defaultValue);
	}

	@Override
	public void setMax(String max) throws InkMLComplianceException {
		throw new InkMLComplianceException("A boolean channel has no maximum or minimum");
	}

	@Override
	public void setMin(String min) throws InkMLComplianceException {
		throw new InkMLComplianceException("A boolean channel has no maximum or minimum");
	}

	@Override
	public Type getType() {
		return InkChannel.Type.BOOLEAN;
	}

	@Override
	protected void exportDefaultToInkML(Element c) {
		if(this.defaultValue){
			c.setAttribute("default", "T");
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public double doublize(Object o) {
        return (((Boolean)o).booleanValue())? 1:0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object objectify(double d) {
        return new Boolean(d>0.5);
    }

}
