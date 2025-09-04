package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.spamalot.arcade.robostar.world.WorldUtils;

public class Boss {
  private Vector2 pos = new Vector2();
  private Vector2 vel = new Vector2();
  private float radius = 42f;
  private float hp = 220f;
  private boolean alive = true;

  public Boss(Vector2 start) {
    pos.set(start);
  }

  public void update(float delta, Player player, float worldW, float worldH) {
    // simple chase with mild acceleration
    Vector2 dir = new Vector2(player.getPos()).sub(pos).nor();
    vel.mulAdd(dir, 60f * delta);
    vel.clamp(0, 120f);
    pos.mulAdd(vel, delta);

    WorldUtils.wrap(pos, worldW, worldH);
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

  public float getHp() {
    return hp;
  }

  public void setHp(float hp) {
    this.hp = hp;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }
}
