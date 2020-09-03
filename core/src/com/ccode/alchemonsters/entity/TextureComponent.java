package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {

	public TextureRegion texture;
	public float originX = 0;
	public float originY = 0;
	
	public TextureComponent(TextureRegion texture) {
		this.texture = texture;
	}
	
	public TextureComponent(TextureRegion texture, int originX, int originY) {
		this.texture = texture;
		this.originX = originX;
		this.originY = originY;
	}
	
}
