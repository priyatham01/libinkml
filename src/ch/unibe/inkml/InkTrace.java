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

import ch.unibe.eindermu.utils.Aspect;


public abstract class InkTrace extends InkTraceLike<InkTrace> implements Comparable<InkTrace>{
    
    protected static final Aspect ON_CHANGE = new Aspect(){};
    public static final Aspect ON_TRACE_REMOVED = new Aspect(){};
	
	
	private InkTraceGroup parent;
        
    public InkTrace(InkInk ink, InkTraceGroup parent) {
		super(ink);
		this.setParent(parent);
	}

	
	

    
    public void setParent(InkTraceGroup p) {
        this.parent = p;
    }
    
     
    public InkTraceGroup getParent() {
        return this.parent;
    }
    
    public InkTraceGroup getRoot() {
        if(this.getParent() == null) {
            return (InkTraceGroup) this;
        } else {
            return this.getParent().getRoot();
        }
    }
    

    
    public int compareTo(InkTrace o) {
        return (int) ((this.getTimeSpan().start - o.getTimeSpan().start) * 100);
    }
	public abstract boolean isView();





    /**
     * @param canvasTraceFormat
     * @return
     */
    public abstract boolean testFormat(InkTraceFormat canvasTraceFormat);





	//abstract public boolean testFormat(InkTraceFormat canvasTraceFormat);
}
