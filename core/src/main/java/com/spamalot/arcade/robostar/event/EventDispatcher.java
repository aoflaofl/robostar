package com.spamalot.arcade.robostar.event;

import java.util.function.Consumer;

/**
 * Simple event dispatcher interface using the observer pattern.
 */
public interface EventDispatcher {
  /** Register a listener for a specific event type. */
  <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener);

  /** Publish an event to all subscribed listeners. */
  void publish(GameEvent event);
}

