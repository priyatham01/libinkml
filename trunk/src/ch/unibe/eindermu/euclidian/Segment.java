/**
 * 
 */
package ch.unibe.eindermu.euclidian;

import java.awt.geom.Point2D;

public class Segment{
    private Point2D start;
    private Point2D end;
    public Segment( Point2D start, Point2D end){
        this.start = start;
        this.end = end;
    }
    
    public boolean isCrossing(Segment other){
        return getCrossing(other) != null;
    }
    
    /**
     * calculates crossing point between two segments
     * @author Elias Gerber
     * @param other
     * @return
     */
    public Point2D getCrossing(Segment other){
        assert other != null;
        assert this != other;
        assert this.end != other.start;
        assert this.start != other.end;
        double xA = start.getX();
        double yA = start.getY();
        double xB = end.getX();
        double yB = end.getY();
        double xC = other.start.getX();
        double yC = other.start.getY();
        double xD = other.end.getX();
        double yD = other.end.getY();
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
    
    public double distanceFromPoint(Point2D point) {
        
        double cx = point.getX(),
         cy = point.getY(),
         ax = start.getX(),
         ay = start.getY(),
         bx = end.getX(),  
         by = end.getY();
        //
        // find the distance from the point (cx,cy) to the line
        // determined by the points (ax,ay) and (bx,by)
        //
        // distanceSegment = distance from the point to the line segment
        // distanceLine = distance from the point to the line (assuming
        // infinite extent in both directions
        //

        /*
         * 
         * Subject 1.02: How do I find the distance from a point to a line?
         * 
         * 
         * Let the point be C (Cx,Cy) and the line be AB (Ax,Ay) to (Bx,By). Let
         * P be the point of perpendicular projection of C on AB. The parameter
         * r, which indicates P's position along AB, is computed by the dot
         * product of AC and AB divided by the square of the length of AB:
         * 
         * (1) AC dot AB r = --------- ||AB||^2
         * 
         * r has the following meaning:
         * 
         * r=0 P = A r=1 P = B r<0 P is on the backward extension of AB r>1 P is
         * on the forward extension of AB 0<r<1 P is interior to AB
         * 
         * The length of a line segment in d dimensions, AB is computed by:
         * 
         * L = sqrt( (Bx-Ax)^2 + (By-Ay)^2 + ... + (Bd-Ad)^2)
         * 
         * so in 2D:
         * 
         * L = sqrt( (Bx-Ax)^2 + (By-Ay)^2 )
         * 
         * and the dot product of two vectors in d dimensions, U dot V is
         * computed:
         * 
         * D = (Ux * Vx) + (Uy * Vy) + ... + (Ud * Vd)
         * 
         * so in 2D:
         * 
         * D = (Ux * Vx) + (Uy * Vy)
         * 
         * So (1) expands to:
         * 
         * (Cx-Ax)(Bx-Ax) + (Cy-Ay)(By-Ay) r = -------------------------------
         * L^2
         * 
         * The point P can then be found:
         * 
         * Px = Ax + r(Bx-Ax) Py = Ay + r(By-Ay)
         * 
         * And the distance from A to P = r*L.
         * 
         * Use another parameter s to indicate the location along PC, with the
         * following meaning: s<0 C is left of AB s>0 C is right of AB s=0 C is
         * on AB
         * 
         * Compute s as follows:
         * 
         * (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay) s = ----------------------------- L^2
         * 
         * 
         * Then the distance from C to P = |s|*L.
         */

        double r_numerator = (cx - ax) * (bx - ax) + (cy - ay) * (by - ay);
        double r_denomenator = (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
        double r = r_numerator / r_denomenator;
        //
        //double px = ax + r * (bx - ax);
        //double py = ay + r * (by - ay);
        //
        double s = ((ay - cy) * (bx - ax) - (ax - cx) * (by - ay))
                / r_denomenator;

        double distanceLine = Math.abs(s) * Math.sqrt(r_denomenator);

        if ((r >= 0) && (r <= 1)) {
            return distanceLine;
        } else {
            double dist1 = (cx - ax) * (cx - ax) + (cy - ay) * (cy - ay);
            double dist2 = (cx - bx) * (cx - bx) + (cy - by) * (cy - by);
            if (dist1 < dist2) {
                return Math.sqrt(dist1);
            } else {
                return Math.sqrt(dist2);
            }
        }
    }

    /**
     * @param segment
     * @return
     */
    public double distance(Segment segment) {
        double dist = Integer.MAX_VALUE;
        double tdist = distanceFromPoint(segment.start);
        if(tdist < dist){
            dist = tdist;
        }
        tdist = distanceFromPoint(segment.end);
        if(tdist < dist){
            dist = tdist;
        }
        tdist = segment.distanceFromPoint(start);
        if(tdist < dist){
            dist = tdist;
        }
        tdist = segment.distanceFromPoint(end);
        if(tdist < dist){
            dist = tdist;
        }
        return dist;
    }
}