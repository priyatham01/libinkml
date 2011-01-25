/**
 * 
 */
package ch.unibe.eindermu;


import ch.unibe.eindermu.Messenger.MessageSink;

/**
 * @author emanuel
 *
 */
public class CmdLineMessenger implements MessageSink {
    private String appName;

    public CmdLineMessenger(String applicationName){
        appName = applicationName;
    }
    @Override
    public void error(String message) {
        send(appName + ": " + message);
    }

    @Override
    public void inform(String message) {
        send(message);
    }

    @Override
    public void warn(String message) {
         send("Warning: "+message);
    }
    
    public void send(String message){
        System.err.println(message);
    }

}
