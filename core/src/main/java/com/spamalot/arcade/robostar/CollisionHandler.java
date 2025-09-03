package com.spamalot.arcade.robostar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles collision detection and responses for world entities.
 */
class CollisionHandler {
  private final WorldManager world;

  CollisionHandler(WorldManager world) {
    this.world = world;
  }

  void handle() {
    handleBulletEnemy();
    handlePlayerEnemy();
    handleBossCollisions();
    handlePlayerPickups();
  }

  void handleBombExplosion(Bomb b) {
    Boss boss = world.boss;
    if (boss != null && boss.isAlive()
        && boss.getPos().dst2(b.getPos()) < (boss.getRadius() + 90f) * (boss.getRadius() + 90f)) {
      boss.setHp(boss.getHp() - 35f);
      if (boss.getHp() <= 0) {
        boss.setAlive(false);
        world.score += 1000 * world.wave;
        world.nextWave();
      }
    }
  }

  private void handleBulletEnemy() {
    for (int i = world.enemies.size - 1; i >= 0; i--) {
      Enemy e = world.enemies.get(i);
      for (int j = world.bullets.size - 1; j >= 0; j--) {
        Bullet b = world.bullets.get(j);
        if (e.getPos().dst2(b.getPos()) < (e.getRadius() + b.getRadius()) * (e.getRadius() + b.getRadius())) {
          e.setAlive(false);
          world.bullets.removeIndex(j);
          world.score += 50;
          break;
        }
      }
      if (!e.isAlive()) {
        // handled in world.remove loop
      }
    }
  }

  private void handlePlayerEnemy() {
    Player player = world.player;
    for (Enemy e : world.enemies) {
      if (player.getInvuln() <= 0 && e.getPos().dst2(player.getPos()) < (e.getRadius() + player.getRadius())
          * (e.getRadius() + player.getRadius())) {
        world.lives--;
        player.respawn(new Vector2(GameRoot.WORLD_W / 2f, GameRoot.WORLD_H / 2f));
      }
    }
  }

  private void handleBossCollisions() {
    Boss boss = world.boss;
    if (boss == null) {
      return;
    }

    for (int j = world.bullets.size - 1; j >= 0; j--) {
      Bullet b = world.bullets.get(j);
      if (boss.getPos().dst2(b.getPos()) < (boss.getRadius() + b.getRadius()) * (boss.getRadius() + b.getRadius())) {
        boss.setHp(boss.getHp() - 5f);
        world.bullets.removeIndex(j);
        if (boss.getHp() <= 0) {
          boss.setAlive(false);
          world.score += 1000 * world.wave;
          world.nextWave();
          return;
        }
      }
    }

    Player player = world.player;
    if (player.getInvuln() <= 0 && boss.getPos().dst2(player.getPos()) < (boss.getRadius() + player.getRadius())
        * (boss.getRadius() + player.getRadius())) {
      world.lives--;
      player.respawn(new Vector2(GameRoot.WORLD_W / 2f, GameRoot.WORLD_H / 2f));
    }
  }

  private void handlePlayerPickups() {
    Player player = world.player;
    for (int i = world.crystals.size - 1; i >= 0; i--) {
      Pickup p = world.crystals.get(i);
      if (player.getPos().dst2(p.getPos()) < (player.getRadius() + p.getRadius())
          * (player.getRadius() + p.getRadius())) {
        world.crystals.removeIndex(i);
        world.crystalsCollected++;
        world.score += 10;
        if (world.crystalsCollected >= 3) {
          world.crystalsCollected -= 3;
          world.bombsAvailable++;
        }
      }
    }
    for (int i = world.humans.size - 1; i >= 0; i--) {
      Pickup p = world.humans.get(i);
      if (player.getPos().dst2(p.getPos()) < (player.getRadius() + p.getRadius())
          * (player.getRadius() + p.getRadius())) {
        world.humans.removeIndex(i);
        world.score += 150;
        float r = MathUtils.random();
        if (r < 0.33f) {
          player.powerSpeed(8f);
        } else if (r < 0.66f) {
          player.powerFireRate(8f);
        } else {
          player.powerShield(6f);
        }
      }
    }
  }
}
