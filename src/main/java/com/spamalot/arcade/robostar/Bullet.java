package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public Vector2 pos = new Vector2();
    public Vector2 vel = new Vector2();
    public float radius = 3f;
    public boolean alive = true;
    private float lifetime = 1.8f;

    public Bullet(Vector2 pos, Vector2 vel, float damage) {
        this.pos.set(pos);
        this.vel.set(vel);
    }

    public void update(float delta, float worldW, float worldH) {
        lifetime -= delta;
        if (lifetime <= 0) alive = false;
        pos.mulAdd(vel, delta);
        // wrap
        if (pos.x < 0) pos.x += worldW;
        else if (pos.x >= worldW) pos.x -= worldW;
        if (pos.y < 0) pos.y += worldH;
        else if (pos.y >= worldH) pos.y -= worldH;
    }

    public void render(ShapeRenderer s) {
        s.setColor(Color.YELLOW);
        s.circle(pos.x, pos.y, radius);
    }
}
