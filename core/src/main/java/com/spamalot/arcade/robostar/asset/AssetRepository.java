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

  /** All textures that need disposing. */
  private final Array<Texture> managedTextures = new Array<>();

  /** Load all assets. */
  public void load() {
    loadDirectional("player/player", playerFrames);
    loadDirectional("enemy/enemy", enemyFrames);
  }

  private void loadDirectional(String prefix, Map<Direction, TextureRegion[]> out) {
    for (Direction dir : Direction.values()) {
      String path = prefix + "_" + dir.name().toLowerCase() + ".png";
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

  /** Dispose of all loaded textures. */
  public void dispose() {
    for (Texture t : managedTextures) {
      t.dispose();
    }
    managedTextures.clear();
  }
}
