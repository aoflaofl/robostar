package com.spamalot.arcade.robostar.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import com.spamalot.arcade.robostar.GameRoot;
import com.spamalot.arcade.robostar.event.BossBuildProgressEvent;
import com.spamalot.arcade.robostar.event.EventDispatcher;
import com.spamalot.arcade.robostar.event.SimpleEventDispatcher;
import com.spamalot.arcade.robostar.world.WorldManager;

/**
 * Main play screen delegating world updates and rendering.
 */
public class PlayScreen implements Screen {
  private final GameRoot game;
  private final WorldManager world;
  private final HudRenderer hud;
  private final EventDispatcher dispatcher;

  public PlayScreen(GameRoot game) {
    this.game = game;
    this.dispatcher = new SimpleEventDispatcher();
    this.world = new WorldManager(game, dispatcher);
    this.hud = new HudRenderer(game, world);
    dispatcher.subscribe(BossBuildProgressEvent.class, e -> world.addBossBuildProgress(e.amount()));
  }

  @Override
  public void render(float delta) {
    world.update(delta, game.input);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    world.render(game.batch);
    hud.render(game.batch, game.shapes);

    if (world.lives < 0) {
      game.setScreen(new GameOverScreen(game, world.score));
    }
  }

  @Override
  public void resize(int width, int height) {
    world.resize(width, height);
  }

  @Override
  public void show() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
  }
}
