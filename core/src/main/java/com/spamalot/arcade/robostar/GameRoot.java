package com.spamalot.arcade.robostar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameRoot extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;
    public Preferences prefs;
    public InputController input;

    public static final int VIEW_W = 1280;
    public static final int VIEW_H = 720;
    public static final int WORLD_W = VIEW_W * 3;
    public static final int WORLD_H = VIEW_H * 3;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont(); // default font
        prefs = Gdx.app.getPreferences("hybrid-arcade-shooter");
        input = new InputController();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapes.dispose();
        font.dispose();
        input.dispose();
    }

    public int getHighScore() {
        return prefs.getInteger("highscore", 0);
    }

    public void setHighScore(int score) {
        if (score > getHighScore()) {
            prefs.putInteger("highscore", score);
            prefs.flush();
        }
    }
}
