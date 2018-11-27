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
			// barrel uses normal maps
			texturedModel.getTexture().setNormalMap(loader.loadTexture("objects/barrelNormal"));
			// barrel uses specular map just because I want to test specular maps with normal mapped objects
			texturedModel.getTexture().setShineDamper(5);
			texturedModel.getTexture().setReflectivity(0.5f);
			texturedModel.getTexture().setSpecularMap(loader.loadTexture("objects/barrelSpecular"));
		}
		entity = new Entity(texturedModel, position, 0, 0, 0, 3f);
		texturedModel.getTexture().setShineDamper(10);
		texturedModel.getTexture().setReflectivity(0.5f);
	}
}
