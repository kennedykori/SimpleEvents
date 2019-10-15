package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ProgressChangedEventTest {
	
	@Test
	public void testPreviousValue() {
		// The previous value to test against
		Float previousValue = Float.valueOf(".23");
		
		// Create a progress event with the previous value created above
		ProgressChangedEvent event = new ProgressChangedEvent(this, previousValue, Float.valueOf(".24"));
		
		// Assert that the previous value is correct
		assertEquals(previousValue, event.getPreviousValue());
	}
	
	@Test
	public void testNewValue() {
		// The new value to test against
		Float newValue = Float.valueOf(".24");
		
		// Create a progress event with the new value created above
		ProgressChangedEvent event = new ProgressChangedEvent(this, Float.valueOf(".23"), newValue);
		
		// Assert that the new value is correct
		assertEquals(newValue, event.getNewValue());
	}
	
	@ParameterizedTest
	@CsvSource({
		".2367, 23.67%",
		".12, 12.00%",
		"1, 100.00%",
		"0, 0.00%",
		".09999, 10.00%"
	})
	public void testPreviousPercentageValue(String progress, String progressAsPercetage) {
		// The previous value to test against
		Float previousValue = Float.valueOf(progress);
		
		// Create a progress event with the previous value created above
		ProgressChangedEvent event = new ProgressChangedEvent(this, previousValue, Float.valueOf(".24"));
		
		// Assert that the previous value as a percentage is correct
		assertEquals(progressAsPercetage, event.getPreviousValueAsPercentage());
	}
	
	@ParameterizedTest
	@CsvSource({
		".267, 26.70%",
		".10, 10.00%",
		"1, 100.00%",
		"0, 0.00%",
		".0994, 9.94%"
	})
	public void testNewPercentageValue(String progress, String progressAsPercetage) {
		// The new value to test against
		Float newValue = Float.valueOf(progress);
		
		// Create a progress event with the new value created above
		ProgressChangedEvent event = new ProgressChangedEvent(this, Float.valueOf(".23"), newValue);
		
		// Assert that the new value as a percentage is correct
		assertEquals(progressAsPercetage, event.getNewValueAsPercentage());
	}

	@Test
	public void testAcceptsValidProgessValuesOnly() {
		// Assert that a NullPointerException is thrown when null values are passed
		assertThrows(NullPointerException.class, () -> new ProgressChangedEvent(this, null, null));
		assertThrows(NullPointerException.class, () -> new ProgressChangedEvent(this, Float.valueOf(".45"), null));
		assertThrows(NullPointerException.class, () -> new ProgressChangedEvent(this, null, Float.valueOf(".65")));
		
		// Assert that negative values are not allowed
		assertThrows(IllegalArgumentException.class, () -> new ProgressChangedEvent(this, Float.valueOf("-1.15"), Float.valueOf(".45")));
		assertThrows(IllegalArgumentException.class, () -> new ProgressChangedEvent(this, Float.valueOf(".15"), Float.valueOf("-1.45")));
		
		// Assert that values greater than 1 are not allowed
		assertThrows(IllegalArgumentException.class, () -> new ProgressChangedEvent(this, Float.valueOf("1.2078"), Float.valueOf("0.12")));
		assertThrows(IllegalArgumentException.class, () -> new ProgressChangedEvent(this, Float.valueOf(".65"), Float.valueOf("3")));
	}
	
	@ParameterizedTest
	@CsvSource({
		".267, 26.70%, .34897, 34.90%",
		".10, 10.00%, .21996, 22.00%",
		"1, 100.00%, .9, 90.00%",
		"0, 0.00%, .7899, 78.99%",
		".0994, 9.94%, .0096, 0.96%"
	})
	public void testToString(String previousValue, String previousValueAsPercetage, String newValue, String newValueAsPercetage) {
		// Create a progress event with the supplied values
		ProgressChangedEvent event = new ProgressChangedEvent(this, Float.valueOf(previousValue), Float.valueOf(newValue));
		
		// Create the expected toString value
		String expectedToString = String.format("[ProgressChanged:[ %s => %s ]]", previousValueAsPercetage, newValueAsPercetage);
		
		// Assert that the toString method returns the correct value
		assertEquals(expectedToString, event.toString());
	}
}
