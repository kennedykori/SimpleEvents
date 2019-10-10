/**
 * 
 */
package com.kori_47.events;

import static java.util.Objects.requireNonNull;

/**
 * This is an {@link Event event} that can be used to indicate that progress of a task has changed. It inherits from 
 * {@link ValueChangedEvent} and accepts {@code Float} progress values between 0.0 to 1.0 for both the previous and 
 * new value. Two additional methods; {@link #getPreviousValueAsPercentage()} and {@link #getNewValueAsPercentage()}, 
 * that can return the previous and next value respectively, as percentages have also been added.
 * 
 * @author Kennedy Kori
 *
 * @since Oct 10, 2019, 7:00:33 PM
 */
public class ProgressChangedEvent extends ValueChangedEvent<Float> {

	/**
	 * Creates a new {@code ProgressChangedEvent} with the given source, previous and new value.
	 * 
	 * @param source the source where this event originated.
	 * @param previousValue the previous progress value before the change.
	 * @param newValue the new progress value after the change.
	 */
	public ProgressChangedEvent(Object source, Float previousValue, Float newValue) {
		super(source, requireValidProgress(previousValue, "previousValue"), requireValidProgress(newValue, "newValue"));
	}
	
	/**
	 * Returns the previous progress value as a percentage.
	 * 
	 * @return the previous progress value as a percentage.
	 */
	public String getPreviousValueAsPercentage() {
		return toPercentage(this.getPreviousValue());
	}
	
	/**
	 * Returns the new progress value as a percentage.
	 * 
	 * @return the new progress value as a percentage.
	 */
	public String getNewValueAsPercentage() {
		return toPercentage(this.getNewValue());
	}
	
	@Override
	public String toString() {
		return String.format("[ProgressChanged:[ %s => %s ]]", getPreviousValueAsPercentage(), getNewValueAsPercentage());
	}
	
	private static final String toPercentage(Float value) {
		requireNonNull(value);
		return String.format("%.2f", value) + "%";
	}
	
	private static final Float requireValidProgress(Float progress, String name) {
		requireNonNull(name);
		if (progress.floatValue() < 0f && progress.floatValue() > 1f)
			throw new IllegalArgumentException(name + " must be greater than or equal to 0 and less than or equal to 1.");
		return progress;
	}
}
