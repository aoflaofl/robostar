package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
        if (time >= dur) alive = false;
    }

    public void render(ShapeRenderer s) {
        float t = Math.min(1f, time/dur);
        float r = 30 + 120 * t;
        s.setColor(new Color(1f, 0.5f*(1f-t), 0, 0.6f*(1f-t)+0.2f));
        s.circle(pos.x, pos.y, r, 24);
    }
}
