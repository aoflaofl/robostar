package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bomb {
    public Vector2 pos = new Vector2();
    public Vector2 vel = new Vector2();
    public boolean exploded = false;
    private float fuse = 0.8f;
    private float radius = 6f;

    public Bomb(Vector2 pos, Vector2 vel) {
        this.pos.set(pos);
        this.vel.set(vel);
    }

    public void update(float delta, float worldW, float worldH) {
        if (exploded) return;
        fuse -= delta;
        pos.mulAdd(vel, delta);
        vel.scl(0.995f); // decelerate slightly
        if (fuse <= 0) exploded = true;

        // wrap
        if (pos.x < 0) pos.x += worldW;
        else if (pos.x >= worldW) pos.x -= worldW;
        if (pos.y < 0) pos.y += worldH;
        else if (pos.y >= worldH) pos.y -= worldH;
    }

    public void render(ShapeRenderer s) {
        if (!exploded) {
            s.setColor(Color.RED);
            s.circle(pos.x, pos.y, radius);
        }
    }
}
