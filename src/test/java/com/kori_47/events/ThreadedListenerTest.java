package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
		// TODO I should add more appropriate test for multi-threaded environment
		ListenerTest.super.testFireEvent(listener);
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
}
