package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SimpleEventTest {

	@Test
	public void testEventSource() {
		// Create a source object
		Object source = new Object();
		
		// Create an event with given source
		SimpleEvent event = new SimpleEvent(source);
		
		// Check that the event source is the correct source
		assertEquals(source, event.source());
	}

	@Test
	public void testThrowsNullPointerException() {
		// Create a null source
		Object nullSource = null;
		
		// Check that a NullPointerException is thrown when a null source is passed
		// to the SimpleEvent constructor
		assertThrows(NullPointerException.class, () -> new SimpleEvent(nullSource));
	}
}
