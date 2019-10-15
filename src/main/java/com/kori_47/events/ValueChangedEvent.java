/**
 * 
 */
package com.kori_47.events;

/**
 * <p>
 * This is an {@link Event event} that can be used to signal that a value of interest has changed. 
 * The {@link #getPreviousValue()} and {@link #getNewValue()} methods can be used to retrieve the 
 * previous and new value respectively.
 * </p>
 * <p>
 * <i>
 * <b>NOTE:</b> This class doesn't check whether the previous and new value are equal, if both values 
 * are equal, they will be accepted just fine without any exceptions being thrown. Derivatives of this 
 * class may choose to enforce that the values be not equal.
 * </i>
 * </p>
 * 
 * 
 * @param <T> the type of value that changed.
 * 
 * @author Kennedy Kori
 *
 * @since Oct 10, 2019, 6:42:02 PM
 */
public class ValueChangedEvent<T> extends SimpleEvent {
	
	private final T previousValue;
	private final T newValue;

	/**
	 * Creates a new {@code ValueChangedEvent} with the given source, previous value and new value.
	 * 
	 * @param source the object where this event originated.
	 * @param previousValue the previous value before the changed happened.
	 * @param newValue the current value.
	 */
	public ValueChangedEvent(Object source, T previousValue, T newValue) {
		super(source);
		this.previousValue = previousValue;
		this.newValue = newValue;
	}

	/**
	 * Returns the previous value before the change.
	 * 
	 * @return the previous value.
	 */
	public T getPreviousValue() {
		return previousValue;
	}

	/**
	 * Returns the current value after the change.
	 * 
	 * @return the current value.
	 */
	public T getNewValue() {
		return newValue;
	}
}
