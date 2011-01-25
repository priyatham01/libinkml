/**
 * 
 */
package ch.unibe.inkml;

import org.w3c.dom.Element;

/**
 * The <sampleRate> element captures the rate at which ink samples are reported
 * by the ink source. Many devices report at a uniform rate; other devices may
 * skip duplicate points or report samples only when there is a change in
 * direction. This is indicated using the uniform attribute, which must be
 * designated "false" (non-uniform) if any pen-down points are skipped or if the
 * sampling is irregular.
 * 
 * A time channel should be used to get time information when the sampling rate
 * is not uniform. When the sampling rate is not uniform, the value attribute of
 * the <sampleRate> element specifies the maximum sampling rate.
 * 
 * @see http://www.w3.org/TR/InkML/#sampleRate
 * @author emanuel
 * 
 */
public class InkSamplerate extends InkElement {

    public static final String INKML_ATTR_UNIFORM = "uniform";
    public static final String INKML_ATTR_VALUE = "value";

    public static final String INKML_NAME = "sampleRate";

    /**
     * Sampling uniformity: Is the sample rate consistent, with no dropped
     * points? Required: no, Default: true
     */
    private boolean uniform;

    /**
     * The basic sample rate in samples/second.
     */
    private double value;
    
    
    /**
     * Constructor adding the ink, the document which contains this element
     * @param ink
     */
    public InkSamplerate(InkInk ink) {
        super(ink);
    }

    
    //setters and getters
    
    /**
     * Sampling uniformity: Is the sample rate consistent, with no dropped
     * points?
     * @return the uniform
     */
    public boolean isUniform() {
        return uniform;
    }


    /**
     * Sampling uniformity: Is the sample rate consistent, with no dropped
     * points?
     * @param uniform the uniform to set
     */
    public void setUniform(boolean uniform) {
        this.uniform = uniform;
    }


    /**
     * The basic sample rate in samples/second.
     * @return the value
     */
    public double getValue() {
        return value;
    }


    /**
     * The basic sample rate in samples/second.
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    

    
    @Override
    public void buildFromXMLNode(Element node) throws InkMLComplianceException {
        uniform = true;
        if (node.hasAttribute(INKML_ATTR_UNIFORM)) {
            String answer = node.getAttribute(INKML_ATTR_UNIFORM)
                    .toLowerCase();
            if (answer.equals("false")) {
                uniform = false;
            }
        }

        if (!node.hasAttribute(INKML_ATTR_VALUE)) {
            throw new InkMLComplianceException(
                    "sampleRate element must contain value attribute");
        }
        try {
            value = Double
                    .parseDouble(node.getAttribute(INKML_ATTR_VALUE));
        } catch (NumberFormatException e) {
            throw new InkMLComplianceException(String.format(
                    "The attribute '%s' of element '%s' must contain double value.",INKML_ATTR_VALUE,INKML_NAME));
        }
    }

    @Override
    public void exportToInkML(Element parent) throws InkMLComplianceException {
        if (!parent.getNodeName().equals(InkInkSource.INKML_NAME)) {
            throw new InkMLComplianceException(
                    String
                            .format(
                                    "Element '%s' may be contained by element '%s' but not by '%s'",
                                    INKML_NAME,
                                    InkInkSource.INKML_NAME, parent
                                            .getNodeName()));
        }
        Element sampleRate = parent.getOwnerDocument().createElement(
                INKML_NAME);
        if (!uniform) {
            sampleRate.setAttribute(INKML_ATTR_UNIFORM, "false");
        }
        sampleRate.setAttribute(INKML_ATTR_VALUE, Double.toString(value));
        parent.appendChild(sampleRate);
    }

}
