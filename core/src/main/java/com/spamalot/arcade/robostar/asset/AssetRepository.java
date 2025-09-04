package com.spamalot.arcade.robostar.asset;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.spamalot.arcade.robostar.Direction;

/**
 * Loads and stores image assets for game entities.
 *
 * <p>The repository loads sprite sheets for each {@link Direction}. Each
 * sheet is assumed to contain square frames laid out horizontally.</p>
 */
public class AssetRepository {
  private final Map<Direction, TextureRegion[]> playerFrames = new EnumMap<>(Direction.class);
  private final Map<Direction, TextureRegion[]> enemyFrames = new EnumMap<>(Direction.class);

  /** Additional entity textures. */
  private Texture bulletTex;
  private Texture bombTex;
  private Texture crystalTex;
  private Texture humanTex;
  private Texture explosionTex;
  private Texture bossTex;

  /** All textures that need disposing. */
  private final Array<Texture> managedTextures = new Array<>();

  /** Load all assets. */
  public void load() {
    loadDirectional("sprites/player", playerFrames);
    loadDirectional("sprites/enemy", enemyFrames);

    bulletTex = loadTexture("sprites/bullet/bullet.png");
    bombTex = loadTexture("sprites/bomb/bomb.png");
    crystalTex = loadTexture("sprites/pickup/crystal/crystal.png");
    humanTex = loadTexture("sprites/pickup/human/human.png");
    explosionTex = loadTexture("sprites/explosion/1.png");
    bossTex = loadTexture("sprites/boss/down.png");
  }

  private void loadDirectional(String prefix, Map<Direction, TextureRegion[]> out) {
    for (Direction dir : Direction.values()) {
      String path = prefix + "/" + dir.name().toLowerCase() + ".png";
      FileHandle handle = Gdx.files.internal(path);
      if (!handle.exists()) {
        continue; // allow missing assets during development
      }
      Texture tex = new Texture(handle);
      managedTextures.add(tex);
      int frameSize = tex.getHeight();
      int count = Math.max(1, tex.getWidth() / frameSize);
      TextureRegion[] frames = new TextureRegion[count];
      for (int i = 0; i < count; i++) {
        frames[i] = new TextureRegion(tex, i * frameSize, 0, frameSize, frameSize);
      }
      out.put(dir, frames);
    }
  }

  private Texture loadTexture(String path) {
    FileHandle handle = Gdx.files.internal(path);
    if (!handle.exists()) {
      return null;
    }
    Texture tex = new Texture(handle);
    managedTextures.add(tex);
    return tex;
  }

  private TextureRegion getFrame(Map<Direction, TextureRegion[]> map, Direction dir, int frame) {
    TextureRegion[] arr = map.get(dir);
    if (arr == null || arr.length == 0) {
      return null;
    }
    return arr[frame % arr.length];
  }

  /** Retrieve a player animation frame. */
  public TextureRegion getPlayerFrame(Direction dir, int frame) {
    return getFrame(playerFrames, dir, frame);
  }

  /** Retrieve an enemy animation frame. */
  public TextureRegion getEnemyFrame(Direction dir, int frame) {
    return getFrame(enemyFrames, dir, frame);
  }

  public Texture getBullet() {
    return bulletTex;
  }

  public Texture getBomb() {
    return bombTex;
  }

  public Texture getCrystal() {
    return crystalTex;
  }

  public Texture getHuman() {
    return humanTex;
  }

  public Texture getExplosion() {
    return explosionTex;
  }

  public Texture getBoss() {
    return bossTex;
  }

  /** Dispose of all loaded textures. */
  public void dispose() {
    for (Texture t : managedTextures) {
      t.dispose();
    }
    managedTextures.clear();
  }
}
