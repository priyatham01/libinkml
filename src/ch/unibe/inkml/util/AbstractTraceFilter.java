package ch.unibe.inkml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.unibe.inkml.InkInk;
import ch.unibe.inkml.InkTraceView;
import ch.unibe.inkml.InkTraceViewLeaf;

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

    public List<InkTraceViewLeaf> filterLeaf(InkInk ink){
        return filter(ink.getFlatTraceViewLeafs());
    }
    
    public List<InkTraceView> filter(InkInk ink){
        return filter(ink.getViewRoot().getFlattenedViews());
    }
    
    public abstract boolean pass(InkTraceView view);

}
