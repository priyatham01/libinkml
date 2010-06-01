package ch.unibe.eindermu.utils;

import java.util.Collection;
import java.util.HashSet;

public class StringSet extends HashSet<String> {

	private static final long serialVersionUID = 8193427041212856509L;

	public StringSet() {
		super();
	}

	public StringSet(Collection<? extends String> c) {
		super(c);
	}

	public StringSet(int initialCapacity) {
		super(initialCapacity);
	}

	public StringSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	public boolean contains(String string){
		for(String s : this){
			if(s.equals(string)){
				return true;
			}
		}
		return false;
	}

}
