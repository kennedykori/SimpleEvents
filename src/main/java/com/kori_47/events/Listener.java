/**
 * 
 */
package com.kori_47.events;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * An an object of this type is used to manage all the {@link Handler handlers} 
 * registered by clients to this listener for new {@link Event events} on a given 
 * object. When the {@linkplain #fireEvent(Event)} method is called, all handlers 
 * registered in this listener are notified of the new event.
 * 
 * 
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 2:44:51 AM
 */
public interface Listener {
	
	/**
	 * Adds a new {@link Handler} to this listener.
	 * 
	 * @param <T> the type of {@link Event} that the handler supports.
	 * 
	 * @param eventClass the class of the event that the handler supports.
	 * @param handler the handler being registered to this listener.
	 * 
	 */
	<T extends Event> void addHandler(Class<T> eventClass, Handler<T> handler);

	/**
	 * Removes a {@link Handler} from this listener.
	 * 
	 * @param <T> the type of an {@link Event} that the handler supports.
	 * 
	 * @param eventClass the class of the event that the handler supports.
	 * @param handler the handler being removed from this listener.
	 * 
	 */
	<T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler);
	
	
	/**
	 * Fires all {@link Handler handlers} registered under this listener that support 
	 * the given event.
	 * 
	 * @param <T> the type of {@link Event} to fire.
	 * 
	 * @param event the event to fire.
	 * 
	 */ 
	<T extends Event> void fireEvent(T event);
	
	/**
	 * Removes all the {@link Handler handlers} registered in this listener.
	 */
	void clear();

	/**
	 * Returns an {@link Optional} {@code List} of all the {@link Handler handlers} registered 
	 * in this listener that support the given event type. If no such handlers are registered, 
	 * the {@code Optional.isPresent()} will return {@code false}.
	 * 
	 * @param <T> the type of an {@link Event event} that the handlers to be returned support.
	 * 
	 * @param eventClass the class of the event that the handlers to be returned support.
	 *
	 * @return an {@code Optional List} containing all the handlers that support the given event type.
	 */
	<T extends Event> Optional<List<Handler<T>>> getHandlers(Class<T> eventClass);
	
	/**
	 * Returns a {@code Set} of all {@link Event events} that are currently supported by this 
	 * listener. i.e Returns a {@code Set} containing all the events that have {@link Handler handlers} 
	 * registered to handle them.
	 */
	Set<Class<? extends Event>> getSupportedEventTypes();
}