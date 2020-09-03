package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {

	public Animation<TextureRegion> animation;
	public float originX = 0;
	public float originY = 0;
	
	public AnimationComponent(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
	
	public AnimationComponent(Animation<TextureRegion> animation, float originX, float originY) {
		this.animation = animation;
		this.originX = originX;
		this.originY = originY;
	}
	
}
