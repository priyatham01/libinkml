package ch.unibe.eindermu.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringList extends ArrayList<String> {

	private static final long serialVersionUID = 297432135702116020L;

	public StringList(List<String> values) {
		this();
		addAllUnique(values);
	}

	public StringList() {
		super();
	}
    /**
     * @param strings
     */
    public StringList(String[] strings) {
        this();
        addAllUnique(strings);
    }

    /**
     * @param strings
     */
    public boolean addAllUnique(String[] strings) {
        for (String s:strings){
            addUnique(s);
        }
        return true;
    }

    public boolean addAllUnique(Collection<String> c) {
    	for (String s:c){
    		addUnique(s);
    	}
    	return true;
    }
    public void addUnique(String s){
        assert s!=null;
    	if(!this.contains(s)){
    		add(s);
    	}
    }
    @Override
    public boolean add(String s){
        assert s!=null;
        return super.add(s);
    }
    @Override
    public String set(int index, String s){
        assert s!=null;
        return super.set(index,s);
    }


    public String join(String separator) {
        String result = "";
        for(String item : this){
            result += item + separator;
        }
        return result.substring(0, Math.max(0, result.length()-separator.length()));
    }
    
    @Override
    public boolean contains(Object s){
        for(String e : this){
            if(e.equals(s)){
                return true;
            }
        }
        return false;
    }

}
