package com.spamalot.arcade.robostar.asset;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Central storage for game textures and animation frames.
 */
public class AssetRepository implements Disposable {
  private static final int DEFAULT_ANIM_FRAMES = 3;

  private final EnumMap<Direction, TextureRegion[]> playerFrames = new EnumMap<>(Direction.class);
  private Texture playerTexture;

  private final EnumMap<Direction, TextureRegion[]> enemyFrames = new EnumMap<>(Direction.class);
  private Texture enemyTexture;

  private final EnumMap<Direction, TextureRegion[]> bossFrames = new EnumMap<>(Direction.class);
  private Texture bossTexture;

  private Texture bulletTexture;
  private TextureRegion bulletFrame;

  private Texture bombTexture;
  private TextureRegion[] bombFrames;

  private Texture pickupTexture;
  private TextureRegion[] pickupFrames;

  private Texture explosionTexture;
  private TextureRegion[] explosionFrames;

  public AssetRepository() {
    loadPlayer();
    loadEnemy();
    loadBoss();
    loadBullet();
    loadBomb();
    loadPickup();
    loadExplosion();
  }

  private void loadPlayer() {
    playerTexture = new Texture(Gdx.files.internal("player.png"));
    int frameW = playerTexture.getWidth() / DEFAULT_ANIM_FRAMES;
    int frameH = playerTexture.getHeight() / Direction.values().length;
    TextureRegion[][] split = TextureRegion.split(playerTexture, frameW, frameH);
    for (Direction dir : Direction.values()) {
      playerFrames.put(dir, split[dir.ordinal()]);
    }
  }

  private void loadEnemy() {
    enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
    int frameW = enemyTexture.getWidth() / DEFAULT_ANIM_FRAMES;
    int frameH = enemyTexture.getHeight() / Direction.values().length;
    TextureRegion[][] split = TextureRegion.split(enemyTexture, frameW, frameH);
    for (Direction dir : Direction.values()) {
      enemyFrames.put(dir, split[dir.ordinal()]);
    }
  }

  private void loadBoss() {
    bossTexture = new Texture(Gdx.files.internal("boss.png"));
    int frameW = bossTexture.getWidth() / DEFAULT_ANIM_FRAMES;
    int frameH = bossTexture.getHeight() / Direction.values().length;
    TextureRegion[][] split = TextureRegion.split(bossTexture, frameW, frameH);
    for (Direction dir : Direction.values()) {
      bossFrames.put(dir, split[dir.ordinal()]);
    }
  }

  private void loadBullet() {
    bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
    bulletFrame = new TextureRegion(bulletTexture);
  }

  private void loadBomb() {
    bombTexture = new Texture(Gdx.files.internal("bomb.png"));
    int frameW = bombTexture.getWidth() / DEFAULT_ANIM_FRAMES;
    int frameH = bombTexture.getHeight();
    TextureRegion[][] split = TextureRegion.split(bombTexture, frameW, frameH);
    bombFrames = split[0];
  }

  private void loadPickup() {
    pickupTexture = new Texture(Gdx.files.internal("pickup.png"));
    int frameW = pickupTexture.getWidth();
    int frameH = pickupTexture.getHeight();
    TextureRegion[][] split = TextureRegion.split(pickupTexture, frameW, frameH);
    pickupFrames = split[0];
  }

  private void loadExplosion() {
    explosionTexture = new Texture(Gdx.files.internal("explosion.png"));
    int frameW = explosionTexture.getWidth() / DEFAULT_ANIM_FRAMES;
    int frameH = explosionTexture.getHeight();
    TextureRegion[][] split = TextureRegion.split(explosionTexture, frameW, frameH);
    explosionFrames = split[0];
  }

  /**
   * Get a player animation frame.
   */
  public TextureRegion getPlayerFrame(Direction dir, int frame) {
    TextureRegion[] frames = playerFrames.get(dir);
    return frames[frame % frames.length];
  }

  /**
   * Get an enemy animation frame.
   */
  public TextureRegion getEnemyFrame(Direction dir, int frame) {
    TextureRegion[] frames = enemyFrames.get(dir);
    return frames[frame % frames.length];
  }

  /**
   * Get a boss animation frame.
   */
  public TextureRegion getBossFrame(Direction dir, int frame) {
    TextureRegion[] frames = bossFrames.get(dir);
    return frames[frame % frames.length];
  }

  /**
   * Get the bullet texture.
   */
  public TextureRegion getBulletFrame() {
    return bulletFrame;
  }

  /**
   * Get a bomb animation frame.
   */
  public TextureRegion getBombFrame(int frame) {
    return bombFrames[frame % bombFrames.length];
  }

  /**
   * Get a pickup animation frame.
   */
  public TextureRegion getPickupFrame(int frame) {
    return pickupFrames[frame % pickupFrames.length];
  }

  /**
   * Get an explosion animation frame.
   */
  public TextureRegion getExplosionFrame(int frame) {
    return explosionFrames[frame % explosionFrames.length];
  }

  @Override
  public void dispose() {
    if (playerTexture != null) {
      playerTexture.dispose();
    }
    if (enemyTexture != null) {
      enemyTexture.dispose();
    }
    if (bossTexture != null) {
      bossTexture.dispose();
    }
    if (bulletTexture != null) {
      bulletTexture.dispose();
    }
    if (bombTexture != null) {
      bombTexture.dispose();
    }
    if (pickupTexture != null) {
      pickupTexture.dispose();
    }
    if (explosionTexture != null) {
      explosionTexture.dispose();
    }
  }
}
