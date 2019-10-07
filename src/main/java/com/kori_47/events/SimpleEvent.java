package com.kori_47.events;

import static java.util.Objects.requireNonNull;

/**
 * This is a simple implementation of the {@link Event} interface.
 * 
 * @author Kennedy Kori
 *
 * @since Oct 8, 2019, 2:01:45 AM
 */
public class SimpleEvent implements Event {

	/**
	 * the source of this event.
	 */
	private final Object source;
	
	/**
	 * Creates a new event with the given source which must not be null.
	 * 
	 * @param source the source of this event.
	 * 
	 * @throws NullPointerException if source is null.
	 */
	public SimpleEvent(Object source) {
		this.source = requireNonNull(source, "source cannot be null.");
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public Object source() {
		return source;
	}
}
