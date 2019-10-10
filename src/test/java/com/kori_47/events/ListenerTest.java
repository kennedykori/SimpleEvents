package com.kori_47.events;

import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Defines test to be run against {@link Listener} implementations.
 * 
 * @param <T> a type that implements the {@code Listener} interface. 
 * 
 * @author Kennedy Kori
 *
 * @since Oct 10, 2019, 2:29:55 AM
 * 
 */
@TestInstance(Lifecycle.PER_CLASS)
public interface ListenerTest<T extends Listener> {

	/**
	 * Cleans up and closes a {@code Listener} releasing any resources it might be using.
	 * 
	 * @param listener the listener to clean up.
	 */
	void cleanUp(T listener);
	
	/**
	 * Returns a {@code Stream} of {@code Listener} instances to be used for testing.
	 * 
	 * @return a {@code Stream} of {@code Listener} instances to be used for testing.
	 */
	Stream<T> listenerProvider();
	
	@ParameterizedTest
	@MethodSource("listenerProvider")
	default void testAddHandler(T listener) {
		// Create generic handlers
		Handler<SimpleEvent> handler1 = event -> event.source();
		Handler<SimpleEvent> handler2 = event -> event.toString();
		Handler<SimpleEvent> handler3 = event -> event.toString();
		
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
		
		// Assert that a NullPointerException is thrown when null arguments are passed to addHandler
		assertThrows(NullPointerException.class, () -> listener.addHandler(null, null));
		assertThrows(NullPointerException.class, () -> listener.addHandler(SimpleEvent.class, null));
		assertThrows(NullPointerException.class, () -> listener.addHandler(null, handler3));
		
		// Clean up
		cleanUp(listener);
	}

	@ParameterizedTest
	@MethodSource("listenerProvider")
	default void testRemoveHandler(T listener) {
		// Create a List of handlers from the handlers provider
		List<Handler<SimpleEvent>> handlers = simpleEventHandlersProducer().collect(toList());
		
		// Add the handlers to the listener
		handlers.forEach(handler -> listener.addHandler(SimpleEvent.class, handler));
		
		// Assert that all the handlers were added
		assertEquals(handlers.size(), listener.getHandlers(SimpleEvent.class).get().size());
		
		// Remove the handlers one by one and assert that each time, the handler has been removed
		for (int index = 0, remainingHandlers = (handlers.size() - 1); index < handlers.size(); index++, remainingHandlers--) {
			Handler<SimpleEvent> handler = handlers.get(index);
			// Remove the handler
			listener.removeHandler(SimpleEvent.class, handler);
			
			// Assert that the handlers in the listener reduced
			assertEquals(remainingHandlers, listener.getHandlers(SimpleEvent.class).get().size());
		}
		
		// Assert that a NullPointerException is thrown when null arguments are passed to removeHandler
		assertThrows(NullPointerException.class, () -> listener.removeHandler(null, null));
		assertThrows(NullPointerException.class, () -> listener.removeHandler(SimpleEvent.class, null));
		assertThrows(NullPointerException.class, () -> listener.removeHandler(null, handlers.get(0)));
		
		// Clean up
		cleanUp(listener);
	}

	@ParameterizedTest
	@MethodSource("listenerProvider")
	default void testFireEvent(T listener) {
		// Create a value to be increamented
		AtomicInteger testValue = new AtomicInteger();
		
		// Create a handler that should increment testValue by one each time it is called.
		Handler<SimpleEvent> handler = event -> testValue.getAndIncrement();
		
		// Add the handler to the listener
		listener.addHandler(SimpleEvent.class, handler);
		
		// Fire a SimpleEvent
		listener.fireEvent(new SimpleEvent(this));
		
		// Assert that testValue has been increased
		assertEquals(1, testValue.get());
		
		// Clean up
		cleanUp(listener);
	}
	
	/**
	 * This is {@link Handler} producer. All {@code Handler}s produced by the {@code Stream} returned by this 
	 * method are attached to the {@link SimpleEvent} {@code Event}.
	 * 
	 * @return a {@code Stream} of {@code SimpleEvent} handlers.
	 */
	default Stream<Handler<SimpleEvent>> simpleEventHandlersProducer() {
		return Stream.of(
				event -> event.source(),
				event -> event.toString()
		);
	}
}
