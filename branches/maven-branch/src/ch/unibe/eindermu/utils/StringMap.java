package ch.unibe.eindermu.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class StringMap<V> implements Map<String, V>, Iterable<V> {

	private StringList keys;
	private List<V> values;
	
	public StringMap() {
		clear();
		// TODO Auto-generated constructor stub
	}

	public void clear() {
		keys = new StringList();
		values  = new ArrayList<V>();
	}

    @Override
	public boolean containsKey(Object key) {
		return keys.contains((String)key);
	}

    @Override
	public boolean containsValue(Object value) {
		return values.contains(value);
	}

    @Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
        Set<java.util.Map.Entry<String, V>> result = new LinkedHashSet<java.util.Map.Entry<String, V>>();
		for(int i = 0; i<keys.size();i++){
			result.add(new StringEntry<V>(keys.get(i),values.get(i)));
		}
		return result;
	}

	@Override
	public V get(Object key) {
	    if(!keys.contains(key)){
	        return null;
	    }
	    return values.get(keys.indexOf(key));
	}

    @Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

    @Override
	public Set<String> keySet() {
		return new StringSet(keys);
	}
	
	/**
	 * Returns a list of keys in the correct order (the order in which they are added to the map)
	 * @return
	 */
	public StringList keyList(){
		return new StringList(keys);
	}

    @Override
	public V put(String key, V value) {
		if(containsKey(key)){
			values.set(keys.indexOf(key), value);
		}else{
			keys.add(key);
			values.add(value);
		}
		return value;
	}

    @Override
	public void putAll(Map<? extends String, ? extends V> m) {
		for(String k : m.keySet()){
			put(k,m.get(k));
		}
	}

    @Override
	public V remove(Object key) {
	    if(!keys.contains(key)){
	        return null;
	    }
	    int i = keys.indexOf(key);
		keys.remove(i);
		return values.remove(i);
	}

    @Override
	public int size() {
		return keys.size();
	}

    @Override
	public Collection<V> values() {
		return new ArrayList<V>(values);
	}
	
	/**
	 * String Entry class differs from the normal entry class
	 * in that two entries are identical if their key, which is a string, are equal.
	 * @author emanuel
	 *
	 * @param <T> the Type of the element stored.
	 */
	public class StringEntry<T> implements Entry<String, T>{
		
		private String key;
		private T value;
		public StringEntry(String key, T value){
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}

		public T getValue() {
			return value;
		}

		public T setValue(T value) {
			this.value = value;
			return value;
		}
	}

    @Override
    public Iterator<V> iterator() {
        return values.iterator();
    }

}
