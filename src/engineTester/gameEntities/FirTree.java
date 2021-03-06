package engineTester.gameEntities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class FirTree extends GameEntity {
	
	private static TexturedModel texturedModel=null;
	private static final float SIZE_RANDOMIZER_FACTOR = 0.25f;
	
	
	public FirTree(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/tree"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("vegetation/tree")));
		}
		Random random = new Random();
		float size = 5f + ((random.nextFloat()*SIZE_RANDOMIZER_FACTOR)-(SIZE_RANDOMIZER_FACTOR/2f));
        entity = new Entity(texturedModel, position, 0, 0, 0, size);
	}
	
}
