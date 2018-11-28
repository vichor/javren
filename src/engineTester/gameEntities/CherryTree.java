package engineTester.gameEntities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import textures.ModelTexture;

public class CherryTree extends GameEntity {
	
	private static TexturedModel texturedModel=null;
	private static final float SIZE_RANDOMIZER_FACTOR = 0.10f;
	
	
	public CherryTree(Vector3f position) {
		if (texturedModel==null) {
			RawModel rawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/cherry"));
			texturedModel = new TexturedModel(rawModel,new ModelTexture(loader.loadTexture("vegetation/cherry")));
			texturedModel.getTexture().setHasTransparency(true);
			texturedModel.getTexture().setShineDamper(10);
			texturedModel.getTexture().setReflectivity(0.5f);
			texturedModel.getTexture().setBumpMap(loader.loadTexture("vegetation/cherrySpecular"));
		}
		Random random = new Random();
		float size = 2f + ((random.nextFloat()*SIZE_RANDOMIZER_FACTOR)-(SIZE_RANDOMIZER_FACTOR/2f));
        entity = new Entity(texturedModel, position, 0, 0, 0, size);
	}
	
}
