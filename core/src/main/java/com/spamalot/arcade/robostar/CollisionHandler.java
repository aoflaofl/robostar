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
        if (boss != null && boss.alive && boss.pos.dst2(b.pos) < (boss.radius + 90f) * (boss.radius + 90f)) {
            boss.hp -= 35f;
            if (boss.hp <= 0) {
                boss.alive = false;
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
                if (e.pos.dst2(b.pos) < (e.radius + b.radius) * (e.radius + b.radius)) {
                    e.alive = false;
                    world.bullets.removeIndex(j);
                    world.score += 50;
                    break;
                }
            }
            if (!e.alive) {
                // handled in world.remove loop
            }
        }
    }

    private void handlePlayerEnemy() {
        Player player = world.player;
        for (Enemy e : world.enemies) {
            if (player.getInvuln() <= 0 && e.pos.dst2(player.pos) < (e.radius + player.radius) * (e.radius + player.radius)) {
                world.lives--;
                player.respawn(new Vector2(GameRoot.WORLD_W / 2f, GameRoot.WORLD_H / 2f));
            }
        }
    }

    private void handleBossCollisions() {
        Boss boss = world.boss;
        if (boss == null) return;

        for (int j = world.bullets.size - 1; j >= 0; j--) {
            Bullet b = world.bullets.get(j);
            if (boss.pos.dst2(b.pos) < (boss.radius + b.radius) * (boss.radius + b.radius)) {
                boss.hp -= 5f;
                world.bullets.removeIndex(j);
                if (boss.hp <= 0) {
                    boss.alive = false;
                    world.score += 1000 * world.wave;
                    world.nextWave();
                    return;
                }
            }
        }

        Player player = world.player;
        if (player.getInvuln() <= 0 && boss.pos.dst2(player.pos) < (boss.radius + player.radius) * (boss.radius + player.radius)) {
            world.lives--;
            player.respawn(new Vector2(GameRoot.WORLD_W / 2f, GameRoot.WORLD_H / 2f));
        }
    }

    private void handlePlayerPickups() {
        Player player = world.player;
        for (int i = world.crystals.size - 1; i >= 0; i--) {
            Pickup p = world.crystals.get(i);
            if (player.pos.dst2(p.pos) < (player.radius + p.radius) * (player.radius + p.radius)) {
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
            if (player.pos.dst2(p.pos) < (player.radius + p.radius) * (player.radius + p.radius)) {
                world.humans.removeIndex(i);
                world.score += 150;
                float r = MathUtils.random();
                if (r < 0.33f) player.powerSpeed(8f);
                else if (r < 0.66f) player.powerFireRate(8f);
                else player.powerShield(6f);
            }
        }
    }
}
