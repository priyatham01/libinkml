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

package ch.unibe.inkml.util;
import ch.unibe.inkml.InkChannel;
import ch.unibe.inkml.InkTracePoint;
import ch.unibe.inkml.InkTraceView;

public class Timespan implements Comparable<Timespan>{
    
    public double start;
    
    public double end;
    
    private boolean initialized = false;
    
    public Timespan(String start_time, String end_time) {
        this(Double.parseDouble(start_time), Double.parseDouble(end_time));
    }
    
    public Timespan(double start, double end) {
        this.setTimespan(start, end);
    }
    
    public void setTimespan(double start, double end) {
        this.start = start;
        this.end = end;
        this.initialized = true;
    }
    
    public Timespan() {}
    
    public Timespan(Timespan timeSpan) {
		this(timeSpan.start,timeSpan.end);
	}

	public Timespan(InkTracePoint p) {
		this((Double)p.get(InkChannel.Name.T),(Double)p.get(InkChannel.Name.T));
	}

	public double getDuration() {
        return this.end - this.start;
    }
    
	public double getBetween(){
		return getDuration()/2.0 + start;
	}
    public void add(Timespan other) {
    	if(other == null){
    		return;
    	}
        if(this.initialized) {
            this.start = (other.start < this.start) ? other.start : this.start;
            this.end = (other.end > this.end) ? other.end : this.end;
        } else {
            this.setTimespan(other.start, other.end);
        }
        
    }
    
    public boolean equals(Object other){
    	return this.start - ((Timespan)other).start < 0.00001
    			&& this.end - ((Timespan)other).end < 0.00001;
    }
    
    public void add(double diff) {
        this.start += diff;
        this.end += diff;
    }

	public void add(InkTracePoint p) {
		if(this.end < (Double)p.get(InkChannel.Name.T)){
			this.end = (Double)p.get(InkChannel.Name.T);
		}
		if(this.start > (Double)p.get(InkChannel.Name.T)){
			this.start = (Double)p.get(InkChannel.Name.T);
		}
	}

	
	public int compareTo(Timespan o) {
		if(o == null){
			return 1;
		}
		return (int) (this.start - o.start)*1000;
	}

    /**
     * @param inkTraceView
     * @return
     */
    public double distance(Timespan other) {
        if(start <= other.end && end >= other.start){
            return 0.0;
        }
        if(start > other.end){
            return start-other.end;
        }else{
            return other.start-end;
        }
    }
}
