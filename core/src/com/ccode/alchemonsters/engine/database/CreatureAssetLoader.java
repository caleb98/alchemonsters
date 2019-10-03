package com.ccode.alchemonsters.engine.database;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class CreatureAssetLoader extends AsynchronousAssetLoader {

	public CreatureAssetLoader() {
		super(new InternalFileHandleResolver());
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters parameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object loadSync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array getDependencies(String fileName, FileHandle file, AssetLoaderParameters parameter) {
		// TODO Auto-generated method stub
		return null;
	}

}
