package com.spamalot.arcade.robostar;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Maintains world state and updates entities.
 */
class WorldManager {
  final GameRoot game;
  final Camera camera;
  final Viewport viewport;

  final Player player;
  final Array<Bullet> bullets = new Array<>();
  final Array<Bomb> bombs = new Array<>();
  final Array<Enemy> enemies = new Array<>();
  final Array<Pickup> crystals = new Array<>();
  final Array<Pickup> humans = new Array<>();
  final Array<Explosion> explosions = new Array<>();

  final Rectangle worldBounds;

  int score = 0;
  int lives = 3;
  int wave = 1;
  int crystalsCollected = 0;
  int bombsAvailable = 0;

  float time = 0f;
  float spawnTimer = 0f;
  float waveTimer = 0f;
  float waveLength = 55f; // seconds until boss auto-spawns

  Boss boss = null;
  float bossBuildProgress = 0f; // increased by Gatherers "delivering"

  private final CollisionHandler collisionHandler;
  private final EventDispatcher dispatcher;

  WorldManager(GameRoot game, EventDispatcher dispatcher) {
    this.game = game;
    this.dispatcher = dispatcher;
    this.camera = new OrthographicCamera(GameRoot.VIEW_W, GameRoot.VIEW_H);
    this.viewport = new FitViewport(GameRoot.VIEW_W, GameRoot.VIEW_H, camera);
    camera.position.set(GameRoot.VIEW_W / 2f, GameRoot.VIEW_H / 2f, 0);

    worldBounds = new Rectangle(0, 0, GameRoot.WORLD_W, GameRoot.WORLD_H);

    player = new Player(new Vector2(GameRoot.WORLD_W / 2f, GameRoot.WORLD_H / 2f));

    spawnField();
    collisionHandler = new CollisionHandler(this);
  }

  void update(float delta, InputController input) {
    time += delta;
    spawnTimer += delta;
    waveTimer += delta;

    input.update();

    // Player movement & shooting
    player.update(delta, input.getMove(), GameRoot.WORLD_W, GameRoot.WORLD_H);
    Vector2 aim = input.getAim();
    if (aim.len2() > 0.08f) {
      player.tryShoot(delta, aim, bullets);
    }

    // Bomb
    if (input.pollBombPressed() && bombsAvailable > 0) {
      bombsAvailable--;
      bombs.add(new Bomb(new Vector2(player.getPos()), new Vector2(aim).nor().scl(240f)));
    }

    // Update bullets
    for (int i = bullets.size - 1; i >= 0; i--) {
      Bullet b = bullets.get(i);
      b.update(delta, GameRoot.WORLD_W, GameRoot.WORLD_H);
      if (!b.isAlive()) {
        bullets.removeIndex(i);
      }
    }

    // Update bombs/explosions
    for (int i = bombs.size - 1; i >= 0; i--) {
      Bomb b = bombs.get(i);
      b.update(delta, GameRoot.WORLD_W, GameRoot.WORLD_H);
      if (b.isExploded()) {
        explosions.add(new Explosion(new Vector2(b.getPos()), 1.2f));
        bombs.removeIndex(i);
        collisionHandler.handleBombExplosion(b);
      }
    }

    for (int i = explosions.size - 1; i >= 0; i--) {
      Explosion e = explosions.get(i);
      e.update(delta);
      if (!e.alive) {
        explosions.removeIndex(i);
      }
    }

    // Enemies
    for (int i = enemies.size - 1; i >= 0; i--) {
      Enemy e = enemies.get(i);
      e.update(delta, player, crystals, humans, boss, GameRoot.WORLD_W, GameRoot.WORLD_H);
    }

    // Boss spawn conditions
    if (boss == null && (waveTimer >= waveLength || bossBuildProgress >= 1f)) {
      boss = new Boss(randWorld());
    }
    if (boss != null) {
      boss.update(delta, player, GameRoot.WORLD_W, GameRoot.WORLD_H);
    }

    // Simple enemy respawn pacing (except boss)
    if (spawnTimer > 2.5f) {
      spawnTimer = 0f;
      int type = MathUtils.random(2);
      if (type == 0) {
        enemies.add(Enemy.hunter(randWorld(), dispatcher));
      } else if (type == 1) {
        enemies.add(Enemy.gatherer(randWorld(), dispatcher));
      } else {
        enemies.add(Enemy.converter(randWorld(), dispatcher));
      }
      if (MathUtils.randomBoolean(0.6f)) {
        crystals.add(Pickup.crystal(randWorld()));
      }
      if (MathUtils.randomBoolean(0.25f)) {
        humans.add(Pickup.human(randWorld()));
      }
    }

    // Handle collisions
    collisionHandler.handle();

    // Remove dead enemies
    for (int i = enemies.size - 1; i >= 0; i--) {
      if (!enemies.get(i).isAlive()) {
        enemies.removeIndex(i);
      }
    }

    // camera follows player (wrap around)
    camera.position.set(player.getPos().x, player.getPos().y, 0);
    Vector2 camPos = new Vector2(camera.position.x, camera.position.y);
    WorldUtils.wrap(camPos, GameRoot.WORLD_W, GameRoot.WORLD_H);
    camera.position.set(camPos.x, camPos.y, 0);
    camera.update();
  }

  void render(ShapeRenderer shapes) {
    shapes.setProjectionMatrix(camera.combined);
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    player.render(shapes);
    for (Bullet b : bullets) {
      b.render(shapes);
    }
    for (Bomb b : bombs) {
      b.render(shapes);
    }
    for (Enemy e : enemies) {
      e.render(shapes);
    }
    for (Pickup p : crystals) {
      p.render(shapes);
    }
    for (Pickup p : humans) {
      p.render(shapes);
    }
    for (Explosion e : explosions) {
      e.render(shapes);
    }
    if (boss != null) {
      boss.render(shapes);
    }
    shapes.end();
  }

  void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  private void spawnField() {
    MathUtils.random.setSeed(TimeUtils.millis());
    for (int i = 0; i < 30; i++) {
      crystals.add(Pickup.crystal(randWorld()));
    }
    for (int i = 0; i < 8; i++) {
      humans.add(Pickup.human(randWorld()));
    }
    for (int i = 0; i < 15; i++) {
      enemies.add(Enemy.hunter(randWorld(), dispatcher));
    }
    for (int i = 0; i < 10; i++) {
      enemies.add(Enemy.gatherer(randWorld(), dispatcher));
    }
    for (int i = 0; i < 6; i++) {
      enemies.add(Enemy.converter(randWorld(), dispatcher));
    }
  }

  private Vector2 randWorld() {
    return new Vector2(MathUtils.random(worldBounds.width), MathUtils.random(worldBounds.height));
  }

  void nextWave() {
    wave++;
    waveTimer = 0f;
    boss = null;
    bossBuildProgress = 0f;
    for (int i = 0; i < 10 + wave * 3; i++) {
      enemies.add(Enemy.hunter(randWorld(), dispatcher));
    }
    for (int i = 0; i < 6 + wave * 2; i++) {
      enemies.add(Enemy.gatherer(randWorld(), dispatcher));
    }
    for (int i = 0; i < 4 + wave; i++) {
      enemies.add(Enemy.converter(randWorld(), dispatcher));
    }
  }

  void addBossBuildProgress(float amt) {
    bossBuildProgress = Math.min(1f, bossBuildProgress + amt);
  }

}
