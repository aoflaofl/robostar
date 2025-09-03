package com.spamalot.arcade.robostar;

import com.badlogic.gdx.math.Vector2;

/** Utility functions for world wrapping and related operations. */
public final class WorldUtils {
  private WorldUtils() {
    // Utility class
  }

  /**
   * Wrap a position around the world bounds so that objects exiting on one side
   * re-enter on the opposite side.
   *
   * @param pos   position to wrap
   * @param width world width
   * @param height world height
   * @return the wrapped position (same instance as provided)
   */
  public static Vector2 wrap(Vector2 pos, float width, float height) {
    if (pos.x < 0) {
      pos.x += width;
    } else if (pos.x >= width) {
      pos.x -= width;
    }
    if (pos.y < 0) {
      pos.y += height;
    } else if (pos.y >= height) {
      pos.y -= height;
    }
    return pos;
  }
}
