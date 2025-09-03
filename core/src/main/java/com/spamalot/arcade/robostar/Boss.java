package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

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

  public void render(ShapeRenderer s) {
    s.setColor(Color.SCARLET);
    s.circle(pos.x, pos.y, radius, 24);
    // "eye"
    s.setColor(Color.BLACK);
    s.circle(pos.x + MathUtils.cosDeg((System.currentTimeMillis() / 10) % 360) * 10f, pos.y, 8f);
    // health ring
    s.setColor(Color.PINK);
    float ring = Math.max(0, hp) / 220f;
    s.circle(pos.x, pos.y, radius + 4f * ring, 24);
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
