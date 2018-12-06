package engineTester.gameEntities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import textures.ModelTexture;

public class Boulder extends GameEntity {
	
	private static TexturedModel texturedModel = null;
		
	public Boulder (Vector3f position) {
		if (texturedModel == null) {
			texturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("objects/boulder", loader),
        		new ModelTexture(loader.loadTexture("objects/boulder")));
			// boulder uses normal maps
			texturedModel.getTexture().setNormalMap(loader.loadTexture("objects/boulderNormal"));
			//texturedModel.getTexture().setShineDamper(5);
			//texturedModel.getTexture().setReflectivity(0.5f);
		}
		Random random = new Random();
		float rx = 360f * random.nextFloat();
		float ry = 360f * random.nextFloat();
		float rz = 360f * random.nextFloat();
		float scale = 2f * random.nextFloat();
		entity = new Entity(texturedModel, position, rx, ry, rz, scale);
		texturedModel.getTexture().setShineDamper(10);
		texturedModel.getTexture().setReflectivity(0.5f);
	}

}
