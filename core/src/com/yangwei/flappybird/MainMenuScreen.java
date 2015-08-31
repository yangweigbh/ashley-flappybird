/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.yangwei.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen extends ScreenAdapter {
	FlappyBird game;
	OrthographicCamera guiCam;
	Rectangle soundBounds;
	Rectangle playBounds;
	Rectangle highscoresBounds;
	Rectangle rateBounds;
	Vector3 touchPoint;
    float stateTime = 0;
    int velocity = 100;
    float firstLandX = 0f;
    float secondLandX = 720f;
    float playTouchoffset = 0f;
    float scoreTouchoffset = 0f;
    float rateTouchoffset = 0f;

	public MainMenuScreen(FlappyBird game) {
		this.game = game;

		guiCam = new OrthographicCamera(720, 1280);
		guiCam.position.set(720 / 2, 1280 / 2, 0);
		playBounds = new Rectangle(66, 300, 260, 130);
		highscoresBounds = new Rectangle(400, 300, 260, 130);
		rateBounds = new Rectangle(290, 520, 150, 80);
		touchPoint = new Vector3();
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                playTouchoffset = 10;
				game.setScreen(new GameScreen(game));
				return;
			}
			if (highscoresBounds.contains(touchPoint.x, touchPoint.y)) {
                scoreTouchoffset = 10;
//				game.setScreen(new HighscoresScreen(game));
				return;
			}
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);

		game.batcher.disableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.backgroundDayRegion, 0, 0, 720, 1280);
		game.batcher.end();

		game.batcher.enableBlending();
		game.batcher.begin();
        game.batcher.draw(Assets.title, 125, 800, 460, 100);
        game.batcher.draw(Assets.bird1Anim.getKeyFrame(stateTime), 322, 690, 80, 80);
        game.batcher.draw(Assets.buttonRate, 290, 520 - rateTouchoffset, 150, 80);
		game.batcher.draw(Assets.buttonPlay, 60, 300 - playTouchoffset, 260, 130);
		game.batcher.draw(Assets.buttonScore, 400, 300 - scoreTouchoffset, 260, 130);

        game.batcher.draw(Assets.land, firstLandX, 0, 720, 170);
        game.batcher.draw(Assets.land, secondLandX, 0, 720, 170);
		game.batcher.end();
	}

	@Override
	public void render (float delta) {
        stateTime += delta;
        firstLandX -= delta*velocity;
        if (firstLandX <= -720) {
            firstLandX += 1440;
        }
        secondLandX -= delta*velocity;
        if (secondLandX <= -720) {
            secondLandX += 1440;
        }
		update();
		draw();
        if (!Gdx.input.isTouched()) {
            resetTouchOffset();
        }
	}

    private void resetTouchOffset() {
        playTouchoffset = 0f;
        scoreTouchoffset = 0f;
        rateTouchoffset = 0f;
    }
}
