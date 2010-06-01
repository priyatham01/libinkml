/*
 * Created on 23.07.2007
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
 */

package ch.unibe.eindermu.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class AbstractObservable implements Observable{
    
    private Map<Aspect, Set<Observer>> observers;
    
    
    public void registerFor(Aspect event, Observer o) {
        if(this.observers == null) {
            this.observers = new WeakHashMap<Aspect, Set<Observer>>();
        }
        if(!this.observers.containsKey(event)) {
            this.observers.put(event, new HashSet<Observer>());
        }
        this.observers.get(event).add(o);
    }
    
    public void unregisterFor(Aspect event,Observer o){
    	if(this.observers != null){
    	    if(event == Observable.ON_ALL){
    	        for( Set<Observer> set : observers.values()){
	                set.remove(o);
    	        }
    	    }
    	    else if(observers.containsKey(event)){
	    		observers.get(event).remove(o);
    	    }
    	}
    }
    
    public void notifyObserver(Aspect event) {
        notifyObserver(event, this);
    }
    
    public void notifyObserver(Aspect event, Object subject) {
        if(this.observers != null) {
            if(this.observers.containsKey(event)) {
                for(Observer o : observers.get(event)) {
                    o.notifyFor(event, subject);
                }
            }
            if(observers.containsKey(Observable.ON_ALL)) {
                for(Observer o : observers.get(Observable.ON_ALL)) {
                    o.notifyFor(event, subject);
                }
            }
        }
    }
    
}
