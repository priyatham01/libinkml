package ch.unibe.inkml;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ch.unibe.eindermu.Messenger;

public class InkInkSource extends InkUniqueElement {

    public static final String INKML_NAME = "inkSource";
    public static final String INKML_ATTR_MANUFACTURER = "manufacturer";
    public static final String INKML_ATTR_MODEL = "model";
    public static final String INKML_ATTR_SERIAL_NO = "serialNo";
    public static final String INKML_ATTR_SPECIFICATION_REF = "specificationRef";
    public static final String INKML_ATTR_DESCRIPTION = "description";
    public static final String INKML_LATENCY_NAME = "latency";
    public static final String INKML_LATTENCY_ATTR_VALUE = "value";
    public static final String ID_PREFIX = "is";

    /**
     * String identifying the digitizer device manufacturer.
     */
    private String manufacturer;

    /**
     * String identifying the digitizer model.
     */
    private String model;

    /**
     * Unique manufacturer (or other) serial number for the device.
     */
    private String serialNo;

    /**
     * URI of a page providing detailed or additional specifications.
     */
    private URI specificationRef;

    /**
     * String describing the ink source, especially one implemented in software.
     */
    private String description;

    /**
     * Trace format - regular and intermittent channels reported by source (Just
     * for information i guess).
     */
    private InkTraceFormat traceFormat;

    /**
     * Sampling rate of the specifying device
     */
    private InkSamplerate sampleRate;

    /**
     * The latency element captures the basic device latency that applies to all
     * channels, in milliseconds, from physical action to the API time stamp.
     * This is specified at the device level, since all channels often are
     * subject to a common processing and communications latency.
     * 
     * @see http://www.w3.org/TR/InkML/#latency
     */
    private double latency;
    private boolean isLatency = false;

    
    
    /**
     * Construct InkSource specifying the containing document (ink).
     * 
     * @param ink
     *            containing document
     */
    public InkInkSource(InkInk ink) {
        super(ink);
    }

    /**
     * Construct InkSource specifying the containing document (ink) and a unique
     * id.
     * 
     * @param ink
     *            containing document
     * @param id
     *            unique id
     * @throws InkMLComplianceException 
     */
    public InkInkSource(InkInk ink, String id) throws InkMLComplianceException {
        super(ink, id);
    }
    
    /**
     * Returns the {@link #manufacturer}.
     * @return manufacturer you get.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Define the {@link #manufacturer}.
     * @param manufacturer to set.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the {@link #model}.
     * @return model you get.
     */
    public String getModel() {
        return model;
    }

    /**
     * Define the {@link #model}.
     * @param model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the {@link #serialNo}.
     * @return serialNo you get.
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Define the {@link #serialNo}.
     * @param serialNo to set
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * Returns the {@link #description}.
     * @return description you get.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define the {@link #description}.
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * URI of a page providing detailed or additional specifications. returns
     * null if undefined
     * 
     * @return the specificationRef
     */
    public URI getSpecificationRef() {
        return specificationRef;
    }

    /**
     * URI of a page providing detailed or additional specifications. Set null
     * to undefine.
     * 
     * @param specificationRef
     *            the specificationRef to set
     */
    public void setSpecificationRef(URI specificationRef) {
        this.specificationRef = specificationRef;
    }

    /**
     * Get the {@link #latency}.
     * The latency element captures the basic device latency that applies to all
     * channels, in milliseconds, from physical action to the API time stamp.
     * This is specified at the device level, since all channels often are
     * subject to a common processing and communications latency.
     * 
     * Test first if latency is defined with {@link #isLatency()}
     * 
     * @return the latency
     */
    public double getLatency() {
        return latency;
    }

    /**
     * Set {@link #latency} to specified value.
     * 
     * @param latency the latency to set
     */
    public void setLatency(double latency) {
        this.latency = latency;
        isLatency = true;
    }

    /**
     * Set the {@link #latency} to undefined.
     */
    public void unsetLatency() {
        isLatency = false;
    }

    /**
     * Test if the {@link #latency} has been defined
     * @return the isLatencySet
     */
    public boolean isLatency() {
        return isLatency;
    }
    

    public void setTraceFormat(InkTraceFormat traceFormat) {
        this.traceFormat = traceFormat; 
    }

    public InkTraceFormat getTraceFormat(){
        return this.traceFormat;
    }
    
    
    public void setSampleRate(double d){
        if(sampleRate == null){
            sampleRate = new InkSamplerate(getInk());
        }
        sampleRate.setValue(d);
    }
    
    public boolean hasSampleRate(){
        return sampleRate != null;
    }
    
    public double getSampleRate(){
        if(sampleRate == null){
            return 0;
        }
        return sampleRate.getValue();
    }
    
