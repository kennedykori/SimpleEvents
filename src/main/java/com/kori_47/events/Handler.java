/**
 * 
 */
package com.kori_47.events;


/**
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 2:34:46 AM
 */
@FunctionalInterface
public interface Handler<T extends Event> {
	
	/**
	 * Performs a given action when an {@link Event} occurs in an object of interest. A 
	 * client should override this method to perform a desired action when the given event 
	 * is fired.
	 * 
	 * @param event the event that triggered this handler.
	 */
	void handle(T event);

}
