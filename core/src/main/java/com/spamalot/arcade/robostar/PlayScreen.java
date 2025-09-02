package com.spamalot.arcade.robostar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {
    private final GameRoot game;
    private final Camera camera;
    private final Viewport viewport;

    private final Player player;
    private final Array<Bullet> bullets = new Array<>();
    private final Array<Bomb> bombs = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Pickup> crystals = new Array<>();
    private final Array<Pickup> humans = new Array<>();
    private final Array<Explosion> explosions = new Array<>();

    private final Rectangle worldBounds;

    private int score = 0;
    private int lives = 3;
    private int wave = 1;
    private int crystalsCollected = 0;
    private int bombsAvailable = 0;

    private float time = 0f;
    private float spawnTimer = 0f;
    private float waveTimer = 0f;
    private float waveLength = 55f; // seconds until boss auto-spawns

    private Boss boss = null;
    private float bossBuildProgress = 0f; // increased by Gatherers "delivering"

    private final GlyphLayout layout = new GlyphLayout();

    public PlayScreen(GameRoot game) {
        this.game = game;
        this.camera = new OrthographicCamera(GameRoot.VIEW_W, GameRoot.VIEW_H);
        this.viewport = new FitViewport(GameRoot.VIEW_W, GameRoot.VIEW_H, camera);
        camera.position.set(GameRoot.VIEW_W/2f, GameRoot.VIEW_H/2f, 0);

        worldBounds = new Rectangle(0,0, GameRoot.WORLD_W, GameRoot.WORLD_H);

        player = new Player(new Vector2(GameRoot.WORLD_W/2f, GameRoot.WORLD_H/2f));

        // seed world
        spawnField();
    }

    private void spawnField() {
        MathUtils.random.setSeed(TimeUtils.millis());
        // initial enemies, crystals, humans
        for (int i=0;i<30;i++) crystals.add(Pickup.crystal(randWorld()));
        for (int i=0;i<8;i++) humans.add(Pickup.human(randWorld()));
        for (int i=0;i<15;i++) enemies.add(Enemy.hunter(randWorld()));
        for (int i=0;i<10;i++) enemies.add(Enemy.gatherer(randWorld()));
        for (int i=0;i<6;i++) enemies.add(Enemy.converter(randWorld()));
    }

    private Vector2 randWorld() {
        return new Vector2(MathUtils.random(worldBounds.width), MathUtils.random(worldBounds.height));
    }

    @Override
    public void render(float delta) {
        update(delta);
        // apply bus updates
        bossBuildProgress = Math.min(1f, bossBuildProgress + GameBus.bossBuildAdd);
        GameBus.bossBuildAdd = 0f;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // camera follows player (wrap around)
        camera.position.set(player.pos.x, player.pos.y, 0);
        wrapCamera();
        camera.update();

        game.shapes.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);

        // Draw world with shapes
        game.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        // player
        player.render(game.shapes);

        for (Bullet b : bullets) b.render(game.shapes);
        for (Bomb b : bombs) b.render(game.shapes);

        for (Enemy e : enemies) e.render(game.shapes);
        for (Pickup p : crystals) p.render(game.shapes);
        for (Pickup p : humans) p.render(game.shapes);
        for (Explosion e : explosions) e.render(game.shapes);

        if (boss != null) boss.render(game.shapes);
        game.shapes.end();

        // UI
        game.batch.begin();
        String info = String.format("Score %d   Lives %d   Wave %d   Crystals %d   Bombs %d",
                score, lives, wave, crystalsCollected, bombsAvailable);
        layout.setText(game.font, info);
        game.font.draw(game.batch, info, camera.position.x - GameRoot.VIEW_W/2f + 10, camera.position.y + GameRoot.VIEW_H/2f - 10);

        // boss build bar
        float barW = 260f;
        float barH = 10f;
        float px = camera.position.x + GameRoot.VIEW_W/2f - barW - 20;
        float py = camera.position.y + GameRoot.VIEW_H/2f - 24;
        game.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Color.DARK_GRAY);
        game.shapes.rect(px, py, barW, barH);
        game.shapes.setColor(Color.RED);
        game.shapes.rect(px, py, barW * Math.min(1f, bossBuildProgress), barH);
        game.shapes.end();
        game.font.draw(game.batch, "Boss Build", px, py - 4);
        game.batch.end();

        if (lives < 0) {
            game.setScreen(new GameOverScreen(game, score));
        }
    }

    private void update(float delta) {
        time += delta;
        spawnTimer += delta;
        waveTimer += delta;

        game.input.update();

        // Player movement & shooting
        player.update(delta, game.input.getMove(), wrapPos(player.pos), GameRoot.WORLD_W, GameRoot.WORLD_H);
        // shooting
        Vector2 aim = game.input.getAim();
        if (aim.len2() > 0.08f) {
            player.tryShoot(delta, aim, bullets);
        }

        // Bomb
        if (game.input.pollBombPressed() && bombsAvailable > 0) {
            bombsAvailable--;
            bombs.add(new Bomb(new Vector2(player.pos), new Vector2(aim).nor().scl(240f)));
        }

        // Update bullets
        for (int i = bullets.size-1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta, GameRoot.WORLD_W, GameRoot.WORLD_H);
            if (!b.alive) bullets.removeIndex(i);
        }

        // Update bombs/explosions
        for (int i = bombs.size-1; i >= 0; i--) {
            Bomb b = bombs.get(i);
            b.update(delta, GameRoot.WORLD_W, GameRoot.WORLD_H);
            if (b.exploded) {
                explosions.add(new Explosion(new Vector2(b.pos), 1.2f));
                bombs.removeIndex(i);
                // damage boss if near
                if (boss != null && boss.alive && boss.pos.dst2(b.pos) < (boss.radius+90f)*(boss.radius+90f)) {
                    boss.hp -= 35f;
                    if (boss.hp <= 0) {
                        boss.alive = false;
                        score += 1000 * wave;
                        nextWave();
                    }
                }
            }
        }

        for (int i = explosions.size-1; i>=0; i--) {
            Explosion e = explosions.get(i);
            e.update(delta);
            if (!e.alive) explosions.removeIndex(i);
        }

        // Enemies
        for (int i = enemies.size-1; i>=0; i--) {
            Enemy e = enemies.get(i);
            e.update(delta, player, crystals, humans, boss, GameRoot.WORLD_W, GameRoot.WORLD_H);
            // collide bullet
            for (int j = bullets.size-1; j>=0; j--) {
                Bullet b = bullets.get(j);
                if (e.pos.dst2(b.pos) < (e.radius+b.radius)*(e.radius+b.radius)) {
                    e.alive = false;
                    bullets.removeIndex(j);
                    score += 50;
                    break;
                }
            }
            // collide player
            if (player.getInvuln()<=0 && e.pos.dst2(player.pos) < (e.radius+player.radius)*(e.radius+player.radius)) {
                lives--;
                player.respawn(new Vector2(GameRoot.WORLD_W/2f, GameRoot.WORLD_H/2f));
            }
            if (!e.alive) enemies.removeIndex(i);
        }

        // Boss spawn conditions
        if (boss == null && (waveTimer >= waveLength || bossBuildProgress >= 1f)) {
            boss = new Boss(randWorld());
        }
        if (boss != null) {
            boss.update(delta, player, GameRoot.WORLD_W, GameRoot.WORLD_H);
            // collide bullets
            for (int j = bullets.size-1; j>=0; j--) {
                Bullet b = bullets.get(j);
                if (boss.pos.dst2(b.pos) < (boss.radius+b.radius)*(boss.radius+b.radius)) {
                    boss.hp -= 5f;
                    bullets.removeIndex(j);
                    if (boss.hp <= 0) {
                        boss.alive = false;
                        score += 1000 * wave;
                        nextWave();
                        break;
                    }
                }
            }
            // collide player
            if (player.getInvuln()<=0 && boss.pos.dst2(player.pos) < (boss.radius+player.radius)*(boss.radius+player.radius)) {
                lives--;
                player.respawn(new Vector2(GameRoot.WORLD_W/2f, GameRoot.WORLD_H/2f));
            }
        }

        // Pickups
        for (int i = crystals.size-1; i>=0; i--) {
            Pickup p = crystals.get(i);
            if (player.pos.dst2(p.pos) < (player.radius + p.radius)*(player.radius+p.radius)) {
                crystals.removeIndex(i);
                crystalsCollected++;
                score += 10;
                if (crystalsCollected >= 3) {
                    crystalsCollected -= 3;
                    bombsAvailable++;
                }
            }
        }
        for (int i = humans.size-1; i>=0; i--) {
            Pickup p = humans.get(i);
            if (player.pos.dst2(p.pos) < (player.radius + p.radius)*(player.radius+p.radius)) {
                humans.removeIndex(i);
                score += 150;
                // random power-up
                float r = MathUtils.random();
                if (r < 0.33f) player.powerSpeed(8f);
                else if (r < 0.66f) player.powerFireRate(8f);
                else player.powerShield(6f);
            }
        }

        // Simple enemy respawn pacing (except boss)
        if (spawnTimer > 2.5f) {
            spawnTimer = 0f;
            int type = MathUtils.random(2);
            if (type == 0) enemies.add(Enemy.hunter(randWorld()));
            else if (type == 1) enemies.add(Enemy.gatherer(randWorld()));
            else enemies.add(Enemy.converter(randWorld()));
            if (MathUtils.randomBoolean(0.6f)) crystals.add(Pickup.crystal(randWorld()));
            if (MathUtils.randomBoolean(0.25f)) humans.add(Pickup.human(randWorld()));
        }
    }

    private void nextWave() {
        wave++;
        waveTimer = 0f;
        boss = null;
        bossBuildProgress = 0f;
        // add more enemies for difficulty
        for (int i=0;i<10 + wave*3;i++) enemies.add(Enemy.hunter(randWorld()));
        for (int i=0;i<6 + wave*2;i++) enemies.add(Enemy.gatherer(randWorld()));
        for (int i=0;i<4 + wave;i++) enemies.add(Enemy.converter(randWorld()));
    }

    private void wrapCamera() {
        if (camera.position.x < 0) camera.position.x += GameRoot.WORLD_W;
        if (camera.position.x >= GameRoot.WORLD_W) camera.position.x -= GameRoot.WORLD_W;
        if (camera.position.y < 0) camera.position.y += GameRoot.WORLD_H;
        if (camera.position.y >= GameRoot.WORLD_H) camera.position.y -= GameRoot.WORLD_H;
    }

    private Vector2 wrapPos(Vector2 p) {
        if (p.x < 0) p.x += GameRoot.WORLD_W;
        else if (p.x >= GameRoot.WORLD_W) p.x -= GameRoot.WORLD_W;
        if (p.y < 0) p.y += GameRoot.WORLD_H;
        else if (p.y >= GameRoot.WORLD_H) p.y -= GameRoot.WORLD_H;
        return p;
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
