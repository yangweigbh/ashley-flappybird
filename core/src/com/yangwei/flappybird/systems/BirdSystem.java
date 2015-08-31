/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
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

package com.yangwei.flappybird.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.yangwei.flappybird.World;
import com.yangwei.flappybird.components.BirdComponent;
import com.yangwei.flappybird.components.MovementComponent;
import com.yangwei.flappybird.components.StateComponent;
import com.yangwei.flappybird.components.TransformComponent;

public class BirdSystem extends IteratingSystem {
	private static final Family family = Family.all(BirdComponent.class,
            StateComponent.class,
            TransformComponent.class,
            MovementComponent.class).get();

	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public BirdSystem(World world) {
		super(family);
		
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		MovementComponent mov = mm.get(entity);

		if (t.pos.x < 0) {
			t.pos.x = World.WORLD_WIDTH;
		}
		
		if (t.pos.x > World.WORLD_WIDTH) {
			t.pos.x = 0;
		}

		t.rotation = (float) Math.atan(mov.velocity.y);
	}

	public void hitPipe(Entity entity) {
		if (!family.matches(entity)) return;

		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);

		mov.velocity.y = 0;
		state.set(BirdComponent.STATE_FALL);
	}

	public void hitLand(Entity entity) {
		if (!family.matches(entity)) return;

		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);

		mov.velocity.y = 0;
		state.set(BirdComponent.STATE_FALL);
	}
}
