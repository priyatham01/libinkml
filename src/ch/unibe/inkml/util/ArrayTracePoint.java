/**
 * 
 */
package ch.unibe.inkml.util;

import ch.unibe.inkml.InkTraceFormat;
import ch.unibe.inkml.InkTracePoint;
import ch.unibe.inkml.InkChannel.ChannelName;

/**
 * @author emanuel
 *
 */
public class ArrayTracePoint extends InkTracePoint {
    private double[] data;
    private InkTraceFormat format;
    /**
     * @param data
     */
    public ArrayTracePoint(double[] data,InkTraceFormat format) {
        this.data = data;
        this.format = format;
    }

    @Override
    public double get(ChannelName channel) {
        return data[format.getIndex().get(channel)];
    }

    @Override
    public Object getObject(ChannelName channel) {
        return format.objectify(channel, get(channel));
    }

    @Override
    public void set(ChannelName channel, double d) {
        data[format.getIndex().get(channel)] = d;
    }

    @Override
    public void set(ChannelName channel, Object value) {
        set(channel,format.doubleize(channel, value));
    }

}
