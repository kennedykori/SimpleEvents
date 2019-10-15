/**
 * 
 */
package com.kori_47.events;


/**
 * <p>
 * This is a callback that is executed once an {@link Event}, that this handler is interested in, occurs. Before the handler can 
 * be executed, it must be registered to a {@link Listener} using the {@code Listener}'s {@link Listener#addHandler(Class, Handler) addHandler} 
 * method. Clients should override the {@link #handle(Event)} method of this interface and add code that should be 
 * executed when an event of interest occurs.
 * </p>
 * <p>
 * <i><b>NOTE:</b> This is a {@link FunctionalInterface} as it only declares one abstract method: {@code handle(T Event)}.</i>
 * </p>
 * 
 * @param <T> The type of {@code Event} that this handler is intrested in.
 * 
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 2:34:46 AM
 */
@FunctionalInterface
public interface Handler<T extends Event> {
	
	/**
	 * Performs a given action when an {@link Event} of interest occurs. A client should override this method and add code that 
	 * should be executed once such an event occurs.
	 * 
	 * @param event the event of interest to handle.
	 */
	void handle(T event);

}
