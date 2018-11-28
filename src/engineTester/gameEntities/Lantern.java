package engineTester.gameEntities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class Lantern extends GameEntity {
	
	private static TexturedModel texturedModel=null;
	private static final float SIZE_RANDOMIZER_FACTOR = 0;
	
	
	public Lantern(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("objects/lantern"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("objects/lantern2")));
			// bump map for glow effect
			texturedModel.getTexture().setBumpMap(loader.loadTexture("objects/lanternSpecular"));
		}
		Random random = new Random();
		float size = 1f + ((random.nextFloat()*SIZE_RANDOMIZER_FACTOR)-(SIZE_RANDOMIZER_FACTOR/2f));
		float rotationYRandom = 180.0f*random.nextFloat();
        entity = new Entity(texturedModel, position, 0, rotationYRandom, 0, size);
	}
	
}
