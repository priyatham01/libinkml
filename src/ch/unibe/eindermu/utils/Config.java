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

package ch.unibe.eindermu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import ch.unibe.eindermu.utils.CmdLineParser.IllegalOptionValueException;
import ch.unibe.eindermu.utils.CmdLineParser.Option;
import ch.unibe.eindermu.utils.CmdLineParser.UnknownOptionException;
import ch.unibe.eindermu.utils.CmdLineParser.Option.StringOption;

/**
 * This class stores configuration values. 
 * The configurations are first loaded from a mandatory default file.
 * Other configuration files can be specified explicitly with the methods ...
 * 
 * This class also considers command line options if the method parseCommandLine is called.
 * The command line options will override the configuration values specified by the configuration files.
 * 
 * If the command line option "--config FILE" is found, the specified FILE will be read as configuration
 * file, overriding the existing values.
 *
 * @author emanuel  
 */
public class Config extends Properties{
    
    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_FILE = "default_config.txt";
    
    private static final String FILE_NAME = ".properties";

    public static final String MAIN_INSTANCE = "main";
    
    private static StringMap<Config> instances = new StringMap<Config>();
    
    
    private CmdLineParser cmdLineParser;
    
    private WeakReference<ConfigurableApplication> parent;
    
    /**
     * Maping from the option names (the one with -- in front) to a small description text
     */
    private StringMap<String> descriptions;

    /**
     * List of command line options' names
     */
    private StringList names = new StringList();
    
	private StringList otherArgNames = new StringList();

   
	

    public static void setInstance(String key, Config c) {
        if(instances.containsKey(key)){
            throw new Error("only one Config instance can be stored as 'main', the one which handles the commandline arguments.");
        }
        instances.put(key, c);
    }
    public static Config getInstance(String key) {
        return instances.get(key);
    }
    public static Config getMain() {
        return instances.get(MAIN_INSTANCE);
    }
	
    /**
     * Constructer. The parameter parent specifies an object representing the main client of 
     * a Config object. This is used to get the mandatory default configuration file, which has
     * to be found at the same place as the parent.

     * @param parent
     */
    public Config(ConfigurableApplication parent) {
    	this.parent = new WeakReference<ConfigurableApplication>(parent);
    	descriptions = new StringMap<String>();
    	cmdLineParser = new CmdLineParser();
    	addStringOption('c',"config", "", "Application will consider the configuration values specified in this file.");
    	
    	
    	//load default configuration files
        try {
        	URL f = this.parent.get().getClass().getResource(DEFAULT_FILE);
        	if(f==null){
        	    throw new IOException();
        	}
            load(f.openStream());
        } catch(IOException e) {
        	e.printStackTrace();
            System.err.println("default configuration file has not been found.");
            System.exit(1);
        }
        
        File file = new File(this.getUserFilePath());
        if(file.exists()){
            try {
                this.load(new FileInputStream(file));
            } catch(FileNotFoundException e) {
                //will not happen
            } catch(IOException e) {
                System.err.println("Can not read user configuration file '"+file.getPath()+"'");
            }
        }
        
    }

    /**
     * reads configuration form the input stream specified.
     */
    public synchronized void load(InputStream inStream) throws IOException {
    	super.load(inStream);
    	Enumeration<Object> keys = this.keys();
        while(keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if(this.cmdLineParser.getOption("--"+key)==null){
		        if(this.isBoolean(key)) {
		            this.cmdLineParser.addBooleanOption(key);
		        } else {
		            this.cmdLineParser.addStringOption(key);
		        }
		        names.add(key);
            }
        }
    }
    
    public void addStringOption(String name, String default_,String description){
        addStringOption(name, default_);
        descriptions.put(name, description);
    }
    public void addStringOption(char c,String name, String default_,String description){
        addStringOption(c, name, default_);
        descriptions.put(name, description);
    }
    public void addStringOption(String name, String default_){
        this.cmdLineParser.addStringOption(name);
        if(default_!=null){
            this.set(name, default_);
        }
        names.add(name);
    }
    public void addStringOption(char c,String name, String default_){
        this.cmdLineParser.addStringOption(c, name);
        if(default_!=null){
            this.set(name, default_);
        }
        names.add(name);
    }

