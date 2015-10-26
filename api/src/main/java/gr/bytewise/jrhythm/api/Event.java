package gr.bytewise.jrhythm.api;

import com.google.common.eventbus.EventBus;

public class Event {

	protected static EventBus eventBus = new EventBus();

	public static void post(Object obj) {
		eventBus.post(obj);
	}

	public static void register(Object obj) {
		eventBus.register(obj);
	}

	public static void unregister(Object obj) {
		eventBus.unregister(obj);
	}

	public static void refresh() {
		eventBus = new EventBus();
	}

}
