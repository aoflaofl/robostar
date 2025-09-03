package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.spamalot.arcade.robostar.world.WorldUtils;

public class Player {
  private Vector2 pos = new Vector2();
  private Vector2 vel = new Vector2();
  private float radius = 12f;

  private float speed = 240f;
  private float shotCooldown = 0.12f; // seconds between shots
  private float shotTimer = 0f;

  private float invuln = 1.5f; // after spawn

  // power-ups
  private float speedBuff = 0f;
  private float fireBuff = 0f;
  private float shield = 0f;

  public Player(Vector2 start) {
    pos.set(start);
  }

  public void respawn(Vector2 at) {
    pos.set(at);
    vel.setZero();
    invuln = 2.0f;
  }

  public void update(float delta, Vector2 moveInput, float worldW, float worldH) {
    // power timers
    if (speedBuff > 0) {
      speedBuff -= delta;
    }
    if (fireBuff > 0) {
      fireBuff -= delta;
    }
    if (shield > 0) {
      shield -= delta;
    }
    if (invuln > 0) {
      invuln -= delta;
    }

    float spd = speed * (speedBuff > 0 ? 1.5f : 1f);
    vel.set(moveInput).nor().scl(spd);
    pos.add(vel.x * delta, vel.y * delta);

    WorldUtils.wrap(pos, worldW, worldH);

    if (shotTimer > 0) {
      shotTimer -= delta;
    }
  }

  public void tryShoot(float delta, Vector2 aim, Array<Bullet> out) {
    if (shotTimer > 0) {
      return;
    }
    float rate = shotCooldown * (fireBuff > 0 ? 0.55f : 1f);
    shotTimer = rate;
    Vector2 dir = new Vector2(aim).nor();
    Vector2 bpos = new Vector2(pos).add(dir.scl(radius + 4f));
    out.add(new Bullet(bpos, new Vector2(dir).scl(520f), 5f));
  }

  public void powerSpeed(float dur) {
    speedBuff = Math.max(speedBuff, dur);
  }

  public void powerFireRate(float dur) {
    fireBuff = Math.max(fireBuff, dur);
  }

  public void powerShield(float dur) {
    shield = Math.max(shield, dur);
  }

  public boolean hasShield() {
    return shield > 0;
  }

  public void render(ShapeRenderer s) {
    // body
    s.setColor(hasShield() ? Color.CYAN : (invuln > 0 ? Color.LIGHT_GRAY : Color.WHITE));
    // draw a simple triangle ship
    float r = radius;
    s.triangle(pos.x + r, pos.y, pos.x - r, pos.y - r * 0.8f, pos.x - r, pos.y + r * 0.8f);
    // simple thruster rectangle to suggest motion
    s.setColor(Color.ORANGE);
    s.rect(pos.x - r - 4, pos.y - 2, 6, 4);
  }

  public float getInvuln() {
    return invuln;
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
}
