package com.kori_47.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

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
}
