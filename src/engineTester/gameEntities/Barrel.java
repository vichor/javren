package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import textures.ModelTexture;

public class Barrel extends GameEntity {

	private static TexturedModel texturedModel = null;
	
	public Barrel (Vector3f position) {
		if (texturedModel == null) {
			texturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("objects/barrel", loader),
        		new ModelTexture(loader.loadTexture("objects/barrel")));
		}
		entity = new Entity(texturedModel, position, 0, 0, 0, 3f);
		texturedModel.getTexture().setShineDamper(10);
		texturedModel.getTexture().setReflectivity(0.5f);
	}
}
