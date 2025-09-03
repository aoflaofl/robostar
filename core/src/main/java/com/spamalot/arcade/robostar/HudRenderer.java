package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders HUD information such as score and boss build bar.
 */
class HudRenderer {
  private final GameRoot game;
  private final WorldManager world;
  private final GlyphLayout layout = new GlyphLayout();

  HudRenderer(GameRoot game, WorldManager world) {
    this.game = game;
    this.world = world;
  }

  void render(SpriteBatch batch, ShapeRenderer shapes) {
    // HUD text
    batch.setProjectionMatrix(world.camera.combined);
    batch.begin();
    String info = String.format("Score %d   Lives %d   Wave %d   Crystals %d   Bombs %d", world.score, world.lives,
        world.wave, world.crystalsCollected, world.bombsAvailable);
    layout.setText(game.font, info);
    game.font.draw(batch, info, world.camera.position.x - GameRoot.VIEW_W / 2f + 10,
        world.camera.position.y + GameRoot.VIEW_H / 2f - 10);
    batch.end();

    // boss build bar
    float barW = 260f;
    float barH = 10f;
    float px = world.camera.position.x + GameRoot.VIEW_W / 2f - barW - 20;
    float py = world.camera.position.y + GameRoot.VIEW_H / 2f - 24;
    shapes.setProjectionMatrix(world.camera.combined);
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    shapes.setColor(Color.DARK_GRAY);
    shapes.rect(px, py, barW, barH);
    shapes.setColor(Color.RED);
    shapes.rect(px, py, barW * Math.min(1f, world.bossBuildProgress), barH);
    shapes.end();
    batch.begin();
    game.font.draw(batch, "Boss Build", px, py - 4);
    batch.end();
  }
}
