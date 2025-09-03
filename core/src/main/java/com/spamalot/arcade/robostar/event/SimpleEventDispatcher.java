package com.spamalot.arcade.robostar.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Basic in-memory implementation of the {@link EventDispatcher}.
 */
public class SimpleEventDispatcher implements EventDispatcher {
  private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

  @Override
  public synchronized <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
    listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
  }

  @Override
  public synchronized void publish(GameEvent event) {
    List<Consumer<?>> eventListeners = listeners.get(event.getClass());
    if (eventListeners == null) {
      return;
    }
    for (Consumer<?> raw : eventListeners) {
      @SuppressWarnings("unchecked")
      Consumer<GameEvent> listener = (Consumer<GameEvent>) raw;
      listener.accept(event);
    }
  }
}

