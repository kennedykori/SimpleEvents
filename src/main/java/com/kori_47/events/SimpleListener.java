/**
 * 
 */
package com.kori_47.events;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is a simple implementation of the {@link Listener} interface. For most use cases where a 
 * listener is needed, this class should be sufficient.
 * 
 * @author Kennedy Kori
 *
 * @since Sep 25, 2019, 3:04:07 AM
 */
public class SimpleListener extends AbstractListener {
	
	/**
	 * Creates a new {@code SimpleListener}.
	 */
	public SimpleListener() {
		super(new LinkedHashMap<>());
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected List<Handler<? extends Event>> createHandlerList() {
		return new ArrayList<>();
	}
}
