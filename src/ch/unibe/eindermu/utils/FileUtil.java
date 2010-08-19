/**
 * 
 */
package ch.unibe.eindermu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author emanuel
 *
 */
public class FileUtil {
    
    public static void copyFile(File filefrom, File fileto) throws IOException{
        File fileto_ = fileto;
        if(!filefrom.exists()){
            throw new FileNotFoundException(String.format("No such file or directory: '%s'.",filefrom.getPath()));
        }
        if(!filefrom.isFile()){
            throw new IOException(String.format("Can't copy directory: '%s'.",filefrom.getPath()));
        }
        if(!filefrom.canRead()){
            throw new IOException(String.format("Permission denied to read from: '%s'.",filefrom.getPath()));
        }
        if(fileto.isDirectory()){
            fileto_ = new File(fileto,filefrom.getName());
        }
        if(fileto_.exists()){
            if(!fileto_.canWrite()){
                throw new IOException(String.format("Permission denied to write to: '%s'.",fileto_.getPath()));
            }
        }else{
            File parent = fileto_.getAbsoluteFile().getParentFile();
            if(!parent.exists() || parent.isFile()){
                throw new IOException(String.format("No such directory: '%s'",parent.getPath()));
            }
            if(!parent.canWrite()){
                throw new IOException(String.format("Permission denied to write to: '%s'.",fileto_.getPath()));
            }
        }
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(filefrom);
            to = new FileOutputStream(fileto_);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = from.read(buffer))!=-1){
                to.write(buffer,0,bytesRead);
            }
        }finally{
            if(from != null){
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            }
            if(to != null){
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }
    
    
    public static FileInfo getInfo(File file){
        return getInfo(file.getPath());
    }
    
    public static FileInfo getInfo(String filename){
        FileInfo i = new FileInfo();
        i.path = filename;

        int slash = filename.lastIndexOf(File.separatorChar);
        if(slash != -1){
            i.dir = filename.substring(0,slash);
            filename = filename.substring(slash+1);
        }else{
            i.dir = "";
        }
        
        int point = filename.lastIndexOf('.');
        if(point != -1){
            i.extension = filename.substring(point+1);
            i.name = filename.substring(0,point);
        }else{
            i.extension = "";
            i.name = filename;
        }
        return i;
    }
    
    public static class FileInfo{
        public String dir;
        public String name;
        public String extension;
        public String path;
    }

    /**
     * @param dir
     * @param name
     * @param ext
     * @return
     */
    public static String combine(String dir, String name, String ext) {
        String result = "";
        if(dir != null && !dir.isEmpty()){
            result += dir+File.separatorChar;
        }
        result += name;
        if(ext != null && !ext.isEmpty()){
            result += "."+ext;
        }
        return result;
    }
}
