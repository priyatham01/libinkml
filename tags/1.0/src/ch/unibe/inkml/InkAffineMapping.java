package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ch.unibe.inkml.InkChannel.Name;



public class InkAffineMapping extends InkMapping {
	private InkMatrix matrix;
	private int targetNr;
	private int sourceNr;

	public static InkAffineMapping createIdentityInkAffinMapping(InkInk ink,InkTraceFormat sourceFormat, InkTraceFormat targetFormat){
		InkAffineMapping map = new InkAffineMapping(ink);
		int count = 0;
		for(InkChannel c : sourceFormat.getChannels()){
			if(!targetFormat.containsChannel(c.getName())){
				continue;
			}
			count++;
			InkBind b = new InkBind(ink);
			b.source = c.getName();
			map.addBind(b);
		
			InkBind bt = new InkBind(ink);
			bt.target = c.getName();
			map.addBind(bt);
		}
		InkMatrix m = new InkMatrix(ink);
		double[][] fm = new double[count][count];
		double[] tr = new double[count];
		for(int i = 0;i<count;i++){
			fm[i][i] = 1;
		}
		m.setMatrix(fm,tr);
		map.matrix = m;
		return map;
	}
	
	public InkAffineMapping(InkInk ink) {
		super(ink);
	}

	@Override
	public Type getType() {
		return Type.AFFINE;
	}
	
	public void buildFromXMLNode(Element node)throws InkMLComplianceException {
		super.buildFromXMLNode(node);
		Element matrixNode = (Element) node.getElementsByTagName("matrix").item(0);
		if(matrixNode == null){
			throw new InkMLComplianceException("A mapping with @type=\"matrix\" must contain a matrix element");
		}
		matrix = new InkMatrix(this.getInk());
		matrix.buildFromXMLNode(matrixNode);
	}
	
	public InkMatrix getInkMatrix(){
		return this.matrix;
	}
	
	protected void exportToInkMLHook(Element mappingNode) throws InkMLComplianceException {
		matrix.exportToInkML(mappingNode);
	}

	/*
	public void transform(InkTracePoint source, InkTracePoint target,
			InkTraceFormat sourceFormat, InkTraceFormat targetFormat)
			throws InkMLComplianceException {
		double[] values = new double[getSourceD()];
		int t = 0;
		for(InkChannel.Name b : this.getSourceNames(sourceFormat)){
			values[t++] = source.getDouble(b,sourceFormat);
		}
		values = matrix.transform(values);
		int i = 0;
		for(InkChannel.Name b : this.getTargetNames(targetFormat)){
			target.set(b, values[i++],targetFormat);
		}
	}*/

    private InkChannel.Name[] targetChanneName;
    private InkTraceFormat cached_targetFormat; 
	
	/**
     * @param targetFormat
     * @return
	 * @throws InkMLComplianceException 
     */
    private InkChannel.Name[] getTargetNames(InkTraceFormat targetFormat) throws InkMLComplianceException {
        if(cached_targetFormat != targetFormat){
            cached_targetFormat = targetFormat;
            ArrayList<InkBind> l= new ArrayList<InkBind>();
            for(InkBind b : this.getBinds()){
                if(b.hasTarget()){
                    l.add(b);
                }
            }
            targetChanneName = new InkChannel.Name[l.size()];
            for(int i = 0;i<targetChanneName.length;i++){
                targetChanneName[i] = l.get(i).getTarget(targetFormat); 
            }
        }
        return targetChanneName;
    }

    private InkChannel.Name[] sourceChanneName;
	private InkTraceFormat cached_sourceFormat;	
	/**
     * @param sourceFormat
     * @return
	 * @throws InkMLComplianceException 
     */
    private InkChannel.Name[] getSourceNames(InkTraceFormat sourceFormat) throws InkMLComplianceException {
        if(cached_sourceFormat != sourceFormat){
            cached_sourceFormat = sourceFormat;
            ArrayList<InkBind> l= new ArrayList<InkBind>();
            for(InkBind b : this.getBinds()){
                if(b.hasSource()){
                    l.add(b);
                }
            }
            sourceChanneName = new InkChannel.Name[l.size()];
            for(int i = 0;i<sourceChanneName.length;i++){
                sourceChanneName[i] = l.get(i).getSource(sourceFormat); 
            }
        }
        return sourceChanneName;
    } 
    

    public double[][] getMatrix() {
		return this.matrix.getMatrix();
	}

    /*
	@Override
	public void backTransform(InkTracePoint source, InkTracePoint canvasPoint,
			InkTraceFormat canvasFormat, InkTraceFormat sourceFormat)
			throws InkMLComplianceException {
		double[] values = new double[getTargetD()];
		int t = 0;
		for(InkBind b : this.getBinds()){
			if(!b.hasTarget()){
				continue;
			}
			values[t++] = canvasPoint.getDouble(b.getTarget(canvasFormat),canvasFormat);
		}
		values = matrix.backTransform(values);
		int i = 0;
		for(InkBind b : this.getBinds()){
			if(!b.hasSource()){
				continue;
			}
			source.set(b.getSource(sourceFormat), values[i++], sourceFormat);
		}
		
	}
	*/
	


	public int getTargetD() {
		if(targetNr == 0){
			for(InkBind b : this.getBinds()){
				if(b.hasTarget()){
					targetNr++;
				}
			}
		}
		return targetNr;
	}
	public int getSourceD() {
		if(sourceNr == 0){
			for(InkBind b : this.getBinds()){
				if(b.hasSource()){
					sourceNr++;
				}
			}
		}
		return sourceNr;
	}

	@Override
	public boolean isInvertible() {
		return matrix.isInvertible();
	}

    /**
     * {@inheritDoc}
     * @throws InkMLComplianceException 
     */
    @Override
    public void backTransform(double[][] sourcePoints, double[][] targetPoints,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat) throws InkMLComplianceException {
        Name[] sourceNames = getSourceNames(sourceFormat);
        int[] sourceIndices = new int[sourceNames.length];
        for(int i=0;i<sourceNames.length;i++){
            sourceIndices[i] = sourceFormat.indexOf(sourceNames[i]);
        }
        Name[] targetNames = getTargetNames(canvasFormat);
        int[] targetIndices = new int[targetNames.length];
        for(int i=0;i<targetNames.length;i++){
            targetIndices[i] = canvasFormat.indexOf(targetNames[i]);
        }
        this.matrix.backtransform(sourcePoints,targetPoints,sourceIndices,targetIndices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(double[][] sourcePoints, double[][] targetPoints,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat) throws InkMLComplianceException {
        Name[] sourceNames = getSourceNames(sourceFormat);
        int[] sourceIndices = new int[sourceNames.length];
        for(int i=0;i<sourceNames.length;i++){
            sourceIndices[i] = sourceFormat.indexOf(sourceNames[i]);
        }
        Name[] targetNames = getTargetNames(targetFormat);
        int[] targetIndices = new int[targetNames.length];
        for(int i=0;i<targetNames.length;i++){
            targetIndices[i] = targetFormat.indexOf(targetNames[i]);
        }
        this.matrix.transform(sourcePoints,targetPoints,sourceIndices,targetIndices);
    }

}
