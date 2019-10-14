/**
 * 
 */
package com.kori_47.events;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * <p>
 * This is an object used to manage {@link Handler handlers} registered to it by clients. Ideally, a listener should be attached 
 * to/or be an object that triggers {@link Event events} that are of interest to other objects. The {@link #addHandler(Class, Handler)} 
 * and {@link #removeHandler(Class, Handler)} methods are used to register and unregister handlers from a listener respectively. 
 * The {@link #fireEvent(Event)} method is used to notify registered handlers that an event of interest has occurred.
 * </p>
 * <p>
 * The {@link #getHandlers(Class)} method can be used to retrieve all the handlers currently registered to this {@code Listener} that are 
 * interested in a given {@code Event} type. The {@link #getSupportedEventTypes()} returns a {@code Set} of all the {@code Event} types that 
 * have handlers registered to this listener. The {@link #clear()} method removes all handlers from a listener and should called once a listener 
 * is ready for disposal in order to avoid memory leaks.
 * </p>
 * 
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 2:44:51 AM
 */
public interface Listener {
	
	/**
	 * Adds a new {@link Handler} to this listener.
	 * 
	 * @param <T> the type of {@link Event} that the handler is interested in.
	 * 
	 * @param eventClass the class of the event that the handler is interested in.
	 * @param handler the handler being registered to this listener.
	 * 
	 * @throws NullPointerException if any of the arguments given is/are {@code null}.
	 */
	<T extends Event> void addHandler(Class<T> eventClass, Handler<T> handler);

	/**
	 * Removes a {@link Handler} from this listener.
	 * 
	 * @param <T> the type of {@link Event} that the handler is interested in.
	 * 
	 * @param eventClass the class of the event that the handler is interested in.
	 * @param handler the handler being removed from this listener.
	 * 
	 * @throws NullPointerException if any of the arguments given is/are {@code null}.
	 */
	<T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler);
	
	
	/**
	 * Executes all {@link Handler handlers} registered under this listener that support 
	 * the given event.
	 * 
	 * @param <T> the type of {@link Event} to fire.
	 * 
	 * @param event the event to fire.
	 * 
	 * @throws NullPointerException if event is {@code null}.
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
	 * 
	 * @throws NullPointerException if {@code eventClass} is {@code null}.
	 */
	<T extends Event> Optional<List<Handler<T>>> getHandlers(Class<T> eventClass);
	
	/**
	 * Returns a {@code Set} of all {@link Event events} that are currently supported by this 
	 * listener. i.e Returns a {@code Set} containing all the events that have {@link Handler handlers} 
	 * registered to handle them.
	 * 
	 * @return a {@code Set} of all {@code Events} that have handlers registered in this listener.
	 */
	Set<Class<? extends Event>> getSupportedEventTypes();
}
