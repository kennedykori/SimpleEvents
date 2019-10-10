package com.kori_47.events;

import java.util.stream.Stream;

public class SimpleListenerTest implements ListenerTest<SimpleListener> {

	@Override
	public void cleanUp(SimpleListener listener) {
		listener.clear();
	}

	@Override
	public Stream<SimpleListener> listenerProvider() {
		return Stream.of(new SimpleListener());
	}
}
