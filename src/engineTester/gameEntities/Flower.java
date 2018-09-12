package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class Flower extends GameEntity {
	
	private static TexturedModel texturedModel=null;
	
	
	public Flower(Vector3f position) {
		if (texturedModel == null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/grassModel"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("vegetation/flower")));
			texturedModel.getTexture().setHasTransparency(true);
			texturedModel.getTexture().setUseFakeLighting(true);
		}
		entity = new Entity(texturedModel, position, 0, 0, 0, 1f);	
	}

}
