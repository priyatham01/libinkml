package ch.unibe.eindermu.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ch.unibe.eindermu.utils.NumberList;

public class NumberListTest {

    public NumberList.Double getNewNL(){
        double[] d = new double[]{
                0,
                0,
                -1,
                -1.5,
                -1.24,
                0,
                1,
                2.3,
                2.5,
                2.55,
                2.55,
                2.55,
                2.3,
                2.5,
                2.7,
                3,
                5,
                3,
                2,
                0,
                1,
                0};
        NumberList.Double nl = new NumberList.Double(d);
        return nl;
    }
    
    @Test
    public void testSmooth() {
        NumberList.Double nl = getNewNL();
        assertEquals(4,nl.getLocalMaxima().size());
        nl.smooth(2);
        assertTrue(5>nl.getMax());
        assertTrue(-1.5<nl.getMin());
        assertEquals(2,nl.getLocalMaxima().size());
    }

    @Test
    public void testMin() {
        NumberList.Double nl = getNewNL();
        assertEquals(new Double(-1.5), nl.getMin());
    }
    
    @Test
    public void testMinIndex() {
        NumberList.Double nl = getNewNL();
        assertEquals(3, (int)nl.getMinIndex());
    }
    
    @Test
    public void testMax() {
        NumberList.Double nl = getNewNL();
        assertEquals(5.0,(double) nl.getMax(),0.0001);
    }
    
    @Test
    public void testMaxIndex() {
        NumberList.Double nl = getNewNL();
        assertEquals(16, nl.getMaxIndex());
    }

    @Test
    public void testGetLocalMaxima() {
        NumberList.Double nl = getNewNL();
        List<Integer> dl = nl.getLocalMaxima(0);
        assertEquals(4, dl.size());
        assertEquals(1, (int)dl.get(0));
        assertEquals(10,(int) dl.get(1));
        assertEquals(16,(int) dl.get(2));
        assertEquals(20,(int) dl.get(3));
    }
    
    @Test
    public void testGetLocalMinima() {
        NumberList.Double nl = getNewNL();
        List<Integer> dl = nl.getLocalMinima(0);
        assertEquals(3, (int)dl.size());
        assertEquals(3, (int)dl.get(0));
        assertEquals(12, (int)dl.get(1));
        assertEquals(19, (int)dl.get(2));
    }
    @Test
    public void testGetZeroPoints(){
        NumberList.Double nl = getNewNL();
        List<java.lang.Double> dl = nl.getZeroPoints(0);
        assertEquals(3, dl.size());
        assertEquals(.5, (double)dl.get(0),0.0001);
        assertEquals(5.0, (double)dl.get(1),0.0001);
        assertEquals(19.0, (double)dl.get(2),0.0001);
        
    }

}
