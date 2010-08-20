package ch.unibe.inkml.util;

import ch.unibe.inkml.InkChannel;
import ch.unibe.inkml.InkMLComplianceException;
import ch.unibe.inkml.InkTracePoint;



abstract public class Formatter {
	private InkChannel c;
	private double lastValue = 0;
	private boolean hasLastValue = false;
	public Formatter(InkChannel c) {
		this.c = c;
	}
	
	abstract public String getNext(InkTracePoint sp);
	
	public InkChannel getChannel(){
		return c;
	}
	
	abstract public String getNext(double next);
	abstract protected String valueOf(double n);

	public void setLastValue(double value){
		lastValue = value;
		hasLastValue = true;
	}
	public void unsetLastValue(){
        lastValue = 0;
        hasLastValue = false;
    }
	public double getLastValue() {
		return this.lastValue;
	}
    /**
     * @return True if the last value is known, false otherwise
     */
    protected boolean hasLastValue() {
        return hasLastValue;
    }


    /**
     * Return the double extracted from the stoken in the formated string.
     * @param String to extract the double value from
     * @return the double which has been extracted from the string in the current context
     * @throws InkMLComplianceException 
     */
    public abstract double consume(String result) throws InkMLComplianceException;
	
}
