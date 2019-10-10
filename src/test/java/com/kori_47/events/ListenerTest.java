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
		// Create lists of handlers from the handlers provider
		List<Handler<SimpleEvent>> simpleEventHandlers = simpleEventHandlersProducer().collect(toList());
		List<Handler<ProgressChangedEvent>> progressChangedEventHandlers = progressChangedEventHandlersProducer().collect(toList());
		
		// Assert that the listener doesn't have SimpleEventHandlers before we start
		assertFalse(listener.getHandlers(SimpleEvent.class).isPresent());
		
		// Add SimpleEvent handlers to the listener and assert that each time, a handler is added
		for (int count = 0; count < simpleEventHandlers.size(); count++) {
			// Add the handler to the listener
			listener.addHandler(SimpleEvent.class, simpleEventHandlers.get(count));
			
			// Assert that SimpleEvent handlers increased
			assertEquals((count + 1), listener.getHandlers(SimpleEvent.class).get().size());
		}
	
		// Assert that the listener doesn't have ProgressChangedEvents before we start
		assertFalse(listener.getHandlers(ProgressChangedEvent.class).isPresent());
		
		// Add ProgressChangedEvent handlers to the listener and assert that each time, a handler is added
		for (int count = 0; count < progressChangedEventHandlers.size(); count++) {
			// Add the handler to the listener
			listener.addHandler(ProgressChangedEvent.class, progressChangedEventHandlers.get(count));
			
			// Assert that ProgressChangedEvent handlers increased
			assertEquals((count + 1), listener.getHandlers(ProgressChangedEvent.class).get().size());
		}
		
		// Assert that a NullPointerException is thrown when null arguments are passed to addHandler
		assertThrows(NullPointerException.class, () -> listener.addHandler(null, null));
		assertThrows(NullPointerException.class, () -> listener.addHandler(SimpleEvent.class, null));
		assertThrows(NullPointerException.class, () -> listener.addHandler(null, simpleEventHandlers.get(0)));
		
		// Clean up
		cleanUp(listener);
	}

	@ParameterizedTest
	@MethodSource("listenerProvider")
	default void testRemoveHandler(T listener) {
		// Create lists of handlers from the handlers provider
		List<Handler<SimpleEvent>> simpleEventHandlers = simpleEventHandlersProducer().collect(toList());
		List<Handler<ProgressChangedEvent>> progressChangedEventHandlers = progressChangedEventHandlersProducer().collect(toList());
		
		// Add SimpleEvent handlers to the listener
		simpleEventHandlers.forEach(handler -> listener.addHandler(SimpleEvent.class, handler));
		
		// Assert that all SimpleEvent handlers were added
		assertEquals(simpleEventHandlers.size(), listener.getHandlers(SimpleEvent.class).get().size());
		
		// Remove SimpleEvent handlers one by one and assert that each time, the handler has been removed
		for (int index = 0, remainingHandlers = (simpleEventHandlers.size() - 1); index < simpleEventHandlers.size(); index++, remainingHandlers--) {
			Handler<SimpleEvent> handler = simpleEventHandlers.get(index);
			// Remove the handler
			listener.removeHandler(SimpleEvent.class, handler);
			
			// Assert that the handlers in the listener reduced
			assertEquals(remainingHandlers, listener.getHandlers(SimpleEvent.class).get().size());
		}
		
		// Add ProgressChangedEvent handlers to the listener
		progressChangedEventHandlers.forEach(handler -> listener.addHandler(ProgressChangedEvent.class, handler));
		
		// Assert that all ProgressChangedEvent handlers were added
		assertEquals(progressChangedEventHandlers.size(), listener.getHandlers(ProgressChangedEvent.class).get().size());
		
		// Remove ProgressChangedEvent handlers one by one and assert that each time, the handler has been removed
		for (int index = 0, remainingHandlers = (progressChangedEventHandlers.size() - 1); index < simpleEventHandlers.size(); index++, remainingHandlers--) {
			Handler<ProgressChangedEvent> handler = progressChangedEventHandlers.get(index);
			// Remove the handler
			listener.removeHandler(ProgressChangedEvent.class, handler);
			
			// Assert that the handlers in the listener reduced
			assertEquals(remainingHandlers, listener.getHandlers(ProgressChangedEvent.class).get().size());
		}
		
		// Assert that a NullPointerException is thrown when null arguments are passed to removeHandler
		assertThrows(NullPointerException.class, () -> listener.removeHandler(null, null));
		assertThrows(NullPointerException.class, () -> listener.removeHandler(SimpleEvent.class, null));
		assertThrows(NullPointerException.class, () -> listener.removeHandler(null, simpleEventHandlers.get(0)));
		
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
	 * This is a {@link Handler} producer. All {@code Handler}s produced by the {@code Stream} returned by this 
	 * method are attached to {@link SimpleEvent}.
	 * 
	 * @return a {@code Stream} of {@code SimpleEvent} handlers.
	 */
	default Stream<Handler<SimpleEvent>> simpleEventHandlersProducer() {
		return Stream.of(
				event -> event.source(),
				event -> event.toString()
		);
	}
	
	/**
	 * This is a {@link Handler} producer. All {@code Handler}s produced by the {@code Stream} returned by this 
	 * method are attached to {@link ProgressChangedEvent}.
	 * 
	 * @return a {@code Stream} of {@code SimpleEvent} handlers.
	 */
	default Stream<Handler<ProgressChangedEvent>> progressChangedEventHandlersProducer() {
		return Stream.of(
				event -> event.getPreviousValue(),
				event -> event.getNewValue(),
				event -> event.source()
		);
	}
}
