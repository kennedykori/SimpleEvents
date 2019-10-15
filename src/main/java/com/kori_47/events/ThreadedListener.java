/**
 * 
 */
package com.kori_47.events;

import static java.util.Objects.requireNonNull;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.synchronizedList;

import static com.kori_47.utils.ObjectUtils.requireNonNegative;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * This is an implementation of a {@link Listener} that handles each {@link Handler} on it's own 
 * thread. That is, when {@link #fireEvent(Event)} is called, each qualifying {@code Handler}'s 
 * {@link Handler#handle(Event) handle} method is executed on a separate thread <i>(whether new or reused)</i>. 
 * This type of listener is mostly useful for handling long running tasks that might block the main thread 
 * <i>(or the thread calling {@code fireEvent})</i> - e.g a handler that sends data over a network connection or 
 * saves data to a file - and thus delay or prevent other time sensitive handler's from being executed.
 * </p>
 * 
 * <p>
 * Internally, this class uses an {@link ExecutorService} to manage execution of the handlers so the 
 * {@link #dispose()} method is used to shutdown the {@code ExecutorService} and free it's resources. Once 
 * {@code dispose()} has been called on a listener, the listener is {@link #clear() cleared} and cannot be used for 
 * further handling of events. After disposing, an {@link IllegalStateException} will be thrown if any of the following 
 * methods are called:
 * </p>
 * <ul>
 * 		<li>{@link #addHandler(Class, Handler)}</li>
 * 		<li>{@link #removeHandler(Class, Handler)}</li>
 * 		<li>{@link #fireEvent(Event)}</li>
 * 		<li>{@link #getHandlers(Class)}</li>
 * 		<li>{@link #getSupportedEventTypes()}</li>
 * </ul> 
 * The {@link #isDisposed()} method can be used to check if a listener has been disposed. Both {@code isDisposed()} 
 * and {@link #clear()} method are safe to call even after after the listener has been disposed.
 * 
 * <p>
 * <i><b>NOTE:</b> Instances of this class are thread safe and can be called from multiple threads.</i>
 * </p>
 * 
 * @author Kennedy Kori
 *
 * @since Oct 8, 2019, 2:46:35 PM
 */
public class ThreadedListener extends AbstractListener {
	
	private static final int DEFAULT_MAX_THREADS = 16;
	
	
	private final ExecutorService listenerService;
	private final boolean shutdownServiceOnDispose;
	private volatile boolean disposed;
	
	/**
	 * <p>
	 * Creates a new {@code ThreadedListener} with 16 maximum threads.
	 * </p>
	 * 
	 * <p>
	 * <i><b>Note:</b> All the threads created under the listener returned by this constructor will be daemon threads.</i>
	 * </p>
	 */
	public ThreadedListener() {
		this(DEFAULT_MAX_THREADS);
	}

	/**
	 * <p>
	 * Creates a new {@code ThreadedListener} with the given maximum threads. if 0 is given as {@code maxThreads}, then the number 
	 * of threads that this listener can use is not limited and every time a thread is needed but one isn't available for reuse, a new 
	 * thread will be created. On the other hand, if {@code maxThreads} is greater than 0 and the number of threads currently in use 
	 * are equal to {@code maxThreads}, then any handler will have to wait for a currently executing handler to finish <i>(i.e, for a 
	 * thread to be freed)</i> before it can be executed.
	 * </p>
	 * 
	 * <p>
	 * <i><b>Note:</b> All the threads created under the listener returned by this constructor will be daemon threads.</i>
	 * </p>
	 * 
	 * @param maxThreads the maximum threads that this listener should use or 0 for unlimited threads.
	 * 
	 * @throws IllegalArgumentException if {@code maxThreads} is negative.
	 */
	public ThreadedListener(int maxThreads) {
		this(maxThreads, createDefaultThreadFactory());
	}
	
	/**
	 * Creates a new {@code ThreadedListener} whose {@code ExecutorService} will have the given maximum threads and use the 
	 * given {@link ThreadFactory}. if 0 is given as {@code maxThreads}, then the number of threads that this listener can use 
	 * is not limited and every time a thread is needed but one isn't available for reuse, a new thread will be created. On the 
	 * other hand, if {@code maxThreads} is greater than 0 and the number of threads currently in use are equal to {@code maxThreads}, 
	 * then any handler will have to wait for a currently executing handler to finish <i>(i.e, for a thread to be freed)</i> before 
	 * it can be executed.
	 * 
	 * @param maxThreads the maximum threads that this listener should use or 0 for unlimited threads.
	 * @param factory the {@code ThreadFactory} that this listener will use when creating new threads.
	 * 
	 * @throws IllegalArgumentException if {@code maxThreads} is negative.
	 * @throws NullPointerException if factory is {@code null}.
	 */
	public ThreadedListener(int maxThreads, ThreadFactory factory) {
		this(createDefaultService(maxThreads, factory), true);
	}
	
	/**
	 * Creates a new {@code ThreadedListener} with the given {@code ExecutorService}. The {@code shutdownServiceOnDispose} 
	 * determines whether the executor is immediately shutdown after {@link #dispose()} is called. If 
	 * {@code shutdownServiceOnDispose} is {@code false}, the service will not be shutdown after dispose and it's up to the 
	 * caller to shutdown the service.
	 * 
	 * @param service the executor to be used by the new listener.
	 * @param shutdownServiceOnDispose if {@code true}, shutdown the executor service when this listener is disposed.
	 * 
	 * @throws NullPointerException if {@code service} is {@code null}.
	 */
	public ThreadedListener(ExecutorService service, boolean shutdownServiceOnDispose) {
		super(synchronizedMap(new LinkedHashMap<>()));
		this.listenerService = requireNonNull(service, "service cannot be null.");
		this.shutdownServiceOnDispose = shutdownServiceOnDispose;
	}

	/**
	* {@inheritDoc}
	* @throws IllegalStateException if this listener has already been disposed.
	* @throws NullPointerException if any of the arguments given is/are {@code null}.
	*/
	@Override
	public <T extends Event> void addHandler(Class<T> eventClass, Handler<T> handler) {
		checkState();
		super.addHandler(eventClass, handler);
	}

	/**
	* {@inheritDoc}
	* @throws IllegalStateException if this listener has already been disposed.
	* @throws NullPointerException if any of the arguments given is/are {@code null}.
	*/
	@Override
	public <T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler) {
		checkState();
		super.removeHandler(eventClass, handler);
	}

	/**
	* {@inheritDoc}
	* @throws IllegalStateException if this listener has already been disposed.
	* @throws NullPointerException if event is {@code null}.
	*/
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void fireEvent(T event) {
		checkState();
		requireNonNull(event, "event cannot be null.");
		if (registeredHandlers.containsKey(event.getClass())) {
			registeredHandlers.get(event.getClass()).forEach(
				handler -> listenerService.submit(() -> ((Handler<T>) handler).handle(event))
			);
		}
	}

	/**
	* {@inheritDoc}
	* @throws IllegalStateException if this listener has already been disposed.
	* @throws NullPointerException if {@code eventClass} is {@code null}.
	*/
	@Override
	public <T extends Event> Optional<List<Handler<T>>> getHandlers(Class<T> eventClass) {
		checkState();
		return super.getHandlers(eventClass);
	}

	/**
	* {@inheritDoc}
	* @throws IllegalStateException if this listener has already been disposed.
	*/
	@Override
	public Set<Class<? extends Event>> getSupportedEventTypes() {
		checkState();
		return super.getSupportedEventTypes();
	}
	
	/**
	 * Disposes this listener and {@link #clear() clears} it. After this method returns, this listener 
	 * will be unusable and calling most of the methods of this call will throw an {@link IllegalStateException}. 
	 * Calling this method after a listener has already been disposed has no effect.
	 */
	public void dispose() {
		// if the listener has already been disposed, there's no need to continue, return immediately
		if (disposed) return;
		synchronized (this) {
			// if shutdownOnDispose is true, shutdown the listenerService.
			if (shutdownServiceOnDispose) {
				listenerService.shutdown();
				// if the listenerService did not shutdown properly, forcefully shut it down.
				if (!listenerService.isTerminated()) listenerService.shutdownNow();
			}
			// Clear this listener
			clear();
			// set disposed to true
			disposed = true;
		}
	}
	
	/**
	 * Returns {@code true} if this listener is disposed, i.e, if {@link #dispose()} has already 
	 * been called on this listener.
	 * 
	 * @return {@code true} if this listener is disposed, {@code false} otherwise.
	 */
	public boolean isDisposed() {
		return disposed;
	}
	
	@Override
	protected List<Handler<? extends Event>> createHandlerList() {
		return synchronizedList(new ArrayList<>());
	}

	/**
	 * Checks that if if this listener is disposed and throws an {@link IllegalStateException} if it 
	 * has been disposed.
	 * 
	 * @throws IllegalStateException if this listener is disposed.
	 */
	private void checkState() {
		if (isDisposed())
			throw new IllegalStateException("This listener is already disposed.");
	}
	
	/**
	 * Creates a {@code ThreadFactory} that returns daemon threads 
	 */
	private static ThreadFactory createDefaultThreadFactory() {
		AtomicLong threadCount = new AtomicLong(1);
		ThreadFactory threadFactory = runnable -> {
			Thread thread = new Thread(runnable, "ThreadedListener Handler Thread:" + threadCount.getAndIncrement());
			thread.setDaemon(true);
			
			return thread;
		};
		
		return threadFactory;
	}
	
	/**
	 * Creates a new {@code ExecutorService} with the given maximum threads and {@code ThreadFactory}.
	 */
	private static ExecutorService createDefaultService(int maxThreads, ThreadFactory factory) {
		requireNonNegative(maxThreads, "maxThreads cannot be negative");
		requireNonNull(factory, "factory cannot be null.");
		
		return (maxThreads == 0)? Executors.newCachedThreadPool(factory)
				: (maxThreads == 1)? Executors.newSingleThreadExecutor(factory)
						: Executors.newFixedThreadPool(maxThreads, factory);
	}
}
