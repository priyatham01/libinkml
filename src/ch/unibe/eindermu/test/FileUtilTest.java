/**
 * 
 */
package ch.unibe.eindermu.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import ch.unibe.eindermu.utils.FileUtil;
import ch.unibe.eindermu.utils.FileUtil.FileInfo;

/**
 * @author emanuel
 *
 */
public class FileUtilTest {

    /**
     * Test method for {@link ch.unibe.eindermu.utils.FileUtil#getInfo(java.lang.String)}.
     */
    @Test
    public void testGetInfo() {
        char c = File.separatorChar;
        FileInfo i = FileUtil.getInfo(c+"test"+c+"test.txt");
        assertEquals("txt", i.extension);
        assertEquals(c+"test", i.dir);
        assertEquals("test", i.name);
        
        i = FileUtil.getInfo(c+"test"+c+"test_txt");
        assertEquals("", i.extension);
        assertEquals(c+"test", i.dir);
        assertEquals("test_txt", i.name);
        
        i = FileUtil.getInfo("test"+c+"test.txt.ab");
        assertEquals("ab", i.extension);
        assertEquals("test", i.dir);
        assertEquals("test.txt", i.name);
        
        i = FileUtil.getInfo(c+"test_test.txt.ab");
        assertEquals("ab", i.extension);
        assertEquals("", i.dir);
        assertEquals("test_test.txt", i.name);
    }

}