	public void addBooleanOption(String name, String description) {
		addBooleanOption(name);
    	descriptions.put(name, description);
	}
    public void addBooleanOption(char c,String name,String description){
    	addBooleanOption(c, name);
    	descriptions.put(name, description);
    }
    public void addBooleanOption(String name){
    	this.cmdLineParser.addBooleanOption(name);
    	if(!this.containsKey(name)){
    		this.set(name,"off");
    	}
    	names.add(name);
    }
    public void addBooleanOption(char c,String name){
    	this.cmdLineParser.addBooleanOption(c, name);
    	if(!this.containsKey(name)){
    		this.set(name,"off");
    	}
    	names.add(name);
    }
    
    public void nameOtherArg(String name,String description){
    	descriptions.put(name, description);
    	nameOtherArg(name);
    }
    public void nameOtherArg(String name){
    	otherArgNames.add(name);
    }
    
    public String getHelpString(){
    	
        //get column width
    	int maxWidth = 100;
    	if(System.getenv("COLUMNS") != null && System.getenv("COLUMNS") != ""){
    		maxWidth = Integer.parseInt(System.getenv("COLUMNS"));
    	}

    	//create inkanno command line pattern
    	StringBuffer sb = new StringBuffer("Usage: "+parent.get().getApplicationName()+" [OPTIONS] ");
    	for(String otherArgs : otherArgNames){
    		sb.append("["+otherArgs.toUpperCase()+"] ");
    	}
    	sb.append("\n");
    	
    	
    	//Application description
        appendBroken(sb, parent.get().getApplicationDescription(), 0, maxWidth);
        sb.append("\n\n");
        
    	
    	
    	//evaluate the maximal length of a option's name
        //and test if any option has a value
    	int indentSize = 0;
    	boolean hasValue = false;
    	for(String key : names){
    		if(key.length() > indentSize){
    			indentSize = key.length();
    		}
    		if(cmdLineParser.getOption("--"+key).wantsValue()){
    			hasValue = true;
    		}
    	}
    	//create indent info
    	indentSize +=  (hasValue)?16:10;
    	String indent = "";
    	for(int i=0;i<indentSize;i++){
			indent+=" ";
		}
    	
    	
        //for each option
    	for(String key : names){
    		Option o = cmdLineParser.getOption("--"+key);
    		String line = "";
    		
    		String longForm = o.longForm();
    		if(o.wantsValue()){
    			longForm = longForm+"=VALUE";
    		}
    		String shortForm = (o.shortForm()!=null)? "-"+o.shortForm():"";

    		line = String.format("  %2s --%-20s ",shortForm,longForm);
    		
    		if(line.length() > indent.length()){
    			sb.append(line+"\n");
    			line = ""+indent;
    		}
    		if(descriptions.containsKey(key)){
    		    line += descriptions.get(key);
    		}
    		appendBroken(sb, line , indentSize, maxWidth);
    		
    	}
    	sb.append("\n");
    	for(String key : otherArgNames){
    	    String name = "  ["+key.toUpperCase()+"]"; 
    		name += indent.substring(name.length());
    		appendBroken(sb, name + descriptions.get(key),indentSize,maxWidth);
    		
    	}
    	return sb.toString();
    }
    
    
    
    private void appendBroken(StringBuffer buffer,String str, int indentSize, int maxWidth){
        String desc = str;
        String line = "";
        String indent = "";
        for(int i = 0 ; i< indentSize; i++){
             indent += " ";
        }
        int i = 0;
        int lastSpace = 0;
        while (i< desc.length()){
            line += desc.charAt(i);
            if(line.charAt(line.length()-1) == '\n' ){
                buffer.append('\n'+line.substring(0,line.length()-1));
                line = indent;
            }
            if(line.length() > maxWidth){
                if(lastSpace > indentSize){
                    buffer.append("\n"+line.substring(0,lastSpace));
                    line = indent+line.substring(lastSpace+1);
                }else{
                    buffer.append("\n"+line.substring(0,line.length()-1));
                    line = indent+line.substring(line.length()-1);
                }
                lastSpace = 0;
            }
            if(line.charAt(line.length()-1) == ' ' ){
                lastSpace = line.length()-1;
            }
            i++;
        }
        buffer.append("\n"+line+"\n");
    }
    
    
    private boolean isBoolean(String key) {
        if(this.get(key) == null){
            return false;
        }
        String value = this.get(key).toLowerCase();
        return (value.equals("yes") || value.equals("no") || value.equals("on") || value.equals("off"));
    }
    
