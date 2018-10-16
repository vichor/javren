package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import objconverter.OBJLoader;
import textures.ModelTexture;

public class Rocks extends GameEntity {

	private static TexturedModel texturedModel = null;
	
	public Rocks () {
		if (texturedModel == null) {
			//texturedModel = new TexturedModel(OBJFileLoader.loadOBJ("objects/rocks", loader),
			texturedModel = new TexturedModel(OBJLoader.loadObjModel("objects/rocks", loader),
        		new ModelTexture(loader.loadTexture("objects/rocks")));
		}
		entity = new Entity(texturedModel, new Vector3f(400,21,-400), 0, 0, 0, 400);
	}
}
