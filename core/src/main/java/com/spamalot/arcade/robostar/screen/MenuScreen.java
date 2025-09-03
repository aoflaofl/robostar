package com.spamalot.arcade.robostar.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.spamalot.arcade.robostar.GameRoot;

public class MenuScreen implements Screen {
  private final GameRoot game;
  private final Camera camera;
  private final Viewport viewport;
  private float blink = 0f;

  public MenuScreen(GameRoot game) {
    this.game = game;
    this.camera = new OrthographicCamera(GameRoot.VIEW_W, GameRoot.VIEW_H);
    this.viewport = new FitViewport(GameRoot.VIEW_W, GameRoot.VIEW_H, camera);
    camera.position.set(GameRoot.VIEW_W / 2f, GameRoot.VIEW_H / 2f, 0);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0.05f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.batch.setProjectionMatrix(camera.combined);
    game.shapes.setProjectionMatrix(camera.combined);

    game.input.update();

    game.batch.begin();
    game.font.draw(game.batch, "HYBRID ARCADE SHOOTER", 40, GameRoot.VIEW_H - 40);
    game.font.draw(game.batch, "High Score: " + game.getHighScore(), 40, GameRoot.VIEW_H - 80);
    if (!game.input.hasController()) {
      game.font.setColor(Color.RED);
      game.font.draw(game.batch, "Connect a dual-stick controller to start.", 40, GameRoot.VIEW_H / 2f);
      game.font.setColor(Color.WHITE);
    } else {
      blink += delta;
      if ((int) (blink * 2) % 2 == 0) {
        game.font.draw(game.batch, "Press START to Play", 40, GameRoot.VIEW_H / 2f);
      }
      game.font.draw(game.batch, "Controller: " + game.input.controllerName(), 40, GameRoot.VIEW_H / 2f - 40);
      game.font.draw(game.batch, "Left Stick: Move   Right Stick: Aim/Fire   R1: Bomb", 40, GameRoot.VIEW_H / 2f - 80);
    }
    game.batch.end();

    if (game.input.hasController() && game.input.isStartPressed()) {
      game.setScreen(new PlayScreen(game));
    }
  }

  @Override
  public void show() {
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
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
