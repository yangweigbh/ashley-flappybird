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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Arrays;
import java.util.Collections;

public class Assets {
    private static ObjectMap<String, Rectangle> atlas = new ObjectMap<String, Rectangle>();

	public static Texture items;
	public static TextureRegion backgroundDayRegion;
    public static TextureRegion backgroundNightRegion;
	public static TextureRegion buttonMenu;
	public static TextureRegion buttonOk;
	public static TextureRegion buttonPause;
	public static TextureRegion buttonPlay;
	public static TextureRegion buttonRate;
	public static TextureRegion buttonResume;
	public static TextureRegion buttonScore;
	public static TextureRegion buttonShare;
    public static TextureRegion land;
	public static TextureRegion title;
    public static TextureRegion readyTitle;
    public static TextureRegion tutorial;
	public static Animation bird1Anim;
	public static Animation bird2Anim;
	public static Animation bird3Anim;

	public static Sound dieSound;
	public static Sound hitSound;
	public static Sound pointSound;
	public static Sound swooshSound;
	public static Sound wingSound;

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {
		items = loadTexture("data/atlas.png");
        parseAtlas(Gdx.files.internal("data/atlas.txt"));
        backgroundDayRegion = getTextureRegion("bg_day");
        backgroundNightRegion = getTextureRegion("bg_night");
        buttonMenu = getTextureRegion("button_menu");
        buttonOk = getTextureRegion("button_ok");
        buttonPause = getTextureRegion("button_pause");
        buttonPlay = getTextureRegion("button_play");
        buttonRate = getTextureRegion("button_rate");
        buttonResume = getTextureRegion("button_resume");
        buttonScore = getTextureRegion("button_score");
        buttonShare = getTextureRegion("button_share");
        title = getTextureRegion("title");
        land = getTextureRegion("land");
        readyTitle = getTextureRegion("text_ready");
        tutorial = getTextureRegion("tutorial");

		bird1Anim = new Animation(0.1f, getTextureRegion("bird0_0"), getTextureRegion("bird0_1"), getTextureRegion("bird0_2"), getTextureRegion("bird0_0"));
		bird2Anim = new Animation(0.1f, getTextureRegion("bird1_0"), getTextureRegion("bird1_1"), getTextureRegion("bird1_2"), getTextureRegion("bird1_0"));
		bird3Anim = new Animation(0.1f, getTextureRegion("bird2_0"), getTextureRegion("bird2_1"), getTextureRegion("bird2_2"), getTextureRegion("bird2_0"));

		dieSound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_die.wav"));
		pointSound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_point.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_hit.wav"));
		swooshSound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_swooshing.wav"));
		wingSound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_wing.wav"));
		
		bird1Anim.setPlayMode(PlayMode.LOOP);
		bird2Anim.setPlayMode(PlayMode.LOOP);
		bird3Anim.setPlayMode(PlayMode.LOOP);
	}

    private static TextureRegion getTextureRegion(String region) {
        Gdx.app.log("YangWei", region + atlas.get(region).x + atlas.get(region).y + atlas.get(region).width + atlas.get(region).height);
        return new TextureRegion(items, (int)atlas.get(region).x, (int)atlas.get(region).y, (int)atlas.get(region).width, (int)atlas.get(region).height);
    }

    private static void parseAtlas(FileHandle internal) {
        String[] strings = internal.readString().split("\n");
        for (int i = 0; i < strings.length; i++) {
            String[] line = strings[i].split(" ");
            atlas.put(line[0], new Rectangle(1024 * Float.parseFloat(line[3]), 1024 * Float.parseFloat(line[4]), Float.parseFloat(line[1]), Float.parseFloat(line[2])));
        }
    }

    public static void playSound (Sound sound) {
		 sound.play(1);
	}
}
