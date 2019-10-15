# SimpleEvents
This is a light weight events library written in java. It is composed of three main elements:

#### Events

An Event is simply an occurrence of interest during the normal execution of a program. All events implement the *Event* interface.

#### Handlers

A Handler is a callback that is executed once an Event occurs. Before a handler can be executed, it must be registered to a listener. All handlers must be implement the *Handler* interface.


#### Listeners

A Listener is an object used to manage handlers registered to it. A handler should be attached to or be an object that can trigger events that are of interest to other objects. All listeners must implement the *Listener* interface.

## Get Started

To use the library, add the following to your `build.gradle`:

```gradle
dependencies {
    api 'com.kori_47:events:1.0.0'
    // Other dependencies ...
}
```

Make sure you have jcenter in your repositories closure:

```gradle
repositories {
    jcenter()
    // Other repositories ...
}
```

Alternatively, you can compile and build the library using [gradle](https://gradle.org/). Just:

* Clone the project from [github](https://github.com/kennedykori/SimpleEvents)
* CD in the project root and run:

```bash
./gradlew build
```
* A jar will be generated in `build/libs`, add it to your classpath and you're good to go.

Thats it!!!

## Usage

You can start by using the existing concrete implementations of the *Event* interface to describe your events or create new event types by implementing the *Event* interface. E.g.

```java
import com.kori_47.events.SimpleEvent;


public class TimerChangedEvent {

	private final long previousValue;
	private final long newValue;
	
	public TimerChangedEvent(Object source, long previousValue, long newValue) {
		super(source);
		this.previousValue = previousValue;
		this.newValue = newValue;
	}

	public long getPreviousValue() {
		return previousValue;
	}
	
	public long getNewValue() {
		return newValue;
	}
} 

```

After describing your events, attach a listener to your events emitting object. E.g.

```java
import com.kori_47.events.Listener;
import com.kori_47.events.SimpleListener;


public class Timer {
	
	private final Listener listener = new SimpleListener();
	
	public Listener getListener() {
		return listener;
	}
	
	private void increamentTime() {
		long previousTime = // the time before the change
		// Increment time ...
		long newTime = // the current time
		
		// Create a new event to indicate that the time has changed
		TimerChangedEvent event = TimerChangedEvent(this, previousTime, newTime);
		// Fire the new event to notify handlers that the time has changed
		listener.fireEvent(event);
	}
	
	// Other properties and methods ...
}

```

Then describe handlers to be executed when events occur and register them to a listener that is attached to an object that emitts the aforementioned events. E.g.

```java
import com.kori_47.events.Handler;

public class TestTimer {
	
	public static void main(String[] args) {
		// Create the timer, i.e the event emitting objetcs
		Timer timer = new Timer();
		
		// Create a handler to print the new time on the console
		Handler<TimerChangedEvent> handler = event -> 
			System.out.println("Prev time : " + event.getPreviousTime() + ", New time : " + event.getNewTime() );
	
		// Register the new handler to a listener
		timer.getListerner().addHandler(TimerChangedEvent.class, handler);
		
		// Start the timer
		timer.start();
	}
}

```

Assuming the timer starts from zero, the code above should produce the following output:

```bash
Prev time : 0, New time : 1
Prev time : 1, New time : 2
Prev time : 2, New time : 3
Prev time : 3, New time : 4
Prev time : 4, New time : 5
Prev time : 5, New time : 6
// and so on

```

## API Reference

The documentation of the library can be generated using gradle, just:
* CD into the project root
* Run the following command

```bash
./gradlew javadoc
```

* Then CD into `build/docs/javadoc` and open `index.html` in your browser of choice.

## Test

To run the tests, CD into the project root and run:

```bash
./gradlew test
```

The test coverage report can be generated as follows:
* CD into the project root
* Run the following command

```bash
./gradlew jacocoTestReport
```

* The CD into `build/reports/jacoco/test/html` and open `index.html` in your browser

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)

Copyright (c) 2019 Kennedy Kori

