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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import ch.unibe.inkml.InkTracePoint;



@SuppressWarnings("serial")
public class TraceBound extends Rectangle2D.Double {
    
    public TraceBound(InkTracePoint point) {
        super(point.getX(),point.getY(),0,0);
    }
    
    public TraceBound(Rectangle2D bounds) {
    	super();
    	setRect(bounds);
    }
    
    public TraceBound() {
    	super();
    }
    
	public int distanceToPoints(Point2D p) {
        int d = Integer.MAX_VALUE;
        for(Point2D angle : this.getPoints()) {
            int ld = (int) angle.distance(p);
            if(ld < d) {
                d = ld;
            }
        }
        return d;
    }
    
    public Set<Point2D> getPoints() {
        Set<Point2D> result = new HashSet<Point2D>();
        result.add(new Point2D.Double(this.getX(), this.getY()));
        result.add(new Point2D.Double(this.getX() + this.getWidth(), this.getY()));
        result.add(new Point2D.Double(this.getX() + this.getWidth(), this.getY() + this.getHeight()));
        result.add(new Point2D.Double(this.getX(), this.getY() + this.getHeight()));
        return result;
    }
    /**
     * Returns a new TraceBound whos height is grown by 2 times "vertical" and whos width is grown by 2 times "horizontal"
     * @param horizontal number of pixels to grow height
     * @param vertical number of pixels to grow width
     * @return
     */
    public TraceBound growNew(double horizontal, double vertical){
    	TraceBound newb = new TraceBound(this);
    	newb.grow(horizontal, vertical);
    	return newb;
    }
    
    /**
     * Grows the TraceBound's height by 2 times "vertical" and its width by 2 times "horizontal"
     * @param horizontal
     * @param vertical
     */
    public void grow(double horizontal, double vertical) {
        double x0 = this.x;
        double y0 = this.y;
        double x1 = this.width;
        double y1 = this.height;
        x1 += x0;
        y1 += y0;

        x0 -= horizontal;
        y0 -= vertical;
        x1 += horizontal;
        y1 += vertical;

        if (x1 < x0) {
            // Non-existant in X direction
            // Final width must remain negative so subtract x0 before
            // it is clipped so that we avoid the risk that the clipping
            // of x0 will reverse the ordering of x0 and x1.
            x1 -= x0;
            if (x1 < -java.lang.Double.MAX_VALUE) x1 = -java.lang.Double.MAX_VALUE;
            if (x0 < -java.lang.Double.MAX_VALUE) x0 = -java.lang.Double.MAX_VALUE;
            else if (x0 > java.lang.Double.MAX_VALUE) x0 = java.lang.Double.MAX_VALUE;
        } else { // (x1 >= x0)
            // Clip x0 before we subtract it from x1 in case the clipping
            // affects the representable area of the rectangle.
            if (x0 < -java.lang.Double.MAX_VALUE) {
                x0 = -java.lang.Double.MAX_VALUE;
            }
            else if (x0 > java.lang.Double.MAX_VALUE) {
                x0 = java.lang.Double.MAX_VALUE;
            }
            x1 -= x0;
            // The only way x1 can be negative now is if we clipped
            // x0 against MIN and x1 is less than MIN - in which case
            // we want to leave the width negative since the result
            // did not intersect the representable area.
            if (x1 < -java.lang.Double.MAX_VALUE) x1 = -java.lang.Double.MAX_VALUE;
            else if (x1 > java.lang.Double.MAX_VALUE) x1 = java.lang.Double.MAX_VALUE;
        }

        if (y1 < y0) {
            // Non-existant in Y direction
            y1 -= y0;
            if (y1 < -java.lang.Double.MAX_VALUE) y1 = -java.lang.Double.MAX_VALUE;
            if (y0 < -java.lang.Double.MAX_VALUE) y0 = -java.lang.Double.MAX_VALUE;
            else if (y0 > java.lang.Double.MAX_VALUE) y0 = java.lang.Double.MAX_VALUE;
        } else { // (y1 >= y0)
            if (y0 < -java.lang.Double.MAX_VALUE) y0 = -java.lang.Double.MAX_VALUE;
            else if (y0 > java.lang.Double.MAX_VALUE) y0 = java.lang.Double.MAX_VALUE;
            y1 -= y0;
            if (y1 < -java.lang.Double.MAX_VALUE) y1 = -java.lang.Double.MAX_VALUE;
            else if (y1 > java.lang.Double.MAX_VALUE) y1 = java.lang.Double.MAX_VALUE;
        }

        setRect(x0,y0,x1,y1);
    }
}
