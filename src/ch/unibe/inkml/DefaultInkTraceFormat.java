/**
 * 
 */
package ch.unibe.inkml;

import ch.unibe.inkml.InkChannel.Orientation;

/**
 * @author emanuel
 *
 */
public final class DefaultInkTraceFormat extends InkTraceFormat {
    public DefaultInkTraceFormat(InkInk ink){
        super(ink);
        try {
            InkChannel channelX = new InkChannelDouble(ink);
            channelX.setName(InkChannel.ChannelName.X);
            channelX.setUnits("em");
            channelX.setOrientation(Orientation.P);
            addChannel(channelX);
            
            InkChannel channelY = new InkChannelDouble(ink);
            channelY.setName(InkChannel.ChannelName.Y);
            channelY.setOrientation(Orientation.P);
            channelY.setUnits("em");
            addChannel(channelY);
            setFinal();
        } catch (InkMLComplianceException e) {
            System.err.println("Its a Bug, please fix it, or contact developer");
            e.printStackTrace();
            //Will not happen here, unless it is a bug
        }
    }
    
    public boolean equals(Object other){
        return other instanceof DefaultInkTraceFormat; 
    }
}
