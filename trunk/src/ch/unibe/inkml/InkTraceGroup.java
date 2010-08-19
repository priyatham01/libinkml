/*
 * Created on 23.07.2007
 *
 * Copyright (C) 2007  Emanuel Indermühle <emanuel@inthemill.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 */

package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.unibe.eindermu.utils.Aspect;
import ch.unibe.eindermu.utils.Observer;
import ch.unibe.inkml.util.Timespan;
import ch.unibe.inkml.util.TraceBound;
import ch.unibe.inkml.util.TraceVisitor;


public class InkTraceGroup extends InkTrace{
    
	private String brushRef;
	
	private List<InkTrace> traces = new ArrayList<InkTrace>();
    
    private TraceBound bounds = new TraceBound();

    private Timespan timespan;
	
	
    public InkTraceGroup(InkInk ink, InkTraceGroup parent) {
		super(ink, parent);
		this.initialize();
	}

	
    
	public void initialize() {
        registerFor(InkTrace.ON_CHANGE, new Observer(){
            public void notifyFor(Aspect event, Object subject) {
                bounds = null;
                timespan = null;
                if(InkTraceGroup.this.getParent() == null) {
                    getInk().notifyObserver(InkInk.ON_CHANGE);
                } else {
                    getParent().notifyObserver(InkTrace.ON_CHANGE);
                }
            }
        });
        registerFor(InkTrace.ON_TRACE_REMOVED, new Observer(){
            @Override
            public void notifyFor(Aspect event, Object subject) {
                if(getParent() != null) {
                    getParent().notifyObserver(InkTrace.ON_TRACE_REMOVED);
                }
            }
        });
    }
    
    
    public void refreshBound() {
        if(this.traces.size() == 0) {
            this.bounds = null;
        } else {
            this.bounds = new TraceBound(this.traces.get(0).getBounds());
            for(InkTrace s : this.getTraces()) {
                this.bounds.add(s.getBounds());
            }
        }
    }
    
    public Timespan getTimeSpan() {
        if(timespan == null && this.traces.size() > 0){
            timespan = new Timespan();
            for(InkTrace s : this.traces) {
                if(s.getTimeSpan() != null){
                    timespan.add(s.getTimeSpan());
                }
            }
        }
        return timespan;
    }
    
    public Iterator<InkTracePoint> iterator() {
        return this.pointIterable().iterator();
    }
    
    
    public Iterable<InkTracePoint> pointIterable(){
        return new Iterable<InkTracePoint>() {
            Iterator<InkTracePoint> it  = new Iterator<InkTracePoint> (){
                private Iterator<InkTrace> traceIterator = getTraces().iterator();
                private Iterator<InkTracePoint> current = null; 
                public boolean hasNext() {
                    while(current == null || !current.hasNext()){
                        if(traceIterator.hasNext()){
                            current = traceIterator.next().pointIterable().iterator();
                        }else{
                            return false;
                        }
                    }
                    return true;
                }
                public InkTracePoint next() {
                    return current.next();
                }
    
                public void remove() {
                    current.remove();
                }
            };
            public Iterator<InkTracePoint> iterator() {
                return it;
            }
        };

    }

    
    public List<InkTrace> getTraces() {
        return this.traces;
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public TraceBound getBounds() {
        return this.bounds;
    }

	public List<InkTrace> getFlattenedTraceLeafs() {
		List<InkTrace> result = new ArrayList<InkTrace>();
		for(InkTrace s : traces){
			if(s.isLeaf()){
				result.add(s);
			}else{
				result.addAll(((InkTraceGroup)s).getFlattenedTraceLeafs());
			}
		}
		return result;
	}
	
	public List<InkTrace> getFlattenedTraces() {
        List<InkTrace> result = new ArrayList<InkTrace>();
        for(InkTrace s : traces){
            if(s.isLeaf()){
                result.add(s);
            }else{
                result.addAll(((InkTraceGroup)s).getFlattenedTraces());
            }
        }
        return result;
    }
	
	public InkBrush getBrush(){
		if(this.brushRef != null){
			return (InkBrush) this.getInk().getDefinitions().get(this.brushRef);
		}else if(this.hasLocalContext() && this.getLocalContext().getBrush() != null){
			return this.getLocalContext().getBrush();
		}else if(!this.isRoot()){
			return this.getParent().getBrush();
		}else{
			return this.getContext().getBrush();
		}
	}

	public void buildFromXMLNode(Element node) throws InkMLComplianceException{
		super.buildFromXMLNode(node);
		this.brushRef = this.loadAttribute(node, "brushRef", null);
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element el = (Element)node;
			if(el.getNodeName().equals("traceGroup")){
				InkTraceGroup g = new InkTraceGroup(this.getInk(),this);
				g.buildFromXMLNode(el);
				this.addTrace(g);
			}else if (el.getNodeName().equals("trace")){
				InkTraceLeaf t = new InkTraceLeaf(this.getInk(),this);
				t.buildFromXMLNode(el);
				this.addTrace(t);
			}
		}
	}
	
