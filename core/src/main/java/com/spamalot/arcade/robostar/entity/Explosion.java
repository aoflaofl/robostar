package com.spamalot.arcade.robostar.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
  public Vector2 pos = new Vector2();
  public float time = 0f;
  public float dur;
  public boolean alive = true;

  public Explosion(Vector2 pos, float dur) {
    this.pos.set(pos);
    this.dur = dur;
  }

  public void update(float delta) {
    time += delta;
    if (time >= dur) {
      alive = false;
    }
  }

  public void render(SpriteBatch batch, Texture tex) {
    if (tex == null) {
      return;
    }
    float t = Math.min(1f, time / dur);
    float r = 30 + 120 * t;
    batch.setColor(new Color(1f, 0.5f * (1f - t), 0, 0.6f * (1f - t) + 0.2f));
    batch.draw(tex, pos.x - r, pos.y - r, r * 2f, r * 2f);
    batch.setColor(Color.WHITE);
  }
}
