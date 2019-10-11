package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;;

public class ThreadedListenerTest implements ListenerTest<ThreadedListener> {
	
	@Override
	public void cleanUp(ThreadedListener listener) {
		listener.dispose();
	}
	
	@Override
	public Stream<ThreadedListener> listenerProvider() {
		ExecutorService service = Executors.newFixedThreadPool(3);
		return Stream.of(
				new ThreadedListener(),										// An instance of thread listener using the default constructor
				new ThreadedListener(0),									// A thread listener with unlimited threads
				new ThreadedListener(1, Executors.defaultThreadFactory()),	// A thread listener with a single thread and a custome thread factory
				new ThreadedListener(service, true)							// A thread with a custom executor service
			);
	}
	
	@ParameterizedTest
	@MethodSource("listenerProvider")
	@Override
	public void testFireEvent(ThreadedListener listener) {
		/*
		 * THIS TESTS ARE VERY HACKY AND MIGHT NOT ALWAYS PASS DUE TO THE 
		 * NON-DETERMINISTIC NATURE OF MULTI-THREADED ENVIROMNETS. IF YOU 
		 * HAVE A BETTER WAY OF TESTING MULTI-THREADED CODE, FEEL FREE TO 
		 * CONTACT ME OR FORK THE REPO AND CREATE A PULL REQUEST.
		 */ 
		
		
		
		// Create a value to be increamented
		AtomicInteger testValue = new AtomicInteger();
		
		// Duration to block the thread
		long duration = 4000;
		
		// Create a handler that should increment testValue by one after the specified duration.
		Handler<SimpleEvent> handler1 = event -> {
			synchronized (this) {	
				block(duration);
				testValue.getAndIncrement();
			}
		};
		
		// Add the handler to the listener
		listener.addHandler(SimpleEvent.class, handler1);
		
		// Fire a SimpleEvent
		listener.fireEvent(new SimpleEvent(this));
		
		// Assert that testValue has not yet increased
		assertEquals(0, testValue.get());
		
		// Fire another SimpleEvent
		listener.fireEvent(new SimpleEvent(this));
		
		// Assert that testValue has not yet increased
		assertEquals(0, testValue.get());
		
		// Wait for the previously fired events to complete
		block(duration * 3); // 3 times the original duration should be long enough for handlers to have completed
		
		// Assert that testValue has increased after the handlers fired
		assertEquals(2, testValue.get());
		
		// Assert that no errors occur if fireEvent is called with an event with no registered handlers
		assertFalse(listener.getHandlers(ProgressChangedEvent.class).isPresent()); // Assert that there no ProgressChangedEvent handlers
		assertDoesNotThrow(() -> listener.fireEvent(new ProgressChangedEvent(this, Float.valueOf(".1"), Float.valueOf(".2"))));
		
		// Assert that testValue wasn't changed by the previous fireEvent() call of a non SimpleEvent
		assertEquals(2, testValue.get());
		
		// Create a handler that should increment testValue by one immediately.
		Handler<SimpleEvent> handler2 = event -> testValue.getAndIncrement();

		// Add the handler to the listener
		listener.addHandler(SimpleEvent.class, handler2);
		
		// Fire a SimpleEvent 3 times
		listener.fireEvent(new SimpleEvent(this));
		listener.fireEvent(new SimpleEvent(this));
		listener.fireEvent(new SimpleEvent(this));
		
		// Wait for at least one handler to finish execution
		block(duration * 2);
		
		// Assert that testValue has increased by one, i.e At least one handler has executed to completion 
		assertTrue(testValue.get() > 2);
		
		// Clean up
		cleanUp(listener);
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
		
		// Assert that an IllegalStateException is thrown when some methods are called after the listener has been disposed
		assertThrows(IllegalStateException.class, () -> listener.addHandler(SimpleEvent.class, simpleEventHandlersProducer().findAny().get()));
		assertThrows(IllegalStateException.class, () -> listener.removeHandler(SimpleEvent.class, simpleEventHandlersProducer().findAny().get()));
		assertThrows(IllegalStateException.class, () -> listener.fireEvent(new SimpleEvent(this)));
		assertThrows(IllegalStateException.class, () -> listener.getHandlers(SimpleEvent.class));
		assertThrows(IllegalStateException.class, () -> listener.getSupportedEventTypes());
		
		// Call dispose multiple times to make sure it returns cleanly even after a listener has already been disposed
		listener.dispose();
		listener.dispose();
		listener.dispose();
	}

	@Test
	public void testDisposeForListenerWithExternalExecutor() {
		// Create the ExecutorService
		ExecutorService service = Executors.newCachedThreadPool();
		
		// Create a listener with the provided service
		ThreadedListener listener = new ThreadedListener(service, false);
		
		// Assert that the service is running before we dispose the listener
		assertFalse(service.isShutdown());
		
		// Dispose the listner
		listener.dispose();
		
		// Assert that the service wasn't shutdown when we called dispose
		assertFalse(service.isShutdown());
		
		// Shutdown the service
		service.shutdownNow();
	}
	
	/**
	 * Block the calling thread for the specified duration in milliseconds.
	 * 
	 * @param duration the length of time to block the calling thread.
	 */
	private void block(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Blocking was interrupted", ex);
		}
	}
}
