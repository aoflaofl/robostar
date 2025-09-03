package com.spamalot.arcade.robostar;

/**
 * Event published when the boss build progresses.
 */
public record BossBuildProgressEvent(float amount) implements GameEvent {
}

