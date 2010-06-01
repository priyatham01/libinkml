package ch.unibe.inkml.util;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import ch.unibe.eindermu.utils.GraphicsBackup;
import ch.unibe.inkml.InkTraceViewContainer;
import ch.unibe.inkml.InkTraceViewLeaf;

public abstract class TraceGraphVisitor extends TraceVisitor{

	
	private double strokeWidth = 1;
	private Graphics2D graphics;
	
	public void visitHook(InkTraceViewContainer container) {
		GraphicsBackup gb = new GraphicsBackup(getGraphics());
		super.visitHook(container);
		gb.reset();
	}

	public void visitHook(InkTraceViewLeaf leaf) {
		GraphicsBackup gb = new GraphicsBackup(getGraphics());
		paintLeaf(leaf);
		gb.reset();
	}
	
	protected void paintLeaf(InkTraceViewLeaf s) {
        BasicStroke stroke = new BasicStroke((float) getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        if(s.getBrush() != null && s.getBrush().isEraser()) {
            getGraphics().setColor(getGraphics().getBackground());
            stroke = new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
        getGraphics().setStroke(stroke);
        s.drawPolyLine(getGraphics());
    }
	
	
	public Graphics2D getGraphics() {
		return graphics;
	}
	public void setGraphics(Graphics2D graphics) {
		this.graphics = graphics;
	}
	public void setStrokeWidth(double d) {
		strokeWidth = d;
	}
	public double getStrokeWidth(){
		return strokeWidth;
	}
}
