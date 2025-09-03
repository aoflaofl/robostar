package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
  public enum Type {
    HUNTER, GATHERER, CONVERTER, ZOMBIE
  }

  private Type type;
  private Vector2 pos = new Vector2();
  private Vector2 vel = new Vector2();
  private float radius = 10f;
  private boolean alive = true;
  private float carryCrystal = 0f; // for gatherers

  public Enemy(Type type, Vector2 start) {
    this.type = type;
    this.pos.set(start);
  }

  public static Enemy hunter(Vector2 at) {
    Enemy e = new Enemy(Type.HUNTER, at);
    e.radius = 10f;
    return e;
  }

  public static Enemy gatherer(Vector2 at) {
    Enemy e = new Enemy(Type.GATHERER, at);
    e.radius = 9f;
    return e;
  }

  public static Enemy converter(Vector2 at) {
    Enemy e = new Enemy(Type.CONVERTER, at);
    e.radius = 11f;
    return e;
  }

  public static Enemy zombie(Vector2 at) {
    Enemy e = new Enemy(Type.ZOMBIE, at);
    e.radius = 9f;
    return e;
  }

  public void update(float delta, Player player, Array<Pickup> crystals, Array<Pickup> humans, Boss boss, float worldW,
      float worldH) {
    switch (type) {
      case HUNTER:
        moveTowards(delta, player.getPos(), 140f);
        break;
      case GATHERER:
        // move to nearest crystal; if none, wander; once "carried", move toward boss
        // (or center until boss)
        Pickup nearest = nearestPickup(crystals, pos);
        if (carryCrystal <= 0 && nearest != null) {
          moveTowards(delta, nearest.getPos(), 110f);
          if (pos.dst2(nearest.getPos()) < (radius + nearest.getRadius()) * (radius + nearest.getRadius())) {
            carryCrystal = 1f;
            nearest.setAlive(false);
            crystals.removeValue(nearest, true);
          }
        } else {
          Vector2 target = boss != null ? boss.getPos() : new Vector2(worldW * 0.5f, worldH * 0.5f);
          moveTowards(delta, target, 120f);
          if (boss != null && pos.dst2(boss.getPos()) < (radius + boss.getRadius()) * (radius + boss.getRadius())) {
            // delivered
            carryCrystal = 0f;
            bossBuildProgressInc(0.08f); // global-ish: see Hack note
          }
        }
        break;
      case CONVERTER:
        Pickup human = nearestPickup(humans, pos);
        if (human != null) {
          moveTowards(delta, human.getPos(), 130f);
          if (pos.dst2(human.getPos()) < (radius + human.getRadius()) * (radius + human.getRadius())) {
            // convert to zombie enemy
            humans.removeValue(human, true);
            // spawn zombie
            type = Type.ZOMBIE;
          }
        } else {
          wander(delta, 80f);
        }
        break;
      case ZOMBIE:
        moveTowards(delta, player.getPos(), 150f);
        break;
    }

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

  private void moveTowards(float delta, Vector2 target, float speed) {
    vel.set(target).sub(pos).nor().scl(speed);
    pos.mulAdd(vel, delta);
  }

  private void wander(float delta, float speed) {
    vel.add(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor().scl(speed);
    pos.mulAdd(vel, delta);
    vel.scl(0.92f);
  }

  private Pickup nearestPickup(Array<Pickup> list, Vector2 from) {
    Pickup best = null;
    float bestD2 = Float.MAX_VALUE;
    for (Pickup p : list) {
      float d2 = p.getPos().dst2(from);
      if (d2 < bestD2) {
        bestD2 = d2;
        best = p;
      }
    }
    return best;
  }

  // --- Hack: communicate progress to the active PlayScreen bossBuildProgress ---
  private void bossBuildProgressInc(float amt) {
    // This is a placeholder hack, but we can't reference PlayScreen directly.
    // In a more structured ECS, you'd use events. For the prototype,
    // we'll simply update a global in GameBus. See GameBus class.
    GameBus.bossBuildAdd += amt;
  }

  public void render(ShapeRenderer s) {
    switch (type) {
      case HUNTER:
        s.setColor(Color.FIREBRICK);
        break;
      case GATHERER:
        s.setColor(Color.CORAL);
        break;
      case CONVERTER:
        s.setColor(Color.PURPLE);
        break;
      case ZOMBIE:
        s.setColor(Color.GREEN);
        break;
    }
    s.circle(pos.x, pos.y, radius);
    if (carryCrystal > 0) {
      s.setColor(Color.CYAN);
      s.circle(pos.x, pos.y + radius + 2, 2f);
    }
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

  public Type getType() {
    return type;
  }
}
