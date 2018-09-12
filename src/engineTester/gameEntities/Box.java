package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import objconverter.OBJLoader;
import textures.ModelTexture;

public class Box extends GameEntity {

	private static TexturedModel texturedModel = null;
	
	public Box (Vector3f position) {
		if (texturedModel == null) {
			texturedModel = new TexturedModel(OBJLoader.loadObjModel("objects/box", loader),
        		new ModelTexture(loader.loadTexture("objects/box")));
		}
		entity = new Entity(texturedModel, position, 0, 0, 0, 6);
	}
}
