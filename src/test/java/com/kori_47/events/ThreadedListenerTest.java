package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;;

public class ThreadedListenerTest {
	
	@Test
	public void testAddHandler() {
		// Create a listener
		ThreadedListener listener = new ThreadedListener();
		
		// Create generic handlers
		Handler<SimpleEvent> handler1 = e -> e.source();
		Handler<SimpleEvent> handler2 = e -> e.toString();
		
		// Assert that the listener has no SimpleEvent handlers before we add them
		assertFalse(listener.getHandlers(SimpleEvent.class).isPresent());
		
		// Add the first SimpleEvent handler
		listener.addHandler(SimpleEvent.class, handler1);
		
		// Assert that the listener has 1 SimpleEvent handler
		assertEquals(1, listener.getHandlers(SimpleEvent.class).get().size());
		
		// Add the second SimpleEvent handler
		listener.addHandler(SimpleEvent.class, handler2);
		
		// Assert that the listener has 2 SimpleEvent handlers
		assertEquals(2, listener.getHandlers(SimpleEvent.class).get().size());
		
		listener.dispose();
	}

	@ParameterizedTest
	@MethodSource("listenerProvider")
	public void testDispose(ThreadedListener listener) {
		// Assert that the listener is not disposed initially
		assertFalse(listener.isDisposed());
		
		// Assert that the listener methods do not throw IllegalStateException before the listener is disposed
		//assertDoesNotThrow(IllegalStateException.class, () -> );
		
		// Dispose the listener
		listener.dispose();
		
		// Assert that the listener is now disposed
		assertTrue(listener.isDisposed());
	}
	
	public static Stream<ThreadedListener> listenerProvider() {
		ExecutorService service = Executors.newFixedThreadPool(3);
		return Stream.of(
				new ThreadedListener(),										// An instance of thread listener using the default constructor
				new ThreadedListener(0),									// A thread listener with unlimited threads
				new ThreadedListener(1, Executors.defaultThreadFactory()),	// A thread listener with a single thread and a custome thread factory
				new ThreadedListener(service, true)							// A thread with a custom executor service
			);
	}
}
