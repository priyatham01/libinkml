package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.NotImplementedException;


public class InkLookUpMapping extends InkMapping {

	public InkLookUpMapping(InkInk ink) {
		super(ink);
		throw new NotImplementedException();
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exportToInkMLHook(Element mappingNode)
			throws InkMLComplianceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInvertible() {
		return false;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void backTransform(double[][] sourcePoints, double[][] points,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat)
            throws InkMLComplianceException {
        //TODO
        throw new NotImplementedException();
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(double[][] sourcePoints, double[][] points,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat)
            throws InkMLComplianceException {
      //TODO
        throw new NotImplementedException();
        
    }


}
