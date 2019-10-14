/**
 * 
 */
package com.kori_47.events;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is a skeletal implementation of the {@link Listener} interface from which more concrete implementations 
 * of {@code Event} can inherit from.
 * 
 * @author Kennedy Kori
 *
 * @since Oct 9, 2019, 4:43:44 PM
 */
public abstract class AbstractListener implements Listener {
	
	/**
	 * stores all registered handlers
	 */
	protected final Map<Class<? extends Event>, List<Handler<? extends Event>>> registeredHandlers;

	/**
	 * Creates a new Listener with the given listener storage.
	 * 
	 * @param registeredHandlers the {@code Map} to store listeners.
	 * 
	 * @throws NullPointerException if {@code registeredHandlers} is {@code null}.
	 */
	public AbstractListener(Map<Class<? extends Event>, List<Handler<? extends Event>>> registeredHandlers) {
		this.registeredHandlers = requireNonNull(registeredHandlers, "registeredHandlers cannot be null.");
	}

	/**
	* {@inheritDoc} 
	*/
	@Override
	public <T extends Event> void addHandler(Class<T> eventClass, Handler<T> handler) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		requireNonNull(handler, "handler cannot be null.");
		registeredHandlers.merge(
			eventClass, createHandlerList(),
			(oldValue, value) -> oldValue
		);
		registeredHandlers.get(eventClass).add(handler);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public <T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		requireNonNull(handler, "handler cannot be null.");
		registeredHandlers.computeIfPresent(
			eventClass,
			(key, value) -> {
				value.remove(handler);
				return value;
			}
		);
	}

	/**
	* {@inheritDoc}
	*/
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void fireEvent(T event) {
		requireNonNull(event, "event cannot be null.");
		if (registeredHandlers.containsKey(event.getClass())) {
			registeredHandlers.get(event.getClass()).forEach(
				handler -> ((Handler<T>) handler).handle(event)
			);
		}
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void clear() {
		// First clear the lists to avoid memory risks
		registeredHandlers.values().forEach(list -> list.clear());
		// Clear the map
		registeredHandlers.clear();
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public <T extends Event> Optional<List<Handler<T>>> getHandlers(Class<T> eventClass) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		if (!registeredHandlers.containsKey(eventClass)) return Optional.ofNullable(null);
		@SuppressWarnings("unchecked")
		List<Handler<T>> handlers = registeredHandlers.get(eventClass).stream().map(handler -> (Handler<T>) handler).collect(toList());
		return Optional.ofNullable(handlers);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public Set<Class<? extends Event>> getSupportedEventTypes() {
		return new HashSet<>(registeredHandlers.keySet());
	}
	
	/**
	 * Returns a {@code List} to be used in the storage of {@link Handler handlers} of a given {@link Event event}.
	 * 
	 * @return a list.
	 */
	protected abstract List<Handler<? extends Event>> createHandlerList();
}
