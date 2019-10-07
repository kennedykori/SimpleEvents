/**
 * 
 */
package com.kori_47.events;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 3:04:07 AM
 */
public class SimpleListener implements Listener {
	
	private final Map<Class<? extends Event>, List<Handler<? extends Event>>> registeredHandlers;

	/**
	 * 
	 */
	public SimpleListener() {
		this.registeredHandlers = new LinkedHashMap<>();
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public <T extends Event> void addHandler(Class<T> eventClass, Handler<T> handler) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		requireNonNull(handler, "handler cannot be null.");
		registeredHandlers.merge(eventClass, new ArrayList<>(), (oldValue, value) -> {oldValue.add(handler); return oldValue;});
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public <T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		requireNonNull(handler, "handler cannot be null.");
		registeredHandlers.computeIfPresent(eventClass, (key, value) -> {value.remove(handler); return value;});
	}

	/**
	* {@inheritDoc}
	*/
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void fireEvent(T event) {
		requireNonNull(event, "event cannot be null.");
		if (registeredHandlers.containsKey(event.getClass()))
			registeredHandlers.get(event.getClass()).forEach(handler -> ((Handler<T>) handler).handle(event));
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void clear() {
		registeredHandlers.values().forEach(list -> list.clear());
		registeredHandlers.clear();
	}

	/**
	* {@inheritDoc}
	*/
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> Optional<List<Handler<T>>> getHandlers(Class<T> eventClass) {
		requireNonNull(eventClass, "eventClass cannot be null.");
		List<Handler<T>> handlers = null;
		if (registeredHandlers.containsKey(eventClass))
			handlers = registeredHandlers.get(eventClass).stream().map(handler -> (Handler<T>) handler).collect(toList());
		return Optional.ofNullable(handlers);
	}

	/**
	 * Returns a {@code Set} view of all {@link Event events} that are currently supported by this 
	 * listener. i.e Returns a {@code Set} containing all the events that have {@link Handler handlers} 
	 * registered to handle them.
	 */
	@Override
	public Set<Class<? extends Event>> getSupportedEventTypes() {
		return new HashSet<>(registeredHandlers.keySet());
	}
}
