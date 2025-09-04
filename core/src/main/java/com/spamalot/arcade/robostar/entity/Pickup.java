package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.spamalot.arcade.robostar.asset.AssetRepository;

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

  public void render(SpriteBatch batch, AssetRepository assets) {
    Texture tex = kind == Kind.CRYSTAL ? assets.getCrystal() : assets.getHuman();
    if (tex == null) {
      return;
    }
    float size = radius * 2f;
    batch.draw(tex, pos.x - radius, pos.y - radius, size, size);
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
