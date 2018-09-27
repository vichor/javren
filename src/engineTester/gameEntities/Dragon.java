package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class Dragon extends GameEntity {
	private static TexturedModel texturedModel=null;
	
	
	public Dragon(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("dragon/dragon"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("dragon/skin")));
		}
        entity = new Entity(texturedModel, position, 0, 0, 0, 0.6f);
	}
		
}
