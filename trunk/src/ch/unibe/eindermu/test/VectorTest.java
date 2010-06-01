package ch.unibe.eindermu.test;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

import org.junit.Test;

import ch.unibe.eindermu.euclidian.Vector;

public class VectorTest {

    public void assertVeryNear(Vector expected,Vector actual){
        if (expected == null && actual == null)
            return;
        if (expected != null && expected.equals(actual))
            return;
        else if(Math.abs(expected.getX() - actual.getX()) < 0.000001
                && Math.abs(expected.getY() - actual.getY()) < 0.000001)
            return; 
        else
            assertEquals(expected, actual);
    }
    
    @Test
    public void testVectorDoubleDouble() {
        double x =9.45;
        double y = 3.58;
        Vector v = new Vector(x,y);
        assertEquals(x, v.getX(),0.00001);
        assertEquals(y, v.getY(),0.00001);
    }

    @Test
    public void testVectorPoint2D() {
        double x =9.45;
        double y = 3.58;
        Point2D point = new Point2D.Double(x,y);
        Vector v = new Vector(point);
        assertEquals(x, v.getX(),0.00001);
        assertEquals(y, v.getY(),0.00001);
    }

    @Test
    public void testLength() {
        double[] x = {0,1,4,-4,0,5.3};
        double[] y = {1,0,3,3,-5,-8.4};
        double[] l = {1,1,5,5,5,9.9322706};
        for(int i = 0;i<6;i++){
            Vector v = new Vector(x[i],y[i]);
            assertEquals(l[i], v.length(),0.00001);
        }
    }

    @Test
    public void testPlus() {
        Vector[] vs = {
                new Vector(0,1),
                new Vector(4,3),
                new Vector(-4,-9),
                new Vector(5.3,-8.4),
                new Vector(-9.47,3.22)
        };
        for(int x = 0;x < 5;x++){
            for(int y = 0;y < 5;y++){
                assertEquals(new Vector(vs[x].getX() + vs[y].getX(),vs[x].getY() + vs[y].getY()),vs[x].plus(vs[y]));        
            }
        }
    }

    @Test
    public void testMinusVector() {
        Vector[] vs = {
                new Vector(0,1),
                new Vector(4,3),
                new Vector(-4,-9),
                new Vector(5.3,-8.4),
                new Vector(-9.47,3.22)
        };
        for(int x = 0;x < 5;x++){
            for(int y = 0;y < 5;y++){
                assertEquals(new Vector(vs[x].getX() - vs[y].getX(),vs[x].getY() - vs[y].getY()),vs[x].minus(vs[y]));        
            }
        }
    }

    @Test
    public void testNorm() {
        Vector[] vs = {
                new Vector(0,1),
                new Vector(4,3),
                new Vector(-4,-9),
                new Vector(5.3,-8.4),
                new Vector(-9.47,10.22)
        };
        Vector[] sl = {
                new Vector(0,1),
                new Vector(0.8,.6),
                new Vector(-0.40613847,-0.91381155),
                new Vector(0.53361413,-0.84572806),
                new Vector(-0.67967991,0.73350884)
        };
        for(int x = 0;x < 5;x++){
            assertVeryNear(sl[x], vs[x].norm());
        }
    }

    @Test
    public void testScalar() {
        double[] xs = {0, 4, -4,  5.3, -9.47};
        double[] ys = {1, 3, -9, -8.4, 10.22};
        Vector[] vs = new Vector[5];
        for(int x = 0;x < 5;x++){
            vs[x] = new Vector(xs[x],ys[x]); 
        }
        double [][] result = 
        {
                {    1,    3,     -9,    -8.4,    10.22},
                {    3,   25,    -43,      -4,    -7.22},
                {   -9,  -43,     97,    54.4,    -54.1},
                { -8.4,   -4,   54.4,   98.65, -136.039},
                {10.22,-7.22,  -54.1,-136.039, 194.1293}
        };
        for(int x = 0;x < 5;x++){
            for(int y = 0;y < 5;y++){
                assertEquals(result[x][y],vs[x].scalar(vs[y]),0.00001);
            }
        }
    }

    @Test
    public void testAngleBetween() {
        double[] xs = {0, 1, -4};
        double[] ys = {1, 0, -9};
        Vector[] vs = new Vector[3];
        for(int x = 0;x < 3;x++){
            vs[x] = new Vector(xs[x],ys[x]); 
        }
        double pi = Math.PI;
        double [][] result = 
        {
                {    0, pi/2,  -(pi/2+Math.asin(-9/-vs[2].length()))},
                { -pi/2,    0,    pi-(Math.asin(-9/-vs[2].length()))},
                {   (pi/2+Math.asin(-9/-vs[2].length())),  -pi+(Math.asin(-9/-vs[2].length())),      0}
        };
        for(int y = 0;y < 3;y++){
            for(int x = 0;x < 3;x++){
                assertEquals(result[y][x],vs[x].angleBetween(vs[y]),0.00001);
            }
        }
    }

}
