package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class FirTree extends GameEntity {
	
	private static TexturedModel texturedModel=null;
	
	
	public FirTree(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/tree"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("vegetation/tree")));
		}
        entity = new Entity(texturedModel, position, 0, 0, 0, 5f);
	}
	
}