    public void parseCommandLine(String[] args) throws IllegalOptionValueException, UnknownOptionException {
        cmdLineParser.parse(args);
        {
            StringOption configOption = (StringOption) cmdLineParser.getOption("--config");
            String configFile = (String) this.cmdLineParser.getOptionValue(configOption);
            
            if(configFile != null) {
                File config = new File(configFile);
                try {
                    this.load(new FileInputStream(config));
                } catch(FileNotFoundException e) {
                    throw new IllegalOptionValueException(configOption, e.getMessage());
                } catch(IOException e) {
                    throw new IllegalOptionValueException(configOption, e.getMessage());
                }
            }
        }
        
       for(String key : names){
            if(this.isBoolean(key)) {
            	this.cmdLineParser.getOption(key);
            	this.set(key, (Boolean) this.cmdLineParser.getOptionValue("--" + key, this.getB(key)));
            } else {
                if(this.cmdLineParser.getOptionValue("--" + key, this.get(key)) != null){
                	try{
                		this.set(key, (String) this.cmdLineParser.getOptionValue("--" + key, this.get(key)));
                	}catch(ClassCastException e){
                		throw new IllegalOptionValueException(this.cmdLineParser.getOption("--" + key), this.cmdLineParser.getOptionValue("--" + key, this.get(key)).getClass().toString());
                	}
                }
            }
        }
        int i = 0;
        int j = 0;
        for(;j<otherArgNames.size();j++){
        	if(cmdLineParser.getRemainingArgs().length > i){
        	    cmdLineParser.addValue(cmdLineParser.getOption("--"+otherArgNames.get(j)),cmdLineParser.getRemainingArgs()[i]);
        	    set(otherArgNames.get(j),cmdLineParser.getRemainingArgs()[i++]);
        	}
        }
        for(;i<cmdLineParser.getRemainingArgs().length;i++){
            cmdLineParser.addValue(cmdLineParser.getOption("--"+otherArgNames.get(j-1)),cmdLineParser.getRemainingArgs()[i]);
            set(otherArgNames.get(j-1),cmdLineParser.getRemainingArgs()[i]);
        }      
    }
    
    public void set(String key, boolean value) {
        this.setProperty(key, (value) ? "Yes" : "No");
    }
    
    public boolean getB(String key) {
        String result = getProperty(key);
        return result != null && (result.toLowerCase().equals("yes") || result.toLowerCase().equals("on") || result.toLowerCase().equals("1"));
    }
    
    public void set(String key, int value) {
        this.setProperty(key, value + "");
    }
    
    public int getI(String key) {
        return Integer.parseInt(this.getProperty(key));
    }
    
    public void set(String key, String value) {
        this.setProperty(key, value);
    }
    
    public String get(String key) {
        return getProperty(key);
    }
    
    public void set(String key, double value) {
        this.setProperty(key, value + "");
    }
    
    public StringList getMultiple(String key){
        StringList sl = new StringList();
        Vector<Object> v = cmdLineParser.getOptionValues(cmdLineParser.getOption("--" + key));
        for(Object value : v){
            sl.add(value.toString());
        }
        return sl;
    }
    
    public double getD(String key) {
        return Double.parseDouble(getProperty(key));
    }
    
    public void save() throws FileNotFoundException, IOException {
        File file = new File(this.getUserFilePath());
        this.store(new FileOutputStream(file), null);
    }
    
    private String getUserFilePath() {
        if(System.getProperty("os.name").equals("Linux")) {
            return System.getProperty("user.home") + File.separator + "."+parent.get().getApplicationName()+FILE_NAME;
        } else {
            return System.getenv("APPDATA") + File.separator +"."+parent.get().getApplicationName()+FILE_NAME;
        }
    }

    public ConfigurableApplication getApplication() {
        return parent.get();
    }


    
}