	@Override
	public void exportToInkML(Element parent) throws InkMLComplianceException {
		if(this.isRoot() && parent.getNodeName().equals("ink") && this.getCurrentContext() != this.getInk().getCurrentContext()){
			this.getCurrentContext().exportToInkML(parent);
		}
		Element traceGroupNode  = parent.getOwnerDocument().createElement("traceGroup");
		parent.appendChild(traceGroupNode);
		super.exportToInkML(traceGroupNode);
		this.writeAttribute(traceGroupNode, "brushRef", this.brushRef, "");
		for(InkTrace trace : this.traces){
			trace.exportToInkML(traceGroupNode);
		}
		
	}

	public List<InkTrace> getContent() {
		return this.traces;
	}


	public List<InkTracePoint> getPoints(String from, String to) {
        List<InkTracePoint> pointList= new ArrayList<InkTracePoint>();
        String[] fromSplitted = from.split("\\.", 1);
        String[] tosplitted = null;
        int toOnThisLevel = traces.size();
        if(to != null){
            tosplitted = to.split("\\.", 1);
            toOnThisLevel = Integer.parseInt(tosplitted[0]);
        }
        int fromOnThisLevel = Integer.parseInt(fromSplitted[0]);
        int childrenIndex = 0;
        String fromForChildren;
        String toForChildren;
        for(InkTrace element : traces){
            childrenIndex++;
            if(childrenIndex < fromOnThisLevel || childrenIndex > toOnThisLevel){
                continue;
            }
            fromForChildren = "1";
            toForChildren = null;
            if(childrenIndex == fromOnThisLevel && fromSplitted.length>1){
                fromForChildren = fromSplitted[1];
            }
            if(childrenIndex == toOnThisLevel && tosplitted.length>1){
                toForChildren = tosplitted[1];
            }
            pointList.addAll(element.getPoints(fromForChildren,toForChildren));
        }
        return pointList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<InkTracePoint> getPoints() {
        return getPoints("1",null);
    } 


	@Override
	public boolean isView() {
		return false;
	}



	@Override
	public boolean testFormat(InkTraceFormat canvasTraceFormat) {
		for(InkTrace t : this.getTraces()){
			if(!t.testFormat(canvasTraceFormat)){
				return false;
			}
		}
		return false;
	}



	public void remove(InkTrace trace) {
	    if(traces.contains(trace)){
			traces.remove(trace);
		}
		if(traces.isEmpty()){
		    if(getParent() == null){
		        getInk().removeTrace(this);
		        getInk().notifyObserver(InkInk.ON_TRACE_REMOVED);
		        getInk().notifyObserver(InkInk.ON_CHANGE);
		    }else{
		        getParent().remove(this);
		    }
		}else{
		    notifyObserver(InkTrace.ON_TRACE_REMOVED);
		    notifyObserver(InkTrace.ON_CHANGE);
		}
	}



    @Override
    public InkTracePoint getPoint(int i) {
        int offset = 0;
        for(InkTrace trace : this.traces){
            if(trace.getPointCount() + offset > i ){
                return trace.getPoint(i-offset);
            }
            offset += trace.getPointCount();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getPointCount() {
        int res = 0;
        for(InkTrace trace : this.traces){
            res += trace.getPointCount();
        }
        return res;
    }



    @Override
    public void accept(TraceVisitor visitor) {
        visitor.visit(this);
        
    }



    public void delegateVisitor(TraceVisitor visitor) {
        for(InkTrace view : this.traces){
            view.accept(visitor);
        }
    }




    public void addTrace(InkTrace tv) {
        traces.add(tv);
    }




    public void addTraces(Collection<InkTrace> traceList) {
        traces.addAll(traceList);
    }
}