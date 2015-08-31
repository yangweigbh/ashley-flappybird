package com.yangwei.flappybird;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.yangwei.flappybird.components.BirdComponent;
import com.yangwei.flappybird.components.MovementComponent;
import com.yangwei.flappybird.systems.AnimationSystem;
import com.yangwei.flappybird.systems.BackgroundSystem;
import com.yangwei.flappybird.systems.BirdSystem;
import com.yangwei.flappybird.systems.BoundsSystem;
import com.yangwei.flappybird.systems.CameraSystem;
import com.yangwei.flappybird.systems.CollisionSystem;
import com.yangwei.flappybird.systems.GravitySystem;
import com.yangwei.flappybird.systems.MovementSystem;
import com.yangwei.flappybird.systems.PlatformSystem;
import com.yangwei.flappybird.systems.RenderingSystem;
import com.yangwei.flappybird.systems.SquirrelSystem;
import com.yangwei.flappybird.systems.StateSystem;

import java.util.Iterator;

/**
 * Created by yangwei on 15/8/29.
 */
public class GameScreen implements Screen {
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;

    FlappyBird game;

    OrthographicCamera guiCam;
    Vector3 touchPoint;
    World world;
    CollisionListener collisionListener;
    int lastScore;
    String scoreString;

    PooledEngine engine;

    private int state;

    public GameScreen(FlappyBird game) {
        this.game = game;

        state = GAME_READY;
        guiCam = new OrthographicCamera(320, 480);
        guiCam.position.set(320 / 2, 480 / 2, 0);
        touchPoint = new Vector3();
        collisionListener = new CollisionSystem.CollisionListener() {
            @Override
            public void jump () {
                Assets.playSound(Assets.swooshSound);
            }

            @Override
            public void hit () {
                Assets.playSound(Assets.hitSound);
            }

            @Override
            public void coin () {
                Assets.playSound(Assets.pointSound);
            }
        };

        engine = new PooledEngine();

        world = new World(engine);

        engine.addSystem(new BirdSystem(world));
        engine.addSystem(new PlatformSystem());
        engine.addSystem(new CameraSystem());
        engine.addSystem(new BackgroundSystem());
        engine.addSystem(new GravitySystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new StateSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new CollisionSystem(world, collisionListener));
        engine.addSystem(new RenderingSystem(game.batcher));

        engine.getSystem(BackgroundSystem.class).setCamera(engine.getSystem(RenderingSystem.class).getCamera());

        world.create();

        lastScore = 0;
        scoreString = "SCORE: 0";

        pauseSystems();
    }

    public void update (float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        engine.update(deltaTime);

        switch (state) {
            case GAME_READY:
                updateReady();
                break;
            case GAME_RUNNING:
                updateRunning(deltaTime);
                break;
            case GAME_PAUSED:
                updatePaused();
                break;
            case GAME_LEVEL_END:
                updateLevelEnd();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }

    private void updateReady () {
        if (Gdx.input.justTouched()) {
            state = GAME_RUNNING;
            resumeSystems();
        }
    }

    private void updateRunning (float deltaTime) {
        if (Gdx.input.justTouched()) {
            ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(BirdComponent.class, MovementComponent.class).get());
            for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
                Entity next =  iterator.next();
                next.getComponent(MovementComponent.class).velocity.y += 5;
            }
        }

        if (world.score != lastScore) {
            lastScore = world.score;
        }
    }

    private void updatePaused () {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (resumeBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound);
                state = GAME_RUNNING;
                resumeSystems();
                return;
            }

            if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }
    }

    private void updateLevelEnd () {
        if (Gdx.input.justTouched()) {
            engine.removeAllEntities();
            world = new World(engine);
            world.score = lastScore;
            state = GAME_READY;
        }
    }

    private void updateGameOver () {
        if (Gdx.input.justTouched()) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    public void drawUI () {
        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);
        game.batcher.begin();
        switch (state) {
            case GAME_READY:
                presentReady();
                break;
            case GAME_RUNNING:
                presentRunning();
                break;
            case GAME_PAUSED:
                presentPaused();
                break;
            case GAME_OVER:
                presentGameOver();
                break;
        }
        game.batcher.end();
    }

    private void presentReady () {
        game.batcher.draw(Assets.readyTitle, 160 - 192 / 2, 240 - 32 / 2, 192, 32);
        game.batcher.draw(Assets.tutorial, 160 - 192 / 2, 240 - 32 / 2, 192, 32);
    }

    private void presentRunning () {
    }

    private void presentPaused () {
    }

    private void presentGameOver () {
    }

    private void pauseSystems() {
        engine.getSystem(BirdSystem.class).setProcessing(false);
        engine.getSystem(SquirrelSystem.class).setProcessing(false);
        engine.getSystem(PlatformSystem.class).setProcessing(false);
        engine.getSystem(GravitySystem.class).setProcessing(false);
        engine.getSystem(MovementSystem.class).setProcessing(false);
        engine.getSystem(BoundsSystem.class).setProcessing(false);
        engine.getSystem(StateSystem.class).setProcessing(false);
        engine.getSystem(AnimationSystem.class).setProcessing(false);
        engine.getSystem(CollisionSystem.class).setProcessing(false);
    }

    private void resumeSystems() {
        engine.getSystem(BirdSystem.class).setProcessing(true);
        engine.getSystem(SquirrelSystem.class).setProcessing(true);
        engine.getSystem(PlatformSystem.class).setProcessing(true);
        engine.getSystem(GravitySystem.class).setProcessing(true);
        engine.getSystem(MovementSystem.class).setProcessing(true);
        engine.getSystem(BoundsSystem.class).setProcessing(true);
        engine.getSystem(StateSystem.class).setProcessing(true);
        engine.getSystem(AnimationSystem.class).setProcessing(true);
        engine.getSystem(CollisionSystem.class).setProcessing(true);
    }


    @Override
    public void render (float delta) {
        update(delta);
        drawUI();
    }

    @Override
    public void pause () {
        if (state == GAME_RUNNING) {
            state = GAME_PAUSED;
            pauseSystems();
        }
    }
}
