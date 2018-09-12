package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class Tree extends GameEntity {

	private static TexturedModel texturedModel=null;
	
	
	public Tree(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/lowPolyTree"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("vegetation/lowPolyTree")));
		}
        entity = new Entity(texturedModel, position, 0, 0, 0, 0.6f);
	}
		
	
	
}