    public void setSampleRateUniform(boolean isuniform){
        if(sampleRate == null){
            sampleRate = new InkSamplerate(getInk());
        }
        sampleRate.setUniform(isuniform);
    }
    
    @Override
    public void buildFromXMLNode(Element node) throws InkMLComplianceException {
        super.buildFromXMLNode(node);
        {
            NodeList tfs = node
                .getElementsByTagName(InkTraceFormat.INKML_NAME);
            if (tfs.getLength() > 0) {
                traceFormat = new InkTraceFormat(getInk());
                traceFormat.buildFromXMLNode((Element) tfs.item(0));
                if (tfs.getLength() > 1) {
                    Messenger
                        .warn(String
                            .format(
                                "Element '%s' by error contains more than one element '%s'. Ignoring all but first.",
                                INKML_NAME,
                                InkTraceFormat.INKML_NAME));
                }
            }
        }

        {
            NodeList srs = node
                .getElementsByTagName(InkSamplerate.INKML_NAME);
            if (srs.getLength() > 0) {
                sampleRate = new InkSamplerate(getInk());
                sampleRate.buildFromXMLNode((Element) srs.item(0));
                if (srs.getLength() > 1) {
                    Messenger
                        .warn(String
                            .format(
                                "Element '%s' by error contains more than one element '%s'. Ignoring all but first.",
                                INKML_NAME,
                                InkSamplerate.INKML_NAME));
                }
            }
        }

        {
            isLatency = false;
            NodeList las = node
                .getElementsByTagName(INKML_LATENCY_NAME);
            if (las.getLength() > 0) {
                Element l = (Element) las.item(0);
                if (!l.hasAttribute(INKML_LATTENCY_ATTR_VALUE)) {
                    Messenger
                        .warn(String
                            .format(
                                "Element '%s' is optional, its required attribute '%s' is missing. I will ignore whole element",
                                INKML_LATENCY_NAME,
                                INKML_LATTENCY_ATTR_VALUE));
                } else {
                    try {
                        latency = Double.parseDouble(l
                            .getAttribute(INKML_LATTENCY_ATTR_VALUE));
                        isLatency = true;
                    } catch (NumberFormatException e) {
                        throw new InkMLComplianceException(
                            String
                                .format(
                                    "The attribute '%s' of element '%s' must contain double value.",
                                    INKML_LATTENCY_ATTR_VALUE,
                                    INKML_LATENCY_NAME));
                    }
                }
            }
        }

        model = loadAttribute(node, INKML_ATTR_MODEL, null);
        manufacturer = loadAttribute(node, INKML_ATTR_MANUFACTURER, null);
        serialNo = loadAttribute(node, INKML_ATTR_SERIAL_NO, null);

        if(node.hasAttribute(INKML_ATTR_SPECIFICATION_REF)){
            try {
                specificationRef = new URI(loadAttribute(node,
                    INKML_ATTR_SPECIFICATION_REF, null));
            } catch (URISyntaxException e) {
                throw new InkMLComplianceException(String.format(
                    "In element '%s' the attribute '%s' must conform to URI spec.",
                    INKML_NAME, INKML_ATTR_SPECIFICATION_REF));
            }
        }
        
        description = loadAttribute(node, INKML_ATTR_DESCRIPTION, null);
    }

    @Override
    public void exportToInkML(Element parent) throws InkMLComplianceException {
        Element inkSourceNode = parent.getOwnerDocument().createElement(
            INKML_NAME);
        writeAttribute(inkSourceNode, INKML_ATTR_ID, getId(), null);
        writeAttribute(inkSourceNode, INKML_ATTR_MODEL, model, null);
        writeAttribute(inkSourceNode, INKML_ATTR_MANUFACTURER, manufacturer,
            null);
        if (specificationRef != null) {
            writeAttribute(inkSourceNode, INKML_ATTR_SPECIFICATION_REF,
                specificationRef.toString(), "");
        }
        writeAttribute(inkSourceNode, INKML_ATTR_SERIAL_NO, serialNo, null);
        writeAttribute(inkSourceNode, INKML_ATTR_DESCRIPTION, description, null);

        if (traceFormat != null) {
            traceFormat.exportToInkML(inkSourceNode);
        }

        if (sampleRate != null) {
            sampleRate.exportToInkML(inkSourceNode);
        }
        if (isLatency) {
            Element latencyNode = parent.getOwnerDocument().createElement(
                INKML_LATENCY_NAME);
            latencyNode.setAttribute(INKML_LATTENCY_ATTR_VALUE, Double
                .toString(latency));
            inkSourceNode.appendChild(latencyNode);
        }
        parent.appendChild(inkSourceNode);
    }


}
