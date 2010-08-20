package ch.unibe.eindermu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.unibe.eindermu.utils.StringList;

public class StringListTest {
    private StringList ezdv = new StringList();
    
    @Before
    public void init(){
        ezdv = new StringList();
        String s = new String("eins");
        ezdv.add(s);
        s = new String("zwei");
        ezdv.add(s);
        s = new String("drei");
        ezdv.add(s);
        s = new String("vier");
        ezdv.add(s);
    }
    
    @Test
    public void testContainsString() {
        assertTrue(ezdv.contains("eins"));
        assertTrue(ezdv.contains("zwei"));
        assertTrue(ezdv.contains("drei"));
        assertTrue(ezdv.contains("vier"));
        assertFalse(ezdv.contains("fünf"));
        assertFalse(ezdv.contains("sechs"));

    }

       @Test
    public void testAddAllUnique() {
        List<String> a = new ArrayList<String>();
        a.add("eins");
        a.add("drei");
        a.add("fünf");
        a.add("sechs");
        assertEquals(4, ezdv.size());
        ezdv.addAllUnique(a);
        assertEquals(6, ezdv.size());
        assertTrue(ezdv.contains("vier"));
        assertTrue(ezdv.contains("fünf"));
        init();
        assertTrue(ezdv.contains("vier"));
        assertFalse(ezdv.contains("fünf"));
    }

  @Test
    public void testAddUnique() {
        assertEquals(4, ezdv.size());
        ezdv.addUnique("eins");
        assertEquals(4, ezdv.size());
        ezdv.addUnique("fünf");
        assertEquals(5, ezdv.size());
        init();
    }

    @Test
    public void testIndexOfObject() {
        assertEquals(0, ezdv.indexOf("eins"));
        assertEquals(1, ezdv.indexOf("zwei"));
        assertEquals(2, ezdv.indexOf("drei"));
        assertEquals(3, ezdv.indexOf("vier"));
    }

    @Test
    public void testRemoveObject() {
        assertEquals(4, ezdv.size());
        String s = new String("eins");
        assertTrue(ezdv.contains(s));
        ezdv.remove(s);
        assertFalse(ezdv.contains(s));
        assertEquals(3, ezdv.size());
        init();
    }

    @Test
    public void testContainsObject() {
        assertTrue(ezdv.contains((Object)"eins"));
        assertTrue(ezdv.contains((Object)"zwei"));
        assertTrue(ezdv.contains((Object)"drei"));
        assertTrue(ezdv.contains((Object)"vier"));
        assertFalse(ezdv.contains((Object)"fünf"));
        assertFalse(ezdv.contains((Object)"sechs"));
    }
    
    @Test
    public void testJoin(){
        assertEquals("eins,zwei,drei,vier", ezdv.join(","));
        assertEquals("einszweidreivier", ezdv.join(""));
        assertEquals("eins -- zwei -- drei -- vier", ezdv.join(" -- "));
    }

}
