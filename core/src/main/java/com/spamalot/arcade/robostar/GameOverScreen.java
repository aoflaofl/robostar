package com.spamalot.arcade.robostar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen implements Screen {
  private final GameRoot game;
  private final Camera camera;
  private final Viewport viewport;
  private final int score;

  public GameOverScreen(GameRoot game, int score) {
    this.game = game;
    this.score = score;
    this.camera = new OrthographicCamera(GameRoot.VIEW_W, GameRoot.VIEW_H);
    this.viewport = new FitViewport(GameRoot.VIEW_W, GameRoot.VIEW_H, camera);
    camera.position.set(GameRoot.VIEW_W / 2f, GameRoot.VIEW_H / 2f, 0);
    game.setHighScore(score);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.05f, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    game.input.update();

    game.batch.begin();
    game.font.setColor(Color.ORANGE);
    game.font.draw(game.batch, "GAME OVER", 40, GameRoot.VIEW_H - 40);
    game.font.setColor(Color.WHITE);
    game.font.draw(game.batch, "Score: " + score, 40, GameRoot.VIEW_H - 80);
    game.font.draw(game.batch, "High Score: " + game.getHighScore(), 40, GameRoot.VIEW_H - 120);
    game.font.draw(game.batch, "Press START to return to Menu", 40, GameRoot.VIEW_H / 2f);
    game.batch.end();

    if (game.input.isStartPressed()) {
      game.setScreen(new MenuScreen(game));
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
