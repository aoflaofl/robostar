package com.spamalot.arcade.robostar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 * Main play screen delegating world updates and rendering.
 */
public class PlayScreen implements Screen {
    private final GameRoot game;
    private final WorldManager world;
    private final HudRenderer hud;

    public PlayScreen(GameRoot game) {
        this.game = game;
        this.world = new WorldManager(game);
        this.hud = new HudRenderer(game, world);
    }

    @Override
    public void render(float delta) {
        world.update(delta, game.input);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.render(game.shapes);
        hud.render(game.batch, game.shapes);

        if (world.lives < 0) {
            game.setScreen(new GameOverScreen(game, world.score));
        }
    }

    @Override
    public void resize(int width, int height) {
        world.resize(width, height);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
