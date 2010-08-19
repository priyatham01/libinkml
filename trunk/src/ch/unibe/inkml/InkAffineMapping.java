package ch.unibe.inkml;

import java.util.ArrayList;

import org.w3c.dom.Element;

import ch.unibe.inkml.InkChannel.ChannelName;



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
			InkBind b = new InkBind();
			b.source = c.getName();
			map.addBind(b);
		
			InkBind bt = new InkBind();
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

    private InkChannel.ChannelName[] targetChanneName;
    private InkTraceFormat cached_targetFormat; 
	
	/**
     * @param targetFormat
     * @return
	 * @throws InkMLComplianceException 
     */
    private InkChannel.ChannelName[] getTargetNames(InkTraceFormat targetFormat) throws InkMLComplianceException {
        if(cached_targetFormat != targetFormat){
            cached_targetFormat = targetFormat;
            ArrayList<InkBind> l= new ArrayList<InkBind>();
            for(InkBind b : this.getBinds()){
                if(b.hasTarget()){
                    l.add(b);
                }
            }
            targetChanneName = new InkChannel.ChannelName[l.size()];
            for(int i = 0;i<targetChanneName.length;i++){
                targetChanneName[i] = l.get(i).getTarget(targetFormat); 
            }
        }
        return targetChanneName;
    }

    private InkChannel.ChannelName[] sourceChanneName;
	private InkTraceFormat cached_sourceFormat;	
	/**
     * @param sourceFormat
     * @return
	 * @throws InkMLComplianceException 
     */
    private InkChannel.ChannelName[] getSourceNames(InkTraceFormat sourceFormat) throws InkMLComplianceException {
        if(cached_sourceFormat != sourceFormat){
            cached_sourceFormat = sourceFormat;
            ArrayList<InkBind> l= new ArrayList<InkBind>();
            for(InkBind b : this.getBinds()){
                if(b.hasSource()){
                    l.add(b);
                }
            }
            sourceChanneName = new InkChannel.ChannelName[l.size()];
            for(int i = 0;i<sourceChanneName.length;i++){
                sourceChanneName[i] = l.get(i).getSource(sourceFormat); 
            }
        }
        return sourceChanneName;
    } 
    

    public double[][] getMatrix() {
		return this.matrix.getMatrix();
	}


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

    @Override
    public void backTransform(double[][] sourcePoints, double[][] targetPoints,
            InkTraceFormat canvasFormat, InkTraceFormat sourceFormat) throws InkMLComplianceException {
        ChannelName[] sourceNames = getSourceNames(sourceFormat);
        int[] sourceIndices = new int[sourceNames.length];
        for(int i=0;i<sourceNames.length;i++){
            sourceIndices[i] = sourceFormat.indexOf(sourceNames[i]);
        }
        ChannelName[] targetNames = getTargetNames(canvasFormat);
        int[] targetIndices = new int[targetNames.length];
        for(int i=0;i<targetNames.length;i++){
            targetIndices[i] = canvasFormat.indexOf(targetNames[i]);
        }
        this.matrix.backtransform(sourcePoints,targetPoints,sourceIndices,targetIndices);
    }

    @Override
    public void transform(double[][] sourcePoints, double[][] targetPoints,
            InkTraceFormat sourceFormat, InkTraceFormat targetFormat) throws InkMLComplianceException {
        ChannelName[] sourceNames = getSourceNames(sourceFormat);
        int[] sourceIndices = new int[sourceNames.length];
        for(int i=0;i<sourceNames.length;i++){
            sourceIndices[i] = sourceFormat.indexOf(sourceNames[i]);
        }
        ChannelName[] targetNames = getTargetNames(targetFormat);
        int[] targetIndices = new int[targetNames.length];
        for(int i=0;i<targetNames.length;i++){
            targetIndices[i] = targetFormat.indexOf(targetNames[i]);
        }
        this.matrix.transform(sourcePoints,targetPoints,sourceIndices,targetIndices);
    }
    
    public InkMapping clone(InkInk ink){
        InkAffineMapping n = (InkAffineMapping) super.clone(ink);
        n.targetNr = targetNr;
        n.sourceNr = sourceNr;
        n.matrix = matrix.clone(ink);
        return n;
    }

}
