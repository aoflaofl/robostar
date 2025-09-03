package com.spamalot.arcade.robostar;

/**
 * Extremely tiny "event bus" for the prototype to communicate simple scalars
 * across entities without heavy coupling. Reset per-frame by PlayScreen.
 */
public class GameBus {
  public static float bossBuildAdd = 0f;
}
