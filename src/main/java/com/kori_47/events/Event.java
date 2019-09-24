/**
 * 
 */
package com.kori_47.events;

/**
 * This represents an occurrence of interest during the normal execution of 
 * a program. Each event has an associated source which is typically where the 
 * event originated. The {@link #source()} method returns the source of this event.
 * 
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 2:06:25 AM
 */
public interface Event {
	
	/**
	 * Returns the source of this event which is most likely where this event originated.
	 * 
	 * @return the source of this event.
	 */
	Object source();

}
