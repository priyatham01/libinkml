package ch.unibe.eindermu;

import ch.unibe.eindermu.utils.Config;
import ch.unibe.eindermu.utils.ConfigurableApplication;
import ch.unibe.eindermu.utils.CmdLineParser.IllegalOptionValueException;
import ch.unibe.eindermu.utils.CmdLineParser.UnknownOptionException;


/**
 * This is a prototype of a Main class. It provides commandline option parsing, and 
 * basic error handling.
 * To use the funktionality of this class, call go(args) to an instance of your Main class int the main method.
 * buildConfig() is then callen to populate the commandline option parser.
 * Finally start is callen, which starts your code.
 *  
 * 
 * @author emanuel
 *
 */
public abstract class AbstractMain implements ConfigurableApplication{

    private Config c; 

    /**
     * default consturctor creates a new config class
     */
    public AbstractMain(){
        c = new Config(this);
    }

    /**
     * returns the config class which is populated with the command line options
     * @return
     */
    public Config getConfig() {
        return this.c;
    }
    
    /**
     * Starts the funktionality of this class. Namely error handling and command line parsing 
     * @param args
     */
    protected void go(String[] args) {
        Messenger.add(new CmdLineMessenger(getApplicationName()));
        Config.setInstance(Config.MAIN_INSTANCE,c);
        buildConfig();
        getConfig().addBooleanOption('h', "help","display this help and exit");
        parseConfig(args);
        try {
            start();
        } catch(Exception e) {
            e.printStackTrace();
            Messenger.error(e.getMessage());
            System.exit(1);
        }
    }
    
    protected void parseConfig(String[] args) {
        try {
            getConfig().parseCommandLine(args);
        } catch(IllegalOptionValueException e1) {
            Messenger.error("Unknown option specified "+e1.getMessage());
            System.exit(1);
        } catch(UnknownOptionException e1) {
            Messenger.error(e1.getMessage());
            System.exit(1);
        }
        if(getConfig().getB("help")){
            System.out.println(getConfig().getHelpString());
            System.exit(0);
        }
    }

    /**
     * This methods acctually starts your code.
     * When this method is callen, the Command line options are allready parsed
     * @throws Exception
     */
    protected abstract void start() throws Exception;


    /**
     * Here you add commandline options options to the Config object
     * which you can retrieve with the getConfig method
     */
    protected abstract void buildConfig();
    
}
