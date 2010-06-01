package ch.unibe.inkml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.unibe.inkml.InkTraceView;

abstract public class AbstractTraceFilter implements TraceViewFilter {
    
    public <L extends InkTraceView> List<L> filter(Collection<L> list) {
        List<L> result = new ArrayList<L>();
        for(L s : list){
            if(pass(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public abstract boolean pass(InkTraceView view);

}
