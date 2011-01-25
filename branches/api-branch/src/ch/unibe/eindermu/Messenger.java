/**
 * 
 */
package ch.unibe.eindermu;

import java.util.ArrayList;
import java.util.List;

/**
 * This class unifies message handling. 
 * 
 * Messenger is a single point (just static methods) of message handling.
 * It accepts 3 types (information, warning, error) of messages and will direct them to different sinks.
 * 
 * Different {@link #MessageSink}s can be added {@link #add(MessageSink)} to the Messenger. 
 * Each of which have to implement the MessageSink interface.
 * 
 * 
 * @author emanuel
 *
 */
public class Messenger {
    private static List<MessageSink> implementations = new ArrayList<MessageSink>();
    
    /**
     * A MessageSink handles 3 types of messages, and may present them to the user in a way. 
     * @author emanuel
     *
     */
    public static interface MessageSink{
        /**
         * The user must be warned.
         * @param message The warning
         */
        public void warn(String message);
        
        /**
         * An error message must be shown to the user.
         * @param message The error message
         */
        public void error(String message);
        
        /**
         * The user must be informed.
         * @param message The information
         */
        public void inform(String message);
    }
    
    /**
     * Warns the user using the message sinks.
     * @param message The warning
     */
    public static void warn(String message){
        for(MessageSink mi : implementations){
            mi.warn(message);
        }
    }
    
    /**
     * Display the error message to the users using the message sinks.
     * @param message The error message
     */
    public static void error(String message){
        for(MessageSink mi : implementations){
            mi.error(message);
        }
    }
    
    /**
     * Inform the user using the message sinks.
     * @param message The Information
     */
    public static void inform(String message){
        for(MessageSink mi : implementations){
            mi.inform(message);
        }
    }
    
    /**
     * Adds a MessageSink to the messenger.  
     * @param cmdLineMessenger
     */
    public static void add(MessageSink sink) {
        implementations.add(sink);
    }
}
