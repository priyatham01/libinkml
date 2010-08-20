/*
 * Created on 18.02.2008
 *
 * Copyright (C) 2007  Emanuel Indermühle <emanuel@inthemill.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 * 
 * @author emanuel
 * OrderedMap.java
 */
package ch.unibe.eindermu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OrderedMap<V> extends HashMap<String,V> implements Iterable<V>{
    private static final long serialVersionUID = 1L;
    private List<String> keys = new ArrayList<String>();
    
    public V put(String key,V value){
        if(!this.containsKey(key)){
            this.keys.add(key);
        }
        return super.put(key, value);
    }
    public V add(String key,V value){
        return put(key,value);
    }
    
    public V add(V value){
        return put(((Integer) this.keys.size()).toString(),value);
    }
    
    public V remove(Object key){
        this.keys.remove(key);
        return super.remove(key);
    }
    
    public List<String> orderedKeys(){
        return new ArrayList<String>(this.keys);
    }
    
    public List<V> orderedValues(){
        ArrayList<V> l = new ArrayList<V>();
        for(V i : this){
            l.add(i);
        }
        return l;
    }
    
    public Iterator<V> iterator() {
        return new Iterator<V>(){
            private int current = 0;
            private boolean canRemove = false;
            public boolean hasNext() {
                return keys.size() < this.current+1;
            }

            public V next() {
                if(this.canRemove){
                    this.current++;
                }
                this.canRemove = true;
                return get(keys.get(this.current));
            }

            public void remove() {
                if(!this.canRemove){
                    throw new IllegalStateException();
                }
                OrderedMap.this.remove(keys.get(this.current));
                this.canRemove = false;
            }
        };
    }
}
