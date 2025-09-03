package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Pickup {
  public enum Kind {
    CRYSTAL, HUMAN
  }

  private Kind kind;
  private Vector2 pos = new Vector2();
  private float radius = 7f;
  private boolean alive = true;

  public static Pickup crystal(Vector2 at) {
    Pickup p = new Pickup();
    p.kind = Kind.CRYSTAL;
    p.pos.set(at);
    p.radius = 5f;
    return p;
  }

  public static Pickup human(Vector2 at) {
    Pickup p = new Pickup();
    p.kind = Kind.HUMAN;
    p.pos.set(at);
    p.radius = 7f;
    return p;
  }

  public void render(ShapeRenderer s) {
    switch (kind) {
      case CRYSTAL:
        s.setColor(Color.CYAN);
        // diamond shape
        s.triangle(pos.x, pos.y + radius, pos.x - radius, pos.y, pos.x + radius, pos.y);
        s.triangle(pos.x, pos.y - radius, pos.x - radius, pos.y, pos.x + radius, pos.y);
        break;
      case HUMAN:
        s.setColor(Color.SKY);
        s.circle(pos.x, pos.y, radius);
        s.setColor(Color.WHITE);
        s.rect(pos.x - 1, pos.y - radius - 2, 2, 4); // simple "plus" accent
        break;
    }
  }

  public Kind getKind() {
    return kind;
  }

  public Vector2 getPos() {
    return pos;
  }

  public float getRadius() {
    return radius;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }
}
