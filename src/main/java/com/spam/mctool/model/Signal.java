package com.spam.mctool.model;

import java.util.HashSet;
import java.util.Set;

/** 
 * @author konne
 * 
 * The class represents a Signal and it's Listeners.
 * In order to fire the signal call the fire(Event) 
 * function. In order to define what should be done for 
 * every single listener you need to overwrite the function
 * fire(Event,Listener).
 * 
 * Most implementations will use this class through Containment
 * and delegate it's own functions to an object of this class.
 * 
 * @param <Listener>  Which listener class to use is defined
 *                    using the generic parameter Listener
 *                    which may be something like
 *                    SenderAddedOrRemoved.
 */
public abstract class Signal<Listener> {
	public Signal() {
		listeners = new HashSet<Listener>();
	}
	
	/** 
	 * Adds a new listener to the signal
	 * @param l  The listener to be added
	 */
	public void addListener(Listener l) {
		listeners.add(l);
	}
	
	/** 
	 * Removes a listener from the signal
	 * @param l   The listener to be removed
	 */
	public void removeListener(Listener l) {
		listeners.remove(l);
		
	}
	
	/** Fires an event to all listeners
	 * @param event   The event to be fired.
	 * 				  The event class might be something like 
	 *                SenderAddedOrRemovedEvent.
	 */
	public void fire(Object event) {
		for(Listener l : listeners) {
			fire(event,l);
		}	
	}
	
	/**
	 * Fires an event to exactly one listener
	 * This should only be involved by the class itself.
	 * Overwrite this class to define how to pass the 
	 * event to the listener.
	 * 
	 * @param event   The event to be fired.
	 * @param l		  The listener that should receive the event
	 */
	abstract protected void fire(Object event, Listener l);
	
	private Set<Listener> listeners;
}
