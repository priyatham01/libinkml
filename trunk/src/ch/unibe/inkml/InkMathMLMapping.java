package ch.unibe.inkml;

import org.w3c.dom.Element;

import ch.unibe.eindermu.utils.NotImplementedException;


public class InkMathMLMapping extends InkMapping {

	public InkMathMLMapping(InkInk ink) {
		super(ink);
		throw new NotImplementedException();
	}

	@Override
	public Type getType() {
		throw new NotImplementedException();
		//return null;
	}

	@Override
	protected void exportToInkMLHook(Element mappingNode)
			throws InkMLComplianceException {
		throw new NotImplementedException();
		
	}

	@Override
	public boolean isInvertible() {
		return false;
	}

    @Override
    public void backTransform(double[][] sourcePoints, double[][] points,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat)
            throws InkMLComplianceException {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
        
    }

    @Override
    public void transform(double[][] sourcePoints, double[][] points,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat)
            throws InkMLComplianceException {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

}
