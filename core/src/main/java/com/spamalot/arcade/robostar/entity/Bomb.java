package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import com.spamalot.arcade.robostar.world.WorldUtils;

public class Bomb {
  private Vector2 pos = new Vector2();
  private Vector2 vel = new Vector2();
  private boolean exploded = false;
  private float fuse = 0.8f;
  private float radius = 6f;

  public Bomb(Vector2 pos, Vector2 vel) {
    this.pos.set(pos);
    this.vel.set(vel);
  }

  public void update(float delta, float worldW, float worldH) {
    if (exploded) {
      return;
    }
    fuse -= delta;
    pos.mulAdd(vel, delta);
    vel.scl(0.995f); // decelerate slightly
    if (fuse <= 0) {
      exploded = true;
    }

    WorldUtils.wrap(pos, worldW, worldH);
  }

  public void render(ShapeRenderer s) {
    if (!exploded) {
      s.setColor(Color.RED);
      s.circle(pos.x, pos.y, radius);
    }
  }

  public Vector2 getPos() {
    return pos;
  }

  public Vector2 getVel() {
    return vel;
  }

  public boolean isExploded() {
    return exploded;
  }

  public float getRadius() {
    return radius;
  }
}
