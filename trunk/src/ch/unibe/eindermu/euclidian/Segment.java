/**
 * 
 */
package ch.unibe.eindermu.euclidian;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Segment extends Line2D.Double{

    private static final long serialVersionUID = -5561682652208449548L;

    /**
     * /**
     * Constructs and initializes a <code>Line2D</code> from the
     * specified <code>Point2D</code> objects.
     * @param p1 the start <code>Point2D</code> of this line segment
     * @param p2 the end <code>Point2D</code> of this line segment
     */
    
    public Segment(Point2D p1, Point2D p2) {
        super(p1,p2);
    }

    /**
     * Calculates crossing point between this an the other segment
     * @author Elias Gerber
     * @param other
     * @return The crossing point if there is one, null otherwise.
     */
    public Point2D getCrossing(Line2D other){
        assert other != null;
        assert this != other;
        assert this.getP1() != other.getP1();
        assert this.getP2() != other.getP2();
        double xA = getX1();
        double yA = getY1();
        double xB = getX2();
        double yB = getY2();
        double xC = other.getX1();
        double yC = other.getY1();
        double xD = other.getX2();
        double yD = other.getY2();
        double A = xB - xA;
        double B = xD - xC;
        double C = yB - yA;
        double D = yD - yC;
        double denominator = B*C - A*D;
        if (denominator == 0.0)
            return null;
        double r = ( B*(yC - yA) - D*(xC - xA)) / denominator;
        double s = ( A*(yC - yA) - C*(xC - xA)) / denominator;
        if ((r > 0 && r < 1 && s >= 0 && s <= 1) ||
                (r >= 0 && r <= 1 && s > 0 && s < 1)) {
            double xCP = xA + r*A;
            double yCP = yA + r*C;
            return new Point2D.Double(xCP, yCP);
        }
        return null;
    }
    
    /**
     * Calculates the distance between two segments. This is the minimal
     * distance between all points on this segment to all point of the other segment.
     * @param segment The other segment
     * @return Such a distance
     */
    public double distance(Line2D segment) {
        double dist = Integer.MAX_VALUE;
        if(intersectsLine(segment)){
            return 0;
        }
        double tdist = ptSegDist(segment.getP1());
        if(tdist < dist){
            dist = tdist;
        }
        tdist = ptSegDist(segment.getP2());
        if(tdist < dist){
            dist = tdist;
        }
        tdist = segment.ptSegDist(getP1());
        if(tdist < dist){
            dist = tdist;
        }
        tdist = segment.ptSegDist(getP2());
        if(tdist < dist){
            dist = tdist;
        }
        return dist;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.hashCode() == obj.hashCode());
    }
    
    @Override
    public int hashCode(){
        return getP1().hashCode() ^ (getP2().hashCode() * 29);
    }
}