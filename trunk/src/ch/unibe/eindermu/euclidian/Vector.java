package ch.unibe.eindermu.euclidian;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Vector extends Double {
    private static final long serialVersionUID = -3626962593035171438L;
    
    public Vector(double d, double e) {
		super(d,e);
	}
	public Vector(Point2D p) {
		super(p.getX(),p.getY());
	}
	public double length(){
		return distance(0,0);
	}
	public Vector plus(Vector other){
		return new Vector(getX()+other.getX(),getY()+other.getY());
	}
	public Vector minus(Vector other){
		return new Vector(getX()-other.getX(),getY()-other.getY());
	}
	public Vector norm(){
		return new Vector(getX()/length(),getY()/length());
	}
	public double scalar(Vector other){
		return getX()*other.getX() + getY()*other.getY();
	}
	public Vector minus(Point2D p) {
		return minus(new Vector(p));
	}
	/**
	 * Angle between two vectors the value is between -pi and +pi;
	 * @param other
	 * @return
	 */
    public double angleBetween(Vector other) {
        double t = (getX()*other.getY() - getY()*other.getX());
        if(t == 0)
            return 0;
        t/=Math.abs(t);
        return t * Math.acos(scalar(other)/(length()*other.length()));
    }
    public java.lang.Double angleBetween(Point2D other) {
        return angleBetween(new Vector(other));
    }
}
