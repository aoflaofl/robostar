package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
  private Vector2 pos = new Vector2();
  private Vector2 vel = new Vector2();
  private float radius = 3f;
  private boolean alive = true;
  private float lifetime = 1.8f;

  public Bullet(Vector2 pos, Vector2 vel, float damage) {
    this.pos.set(pos);
    this.vel.set(vel);
  }

  public void update(float delta, float worldW, float worldH) {
    lifetime -= delta;
    if (lifetime <= 0) {
      alive = false;
    }
    pos.mulAdd(vel, delta);
    // wrap
    if (pos.x < 0) {
      pos.x += worldW;
    } else if (pos.x >= worldW) {
      pos.x -= worldW;
    }
    if (pos.y < 0) {
      pos.y += worldH;
    } else if (pos.y >= worldH) {
      pos.y -= worldH;
    }
  }

  public void render(SpriteBatch batch, Texture tex) {
    if (tex == null) {
      return;
    }
    float size = radius * 2f;
    batch.draw(tex, pos.x - radius, pos.y - radius, size, size);
  }

  public Vector2 getPos() {
    return pos;
  }

  public Vector2 getVel() {
    return vel;
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
