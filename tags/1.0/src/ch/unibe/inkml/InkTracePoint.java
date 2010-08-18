/**
 * 
 */
package ch.unibe.inkml;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import ch.unibe.eindermu.euclidian.Segment;
import ch.unibe.inkml.InkChannel.Name;

/**
 * @author emanuel
 *
 */
public abstract class InkTracePoint extends Point2D {
 
    
    public static double distanceToPoint(Iterable<InkTracePoint> l, Point2D p) {
        double dist = java.lang.Double.MAX_VALUE;
        for(InkTracePoint po : l) {
            double d = po.distance(p);
            if(d < dist) {
                dist = d;
            }
        }
        return dist;
    }
    
    public static Polygon getPolygon(List<InkTracePoint> l) {
        int[] xs = new int[l.size()];
        int[] ys = new int[l.size()];
        int i = 0;
        for(InkTracePoint p : l) {
            xs[i] = (int) p.getX();
            ys[i++] = (int) p.getY();
        }
        return new Polygon(xs, ys, l.size());
    }

    public static Point2D getCenterOfGravity(Iterable<InkTracePoint> points) {
        double x = 0,y = 0, i = 0;
        for(InkTracePoint p : points){
            x += p.getX();
            y += p.getY();
            i++;
        }
        return new Point2D.Double(x/i,y/i);
    }
    
    /**
     * Change the X coordinate to x
     * @param x the new x value
     */
    public void setX(double x) {
        set(Name.X,x);
    }

    /**
     * Change the Y coordinate to y
     * @param y the new y value 
     */
    public void setY(double y) {
       set(Name.Y,y);
    }
    
    public double getX() {
        return get(Name.X);
    }

    public double getY() {
        return get(Name.Y);
    }

    
    @Override
    public void setLocation(double x, double y) {
        setX(x);setY(y);
    }
    
    public abstract double get(InkChannel.Name t);
    
    public abstract Object getObject(Name name);
    
    public abstract void set(Name name, double d);
    
    public abstract void set(Name name, Object value);

    /**
     * @param points
     * @param points2
     */
    public static double distanceTraceToTrace(List<InkTracePoint> points, List<InkTracePoint> points2) {
        List<Segment> segments= new ArrayList<Segment>();
        List<Segment> segments2= new ArrayList<Segment>();
        if(points.size() == 1){
            return distanceToPoint(points2,points.get(0));
        }
        if(points2.size() == 1){
            return distanceToPoint(points,points2.get(0));
        }
        for(int i1 = 1;i1<points.size();i1++){
            segments.add(new Segment(points.get(i1-1),points.get(i1)));
            for(int i2 = 1;i2<points2.size();i2++){
                if(i1 == 1){
                    segments2.add(new Segment(points2.get(i2-1),points2.get(i2)));        
                }
                if(segments.get(i1-1).isCrossing(segments2.get(i2-1))){
                    return 0;
                }
            }
        }
        double dist = Integer.MAX_VALUE;
        for(int i1 = 0;i1<points.size()-1;i1++){
            for(int i2 = 0;i2<points2.size()-1;i2++){
                double tdist = segments.get(i1).distance(segments2.get(i2));
                if(tdist < dist){
                    dist = tdist;
                }
            }
        }
        return dist;
    }
    
    
}
